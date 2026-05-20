package co.edu.unab.spfbayterangarita.petpulse.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.BottomNavigationBar
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.NotificationPermissionEffect
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.auth.AuthScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar.AgendaScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.chat.ChatScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.home.HomeScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.medical.MedicalRecordScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.onboarding.WelcomeScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.pet.RegisterPetScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.profile.ProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar.NewAppointmentScreen
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AppointmentViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AuthViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.ChatViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.DailyLogViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar.DailyRegisterScreen

@Composable
fun PetPulseApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val petViewModel: PetViewModel = viewModel()
    val dailyLogViewModel: DailyLogViewModel = viewModel()
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val authState = authViewModel.uiState.collectAsState().value

    NotificationPermissionEffect(enabled = authState.session != null)

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
            startDestination = if (authState.session == null) {
                Routes.Auth.route
            } else {
                Routes.Home.route
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Auth.route) {
                AuthScreen(
                    authViewModel = authViewModel,
                    onAuthenticated = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.Welcome.route) {
                WelcomeScreen(
                    onStartClick = {
                        navController.navigate(Routes.RegisterPet.route)
                    }
                )
            }

            composable(Routes.RegisterPet.route) {
                RegisterPetScreen(
                    petViewModel = petViewModel,
                    onContinueClick = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.RegisterPet.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.Home.route) {
                HomeScreen(
                    petViewModel = petViewModel,
                    onAddPetClick = {
                        navController.navigate(Routes.RegisterPet.route)
                    }
                )
            }

            composable(Routes.Agenda.route) {
                AgendaScreen(
                    petViewModel = petViewModel,
                    dailyLogViewModel = dailyLogViewModel,
                    appointmentViewModel = appointmentViewModel,
                    onDailyRegisterClick = {
                        navController.navigate(Routes.DailyRegister.route)
                    },
                    onNewAppointmentClick = { dateMillis ->
                        navController.navigate(Routes.NewAppointment.createRoute(dateMillis))
                    }
                )
            }
            composable(Routes.DailyRegister.route) {
                DailyRegisterScreen(
                    petViewModel = petViewModel,
                    dailyLogViewModel = dailyLogViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.NewAppointment.route,
                arguments = listOf(
                    navArgument("dateMillis") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                NewAppointmentScreen(
                    petViewModel = petViewModel,
                    appointmentViewModel = appointmentViewModel,
                    initialDateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: -1L,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.Medical.route) {
                MedicalRecordScreen(
                    petViewModel = petViewModel,
                    dailyLogViewModel = dailyLogViewModel
                )
            }

            composable(Routes.Chat.route) {
                ChatScreen(
                    petViewModel = petViewModel,
                    dailyLogViewModel = dailyLogViewModel,
                    chatViewModel = chatViewModel
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    petViewModel = petViewModel,
                    authViewModel = authViewModel,
                    onAddPetClick = {
                        navController.navigate(Routes.RegisterPet.route)
                    },
                    onSignedOut = {
                        navController.navigate(Routes.Auth.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
