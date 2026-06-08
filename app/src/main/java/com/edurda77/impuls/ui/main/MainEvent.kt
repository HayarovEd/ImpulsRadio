package com.edurda77.impuls.ui.main

import com.edurda77.impuls.domain.model.RadioStation


sealed class MainEvent {
    class OnPlay(
        val radioStation: RadioStation
    ):MainEvent()
    data object OnStop : MainEvent()
    class SetLike(val isLike: Boolean) : MainEvent()
}