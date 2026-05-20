package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PetViewModel : ViewModel() {

    private val _pets = MutableStateFlow(
        listOf(
            Pet(
                id = "pet_1",
                ownerId = "user_1",
                name = "Max",
                species = "Perro",
                breed = "Golden Retriever",
                birthDate = "12 / Mar / 2021",
                ageText = "3 años",
                weightKg = 28.5,
                bloodType = "A+",
                veterinarianName = "Dr. García",
                level = 7,
                xp = 3428,
                currentStreak = 14,
                consistencyPercent = 89
            ),
            Pet(
                id = "pet_2",
                ownerId = "user_1",
                name = "Luna",
                species = "Gato",
                breed = "Criolla",
                birthDate = "05 / Ene / 2022",
                ageText = "2 años",
                weightKg = 4.8,
                bloodType = "Desconocido",
                veterinarianName = "Dra. Martínez",
                level = 3,
                xp = 1280,
                currentStreak = 6,
                consistencyPercent = 74
            )
        )
    )

    val pets: StateFlow<List<Pet>> = _pets

    private val _selectedPetId = MutableStateFlow("pet_1")
    val selectedPetId: StateFlow<String> = _selectedPetId

    val selectedPet: Pet
        get() = _pets.value.firstOrNull { it.id == _selectedPetId.value }
            ?: _pets.value.first()

    fun selectPet(petId: String) {
        _selectedPetId.value = petId
    }

    fun addPet(
        name: String,
        species: String,
        breed: String,
        birthDate: String
    ) {
        val newPet = Pet(
            id = "pet_${System.currentTimeMillis()}",
            ownerId = "user_1",
            name = name,
            species = species,
            breed = breed,
            birthDate = birthDate,
            ageText = "Sin calcular",
            level = 1,
            xp = 0,
            currentStreak = 0,
            consistencyPercent = 0
        )

        _pets.value = _pets.value + newPet
        _selectedPetId.value = newPet.id
    }
}