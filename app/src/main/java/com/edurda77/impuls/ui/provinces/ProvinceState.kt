package com.edurda77.impuls.ui.provinces

import com.edurda77.impuls.domain.model.Province

data class ProvinceState (
    val provinces: List<Province> = emptyList(),
    val radioUrl: String = "",
    val radioName: String = "",
    val sessionId: Int = 0,
    val track: String = "",
)