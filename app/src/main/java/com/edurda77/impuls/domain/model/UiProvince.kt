package com.edurda77.impuls.domain.model

data class UiProvince(
    val id:Int,
    val name:String,
    val radios: List<RadioStation>,
    val isExpandedRadios: Boolean = false
)
