package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.ChatMessage
import co.edu.unab.spfbayterangarita.petpulse.data.model.DailyLog
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "1",
                message = "Hola, soy PetPulse AI. Cuéntame qué síntomas tiene tu mascota y revisaré sus registros recientes para darte una orientación preventiva.",
                isFromUser = false,
                time = "9:41 AM"
            )
        )
    )

    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(
        text: String,
        selectedPet: Pet,
        recentLogs: List<DailyLog>
    ) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            message = text,
            isFromUser = true,
            time = "Ahora"
        )

        val aiResponse = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            message = generatePreventiveResponse(
                userText = text,
                selectedPet = selectedPet,
                recentLogs = recentLogs
            ),
            isFromUser = false,
            time = "Ahora"
        )

        _messages.value = _messages.value + userMessage + aiResponse
    }

    private fun generatePreventiveResponse(
        userText: String,
        selectedPet: Pet,
        recentLogs: List<DailyLog>
    ): String {
        val text = userText.lowercase()

        val recentSymptoms = recentLogs
            .flatMap { it.symptoms }
            .distinct()

        val hasLowAppetite = recentLogs.any {
            it.appetite.lowercase().contains("parcial") ||
                    it.appetite.lowercase().contains("no com")
        }

        val hasLowActivity = recentLogs.any {
            it.activity.lowercase().contains("sedentario")
        }

        val hasSadMood = recentLogs.any {
            it.mood.lowercase().contains("triste") ||
                    it.mood.lowercase().contains("enfermo")
        }

        return when {
            text.contains("vómito") || text.contains("vomito") || recentSymptoms.contains("vómito") -> {
                "${selectedPet.name} tiene registros que podrían indicar malestar digestivo. Si el vómito se repite, aparece diarrea, decaimiento fuerte o no quiere tomar agua, lo más prudente es contactar a un veterinario."
            }

            text.contains("no come") || text.contains("apetito") || hasLowAppetite -> {
                "En los registros recientes de ${selectedPet.name} aparece apetito bajo o parcial. Observa si también hay decaimiento, vómito, diarrea o cambios en el consumo de agua. Si la falta de apetito continúa, conviene consultar con un veterinario."
            }

            text.contains("triste") || text.contains("decaído") || text.contains("decaido") || hasSadMood || hasLowActivity -> {
                "Revisando los registros de ${selectedPet.name}, hay señales como bajo ánimo o poca actividad. Puede deberse a cansancio, cambios de rutina o malestar. Si notas dolor, fiebre, dificultad para caminar o empeora, busca orientación veterinaria."
            }

            recentSymptoms.isNotEmpty() -> {
                "En los últimos registros de ${selectedPet.name} aparecen estos síntomas: ${recentSymptoms.joinToString(", ")}. Te recomiendo seguir registrando su evolución y consultar si los síntomas aumentan o se mantienen."
            }

            else -> {
                "Por ahora no veo síntomas fuertes en los registros recientes de ${selectedPet.name}. Aun así, registra cualquier cambio en ánimo, apetito, hidratación o actividad para hacer seguimiento preventivo."
            }
        }
    }
}