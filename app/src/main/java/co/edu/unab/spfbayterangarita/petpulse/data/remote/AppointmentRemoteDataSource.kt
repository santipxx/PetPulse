package co.edu.unab.spfbayterangarita.petpulse.data.remote

import android.content.Context
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class AppointmentRemoteDataSource(context: Context) {

    private val firestore = if (FirebaseApp.getApps(context).isNotEmpty()) {
        FirebaseFirestore.getInstance()
    } else {
        null
    }

    fun listenToAppointments(
        userId: String,
        onAppointmentsChanged: (List<Appointment>) -> Unit
    ): ListenerRegistration? {
        val db = firestore ?: return null

        return appointmentsCollection(db, userId).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                return@addSnapshotListener
            }

            val appointments = snapshot.documents.map { document ->
                Appointment(
                    id = document.getString("id").orEmpty().ifBlank { document.id },
                    petId = document.getString("petId").orEmpty(),
                    title = document.getString("title").orEmpty(),
                    type = document.getString("type").orEmpty(),
                    date = document.getString("date").orEmpty(),
                    time = document.getString("time").orEmpty(),
                    place = document.getString("place").orEmpty(),
                    notes = document.getString("notes").orEmpty(),
                    isReminderEnabled = document.getBoolean("isReminderEnabled") ?: true,
                    scheduledAtMillis = document.getLong("scheduledAtMillis") ?: 0L
                )
            }

            onAppointmentsChanged(appointments)
        }
    }

    fun saveAppointment(userId: String, appointment: Appointment) {
        val db = firestore ?: return

        appointmentsCollection(db, userId)
            .document(appointment.id)
            .set(appointment.toFirestoreMap(), SetOptions.merge())
    }

    private fun appointmentsCollection(db: FirebaseFirestore, userId: String): CollectionReference {
        return db.collection("users")
            .document(userId)
            .collection("appointments")
    }

    private fun Appointment.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "petId" to petId,
            "title" to title,
            "type" to type,
            "date" to date,
            "time" to time,
            "place" to place,
            "notes" to notes,
            "isReminderEnabled" to isReminderEnabled,
            "scheduledAtMillis" to scheduledAtMillis
        )
    }

}
