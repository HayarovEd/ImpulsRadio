package com.edurda77.impuls.ui.provinces

import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.ui.uikit.UiText

data class ProvinceState (
    val provinces: List<Province> = emptyList(),
    val isEnableInternet:Boolean = false,
    val message : UiText? = null,
    val isLoading: Boolean = false,
)