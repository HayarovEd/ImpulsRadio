package com.example.myapplication.ui.uikit

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.chibde.visualizer.SquareBarVisualizer
import com.example.myapplication.R

@SuppressLint("InflateParams")
@Composable
fun SquareBarVisualizerRelease(
    modifier: Modifier = Modifier,
    audioSessionId: Int,
) {
    val context = LocalContext.current
    val sw = SquareBarVisualizer(context)
    //val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    //val audioSessionId = audioManager.generateAudioSessionId()
    Log.d("TEST SESSION ID", "audioSessionId $audioSessionId")

    sw.setColor(R.color.purple_200)
    sw.setDensity(100f)
    sw.setGap(2)

    //sw.setPlayer(audioSessionId)
    val view = LayoutInflater.from(context).inflate(R.layout.bar_visualizer, null, false)
    val sd = view.findViewById<SquareBarVisualizer>(R.id.visualizer)
    sd.setColor(R.color.purple_200)
    sd.setDensity(100f)
    sd.setGap(2)
    sd.setPlayer(audioSessionId)
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        factory = {
            view
        })
}