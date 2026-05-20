package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.ChatMessage
import co.edu.unab.spfbayterangarita.petpulse.data.model.DailyLog
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

enum class ChatAnalysisType(
    val label: String,
    val prompt: String
) {
    General(
        label = "Resumen",
        prompt = "Dame un resumen de salud"
    ),
    Symptoms(
        label = "Síntomas",
        prompt = "Analiza los síntomas recientes"
    ),
    AppetiteMood(
        label = "Apetito y ánimo",
        prompt = "Analiza apetito y ánimo"
    ),
    HydrationActivity(
        label = "Hidratación y actividad",
        prompt = "Analiza hidratación y actividad"
    ),
    AlertSigns(
        label = "Señales de alerta",
        prompt = "Busca señales de alerta"
    )
}

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "1",
                message = "Hola, soy PetPulse AI. Elige una opción o escríbeme qué quieres revisar y analizaré los registros recientes de tu mascota.",
                isFromUser = false,
                time = "Ahora"
            )
        )
    )

    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(
        text: String,
        selectedPet: Pet,
        recentLogs: List<DailyLog>,
        rangeDays: Int = DEFAULT_RANGE_DAYS
    ) {
        if (text.isBlank()) return

        val analysisType = inferAnalysisType(text)
        addConversationTurn(
            userText = text,
            selectedPet = selectedPet,
            recentLogs = recentLogs,
            rangeDays = rangeDays,
            analysisType = analysisType
        )
    }

    fun requestGuidedAnalysis(
        analysisType: ChatAnalysisType,
        selectedPet: Pet,
        recentLogs: List<DailyLog>,
        rangeDays: Int
    ) {
        addConversationTurn(
            userText = "${analysisType.prompt} en los últimos $rangeDays días",
            selectedPet = selectedPet,
            recentLogs = recentLogs,
            rangeDays = rangeDays,
            analysisType = analysisType
        )
    }

    private fun addConversationTurn(
        userText: String,
        selectedPet: Pet,
        recentLogs: List<DailyLog>,
        rangeDays: Int,
        analysisType: ChatAnalysisType
    ) {
        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            message = userText,
            isFromUser = true,
            time = "Ahora"
        )

        val aiResponse = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            message = generateAnalysisResponse(
                selectedPet = selectedPet,
                logs = filterLogsByRange(recentLogs, rangeDays),
                rangeDays = rangeDays,
                analysisType = analysisType
            ),
            isFromUser = false,
            time = "Ahora"
        )

        _messages.value = _messages.value + userMessage + aiResponse
    }

    private fun generateAnalysisResponse(
        selectedPet: Pet,
        logs: List<DailyLog>,
        rangeDays: Int,
        analysisType: ChatAnalysisType
    ): String {
        if (logs.isEmpty()) {
            return "No encontré registros de ${selectedPet.name} en los últimos $rangeDays días. Registra ánimo, apetito, hidratación, actividad y síntomas durante varios días para poder detectar patrones.\n\n$VET_RECOMMENDATION"
        }

        val symptomCounts = logs
            .flatMap { it.symptoms }
            .filter { it.isNotBlank() }
            .groupingBy { it.lowercase(Locale.ROOT) }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }

        val lowAppetiteDays = logs.count {
            val appetite = it.appetite.lowercase(Locale.ROOT)
            appetite.contains("parcial") || appetite.contains("no com")
        }
        val sadMoodDays = logs.count {
            val mood = it.mood.lowercase(Locale.ROOT)
            mood.contains("triste") || mood.contains("enfermo")
        }
        val lowActivityDays = logs.count {
            it.activity.lowercase(Locale.ROOT).contains("sedentario")
        }
        val lowHydrationDays = logs.count { it.hydrationLevel < LOW_HYDRATION_THRESHOLD }
        val notesWithContent = logs.mapNotNull { log ->
            log.notes.takeIf { it.isNotBlank() }
        }.takeLast(3)

        val riskLevel = calculateRiskLevel(
            symptomCounts = symptomCounts,
            lowAppetiteDays = lowAppetiteDays,
            sadMoodDays = sadMoodDays,
            lowActivityDays = lowActivityDays,
            lowHydrationDays = lowHydrationDays
        )

        val header = "Analicé ${logs.size} registro(s) de ${selectedPet.name} en los últimos $rangeDays días."
        val riskSummary = "Nivel de atención sugerido: $riskLevel."

        val body = when (analysisType) {
            ChatAnalysisType.General -> buildGeneralAnalysis(
                symptomCounts,
                lowAppetiteDays,
                sadMoodDays,
                lowActivityDays,
                lowHydrationDays,
                notesWithContent
            )

            ChatAnalysisType.Symptoms -> buildSymptomAnalysis(symptomCounts)

            ChatAnalysisType.AppetiteMood -> buildAppetiteMoodAnalysis(
                lowAppetiteDays,
                sadMoodDays,
                logs.size
            )

            ChatAnalysisType.HydrationActivity -> buildHydrationActivityAnalysis(
                lowHydrationDays,
                lowActivityDays,
                logs.size
            )

            ChatAnalysisType.AlertSigns -> buildAlertAnalysis(
                symptomCounts,
                lowAppetiteDays,
                sadMoodDays,
                lowActivityDays,
                lowHydrationDays
            )
        }

        return "$header\n\n$riskSummary\n\n$body\n\n$VET_RECOMMENDATION"
    }

    private fun buildGeneralAnalysis(
        symptomCounts: List<Pair<String, Int>>,
        lowAppetiteDays: Int,
        sadMoodDays: Int,
        lowActivityDays: Int,
        lowHydrationDays: Int,
        notes: List<String>
    ): String {
        val mainSymptoms = symptomCounts.take(4).joinToString(", ") { "${it.first} (${it.second})" }
            .ifBlank { "no hay síntomas repetidos registrados" }
        val recentNotes = notes.joinToString(" | ").ifBlank { "sin notas relevantes" }

        return "Patrón principal: $mainSymptoms.\nDías con apetito bajo: $lowAppetiteDays. Días con ánimo bajo: $sadMoodDays. Días con baja actividad: $lowActivityDays. Días con hidratación baja: $lowHydrationDays.\nNotas recientes: $recentNotes."
    }

    private fun buildSymptomAnalysis(symptomCounts: List<Pair<String, Int>>): String {
        if (symptomCounts.isEmpty()) {
            return "No aparecen síntomas marcados en el rango elegido. Aun así, observa cambios en apetito, ánimo, hidratación o actividad."
        }

        val repeatedSymptoms = symptomCounts.take(5).joinToString(", ") { "${it.first} (${it.second} vez/veces)" }
        val strongestSymptom = symptomCounts.first()

        return "Síntomas detectados: $repeatedSymptoms.\nEl síntoma más repetido fue ${strongestSymptom.first}. Si aparece junto con decaimiento, poco apetito, vómito, diarrea, dolor o dificultad para respirar, aumenta la prioridad de consulta."
    }

    private fun buildAppetiteMoodAnalysis(
        lowAppetiteDays: Int,
        sadMoodDays: Int,
        totalDays: Int
    ): String {
        return "Apetito bajo o parcial en $lowAppetiteDays de $totalDays registro(s). Ánimo triste/enfermo en $sadMoodDays de $totalDays registro(s).\nSi ambos aparecen en los mismos días, puede indicar malestar sostenido y conviene vigilar si come, toma agua y responde normalmente."
    }

    private fun buildHydrationActivityAnalysis(
        lowHydrationDays: Int,
        lowActivityDays: Int,
        totalDays: Int
    ): String {
        return "Hidratación baja en $lowHydrationDays de $totalDays registro(s). Actividad sedentaria en $lowActivityDays de $totalDays registro(s).\nSi la baja hidratación se combina con vómito, diarrea, fiebre, encías secas o letargo, puede ser más delicado."
    }

    private fun buildAlertAnalysis(
        symptomCounts: List<Pair<String, Int>>,
        lowAppetiteDays: Int,
        sadMoodDays: Int,
        lowActivityDays: Int,
        lowHydrationDays: Int
    ): String {
        val alertSymptoms = symptomCounts
            .filter { (symptom, _) ->
                ALERT_SYMPTOMS.any { symptom.contains(it) }
            }
            .joinToString(", ") { "${it.first} (${it.second})" }
            .ifBlank { "no hay síntomas críticos marcados repetidamente" }

        return "Señales a vigilar: $alertSymptoms.\nFactores asociados: apetito bajo $lowAppetiteDays día(s), ánimo bajo $sadMoodDays día(s), actividad baja $lowActivityDays día(s), hidratación baja $lowHydrationDays día(s).\nSi hay dificultad para respirar, dolor intenso, desmayo, sangre, convulsiones, vómitos persistentes o no bebe agua, busca atención veterinaria cuanto antes."
    }

    private fun calculateRiskLevel(
        symptomCounts: List<Pair<String, Int>>,
        lowAppetiteDays: Int,
        sadMoodDays: Int,
        lowActivityDays: Int,
        lowHydrationDays: Int
    ): String {
        val repeatedAlertSymptom = symptomCounts.any { (symptom, count) ->
            count >= 2 && ALERT_SYMPTOMS.any { symptom.contains(it) }
        }
        val concernScore = lowAppetiteDays + sadMoodDays + lowActivityDays + lowHydrationDays

        return when {
            repeatedAlertSymptom || concernScore >= 5 -> "alto"
            concernScore >= 2 || symptomCounts.isNotEmpty() -> "moderado"
            else -> "bajo"
        }
    }

    private fun filterLogsByRange(
        logs: List<DailyLog>,
        rangeDays: Int
    ): List<DailyLog> {
        val cutoffMillis = System.currentTimeMillis() - rangeDays * ONE_DAY_MILLIS

        return logs.filter { log ->
            log.createdAtMillis == 0L || log.createdAtMillis >= cutoffMillis
        }
    }

    private fun inferAnalysisType(text: String): ChatAnalysisType {
        val normalized = text.lowercase(Locale.ROOT)

        return when {
            normalized.contains("apetito") || normalized.contains("come") ||
                normalized.contains("ánimo") || normalized.contains("animo") ||
                normalized.contains("triste") -> ChatAnalysisType.AppetiteMood

            normalized.contains("agua") || normalized.contains("hidrat") ||
                normalized.contains("actividad") || normalized.contains("camina") -> ChatAnalysisType.HydrationActivity

            normalized.contains("alerta") || normalized.contains("grave") ||
                normalized.contains("urgente") || normalized.contains("empeora") -> ChatAnalysisType.AlertSigns

            normalized.contains("síntoma") || normalized.contains("sintoma") ||
                normalized.contains("vómito") || normalized.contains("vomito") ||
                normalized.contains("diarrea") || normalized.contains("tos") -> ChatAnalysisType.Symptoms

            else -> ChatAnalysisType.General
        }
    }

    private companion object {
        const val DEFAULT_RANGE_DAYS = 15
        const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
        const val LOW_HYDRATION_THRESHOLD = 0.4f
        const val VET_RECOMMENDATION =
            "Recomendación: si la mascota sigue empeorando, los síntomas se repiten o notas algo fuera de lo normal, llévala a un veterinario."

        val ALERT_SYMPTOMS = listOf(
            "vómito",
            "vomito",
            "diarrea",
            "dolor",
            "decaimiento",
            "cojera",
            "tos",
            "sangre",
            "convuls",
            "respirar"
        )
    }
}
