package com.example.myapplication.ui.provinces

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.domain.utils.RADIOS_SCREEN
import com.example.myapplication.ui.theme.blue34
import com.example.myapplication.ui.theme.white
import com.example.myapplication.ui.uikit.ItemElement
import com.example.myapplication.ui.uikit.SquareBarVisualizerRelease

@Composable
fun ProvincesScreen(
    modifier: Modifier = Modifier,
    viewModel: ProvinesViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (state.value.sessionId!=0) {
                SquareBarVisualizerRelease(
                    audioSessionId = state.value.sessionId
                )
            }
        }
    ) { paddings ->
        Column(
            modifier = modifier
                .padding(paddings)
                .fillMaxSize()
                .background(color = blue34)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = modifier.height(10.dp))
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "${stringResource(id = R.string.now_is_played)}\n${state.value.radioName}\n${state.value.track}",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight(600),
                    textAlign = TextAlign.Center,
                    color = white
                )
            )
            Spacer(modifier = modifier.height(10.dp))
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(state.value.provinces) {
                    ItemElement(
                        name = it.name,
                        isCenter = true,
                        onClick = {
                            navController.navigate("$RADIOS_SCREEN/${it.id}/${it.name}")
                        })
                }
            }
        }
    }
}