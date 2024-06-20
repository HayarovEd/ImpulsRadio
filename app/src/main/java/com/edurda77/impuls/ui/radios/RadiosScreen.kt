package com.edurda77.impuls.ui.radios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.edurda77.impuls.domain.utils.MAIN_SCREEN
import com.edurda77.impuls.ui.theme.blue34
import com.edurda77.impuls.ui.theme.blue53
import com.edurda77.impuls.ui.theme.white
import com.edurda77.impuls.ui.uikit.ItemElement

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiosScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: RadiosViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = modifier
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = blue53
                ),
                title = {
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        text = state.value.nameOfProvince,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(600),
                            textAlign = TextAlign.Center,
                            color = white
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                            tint = white
                        )
                    }
                }
            )
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
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(state.value.radios) {
                    ItemElement(
                        name = it.name,
                        isCenter = false,
                        onClick = {
                            onEvent(
                                RadiosEvent.OnPlay(
                                    name = it.name,
                                    url = it.url
                                )
                            )
                            navController.navigate(MAIN_SCREEN)
                        }
                    )
                }
            }
        }
    }
}