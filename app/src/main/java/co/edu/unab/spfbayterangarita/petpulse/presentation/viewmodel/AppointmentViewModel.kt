package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppointmentViewModel : ViewModel() {

    private val _appointments = MutableStateFlow(
        listOf(
            Appointment(
                id = "1",
                title = "Baño y peluquería",
                type = "Cuidado",
                date = "Sábado 22 de julio",
                time = "10:00 AM",
                place = "Pet Grooming Canino",
                notes = "Recordatorio: 1 día antes"
            ),
            Appointment(
                id = "2",
                title = "Vacuna Rabia",
                type = "Vacuna",
                date = "Jul 28",
                time = "08:30 AM",
                place = "Clínica Vet Norte",
                notes = "Próxima vacuna programada"
            )
        )
    )

    val appointments: StateFlow<List<Appointment>> = _appointments
}