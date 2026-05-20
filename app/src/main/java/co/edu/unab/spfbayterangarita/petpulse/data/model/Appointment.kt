package co.edu.unab.spfbayterangarita.petpulse.data.model

data class Appointment(
    val id: String = "",
    val petId: String = "",
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val place: String = "",
    val notes: String = "",
    val isReminderEnabled: Boolean = true,
    val scheduledAtMillis: Long = 0L
)
