package com.edurda77.impuls.ui.navigation

import com.edurda77.impuls.domain.utils.MAIN_SCREEN
import com.edurda77.impuls.domain.utils.PROVINCE_SCREEN
import com.edurda77.impuls.domain.utils.RADIOS_SCREEN

sealed class NavigationDestination(val destination: String) {
    data object MainScreenDestination: NavigationDestination(MAIN_SCREEN)
    data object ProvincesScreenDestination: NavigationDestination(PROVINCE_SCREEN)
    data object RadiosScreenDestination: NavigationDestination("$RADIOS_SCREEN/{id}/{name}")

}