package co.edu.unab.spfbayterangarita.petpulse.data.model

data class DailyLog(
    val id: String = "",
    val petId: String = "",
    val date: String = "",
    val mood: String = "",
    val appetite: String = "",
    val hydrationLevel: Float = 0.5f,
    val activity: String = "",
    val symptoms: List<String> = emptyList(),
    val notes: String = ""
)