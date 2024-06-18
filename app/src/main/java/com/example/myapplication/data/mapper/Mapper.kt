package com.example.myapplication.data.mapper

import com.example.myapplication.domain.model.Province

fun String.convertToCategories(): List<Province> {
    return this.split(" ")
        .mapNotNull { part ->
            val (id, name) = part.split("|")
            if (id.all { it.isDigit() }) {
                Province(
                    id = id.toInt(),
                    name = name
                )
            } else {
                null
            }
        }
}