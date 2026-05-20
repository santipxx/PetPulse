package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AuthViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetOrange
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun ProfileScreen(
    petViewModel: PetViewModel,
    authViewModel: AuthViewModel,
    onAddPetClick: () -> Unit,
    onSignedOut: () -> Unit
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value
    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.firstOrNull()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        val pet = selectedPet
        if (bitmap != null && pet != null) {
            petViewModel.updatePetPhoto(pet.id, bitmap)
        }
    }

    if (selectedPet == null) {
        EmptyPetsProfile(
            onAddPetClick = onAddPetClick,
            onSignOutClick = {
                authViewModel.signOut()
                onSignedOut()
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(PetSoftGreen),
            contentAlignment = Alignment.Center
        ) {
            if (selectedPet.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = selectedPet.photoUrl,
                    contentDescription = "Foto de ${selectedPet.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Pets,
                    contentDescription = null,
                    tint = PetGreen,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Text(
            text = selectedPet.name,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${selectedPet.breed.ifBlank { selectedPet.species }} · ${selectedPet.ageText}",
            color = PetTextGray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.CameraAlt, contentDescription = null)
                Text("Foto")
            }

            Button(
                onClick = onAddPetClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Text("Agregar")
            }
        }

        OutlinedButton(
            onClick = { petViewModel.deleteSelectedPet() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = null)
            Text("Eliminar mascota")
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pets.forEach { pet ->
                FilterChip(
                    selected = pet.id == selectedPetId,
                    onClick = {
                        petViewModel.selectPet(pet.id)
                    },
                    label = {
                        Text(pet.name)
                    }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Nivel ${selectedPet.level} - Explorador",
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = { selectedPet.consistencyPercent / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = PetGreen,
                    trackColor = PetSoftGreen
                )

                Text(
                    "${selectedPet.consistencyPercent}% de consistencia",
                    color = PetTextGray
                )

                Text(
                    "${selectedPet.xp} XP acumulados",
                    color = PetTextGray
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = PetOrange
                )

                Text("Mis insignias", fontWeight = FontWeight.Bold)

                Text(
                    text = "Primer registro · Racha ${selectedPet.currentStreak} · Cuidado responsable",
                    color = PetTextGray
                )
            }
        }

        OutlinedButton(
            onClick = {
                authViewModel.signOut()
                onSignedOut()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun EmptyPetsProfile(
    onAddPetClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Pets,
            contentDescription = null,
            tint = PetGreen,
            modifier = Modifier.size(72.dp)
        )

        Text("Aún no tienes mascotas", fontWeight = FontWeight.Bold)
        Text("Agrega una para empezar a usar PetPulse.", color = PetTextGray)

        Button(
            onClick = onAddPetClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Agregar mascota")
        }

        OutlinedButton(
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}
