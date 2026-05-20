package co.edu.unab.spfbayterangarita.petpulse.data.remote

import android.graphics.Bitmap
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class PetRemoteDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun listenToPets(
        userId: String,
        onPetsChanged: (List<Pet>) -> Unit
    ): ListenerRegistration {
        return petsCollection(userId).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                return@addSnapshotListener
            }

            val pets = snapshot.documents.map { document ->
                Pet(
                    id = document.getString("id").orEmpty().ifBlank { document.id },
                    ownerId = document.getString("ownerId").orEmpty(),
                    name = document.getString("name").orEmpty(),
                    species = document.getString("species").orEmpty(),
                    breed = document.getString("breed").orEmpty(),
                    birthDate = document.getString("birthDate").orEmpty(),
                    ageText = document.getString("ageText").orEmpty(),
                    weightKg = document.getDouble("weightKg") ?: 0.0,
                    bloodType = document.getString("bloodType").orEmpty(),
                    veterinarianName = document.getString("veterinarianName").orEmpty(),
                    level = document.getLong("level")?.toInt() ?: 1,
                    xp = document.getLong("xp")?.toInt() ?: 0,
                    currentStreak = document.getLong("currentStreak")?.toInt() ?: 0,
                    consistencyPercent = document.getLong("consistencyPercent")?.toInt() ?: 0,
                    photoUrl = document.getString("photoUrl").orEmpty()
                )
            }

            onPetsChanged(pets)
        }
    }

    fun savePet(userId: String, pet: Pet) {
        petsCollection(userId)
            .document(pet.id)
            .set(pet.toFirestoreMap(), SetOptions.merge())
    }

    fun deletePet(userId: String, petId: String) {
        petsCollection(userId)
            .document(petId)
            .delete()

        storage.reference
            .child("users/$userId/pets/$petId/profile.jpg")
            .delete()
    }

    fun uploadPetPhoto(
        userId: String,
        petId: String,
        bitmap: Bitmap,
        onPhotoUploaded: (String) -> Unit
    ) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, outputStream)

        val photoReference = storage.reference
            .child("users/$userId/pets/$petId/profile.jpg")

        photoReference.putBytes(outputStream.toByteArray())
            .continueWithTask { photoReference.downloadUrl }
            .addOnSuccessListener { uri ->
                val photoUrl = uri.toString()

                petsCollection(userId)
                    .document(petId)
                    .set(mapOf("photoUrl" to photoUrl), SetOptions.merge())

                onPhotoUploaded(photoUrl)
            }
    }

    private fun petsCollection(userId: String): CollectionReference {
        return firestore.collection("users")
            .document(userId)
            .collection("pets")
    }

    private fun Pet.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "ownerId" to ownerId,
            "name" to name,
            "species" to species,
            "breed" to breed,
            "birthDate" to birthDate,
            "ageText" to ageText,
            "weightKg" to weightKg,
            "bloodType" to bloodType,
            "veterinarianName" to veterinarianName,
            "level" to level,
            "xp" to xp,
            "currentStreak" to currentStreak,
            "consistencyPercent" to consistencyPercent,
            "photoUrl" to photoUrl
        )
    }

    private companion object {
        const val PHOTO_QUALITY = 85
    }
}
