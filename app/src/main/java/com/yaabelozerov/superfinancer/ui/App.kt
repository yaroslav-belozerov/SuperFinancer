package com.yaabelozerov.superfinancer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator
import com.yaabelozerov.superfinancer.ui.screens.MainScreen

@Composable
fun App() {
    val navCtrl = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomBar(navCtrl)
    }) { innerPadding ->
        DestinationsNavHost(
            navController = navCtrl,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private enum class BottomBarDestinations(
    val direction: DirectionDestinationSpec,
    val iconActive: ImageVector,
    val iconInactive: ImageVector,
) {
    Home(MainScreenDestination, Icons.Filled.Home, Icons.Outlined.Home)
}

@Composable
private fun BottomBar(navCtrl: NavHostController) {
    val currentDestination =
        navCtrl.currentDestinationAsState().value ?: NavGraphs.root.startDestination

    BottomAppBar {
        BottomBarDestinations.entries.forEach { destination ->
            val selected = destination.direction == currentDestination
            NavigationBarItem(selected = selected, onClick = {
                navCtrl.toDestinationsNavigator()
                    .navigate(destination.direction) { launchSingleTop = true }
            }, icon = {
                Icon(
                    if (selected) destination.iconActive else destination.iconInactive,
                    contentDescription = null
                )
            })
        }
    }
}