package co.edu.unab.spfbayterangarita.petpulse.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.edu.unab.spfbayterangarita.petpulse.data.model.Pet


@Composable
fun PetSelector(
    pets: List<Pet>,
    selectedPetId: String,
    onPetSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = androidx.compose.ui.unit.Dp(8f)
        )
    ) {
        pets.forEach { pet ->
            FilterChip(
                selected = pet.id == selectedPetId,
                onClick = {
                    onPetSelected(pet.id)
                },
                label = {
                    Text(text = pet.name)
                }
            )
        }
    }
}