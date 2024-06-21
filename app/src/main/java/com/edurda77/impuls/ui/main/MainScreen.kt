package com.edurda77.impuls.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.edurda77.impuls.R
import com.edurda77.impuls.domain.utils.PROVINCE_SCREEN
import com.edurda77.impuls.ui.theme.blue34
import com.edurda77.impuls.ui.theme.blue53
import com.edurda77.impuls.ui.theme.white
import com.edurda77.impuls.ui.uikit.ItemElement
import com.edurda77.impuls.ui.uikit.SquareBarVisualizerRelease

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (state.value.sessionId != 0) {
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
                modifier = modifier
                    .fillMaxWidth(),
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = modifier.height(10.dp))
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "${stringResource(id = R.string.now_is_played)} ${state.value.radioName}\n${state.value.track}",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    textAlign = TextAlign.Center,
                    color = white
                )
            )
            Spacer(modifier = modifier.height(10.dp))
            if(state.value.isShowButton) {
                IconButton(
                    modifier = modifier.size(100.dp),
                    onClick = {
                        if (state.value.isPlayed) {
                            onEvent(
                                MainEvent.OnStop
                            )
                        } else {
                            onEvent(
                                MainEvent.OnPlay(
                                    name = state.value.lastRadio.last().name,
                                    url = state.value.lastRadio.last().url,
                                )
                            )
                        }
                    }) {
                    Icon(
                        modifier = modifier.size(100.dp),
                        imageVector = if (state.value.isPlayed) ImageVector.vectorResource(id = R.drawable.baseline_stop_circle) else ImageVector.vectorResource(
                            id = R.drawable.baseline_play_circle_outline
                        ),
                        contentDescription = "",
                        tint = white
                    )

                }
                Spacer(modifier = modifier.height(10.dp))
            }
            Button(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue53
                ),
                onClick = { navController.navigate(PROVINCE_SCREEN) }) {
                Text(
                    text = stringResource(R.string.choise_radio),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = white
                    )
                )
            }
            Spacer(modifier = modifier.height(10.dp))
            Text(
                modifier = modifier.fillMaxWidth(),
                text = stringResource(R.string.last_radios),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    textAlign = TextAlign.Center,
                    color = white
                )
            )
            Spacer(modifier = modifier.height(5.dp))
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(state.value.lastRadio) {
                    ItemElement(
                        name = it.name,
                        isCenter = false,
                        onClick = {
                            onEvent(
                                MainEvent.OnPlay(
                                    name = it.name,
                                    url = it.url
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}