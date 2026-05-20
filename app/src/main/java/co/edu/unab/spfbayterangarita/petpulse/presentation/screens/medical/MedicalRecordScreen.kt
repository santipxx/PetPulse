package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.medical

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.EmptyPetsState
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun MedicalRecordScreen(
    petViewModel: PetViewModel
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.firstOrNull()

    if (selectedPet == null) {
        EmptyPetsState(message = "Agrega una mascota para construir su expediente médico.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Expediente de ${selectedPet.name}",
            fontWeight = FontWeight.Bold
        )

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
                Icon(
                    imageVector = Icons.Rounded.MedicalServices,
                    contentDescription = null,
                    tint = PetGreen
                )

                Text("Resumen médico", fontWeight = FontWeight.Bold)

                Text("Especie: ${selectedPet.species}")
                Text("Raza: ${selectedPet.breed}")
                Text("Edad: ${selectedPet.ageText}")
                Text("Peso: ${selectedPet.weightKg} kg")
                Text("Tipo de sangre: ${selectedPet.bloodType}")
                Text("Veterinario: ${selectedPet.veterinarianName}")
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
                Text("Vacunas & Prevención", fontWeight = FontWeight.Bold)

                Text("Rabia", fontWeight = FontWeight.Medium)

                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth(),
                    color = PetGreen,
                    trackColor = PetSoftGreen
                )

                Text(
                    text = "Al día · Próxima Jul 28 2025",
                    color = PetTextGray
                )

                Text("Polivalente", fontWeight = FontWeight.Medium)

                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.fillMaxWidth(),
                    color = PetGreen,
                    trackColor = PetSoftGreen
                )

                Text(
                    text = "Pendiente de refuerzo",
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Exportación veterinaria", fontWeight = FontWeight.Bold)

                Text(
                    text = "Aquí se generará el resumen PDF de los últimos 30 días con síntomas, comportamiento, dieta y registros médicos.",
                    color = PetTextGray
                )
            }
        }
    }
}
