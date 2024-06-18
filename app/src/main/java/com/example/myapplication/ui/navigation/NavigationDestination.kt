package com.example.myapplication.ui.navigation

import com.example.myapplication.domain.utils.PROVINCE_SCREEN
import com.example.myapplication.domain.utils.RADIOS_SCREEN

sealed class NavigationDestination(val destination: String) {
    data object ProvincesScreenDestination: NavigationDestination(PROVINCE_SCREEN)
    data object RadiosScreenDestination: NavigationDestination("$RADIOS_SCREEN/{id}/{name}")
}