package com.example.jurnalku.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.SoftGreen
import com.example.jurnalku.ui.theme.White

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: AppIconClass
)

@Composable
fun BottomTabBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            label = "Entries",
            route = "entries",
            icon = AppIconClass.Journal
        ),
        BottomNavItem(
            label = "Dateline",
            route = "dateline",
            icon = AppIconClass.Calendar
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp)
    ) {
        Surface(
            color = White,
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val animatedBgColor by animateColorAsState(
                        targetValue = if (isSelected) JungleGreen.copy(alpha = 0.08f) else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "bgColor"
                    )

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )

                    val textAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.6f,
                        label = "textAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(animatedBgColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(item.route) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            ComposableIcon(
                                icon = item.icon,
                                tint = if (isSelected) Color.Unspecified else Black.copy(alpha = 0.4f),
                                size = 28.dp,
                                modifier = Modifier.scale(iconScale)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = item.label,
                                color = if (isSelected) JungleGreen else Black.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                modifier = Modifier.alpha(textAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}
