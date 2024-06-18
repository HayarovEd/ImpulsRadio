package com.example.myapplication.ui.state

import com.example.myapplication.domain.model.Province

data class MainState (
    val provinces: List<Province> = emptyList()
)