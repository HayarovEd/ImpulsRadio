package com.edurda77.impuls.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edurda77.impuls.domain.utils.PROVINCE_SCREEN
import com.edurda77.impuls.ui.provinces.ProvincesScreen
import com.edurda77.impuls.ui.radios.RadiosScreen

@Composable
fun NavController(
    startDestination: String = PROVINCE_SCREEN,
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavigationDestination.ProvincesScreenDestination.destination) {
            ProvincesScreen(
                navController = navController
            )
        }
        composable(
            NavigationDestination.RadiosScreenDestination.destination,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("name") {
                    type = NavType.StringType
                },
            )
        ) {
            RadiosScreen(
                navController = navController
            )
        }
    }
}