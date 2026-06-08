package com.edurda77.impuls.data.mapper

import com.edurda77.impuls.data.local.ProvinceEntity
import com.edurda77.impuls.data.local.RadioEntity
import com.edurda77.impuls.data.local.RadioProvinceEntity
import com.edurda77.impuls.domain.model.Province
import com.edurda77.impuls.domain.model.RadioStation



fun List<RadioEntity>.convertToRadios(): List<RadioStation> {
    return this.map { radio ->
        RadioStation(
            name = radio.name,
            url = radio.url,
            provinceId = radio.provinceId,
            id = radio.id
        )
    }
}

fun List<RadioProvinceEntity>.radioProvinceEntityConvertToRadios(): List<RadioStation> {
    return this.map { radio ->
        RadioStation(
            name = radio.name,
            url = radio.url,
            provinceId = radio.provinceId,
            id = radio.id
        )
    }
}

fun List<ProvinceEntity>.convertToProvinces(): List<Province> {
    return this.map { provinceEntity ->
        Province(
            name = provinceEntity.name,
            id = provinceEntity.id,
        )
    }
}