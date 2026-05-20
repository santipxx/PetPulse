package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.profile

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
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetOrange
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun ProfileScreen(
    petViewModel: PetViewModel
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Pets,
            contentDescription = null,
            tint = PetGreen
        )

        Text(
            text = selectedPet.name,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${selectedPet.breed} · ${selectedPet.ageText}",
            color = PetTextGray
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
                Text(
                    "Nivel ${selectedPet.level} — Explorador Canino",
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
                    text = "Primer paseo · Semana 1 · Racha ${selectedPet.currentStreak} · Veterinario responsable",
                    color = PetTextGray
                )
            }
        }
    }
}