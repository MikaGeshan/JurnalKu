package com.example.jurnalku.ui.entries

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EmojiConfetti(
    icon: AppIconClass,
    onAnimationFinished: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    val emojisCount = 20
    val confettiItems = remember { 
        List(emojisCount) { 
            ConfettiItem(
                xOffset = Random.nextInt(0, screenWidth),
                duration = Random.nextInt(2000, 3500),
                delay = Random.nextInt(0, 800),
                size = Random.nextInt(24, 48),
                rotationMultiplier = Random.nextFloat() * 5f
            )
        } 
    }

    Box(modifier = Modifier.fillMaxSize()) {
        confettiItems.forEach { item ->
            var startTrigger by remember { mutableStateOf(false) }
            
            LaunchedEffect(Unit) {
                delay(item.delay.toLong())
                startTrigger = true
            }

            if (startTrigger) {
                val yAnim = remember { Animatable(-50f) }
                
                LaunchedEffect(Unit) {
                    yAnim.animateTo(
                        targetValue = screenHeight.toFloat() + 100f,
                        animationSpec = tween(item.duration, easing = LinearEasing)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = item.xOffset.dp, y = yAnim.value.dp)
                        .graphicsLayer {
                            rotationZ = yAnim.value * item.rotationMultiplier
                        }
                        .alpha(if (yAnim.value > screenHeight - 100) 1f - (yAnim.value - (screenHeight - 100)) / 100f else 1f)
                ) {
                    ComposableIcon(
                        icon = icon,
                        tint = Color.Unspecified,
                        size = item.size.dp
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(4500) // Max delay + duration + buffer
        onAnimationFinished()
    }
}

private data class ConfettiItem(
    val xOffset: Int,
    val duration: Int,
    val delay: Int,
    val size: Int,
    val rotationMultiplier: Float
)

@Composable
fun MoodPopup(
    icon: AppIconClass,
    onAnimationFinished: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 2.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        delay(1000)
        alpha.animateTo(0f, tween(500))
        onAnimationFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                }
        ) {
            ComposableIcon(
                icon = icon,
                tint = Color.Unspecified,
                size = 120.dp
            )
        }
    }
}
