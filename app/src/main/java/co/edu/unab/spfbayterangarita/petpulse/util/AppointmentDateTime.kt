package co.edu.unab.spfbayterangarita.petpulse.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val appLocale = Locale.forLanguageTag("es-CO")
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", appLocale)
private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", appLocale)

fun todayStartMillis(): Long {
    return LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun formatAppointmentDate(millis: Long): String {
    if (millis <= 0L) return "Sin fecha"

    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormatter)
}

fun formatBirthDate(millis: Long): String {
    if (millis <= 0L) return ""

    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormatter)
}

fun calculateAgeText(birthDateMillis: Long): String {
    if (birthDateMillis <= 0L) return "Sin calcular"

    val birthDate = Instant.ofEpochMilli(birthDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()

    if (birthDate.isAfter(today)) return "Sin calcular"

    val period = Period.between(birthDate, today)

    return when {
        period.years > 0 -> "${period.years} ${if (period.years == 1) "año" else "años"}"
        period.months > 0 -> "${period.months} ${if (period.months == 1) "mes" else "meses"}"
        else -> "${period.days} ${if (period.days == 1) "día" else "días"}"
    }
}

fun formatAppointmentTime(hour: Int, minute: Int): String {
    return LocalTime.of(hour, minute).format(timeFormatter)
}

fun combineDateAndTimeMillis(dateMillis: Long, hour: Int, minute: Int): Long {
    val date = Instant.ofEpochMilli(dateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    return date.atTime(hour, minute)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun isSameLocalDate(firstMillis: Long, secondMillis: Long): Boolean {
    if (firstMillis <= 0L || secondMillis <= 0L) return false

    val zoneId = ZoneId.systemDefault()
    val firstDate = Instant.ofEpochMilli(firstMillis).atZone(zoneId).toLocalDate()
    val secondDate = Instant.ofEpochMilli(secondMillis).atZone(zoneId).toLocalDate()

    return firstDate == secondDate
}
