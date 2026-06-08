package com.edurda77.impuls.ui.main

import com.edurda77.impuls.domain.model.RadioStation


sealed class MainAction {
    class OnPlay(
        val radioStation: RadioStation
    ):MainAction()
    data object OnStop : MainAction()
    class SetLike(val isLike: Boolean) : MainAction()
}