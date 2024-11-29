package com.edurda77.impuls.ui.provinces

import com.edurda77.impuls.domain.model.UiProvince
import com.edurda77.impuls.ui.uikit.UiText

data class ProvinceState (
    val provinces: List<UiProvince> = emptyList(),
    val isEnableInternet:Boolean = true,
    val message : UiText? = null,
    val isLoading: Boolean = false,
)