package com.example.myapplication.ui.provinces

import com.example.myapplication.domain.model.Province

data class ProvinceState (
    val provinces: List<Province> = emptyList(),
    val radioUrl: String = "",
    val radioName: String = "",
    val track: String = "",
)