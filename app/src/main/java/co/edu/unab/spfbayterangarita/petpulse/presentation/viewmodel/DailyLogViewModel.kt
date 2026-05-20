package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.DailyLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DailyLogViewModel : ViewModel() {

    private val _dailyLogs = MutableStateFlow(
        listOf(
            DailyLog(
                id = "log_1",
                petId = "pet_1",
                date = "15 jul",
                mood = "Bien",
                appetite = "Completo",
                hydrationLevel = 0.6f,
                activity = "Moderado",
                symptoms = listOf("leve cansancio"),
                notes = "Se comportó normal durante el día."
            ),
            DailyLog(
                id = "log_2",
                petId = "pet_1",
                date = "16 jul",
                mood = "Triste",
                appetite = "Parcial",
                hydrationLevel = 0.4f,
                activity = "Sedentario",
                symptoms = listOf("decaimiento", "poco apetito"),
                notes = "Durmió más de lo normal."
            ),
            DailyLog(
                id = "log_3",
                petId = "pet_2",
                date = "16 jul",
                mood = "Normal",
                appetite = "Completo",
                hydrationLevel = 0.7f,
                activity = "Activo",
                symptoms = emptyList(),
                notes = "Sin novedades."
            )
        )
    )

    val dailyLogs: StateFlow<List<DailyLog>> = _dailyLogs

    fun getLogsByPet(petId: String): List<DailyLog> {
        return _dailyLogs.value.filter { it.petId == petId }
    }

    fun getRecentSymptomsByPet(petId: String): List<String> {
        return _dailyLogs.value
            .filter { it.petId == petId }
            .flatMap { it.symptoms }
            .distinct()
    }

    fun saveDailyLog(log: DailyLog) {
        _dailyLogs.value = _dailyLogs.value + log
    }
}