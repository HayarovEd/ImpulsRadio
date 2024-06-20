package com.edurda77.impuls.ui.main


sealed class MainEvent {
    class OnPlay(
        val name:String,
        val url:String):MainEvent()
    data object OnStop : MainEvent()
}