package com.yaabelozerov.superfinancer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.superfinancer.ui.screens.BottomBarNav
import com.yaabelozerov.superfinancer.ui.screens.MainScreen

@Composable
fun App() {
    val navCtrl = rememberNavController()
    val backStackEntry by navCtrl.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.hierarchy
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomAppBar {
            BottomBarNav.show.forEach { dest ->
                val selected by remember(currentDestination) {
                    derivedStateOf {
                        currentDestination?.any { it.route == dest::class.qualifiedName } == true
                    }
                }
                NavigationBarItem(selected = selected, icon = {
                    Icon(
                        if (selected) dest.iconSet.active else dest.iconSet.inactive,
                        contentDescription = null
                    )
                }, onClick = {
                    navCtrl.navigate(dest) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navCtrl.graph.startDestinationId) { saveState = true }
                    }
                })
            }
        }
    }) { innerPadding ->
        NavHost(navController = navCtrl, startDestination = BottomBarNav.Main) {
            composable<BottomBarNav.Main> {
                MainScreen(innerPadding)
            }
            composable<BottomBarNav.Finance> { }
        }
    }
}