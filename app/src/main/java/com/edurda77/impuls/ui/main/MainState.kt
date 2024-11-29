package com.edurda77.impuls.ui.main

import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.ui.uikit.UiText


data class MainState (
    val lastRadio: List<RadioStation> = emptyList(),
    val message : UiText? = null,
    val radioUrl: String = "",
    val radioName: String = "",
    val sessionId: Int = 0,
    val track: String = "",
    val isPlayed: Boolean = false,
    val isShowButton: Boolean = false,
    val isEnableInternet:Boolean = true,
)