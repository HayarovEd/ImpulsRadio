package com.edurda77.impuls.ui.provinces

sealed class ProvinceEvent {
    class OnPlay(
        val name: String,
        val url: String,
        val provinceId: Int
    ):ProvinceEvent()
}