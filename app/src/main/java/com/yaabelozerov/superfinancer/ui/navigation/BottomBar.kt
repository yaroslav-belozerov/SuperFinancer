package com.yaabelozerov.superfinancer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.FinanceScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SocialScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

private enum class BottomBarDestinations(
    val direction: Direction,
    val iconActive: ImageVector,
    val iconInactive: ImageVector,
    val restoreState: Boolean = true
) {
    Home(MainScreenDestination, Icons.Filled.Home, Icons.Outlined.Home), Finance(
        FinanceScreenDestination,
        Icons.Filled.AccountBalanceWallet,
        Icons.Outlined.AccountBalanceWallet
    ),
    Social(
        SocialScreenDestination(addToPostArticleUrl = null),
        Icons.Filled.People,
        Icons.Outlined.People,
        false
    ),
}

fun DestinationsNavigator.bottomNavigate(direction: Direction, restore: Boolean = true) = navigate(direction) {
    launchSingleTop = true
    restoreState = restore
    popUpTo(NavGraphs.root.startDestination) {
        saveState = true
    }
}

@Composable
fun BottomBar(navCtrl: NavHostController, onNavigate: (String) -> Unit) {
    val currentDestination =
        navCtrl.currentDestinationAsState().value ?: NavGraphs.root.startDestination

    BottomAppBar {
        BottomBarDestinations.entries.forEach { destination ->
            val selected = destination.direction.route.substringBefore('?') == currentDestination.baseRoute
            NavigationBarItem(selected = selected, onClick = {
                navCtrl.toDestinationsNavigator().bottomNavigate(destination.direction, destination.restoreState)
                onNavigate(destination.direction.route)
            }, icon = {
                Icon(
                    if (selected) destination.iconActive else destination.iconInactive,
                    contentDescription = null
                )
            })
        }
    }
}
