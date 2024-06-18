package com.example.myapplication.domain.repository

interface RadioPlayerRepository {
    fun playRadio(radioUrl: String)
    fun stopRadio()
}