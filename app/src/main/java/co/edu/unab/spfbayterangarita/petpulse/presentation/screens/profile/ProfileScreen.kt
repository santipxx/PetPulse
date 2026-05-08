package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetOrange
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
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
            text = "Max",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Golden Retriever · 3 años",
            color = PetTextGray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Nivel 7 — Explorador Canino", fontWeight = FontWeight.Bold)

                LinearProgressIndicator(
                    progress = { 0.86f },
                    modifier = Modifier.fillMaxWidth(),
                    color = PetGreen
                )

                Text("86% para llegar al siguiente nivel", color = PetTextGray)
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
                Text("Primer paseo · Semana 1 · Racha 7 · Veterinario responsable")
            }
        }
    }
}