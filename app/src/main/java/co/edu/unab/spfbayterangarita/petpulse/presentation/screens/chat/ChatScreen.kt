package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun ChatScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Pets,
                contentDescription = null,
                tint = PetGreen
            )

            Column {
                Text("PetPulse AI", fontWeight = FontWeight.Bold)
                Text("Online", color = PetGreen)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetSoftGreen),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nuevo análisis disponible",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "El sueño de Bella mejoró esta semana.",
                    color = PetTextGray
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Hola, soy PetPulse AI. Cuéntame qué síntomas tiene tu mascota y te daré una orientación preventiva.",
                modifier = Modifier.padding(16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetGreen),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Mi perro está bajo de energía hoy.",
                modifier = Modifier.padding(16.dp),
                color = PetCream
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = "",
            onValueChange = { },
            placeholder = {
                Text("Pregúntale algo a PetPulse")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        )
    }
}