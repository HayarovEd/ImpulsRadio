package com.edurda77.impuls.ui.provinces


sealed class ProvinceEvent {
    data object OnRefresh: ProvinceEvent()
}