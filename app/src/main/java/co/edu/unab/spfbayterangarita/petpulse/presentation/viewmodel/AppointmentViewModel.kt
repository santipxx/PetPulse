package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.local.AppointmentLocalStorage
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import co.edu.unab.spfbayterangarita.petpulse.data.remote.AppointmentRemoteDataSource
import co.edu.unab.spfbayterangarita.petpulse.notifications.AppointmentReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val localStorage = AppointmentLocalStorage(application.applicationContext)
    private val remoteDataSource = AppointmentRemoteDataSource(application.applicationContext)
    private val reminderScheduler = AppointmentReminderScheduler(application.applicationContext)
    private var appointmentListener: ListenerRegistration? = null
    private var currentUserId: String? = null

    private val defaultAppointments = listOf(
        Appointment(
            id = "1",
            petId = "pet_1",
            title = "Baño y peluquería",
            type = "Cuidado",
            date = "Sábado 22 de julio",
            time = "10:00 AM",
            place = "Pet Grooming Canino",
            notes = "Recordatorio: 1 día antes"
        ),
        Appointment(
            id = "2",
            petId = "pet_1",
            title = "Vacuna Rabia",
            type = "Vacuna",
            date = "Jul 28",
            time = "08:30 AM",
            place = "Clínica Vet Norte",
            notes = "Próxima vacuna programada"
        )
    )

    private val _appointments = MutableStateFlow(
        localStorage.loadAppointments(defaultAppointments)
    )

    val appointments: StateFlow<List<Appointment>> = _appointments

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == currentUserId) {
            return@AuthStateListener
        }

        currentUserId = userId
        appointmentListener?.remove()
        appointmentListener = null

        if (userId == null) {
            _appointments.value = emptyList()
        } else {
            listenToRemoteAppointments(userId)
        }
    }

    init {
        auth.addAuthStateListener(authListener)

        auth.currentUser?.uid?.let { userId ->
            currentUserId = userId
            listenToRemoteAppointments(userId)
        }
    }

    fun addAppointment(appointment: Appointment) {
        val updatedAppointments = _appointments.value + appointment

        _appointments.value = updatedAppointments
        localStorage.saveAppointments(updatedAppointments)
        reminderScheduler.schedule(appointment)
        currentUserId?.let { userId ->
            remoteDataSource.saveAppointment(userId, appointment)
        }
    }

    fun getAppointmentsByPet(petId: String): List<Appointment> {
        return _appointments.value.filter { it.petId == petId }
    }

    override fun onCleared() {
        appointmentListener?.remove()
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    private fun listenToRemoteAppointments(userId: String) {
        appointmentListener = remoteDataSource.listenToAppointments(userId) { remoteAppointments ->
            _appointments.value = remoteAppointments
            localStorage.saveAppointments(remoteAppointments)
            reminderScheduler.scheduleAll(remoteAppointments)
        }
    }
}
