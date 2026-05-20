package co.edu.unab.spfbayterangarita.petpulse.data.model

data class Pet(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val birthDate: String = "",
    val ageText: String = "",
    val weightKg: Double = 0.0,
    val bloodType: String = "",
    val veterinarianName: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val currentStreak: Int = 0,
    val consistencyPercent: Int = 0
)