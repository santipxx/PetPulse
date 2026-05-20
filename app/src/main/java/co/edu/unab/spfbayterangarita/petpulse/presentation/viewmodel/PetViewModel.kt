package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet
import co.edu.unab.spfbayterangarita.petpulse.data.remote.PetRemoteDataSource
import co.edu.unab.spfbayterangarita.petpulse.util.calculateAgeText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PetViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val remoteDataSource = PetRemoteDataSource()

    private var petsListener: ListenerRegistration? = null
    private var currentUserId: String? = null

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets

    private val _selectedPetId = MutableStateFlow("")
    val selectedPetId: StateFlow<String> = _selectedPetId

    val selectedPet: Pet?
        get() = _pets.value.firstOrNull { it.id == _selectedPetId.value }
            ?: _pets.value.firstOrNull()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == currentUserId) {
            return@AuthStateListener
        }

        currentUserId = userId
        petsListener?.remove()
        petsListener = null

        if (userId == null) {
            _pets.value = emptyList()
            _selectedPetId.value = ""
        } else {
            listenToPets(userId)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.uid?.let { userId ->
            currentUserId = userId
            listenToPets(userId)
        }
    }

    fun selectPet(petId: String) {
        _selectedPetId.value = petId
    }

    fun addPet(
        name: String,
        species: String,
        breed: String,
        birthDate: String,
        birthDateMillis: Long,
        photoUri: Uri?
    ) {
        val userId = currentUserId ?: return
        val petId = "pet_${System.currentTimeMillis()}"
        val newPet = Pet(
            id = petId,
            ownerId = userId,
            name = name,
            species = species,
            breed = breed,
            birthDate = birthDate,
            ageText = calculateAgeText(birthDateMillis),
            level = 1,
            xp = 0,
            currentStreak = 0,
            consistencyPercent = 0
        )

        _pets.value = _pets.value + newPet
        _selectedPetId.value = petId
        remoteDataSource.savePet(userId, newPet)

        if (photoUri != null) {
            updatePetPhoto(petId, photoUri)
        }
    }

    fun deleteSelectedPet() {
        val userId = currentUserId ?: return
        val petId = _selectedPetId.value.ifBlank { return }
        val remainingPets = _pets.value.filterNot { it.id == petId }

        _pets.value = remainingPets
        _selectedPetId.value = remainingPets.firstOrNull()?.id.orEmpty()
        remoteDataSource.deletePet(userId, petId)
    }

    fun updatePetPhoto(petId: String, photoBitmap: Bitmap) {
        val userId = currentUserId ?: return

        remoteDataSource.uploadPetPhoto(
            userId = userId,
            petId = petId,
            bitmap = photoBitmap,
            onPhotoUploaded = { photoUrl ->
                _pets.value = _pets.value.map { pet ->
                    if (pet.id == petId) {
                        pet.copy(photoUrl = photoUrl)
                    } else {
                        pet
                    }
                }
            }
        )
    }

    fun updatePetPhoto(petId: String, photoUri: Uri) {
        val userId = currentUserId ?: return

        remoteDataSource.uploadPetPhoto(
            userId = userId,
            petId = petId,
            photoUri = photoUri,
            onPhotoUploaded = { photoUrl ->
                _pets.value = _pets.value.map { pet ->
                    if (pet.id == petId) {
                        pet.copy(photoUrl = photoUrl)
                    } else {
                        pet
                    }
                }
            }
        )
    }

    private fun listenToPets(userId: String) {
        petsListener = remoteDataSource.listenToPets(userId) { pets ->
            _pets.value = pets

            val selectedPetStillExists = pets.any { it.id == _selectedPetId.value }
            if (!selectedPetStillExists) {
                _selectedPetId.value = pets.firstOrNull()?.id.orEmpty()
            }
        }
    }

    override fun onCleared() {
        petsListener?.remove()
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}
