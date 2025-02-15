package com.yaabelozerov.superfinancer.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.dependency
import com.yaabelozerov.superfinancer.ui.navigation.BottomBar
import com.yaabelozerov.superfinancer.ui.navigation.FadeAnimation

@Composable
fun App() {
    val navCtrl = rememberNavController()
    val snackBarState = remember { SnackbarHostState() }
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomBar(navCtrl)
    }, snackbarHost = {
        SnackbarHost(hostState = snackBarState, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            Snackbar {
                Row {
                    Text(it.visuals.message)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { it.performAction() }) {
                        it.visuals.actionLabel?.let { Text(it) }
                    }
                }
            }
        }
    }) { innerPadding ->
        DestinationsNavHost(
            navController = navCtrl,
            navGraph = NavGraphs.root,
            defaultTransitions = FadeAnimation,
            dependenciesContainerBuilder = {
                dependency(snackBarState)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}