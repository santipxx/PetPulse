package co.edu.unab.spfbayterangarita.petpulse.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import co.edu.unab.spfbayterangarita.petpulse.R

class AppointmentReminderWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return Result.success()
            }
        }

        val appointmentTitle = inputData.getString(KEY_TITLE).orEmpty().ifBlank { "Evento de PetPulse" }
        val appointmentDate = inputData.getString(KEY_DATE).orEmpty()
        val appointmentTime = inputData.getString(KEY_TIME).orEmpty()
        val appointmentPlace = inputData.getString(KEY_PLACE).orEmpty()
        val appointmentId = inputData.getString(KEY_APPOINTMENT_ID).orEmpty()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(appointmentTitle)
            .setContentText("$appointmentDate · $appointmentTime · $appointmentPlace")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Se acerca: $appointmentTitle\n$appointmentDate · $appointmentTime\n$appointmentPlace")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            appointmentId.hashCode(),
            notification
        )

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de agenda",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos de citas, vacunas y eventos de mascotas"
        }

        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "appointment_reminders"
        const val KEY_APPOINTMENT_ID = "appointment_id"
        const val KEY_TITLE = "title"
        const val KEY_DATE = "date"
        const val KEY_TIME = "time"
        const val KEY_PLACE = "place"
    }
}
