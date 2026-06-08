package com.edurda77.impuls.ui.radios

import com.edurda77.impuls.domain.model.RadioStation


sealed class RadiosEvent {
    class OnPlay(
        val radioStation: RadioStation
    ):RadiosEvent()
    data object OnStop : RadiosEvent()
    data object OnRefresh: RadiosEvent()
}