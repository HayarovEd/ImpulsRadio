package com.edurda77.impuls.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edurda77.impuls.domain.utils.MAIN_SCREEN
import com.edurda77.impuls.domain.utils.PROVINCE_SCREEN
import com.edurda77.impuls.domain.utils.RADIOS_SCREEN
import com.edurda77.impuls.ui.main.MainScreen
import com.edurda77.impuls.ui.provinces.ProvincesScreen
import com.edurda77.impuls.ui.radios.RadiosScreen

@Composable
fun NavController(
    startDestination: String = MAIN_SCREEN,
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavigationDestination.MainScreenDestination.destination) {
            MainScreen(
                onNavigateToProvince = {
                    navController.navigate(PROVINCE_SCREEN)
                }
            )
        }
        composable(NavigationDestination.ProvincesScreenDestination.destination) {
            ProvincesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToRadioScreen = { id, name ->
                    navController.navigate("$RADIOS_SCREEN/${id}/${name}")
                }
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
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToMainScreen = {
                    navController.navigate(MAIN_SCREEN)
                }
            )
        }
    }
}