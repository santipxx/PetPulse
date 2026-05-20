package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetOrange
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.PetSelector
@Composable
fun HomeScreen(
    petViewModel: PetViewModel
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val pet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hola, Santiago",
            fontWeight = FontWeight.Bold
        )
        PetSelector(
            pets = pets,
            selectedPetId = selectedPetId,
            onPetSelected = { petId ->
                petViewModel.selectPet(petId)
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetGreen),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = null,
                        tint = PetCream
                    )

                    Column {
                        Text(
                            text = pet.name.ifBlank { "Max" },
                            color = PetCream,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${pet.breed} · ${pet.ageText}",
                            color = PetCream
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nivel ${pet.level} — Explorador Canino",
                    color = PetCream,
                    fontWeight = FontWeight.Medium
                )

                LinearProgressIndicator(
                    progress = { 0.86f },
                    modifier = Modifier.fillMaxWidth(),
                    color = PetCream,
                    trackColor = PetSoftGreen
                )

                Text(
                    text = "${pet.xp} XP · ${pet.consistencyPercent}% de consistencia",
                    color = PetCream
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.LocalFireDepartment,
                title = "${pet.currentStreak} días",
                subtitle = "Racha actual",
                iconColor = PetOrange
            )

            HomeMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.WbSunny,
                title = "36°C",
                subtitle = "Paseo seguro",
                iconColor = PetOrange
            )
        }

        HomeActionCard(
            icon = Icons.Rounded.CalendarMonth,
            title = "Próxima cita",
            subtitle = "Vacuna Rabia · Jul 28 · 08:30 AM"
        )

        HomeActionCard(
            icon = Icons.Rounded.Chat,
            title = "PetPulse AI",
            subtitle = "Pregúntale sobre síntomas, hábitos o cuidados preventivos."
        )
    }
}

@Composable
fun HomeMiniCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PetCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = PetTextGray
            )
        }
    }
}

@Composable
fun HomeActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PetCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PetGreen
            )

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = PetTextGray
                )
            }
        }
    }
}