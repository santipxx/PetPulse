package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.EmptyPetsState
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AppointmentViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.util.combineDateAndTimeMillis
import co.edu.unab.spfbayterangarita.petpulse.util.formatAppointmentDate
import co.edu.unab.spfbayterangarita.petpulse.util.formatAppointmentTime
import co.edu.unab.spfbayterangarita.petpulse.util.todayStartMillis
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentScreen(
    petViewModel: PetViewModel,
    appointmentViewModel: AppointmentViewModel,
    initialDateMillis: Long,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.firstOrNull()

    if (selectedPet == null) {
        EmptyPetsState(message = "Primero agrega una mascota para crear eventos.")
        return
    }

    val initialTime = remember { LocalTime.now().plusHours(1) }
    val title = remember { mutableStateOf("") }
    val selectedDateMillis = remember {
        mutableLongStateOf(if (initialDateMillis > 0L) initialDateMillis else todayStartMillis())
    }
    val selectedHour = remember { mutableIntStateOf(initialTime.hour) }
    val selectedMinute = remember { mutableIntStateOf(initialTime.minute) }
    val place = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val selectedType = remember { mutableStateOf("Cita") }
    val isReminderEnabled = remember { mutableStateOf(true) }
    val showDateDialog = remember { mutableStateOf(false) }
    val showTimeDialog = remember { mutableStateOf(false) }

    val types = listOf("Cita", "Vacuna", "Desparasitación", "Baño", "Control")

    if (showDateDialog.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis.longValue
        )

        DatePickerDialog(
            onDismissRequest = { showDateDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis.longValue = it
                        }
                        showDateDialog.value = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimeDialog.value) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour.intValue,
            initialMinute = selectedMinute.intValue,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimeDialog.value = false },
            title = { Text("Selecciona la hora") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour.intValue = timePickerState.hour
                        selectedMinute.intValue = timePickerState.minute
                        showTimeDialog.value = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = PetGreen
                )
            }

            Text(
                text = "Nuevo evento",
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Mascota: ${selectedPet.name}",
            fontWeight = FontWeight.Medium
        )

        Text("Tipo de evento")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                FilterChip(
                    selected = selectedType.value == type,
                    onClick = { selectedType.value = type },
                    label = { Text(type) }
                )
            }
        }

        OutlinedTextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Título del evento") },
            placeholder = { Text("Ej: Vacuna rabia") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { showDateDialog.value = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(formatAppointmentDate(selectedDateMillis.longValue))
            }

            OutlinedButton(
                onClick = { showTimeDialog.value = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(formatAppointmentTime(selectedHour.intValue, selectedMinute.intValue))
            }
        }

        OutlinedTextField(
            value = place.value,
            onValueChange = { place.value = it },
            label = { Text("Lugar") },
            placeholder = { Text("Ej: Clínica Vet Norte") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            label = { Text("Notas") },
            placeholder = { Text("Ej: llevar carné de vacunas") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recordarme antes del evento")
            Switch(
                checked = isReminderEnabled.value,
                onCheckedChange = { isReminderEnabled.value = it }
            )
        }

        Button(
            onClick = {
                val scheduledAtMillis = combineDateAndTimeMillis(
                    dateMillis = selectedDateMillis.longValue,
                    hour = selectedHour.intValue,
                    minute = selectedMinute.intValue
                )

                val newAppointment = Appointment(
                    id = "appointment_${System.currentTimeMillis()}",
                    petId = selectedPet.id,
                    title = title.value.ifBlank { selectedType.value },
                    type = selectedType.value,
                    date = formatAppointmentDate(scheduledAtMillis),
                    time = formatAppointmentTime(selectedHour.intValue, selectedMinute.intValue),
                    place = place.value.ifBlank { "Sin lugar" },
                    notes = notes.value,
                    isReminderEnabled = isReminderEnabled.value,
                    scheduledAtMillis = scheduledAtMillis
                )

                appointmentViewModel.addAppointment(newAppointment)
                onSaveClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Guardar evento")
        }
    }
}
