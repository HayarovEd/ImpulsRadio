package com.example.myapplication.ui.radios

sealed class RadiosEvent {
    class OnPlay(
        val name:String,
        val url:String):RadiosEvent()
    data object OnStop : RadiosEvent()
}