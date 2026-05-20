package co.edu.unab.spfbayterangarita.petpulse.data.model

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val isFromUser: Boolean = false,
    val time: String = ""
)