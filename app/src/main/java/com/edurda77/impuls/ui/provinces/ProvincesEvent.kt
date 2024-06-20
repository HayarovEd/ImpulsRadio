package com.edurda77.impuls.ui.provinces

sealed class ProvincesEvent {
    class OnPlay(
        val name:String,
        val url:String):ProvincesEvent()
}