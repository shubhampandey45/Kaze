package com.sp45.kaze.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.sp45.kaze.ui.screens.MainScreen
import com.sp45.kaze.ui.theme.KazeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WindowCompat.setDecorFitsSystemWindows(window, false)

            KazeTheme {
                Box(Modifier.fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)){
                    MainScreen()
                }
            }
        }
    }
}

