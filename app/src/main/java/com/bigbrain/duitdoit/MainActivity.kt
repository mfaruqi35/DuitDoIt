package com.bigbrain.duitdoit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bigbrain.duitdoit.presentation.MainScreen
import com.bigbrain.duitdoit.ui.theme.DuitDoItTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DuitDoItTheme {
                MainScreen()
            }
        }
    }
}