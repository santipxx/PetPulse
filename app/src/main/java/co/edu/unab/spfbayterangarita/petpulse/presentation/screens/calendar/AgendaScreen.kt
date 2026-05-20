package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.EmptyPetsState
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.PetSelector
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AppointmentViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.DailyLogViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray
import co.edu.unab.spfbayterangarita.petpulse.util.formatAppointmentDate
import co.edu.unab.spfbayterangarita.petpulse.util.isSameLocalDate
import co.edu.unab.spfbayterangarita.petpulse.util.todayStartMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    petViewModel: PetViewModel,
    dailyLogViewModel: DailyLogViewModel,
    appointmentViewModel: AppointmentViewModel,
    onDailyRegisterClick: () -> Unit,
    onNewAppointmentClick: (Long) -> Unit
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.firstOrNull()

    if (selectedPet == null) {
        EmptyPetsState(message = "Agrega una mascota para programar vacunas, citas y cuidados.")
        return
    }

    val selectedDateMillis = remember { mutableLongStateOf(todayStartMillis()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis.longValue
    )

    datePickerState.selectedDateMillis?.let { selectedDateMillis.longValue = it }

    val logs = dailyLogViewModel.dailyLogs.collectAsState().value
    val selectedPetLogs = logs.filter { it.petId == selectedPet.id }

    val appointments = appointmentViewModel.appointments.collectAsState().value
    val selectedPetAppointments = appointments
        .filter { appointment ->
            appointment.petId == selectedPet.id &&
                isSameLocalDate(appointment.scheduledAtMillis, selectedDateMillis.longValue)
        }
        .sortedBy { it.scheduledAtMillis }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Agenda de ${selectedPet.name}",
            fontWeight = FontWeight.Bold
        )

        PetSelector(
            pets = pets,
            selectedPetId = selectedPetId,
            onPetSelected = { petId ->
                petViewModel.selectPet(petId)
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Selecciona una fecha",
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
                    )
                },
                headline = {
                    Text(
                        text = formatAppointmentDate(selectedDateMillis.longValue),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            )
        }

        Text(
            text = "Eventos del ${formatAppointmentDate(selectedDateMillis.longValue)}",
            fontWeight = FontWeight.Bold
        )

        if (selectedPetAppointments.isNotEmpty()) {
            selectedPetAppointments.forEach { appointment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PetCard),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = PetGreen
                        )

                        Text(
                            text = appointment.title,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${appointment.time} · ${appointment.place}",
                            color = PetTextGray
                        )

                        if (appointment.notes.isNotBlank()) {
                            Text(
                                text = appointment.notes,
                                color = PetTextGray
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PetCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Sin eventos en esta fecha",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Toca agregar evento para programar una cita, vacuna o cuidado.",
                        color = PetTextGray
                    )
                }
            }
        }

        Button(
            onClick = { onNewAppointmentClick(selectedDateMillis.longValue) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Agregar evento en esta fecha")
        }

        Button(
            onClick = onDailyRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Registrar síntomas de hoy")
        }

        if (selectedPetLogs.isNotEmpty()) {
            val lastLog = selectedPetLogs.last()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PetCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Último registro", fontWeight = FontWeight.Bold)
                    Text("Ánimo: ${lastLog.mood}")
                    Text("Apetito: ${lastLog.appetite}")
                    Text("Actividad: ${lastLog.activity}")
                    Text(
                        text = "Síntomas: ${
                            if (lastLog.symptoms.isEmpty()) "Sin síntomas"
                            else lastLog.symptoms.joinToString(", ")
                        }"
                    )
                }
            }
        }
    }
}
