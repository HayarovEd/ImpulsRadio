package com.edurda77.impuls.domain.model

import java.time.LocalDateTime


data class Song(
    val artist: String,
    val duration: String,
    val id: Int,
    val lastPlayed: LocalDateTime,
    val radioId: Int,
    val title: String
)
