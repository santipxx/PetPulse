package co.edu.unab.spfbayterangarita.petpulse.presentation.navigation

sealed class Routes(val route: String) {
    data object Welcome : Routes("welcome")
    data object RegisterPet : Routes("register_pet")

    data object Home : Routes("home")
    data object Agenda : Routes("agenda")
    data object Medical : Routes("medical")
    data object Chat : Routes("chat")
    data object Profile : Routes("profile")

    data object Weather : Routes("weather")
    data object Notifications : Routes("notifications")
}