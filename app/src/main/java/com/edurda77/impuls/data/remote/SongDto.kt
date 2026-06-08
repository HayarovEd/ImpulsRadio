package com.edurda77.impuls.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongDto(
    @SerialName("artist")
    val artist: String,
    @SerialName("duration")
    val duration: String,
    @SerialName("id")
    val id: Int,
    @SerialName("last_played")
    val lastPlayed: String,
    @SerialName("radio_id")
    val radioId: Int,
    @SerialName("title")
    val title: String
)