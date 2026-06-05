package com.edurda77.impuls.ui.main


sealed class MainEvent {
    class OnPlay(
        val name:String,
        val url:String,
        val provinceId: Int
    ):MainEvent()
    data object OnStop : MainEvent()
    class SetLike(val isLike: Boolean) : MainEvent()
}