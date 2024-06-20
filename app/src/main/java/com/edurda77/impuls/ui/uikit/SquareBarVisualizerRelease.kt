package com.edurda77.impuls.ui.uikit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.chibde.visualizer.SquareBarVisualizer
import com.edurda77.impuls.R
import com.edurda77.impuls.ui.theme.blue34

@SuppressLint("InflateParams")
@Composable
fun SquareBarVisualizerRelease(
    modifier: Modifier = Modifier,
    audioSessionId: Int,
) {
    val context = LocalContext.current
    val view = LayoutInflater.from(context).inflate(R.layout.bar_visualizer, null, false)
    val visualiser = view.findViewById<SquareBarVisualizer>(R.id.visualizer)
    //visualiser.setColor(R.color.teal_200)
    visualiser.setColor(ContextCompat.getColor(context, R.color.teal_200))
    visualiser.setDensity(100f)
    visualiser.setGap(2)
    visualiser.setPlayer(audioSessionId)
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(color = blue34),
        factory = {
            view
        })
}