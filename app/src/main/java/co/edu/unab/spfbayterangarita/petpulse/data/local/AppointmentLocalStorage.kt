package co.edu.unab.spfbayterangarita.petpulse.data.local

import android.content.Context
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import org.json.JSONArray
import org.json.JSONObject

class AppointmentLocalStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        APPOINTMENTS_PREFERENCES,
        Context.MODE_PRIVATE
    )

    fun loadAppointments(defaultAppointments: List<Appointment>): List<Appointment> {
        val storedAppointments = preferences.getString(APPOINTMENTS_KEY, null)
            ?: return defaultAppointments

        return runCatching {
            val jsonArray = JSONArray(storedAppointments)

            List(jsonArray.length()) { index ->
                val appointmentJson = jsonArray.getJSONObject(index)

                Appointment(
                    id = appointmentJson.optString("id"),
                    petId = appointmentJson.optString("petId"),
                    title = appointmentJson.optString("title"),
                    type = appointmentJson.optString("type"),
                    date = appointmentJson.optString("date"),
                    time = appointmentJson.optString("time"),
                    place = appointmentJson.optString("place"),
                    notes = appointmentJson.optString("notes"),
                    isReminderEnabled = appointmentJson.optBoolean("isReminderEnabled", true),
                    scheduledAtMillis = appointmentJson.optLong("scheduledAtMillis", 0L)
                )
            }
        }.getOrDefault(defaultAppointments)
    }

    fun saveAppointments(appointments: List<Appointment>) {
        val jsonArray = JSONArray()

        appointments.forEach { appointment ->
            jsonArray.put(
                JSONObject()
                    .put("id", appointment.id)
                    .put("petId", appointment.petId)
                    .put("title", appointment.title)
                    .put("type", appointment.type)
                    .put("date", appointment.date)
                    .put("time", appointment.time)
                    .put("place", appointment.place)
                    .put("notes", appointment.notes)
                    .put("isReminderEnabled", appointment.isReminderEnabled)
                    .put("scheduledAtMillis", appointment.scheduledAtMillis)
            )
        }

        preferences.edit()
            .putString(APPOINTMENTS_KEY, jsonArray.toString())
            .apply()
    }

    private companion object {
        const val APPOINTMENTS_PREFERENCES = "appointments_preferences"
        const val APPOINTMENTS_KEY = "appointments"
    }
}
