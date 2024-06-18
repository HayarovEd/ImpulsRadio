package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.provinces.ProvincesScreen
import com.example.myapplication.ui.provinces.ProvinesViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProvincesScreen()
                // val viewModel: ProvinesViewModel = hiltViewModel()
                // val state = viewModel.state.collectAsState()
                //Log.d("TEST REMOTE DATA", "provinces ${state.value.provinces}")
            }
        }
    }
}
