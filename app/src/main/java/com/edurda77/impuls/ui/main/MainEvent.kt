package com.edurda77.impuls.ui.main

sealed interface MainEvent {
    data class OnError(val message: String) : MainEvent
}