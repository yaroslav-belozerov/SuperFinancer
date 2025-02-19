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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.Direction
import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.ui.navigation.BottomBar
import com.yaabelozerov.superfinancer.ui.navigation.Fade
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun App() {
    val navCtrl = rememberNavController()
    val snackBarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var startRoute: String? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        startRoute =
            CommonModule.dataStoreManager.getValue(DataStoreManager.Keys.Strings.LAST_ROUTE).first()
                ?: MainScreenDestination.route
    }
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomBar(navCtrl) {
            scope.launch {
                CommonModule.dataStoreManager.setValue(
                    DataStoreManager.Keys.Strings.LAST_ROUTE, it
                )
            }
        }
    }, snackbarHost = {
        SnackbarHost(
            hostState = snackBarState,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
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
        startRoute?.let { start ->
            DestinationsNavHost(
                navController = navCtrl,
                navGraph = NavGraphs.root,
                defaultTransitions = Fade,
                dependenciesContainerBuilder = {
                    dependency(snackBarState)
                },
                start = Direction(start),
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}