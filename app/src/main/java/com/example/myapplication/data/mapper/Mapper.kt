package com.example.myapplication.data.mapper

import com.example.myapplication.domain.model.Province

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


private fun parseNameOfProvince(input: String): String {
    val parts = input.split(" ")
    return parts.dropLast(1).joinToString(" ")
}