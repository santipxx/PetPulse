package co.edu.unab.spfbayterangarita.petpulse.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.BottomNavigationBar
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar.AgendaScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.chat.ChatScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.home.HomeScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.medical.MedicalRecordScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.onboarding.WelcomeScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.pet.RegisterPetScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.profile.ProfileScreen

@Composable
fun PetPulseApp() {
    val navController = rememberNavController()

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val showBottomBar = currentRoute in listOf(
        Routes.Home.route,
        Routes.Agenda.route,
        Routes.Medical.route,
        Routes.Chat.route,
        Routes.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Welcome.route) {
                WelcomeScreen(
                    onStartClick = {
                        navController.navigate(Routes.RegisterPet.route)
                    }
                )
            }

            composable(Routes.RegisterPet.route) {
                RegisterPetScreen(
                    onContinueClick = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Welcome.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Routes.Home.route) {
                HomeScreen()
            }

            composable(Routes.Agenda.route) {
                AgendaScreen()
            }

            composable(Routes.Medical.route) {
                MedicalRecordScreen()
            }

            composable(Routes.Chat.route) {
                ChatScreen()
            }

            composable(Routes.Profile.route) {
                ProfileScreen()
            }
        }
    }
}