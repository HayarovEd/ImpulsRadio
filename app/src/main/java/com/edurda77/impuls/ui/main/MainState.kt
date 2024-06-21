package com.edurda77.impuls.ui.main

import com.edurda77.impuls.domain.model.RadioStation


data class MainState (
    val lastRadio: List<RadioStation> = emptyList(),
    val radioUrl: String = "",
    val radioName: String = "",
    val sessionId: Int = 0,
    val track: String = "",
    val isPlayed: Boolean = false,
    val isShowButton: Boolean = false,
)