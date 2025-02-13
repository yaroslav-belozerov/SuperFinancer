package com.yaabelozerov.superfinancer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
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
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

private enum class BottomBarDestinations(
    val direction: DirectionDestinationSpec,
    val iconActive: ImageVector,
    val iconInactive: ImageVector,
) {
    Home(MainScreenDestination, Icons.Filled.Home, Icons.Outlined.Home),
    Finance(FinanceScreenDestination, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    Social(SocialScreenDestination, Icons.Filled.People, Icons.Outlined.People),
}

@Composable
fun BottomBar(navCtrl: NavHostController) {
    val currentDestination =
        navCtrl.currentDestinationAsState().value ?: NavGraphs.root.startDestination

    BottomAppBar {
        BottomBarDestinations.entries.forEach { destination ->
            val selected = destination.direction == currentDestination
            NavigationBarItem(selected = selected, onClick = {
                navCtrl.toDestinationsNavigator()
                    .navigate(destination.direction) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(NavGraphs.root.startDestination) {
                            saveState = true
                        }
                    }
            }, icon = {
                Icon(
                    if (selected) destination.iconActive else destination.iconInactive,
                    contentDescription = null
                )
            })
        }
    }
}
