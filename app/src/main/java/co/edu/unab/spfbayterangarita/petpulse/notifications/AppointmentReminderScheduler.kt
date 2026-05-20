package co.edu.unab.spfbayterangarita.petpulse.notifications

import android.content.Context
import co.edu.unab.spfbayterangarita.petpulse.data.model.Appointment
import java.util.concurrent.TimeUnit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class AppointmentReminderScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun schedule(appointment: Appointment) {
        if (!appointment.isReminderEnabled || appointment.scheduledAtMillis <= 0L) return

        val reminderAtMillis = calculateReminderAtMillis(appointment.scheduledAtMillis)
        val delayMillis = reminderAtMillis - System.currentTimeMillis()

        if (delayMillis <= 0L) return

        val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointment.id,
                    AppointmentReminderWorker.KEY_TITLE to appointment.title,
                    AppointmentReminderWorker.KEY_DATE to appointment.date,
                    AppointmentReminderWorker.KEY_TIME to appointment.time,
                    AppointmentReminderWorker.KEY_PLACE to appointment.place
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            workName(appointment.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun scheduleAll(appointments: List<Appointment>) {
        appointments.forEach { schedule(it) }
    }

    private fun calculateReminderAtMillis(scheduledAtMillis: Long): Long {
        val now = System.currentTimeMillis()
        val timeUntilEvent = scheduledAtMillis - now

        return when {
            timeUntilEvent > ONE_DAY_MILLIS -> scheduledAtMillis - ONE_DAY_MILLIS
            timeUntilEvent > ONE_HOUR_MILLIS -> scheduledAtMillis - ONE_HOUR_MILLIS
            timeUntilEvent > FIFTEEN_MINUTES_MILLIS -> scheduledAtMillis - FIFTEEN_MINUTES_MILLIS
            else -> scheduledAtMillis
        }
    }

    private fun workName(appointmentId: String): String {
        return "appointment_reminder_$appointmentId"
    }

    private companion object {
        const val FIFTEEN_MINUTES_MILLIS = 15 * 60 * 1000L
        const val ONE_HOUR_MILLIS = 60 * 60 * 1000L
        const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}
