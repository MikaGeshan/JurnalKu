package com.example.jurnalku.ui.components.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.size

@Composable
fun ComposableIcon(
    icon: AppIconClass,
    tint: Color,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    when (icon) {
        is AppIconClass.Vector -> {
            Icon(
                imageVector = icon.icon,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(size)
            )
        }

        is AppIconClass.Drawable -> {
            Icon(
                painter = painterResource(id = icon.resId),
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(size)
            )
        }
    }
}