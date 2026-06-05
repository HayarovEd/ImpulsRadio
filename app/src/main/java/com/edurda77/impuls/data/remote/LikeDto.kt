package com.edurda77.impuls.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeDto(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id")
    val id: Int,
    @SerialName("song")
    val song: String,
    @SerialName("is_like")
    val isLike: Boolean
)