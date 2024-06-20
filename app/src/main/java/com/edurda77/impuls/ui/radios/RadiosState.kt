package com.edurda77.impuls.ui.radios

import com.edurda77.impuls.domain.model.RadioStation

data class RadiosState(
    val radios: List<RadioStation> = emptyList(),
    val isLoading: Boolean = true,
    val nameOfProvince: String = "",
)
