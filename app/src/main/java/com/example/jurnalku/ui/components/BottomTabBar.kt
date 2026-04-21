package com.example.jurnalku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jurnalku.ui.components.icon.AppIconClass
import com.example.jurnalku.ui.components.icon.ComposableIcon
import com.example.jurnalku.ui.theme.Black
import com.example.jurnalku.ui.theme.JungleGreen
import com.example.jurnalku.ui.theme.Grey
import com.example.jurnalku.ui.theme.SoftGreen

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit
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
            icon = {
                ComposableIcon(
                    icon = AppIconClass.Journal,
                    tint = Color.Unspecified,
                    size = 40.dp
                )
            }
        ),
        BottomNavItem(
            label = "Dateline",
            route = "dateline",
            icon = {
                ComposableIcon(
                    icon = AppIconClass.Calendar,
                    tint = Color.Unspecified,
                    size = 40.dp
                )
            }
        )
    )

    NavigationBar(
        containerColor = SoftGreen,
        modifier = Modifier.drawBehind() {
            val strokeWidth = 1.dp.toPx()
            drawLine(
                color = Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(item.route) },
                    icon = { item.icon() },
                    label = null,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JungleGreen,
                        unselectedIconColor = Grey,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}