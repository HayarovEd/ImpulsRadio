package com.example.myapplication.ui.radios

import com.example.myapplication.domain.model.RadioStation

data class RadiosState(
    val radios: List<RadioStation> = emptyList(),
    val isLoading: Boolean = true,
    val nameOfProvince: String = "",
)
