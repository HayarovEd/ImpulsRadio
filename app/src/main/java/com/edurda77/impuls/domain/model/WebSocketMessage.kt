package com.edurda77.impuls.domain.model

sealed interface WebSocketMessage {
    class SongAdd(val songs: List<Song>) : WebSocketMessage
}