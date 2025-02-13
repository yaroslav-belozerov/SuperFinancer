package com.yaabelozerov.superfinancer.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.yaabelozerov.superfinancer.ui.navigation.BottomBar
import com.yaabelozerov.superfinancer.ui.navigation.FadeAnimation

@Composable
fun App() {
    val navCtrl = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomBar(navCtrl)
    }) { innerPadding ->
        DestinationsNavHost(
            navController = navCtrl,
            navGraph = NavGraphs.root,
            defaultTransitions = FadeAnimation,
            modifier = Modifier.padding(innerPadding)
        )
    }
}