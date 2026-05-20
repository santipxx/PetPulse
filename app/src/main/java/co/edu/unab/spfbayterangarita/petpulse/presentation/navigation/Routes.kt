package co.edu.unab.spfbayterangarita.petpulse.presentation.navigation

sealed class Routes(val route: String) {
    data object Auth : Routes("auth")
    data object Welcome : Routes("welcome")
    data object RegisterPet : Routes("register_pet")

    data object Home : Routes("home")
    data object Agenda : Routes("agenda")
    data object NewAppointment : Routes("new_appointment?dateMillis={dateMillis}") {
        fun createRoute(dateMillis: Long): String = "new_appointment?dateMillis=$dateMillis"
    }
    data object Medical : Routes("medical")
    data object Chat : Routes("chat")
    data object Profile : Routes("profile")

    data object Weather : Routes("weather")
    data object Notifications : Routes("notifications")
    data object DailyRegister : Routes("daily_register")
}
