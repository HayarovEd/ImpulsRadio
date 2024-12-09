package com.edurda77.impuls.ui.radios


sealed class RadiosEvent {
    class OnPlay(
        val name: String,
        val url: String,
        val provinceId: Int
    ):RadiosEvent()
    data object OnStop : RadiosEvent()
    data object OnRefresh: RadiosEvent()
}