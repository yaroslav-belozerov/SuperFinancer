package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Navigation Destinations
@Serializable data object MainScreen
@Serializable data object FinanceScreen

@Serializable
sealed class BottomBarNav<T>(val iconSet: IconSet, val route: T) {
    @Serializable
    data object Main : BottomBarNav<MainScreen>(IconSet.HOME, MainScreen)

    @Serializable
    data object Finance : BottomBarNav<FinanceScreen>(IconSet.STAR, FinanceScreen)

    companion object {
        val show = listOf(Main, Finance)
    }
}

@Serializable
enum class IconSet(val inactive: ImageVector, val active: ImageVector) {
    HOME(Icons.Outlined.Home, Icons.Filled.Home),
    STAR(Icons.Outlined.Star, Icons.Filled.Star),
    LIST(Icons.Outlined.Menu, Icons.Filled.Menu)
}
