package com.example.myapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.ui.navigation.NavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NavController()
            }
        }
    }
}
