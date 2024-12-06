package com.edurda77.impuls.ui.provinces

import com.edurda77.impuls.domain.model.UiProvince

sealed class ProvinceEvent {
    class OnPlay(
        val name: String,
        val url: String,
        val provinceId: Int
    ):ProvinceEvent()
    data object OnRefresh: ProvinceEvent()
    class UpdateExpandedProvince(val province: UiProvince): ProvinceEvent()
}