package com.edurda77.impuls.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeRequest(
    @SerialName("song")
    val song: String,
    @SerialName("is_like")
    val isLike: Boolean
)