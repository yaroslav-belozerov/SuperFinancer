package com.yaabelozerov.superfinancer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yaabelozerov.superfinancer.ui.App
import com.yaabelozerov.superfinancer.ui.theme.SuperFinancerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperFinancerTheme {
                App()
            }
        }
    }
}