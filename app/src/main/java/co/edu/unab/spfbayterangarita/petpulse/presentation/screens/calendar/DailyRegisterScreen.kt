package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.data.model.DailyLog
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.DailyLogViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.EmptyPetsState
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyRegisterScreen(
    petViewModel: PetViewModel,
    dailyLogViewModel: DailyLogViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.firstOrNull()

    if (selectedPet == null) {
        EmptyPetsState(message = "Primero agrega una mascota para registrar su estado diario.")
        return
    }

    val selectedMood = remember { mutableStateOf("Bien") }
    val selectedAppetite = remember { mutableStateOf("Completo") }
    val selectedActivity = remember { mutableStateOf("Moderado") }
    val hydration = remember { mutableFloatStateOf(0.6f) }
    val notes = remember { mutableStateOf("") }

    val selectedSymptoms = remember { mutableStateListOf<String>() }

    val moodOptions = listOf("Genial", "Bien", "Normal", "Triste", "Enfermo")
    val appetiteOptions = listOf("Completo", "Parcial", "No comió")
    val activityOptions = listOf("Sedentario", "Moderado", "Activo", "Muy activo")
    val symptomOptions = listOf(
        "Vómito",
        "Diarrea",
        "Tos",
        "Decaimiento",
        "Poco apetito",
        "Cojera",
        "Picazón",
        "Estornudos",
        "Dolor",
        "Sueño excesivo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Registro del día",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Mascota: ${selectedPet.name}",
            fontWeight = FontWeight.Medium
        )

        Text("¿Cómo se siente ${selectedPet.name}?")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            moodOptions.forEach { mood ->
                FilterChip(
                    selected = selectedMood.value == mood,
                    onClick = { selectedMood.value = mood },
                    label = { Text(mood) }
                )
            }
        }

        Text("Apetito")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            appetiteOptions.forEach { appetite ->
                FilterChip(
                    selected = selectedAppetite.value == appetite,
                    onClick = { selectedAppetite.value = appetite },
                    label = { Text(appetite) }
                )
            }
        }

        Text("Hidratación")
        Slider(
            value = hydration.floatValue,
            onValueChange = { hydration.floatValue = it }
        )

        Text("Actividad física")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activityOptions.forEach { activity ->
                FilterChip(
                    selected = selectedActivity.value == activity,
                    onClick = { selectedActivity.value = activity },
                    label = { Text(activity) }
                )
            }
        }

        Text("Síntomas observados")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            symptomOptions.forEach { symptom ->
                FilterChip(
                    selected = selectedSymptoms.contains(symptom),
                    onClick = {
                        if (selectedSymptoms.contains(symptom)) {
                            selectedSymptoms.remove(symptom)
                        } else {
                            selectedSymptoms.add(symptom)
                        }
                    },
                    label = { Text(symptom) }
                )
            }
        }

        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            label = { Text("Notas adicionales") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Button(
            onClick = {
                val newLog = DailyLog(
                    id = "log_${System.currentTimeMillis()}",
                    petId = selectedPet.id,
                    date = "Hoy",
                    mood = selectedMood.value,
                    appetite = selectedAppetite.value,
                    hydrationLevel = hydration.floatValue,
                    activity = selectedActivity.value,
                    symptoms = selectedSymptoms.toList(),
                    notes = notes.value,
                    createdAtMillis = System.currentTimeMillis()
                )

                dailyLogViewModel.saveDailyLog(newLog)
                onSaveClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Guardar registro")
        }
    }
}
