package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun AgendaScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Agenda de Max",
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PetCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = PetGreen
                )

                Text(
                    text = "Sábado 22 de julio",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Baño y peluquería",
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "10:00 AM · Pet Grooming Canino",
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
                Text(
                    text = "Próximos 30 días",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Vacuna Rabia · Jul 28",
                    color = PetTextGray
                )
            }
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Agregar evento")
        }
    }
}