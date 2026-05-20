package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.data.model.ChatMessage
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.ChatViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.DailyLogViewModel
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.PetViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCard
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextDark
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray
import co.edu.unab.spfbayterangarita.petpulse.presentation.components.PetSelector
@Composable
fun ChatScreen(
    petViewModel: PetViewModel,
    dailyLogViewModel: DailyLogViewModel,
    chatViewModel: ChatViewModel
) {
    val pets = petViewModel.pets.collectAsState().value
    val selectedPetId = petViewModel.selectedPetId.collectAsState().value

    val selectedPet = pets.firstOrNull { it.id == selectedPetId }
        ?: pets.first()

    val dailyLogs = dailyLogViewModel.dailyLogs.collectAsState().value
    val selectedPetLogs = dailyLogs.filter { it.petId == selectedPet.id }

    val messages = chatViewModel.messages.collectAsState().value
    val userInput = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Pets,
                contentDescription = null,
                tint = PetGreen
            )

            Column {
                Text(
                    text = "PetPulse AI",
                    fontWeight = FontWeight.Bold
                )
                PetSelector(
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onPetSelected = { petId ->
                        petViewModel.selectPet(petId)
                    }
                )
                Text(
                    text = "Analizando registros de ${selectedPet.name}",
                    color = PetGreen
                )
            }
        }

        RecentSymptomsCard(
            symptoms = selectedPetLogs
                .flatMap { it.symptoms }
                .distinct()
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userInput.value,
                onValueChange = { userInput.value = it },
                placeholder = {
                    Text("Pregúntale algo a PetPulse")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp)
            )

            IconButton(
                onClick = {
                    chatViewModel.sendMessage(
                        text = userInput.value,
                        selectedPet = selectedPet,
                        recentLogs = selectedPetLogs
                    )
                    userInput.value = ""
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Enviar",
                    tint = PetGreen
                )
            }
        }
    }
}

@Composable
fun RecentSymptomsCard(
    symptoms: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PetSoftGreen),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Síntomas recientes",
                fontWeight = FontWeight.Bold,
                color = PetTextDark
            )

            Text(
                text = if (symptoms.isEmpty()) {
                    "Aún no hay síntomas registrados para esta mascota."
                } else {
                    symptoms.joinToString(", ")
                },
                color = PetTextGray
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isFromUser) PetGreen else PetCard,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.message,
                color = if (message.isFromUser) PetCream else PetTextDark
            )
        }
    }
}