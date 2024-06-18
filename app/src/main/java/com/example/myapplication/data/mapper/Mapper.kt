package com.example.myapplication.data.mapper

import com.example.myapplication.domain.model.Province
import com.example.myapplication.domain.model.RadioStation

fun String.convertToCategories(): List<Province> {
    val elements = this.split("|")
    val provinces = mutableListOf<Province>()
    for (i in 0..elements.size - 2) {
        if (elements[i] == elements.first()) {
            val name = parseNameOfProvince(elements[i + 1])
            provinces.add(
                Province(
                    id = elements[i].toInt(),
                    name = name
                )
            )
        } else if (elements[i] == elements[elements.size-2]) {
            val partsNext = elements[i].split(" ")
            val id = partsNext.last().toInt()
            provinces.add(
                Province(
                    id = id,
                    name = elements[i + 1]
                )
            )
        } else {
            val name = parseNameOfProvince(elements[i + 1])
            val partsNext = elements[i].split(" ")
            val id = partsNext.last().toInt()
            provinces.add(
                Province(
                    id = id,
                    name = name
                )
            )
        }
    }
    return provinces
}

fun String.convertToRadios(): List<RadioStation> {
    val elements = this.split("|")
    val provinces = mutableListOf<RadioStation>()
    for (i in 0..elements.size - 2) {
        if (elements[i] == elements.first()) {
            val name = elements[i]
            val partsNext = elements[i+1].split(" ")
            val url = partsNext.first()
            provinces.add(
                RadioStation(
                    name = name,
                    url = url
                )
            )
        } else if (elements[i] == elements[elements.size-2]) {
            val parts = elements[i].split(" ")
            val name = parts.drop(1).joinToString(" ")
            provinces.add(
                RadioStation(
                    name = name,
                    url = elements[i+1]
                )
            )
        } else {
            val parts = elements[i].split(" ")
            val name = parts.drop(1).joinToString(" ")
            val partsNext = elements[i+1].split(" ")
            val url = partsNext.first()
            provinces.add(
                RadioStation(
                    name = name,
                    url = url
                )
            )
        }
    }
    return provinces
}


private fun parseNameOfProvince(input: String): String {
    val parts = input.split(" ")
    return parts.dropLast(1).joinToString(" ")
}