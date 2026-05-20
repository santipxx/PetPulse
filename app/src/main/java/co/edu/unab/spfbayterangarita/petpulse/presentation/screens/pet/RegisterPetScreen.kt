package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.pet

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen

@Composable
fun RegisterPetScreen(
    petViewModel: PetViewModel,
    onContinueClick: () -> Unit
) {
    val petName = remember { mutableStateOf("") }
    val breed = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }
    val selectedSpecies = remember { mutableStateOf("Perro") }
    val photoBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val nameHasError = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            photoBitmap.value = bitmap
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    nameHasError.value = petName.value.isBlank()

                    if (nameHasError.value) {
                        return@Button
                    }

                    petViewModel.addPet(
                        name = petName.value.trim(),
                        species = selectedSpecies.value,
                        breed = breed.value.trim(),
                        birthDate = birthDate.value.trim(),
                        photoBitmap = photoBitmap.value
                    )
                    onContinueClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetGreen
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Guardar mascota", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PetCream)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registra a tu mascota",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(PetSoftGreen),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = photoBitmap.value

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto de la mascota",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = "Mascota",
                        tint = PetGreen,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    cameraLauncher.launch(null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = null
                )
                Text("Tomar foto")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = petName.value,
                onValueChange = {
                    petName.value = it
                    nameHasError.value = false
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                isError = nameHasError.value,
                supportingText = {
                    if (nameHasError.value) {
                        Text("Ingresa el nombre de tu mascota")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Especie",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Perro", "Gato", "Otro").forEach { species ->
                    FilterChip(
                        selected = selectedSpecies.value == species,
                        onClick = { selectedSpecies.value = species },
                        label = { Text(species) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = breed.value,
                onValueChange = { breed.value = it },
                label = { Text("Raza") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = birthDate.value,
                onValueChange = { birthDate.value = it },
                label = { Text("Fecha de nacimiento") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Cake,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}
