package co.edu.unab.spfbayterangarita.petpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.unab.spfbayterangarita.petpulse.presentation.navigation.PetPulseApp
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PetPulseTheme {
                PetPulseApp()
            }
        }
    }
}