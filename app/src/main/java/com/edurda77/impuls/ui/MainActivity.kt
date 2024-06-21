package com.edurda77.impuls.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edurda77.impuls.ui.navigation.NavController
import com.edurda77.impuls.ui.theme.MyApplicationTheme
import com.edurda77.impuls.ui.uikit.AccessScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 123)
        val permissions = if (
            Build.VERSION.SDK_INT >= 33
        ) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS)
        } else  {
            listOf(
                Manifest.permission.RECORD_AUDIO)
        }
        setContent {
            val cameraPermissionState = rememberMultiplePermissionsState(
               permissions
            )

            if (cameraPermissionState.allPermissionsGranted) {
                MyApplicationTheme {
                    NavController()
                }
            } else {
                AccessScreen(
                    cameraPermissionState = cameraPermissionState
                )
            }
        }
    }
}
