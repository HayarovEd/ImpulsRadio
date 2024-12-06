package com.edurda77.impuls.ui.provinces

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edurda77.impuls.R
import com.edurda77.impuls.ui.theme.Pink40
import com.edurda77.impuls.ui.theme.blue34
import com.edurda77.impuls.ui.theme.white
import com.edurda77.impuls.ui.uikit.ItemElement


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProvincesScreen(
    modifier: Modifier = Modifier,
    viewModel: ProvincesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

    val snakeBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.value.message) {
        if (state.value.message != null) {
            snakeBarHostState.showSnackbar(
                message = state.value.message!!.asString(context),
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = blue34,
        snackbarHost = { SnackbarHost(snakeBarHostState) },
        topBar = {
            Column(
                modifier = modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
            ) {
                if (!state.value.isEnableInternet) {
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
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                ) {
                    IconButton(
                        modifier = modifier
                            .align(Alignment.CenterStart),
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                            tint = white
                        )
                    }
                    Text(
                        modifier = modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        text = stringResource(R.string.choise_radio),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(600),
                            textAlign = TextAlign.Center,
                            color = white
                        )
                    )
                }
            }
        }
    ) { paddings ->
        PullToRefreshBox(
            modifier = modifier.padding(paddings),
            isRefreshing = state.value.isLoading,
            onRefresh = { onEvent(ProvinceEvent.OnRefresh) },
            indicator = {
                if (state.value.isLoading) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = modifier
                                .size(100.dp),
                            color = white
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(15.dp),
            ) {
                if (!state.value.isLoading) {
                    LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        state.value.provinces.forEach { province ->
                            stickyHeader {
                                Column(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .background(color = white)
                                        .clickable {
                                            onEvent(ProvinceEvent.UpdateExpandedProvince(province))
                                        }
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        modifier = modifier.fillMaxWidth(),
                                        text = province.name,
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight(600),
                                            textAlign = TextAlign.Start,
                                            color = blue34
                                        )
                                    )
                                }
                            }
                            if (province.isExpandedRadios) {
                                items(province.radios) { radio ->
                                    ItemElement(
                                        name = radio.name,
                                        onClick = {
                                            if (state.value.isEnableInternet) {
                                                onEvent(
                                                    ProvinceEvent.OnPlay(
                                                        name = radio.name,
                                                        url = radio.url,
                                                        provinceId = radio.provinceId
                                                    )
                                                )
                                            }
                                            onNavigateBack()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}