package com.edurda77.impuls.ui.radios

import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.ui.uikit.UiText

data class RadiosState(
    val radios: List<RadioStation> = emptyList(),
    val isLoading: Boolean = false,
    val nameOfProvince: String = "",
    val message: UiText? = null,
    val isEnableInternet:Boolean = false,
    val id: Int = -1,
)
