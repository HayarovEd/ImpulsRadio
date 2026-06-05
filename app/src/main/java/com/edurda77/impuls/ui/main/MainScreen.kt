package com.edurda77.impuls.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edurda77.impuls.R
import com.edurda77.impuls.domain.utils.READ_ERROR_TRACK
import com.edurda77.impuls.ui.theme.Pink40
import com.edurda77.impuls.ui.theme.blue34
import com.edurda77.impuls.ui.theme.blue53
import com.edurda77.impuls.ui.theme.white
import com.edurda77.impuls.ui.uikit.ItemElement
import com.edurda77.impuls.ui.uikit.SquareBarVisualizerRelease

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToProvince: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MainScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToProvince = onNavigateToProvince
    )
}


@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    onEvent: (MainEvent) -> Unit,
    onNavigateToProvince: () -> Unit,
) {

    val snakeBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.message) {
        if (state.message != null) {
            snakeBarHostState.showSnackbar(
                message = state.message.asString(context),
                duration = SnackbarDuration.Short
            )
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = blue34,
        snackbarHost = { SnackbarHost(snakeBarHostState) },
        topBar = {
            if (!state.isEnableInternet) {
                Text(
                    modifier = modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .background(color = Pink40),
                    text = stringResource(R.string.not_internet),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        textAlign = TextAlign.Center,
                        color = white
                    )
                )
            }
        },
        bottomBar = {
            if (state.sessionId != 0 && state.isPlayed) {
                SquareBarVisualizerRelease(
                    modifier = Modifier.navigationBarsPadding(),
                    audioSessionId = state.sessionId
                )
            }
        }
    ) { paddings ->
        Column(
            modifier = modifier
                .padding(paddings)
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = modifier
                    .width(200.dp),
                painter = painterResource(id = R.drawable.logo_impuls_fm),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            if (state.isEnableInternet) {
                Spacer(modifier = modifier.height(10.dp))
                val trackName = if (!state.isPlayed) ""
                else if (state.track == READ_ERROR_TRACK) stringResource(id = R.string.error_read_track) else state.track
                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    text = "${stringResource(id = R.string.now_is_played)} ${state.radioName}\n$trackName",
                    maxLines = 2,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        textAlign = TextAlign.Center,
                        color = white
                    )
                )
            }
            Spacer(modifier = modifier.height(10.dp))
            if (state.isShowButton && state.isEnableInternet) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(
                        modifier = modifier,
                        enabled = state.isPlayed
                                && state.track.isNotBlank()
                                && !state.loadingLike,
                        onClick = {
                            onEvent(MainEvent.SetLike(false))
                        }
                    ) {
                        Icon(
                            modifier = modifier.rotate(180f),
                            imageVector = if (state.lastLike != null
                                && state.lastLike.song.isNotBlank()
                                && state.track == state.lastLike.song
                                && !state.isLiked
                            ) ImageVector.vectorResource(
                                R.drawable.like_icon_filled
                            ) else ImageVector.vectorResource(R.drawable.ic_like),
                            contentDescription = "",
                            tint = white
                        )
                    }
                    IconButton(
                        modifier = modifier.size(100.dp),
                        onClick = {
                            if (state.isPlayed) {
                                onEvent(
                                    MainEvent.OnStop
                                )
                            } else {
                                onEvent(
                                    MainEvent.OnPlay(
                                        name = state.lastRadio.last().name,
                                        url = state.lastRadio.last().url,
                                        provinceId = state.lastRadio.last().provinceId,
                                    )
                                )
                            }
                        }) {
                        Icon(
                            modifier = modifier.size(100.dp),
                            imageVector = if (state.isPlayed) ImageVector.vectorResource(id = R.drawable.stop_circle_48) else ImageVector.vectorResource(
                                id = R.drawable.play_circle_48
                            ),
                            contentDescription = "",
                            tint = white
                        )

                    }
                    IconButton(
                        modifier = modifier,
                        enabled = state.isPlayed
                                && state.track.isNotBlank()
                                && !state.loadingLike,
                        onClick = {
                            onEvent(MainEvent.SetLike(true))
                        }
                    ) {
                        Icon(
                            imageVector = if (state.lastLike != null
                                && state.lastLike.song.isNotBlank()
                                && state.track == state.lastLike.song
                                && state.isLiked
                            ) ImageVector.vectorResource(
                                R.drawable.like_icon_filled
                            ) else ImageVector.vectorResource(R.drawable.ic_like),
                            contentDescription = "",
                            tint = white
                        )
                    }
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
                onClick = onNavigateToProvince
            ) {
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
            if (state.isEnableInternet) {
                Spacer(modifier = modifier.height(5.dp))
                LazyColumn(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(state.lastRadio) {
                        ItemElement(
                            name = it.name,
                            onClick = {
                                onEvent(
                                    MainEvent.OnPlay(
                                        name = it.name,
                                        url = it.url,
                                        provinceId = it.provinceId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MainScreen(
        state = MainState(),
        onEvent = {},
        onNavigateToProvince = {}
    )
}

@Preview
@Composable
private fun Preview1() {
    MainScreen(
        state = MainState(
            isPlayed = true,
            isShowButton = true,
            isEnableInternet = true,
            radioName = "Impuls",
            track = "artist - song"
        ),
        onEvent = {},
        onNavigateToProvince = {}
    )
}