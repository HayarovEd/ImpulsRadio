package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.state.MainViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val state = viewModel.state.collectAsState()
                Log.d("TEST REMOTE DATA", "provinces ${state.value.provinces}")
            }
        }
    }
}
