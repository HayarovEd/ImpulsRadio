package com.example.myapplication.ui.radios

sealed class RadiosEvent {
    class OnPlay(val url:String):RadiosEvent()
    data object OnStop : RadiosEvent()
}