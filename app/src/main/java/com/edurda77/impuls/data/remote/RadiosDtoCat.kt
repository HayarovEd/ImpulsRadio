package com.edurda77.impuls.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadiosDtoCat(
    @SerialName("categoryId")
    val categoryId: Int,
    @SerialName("categoryName")
    val categoryName: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("id")
    val id: Int,
    @SerialName("link")
    val link: String,
    @SerialName("name")
    val name: String
)