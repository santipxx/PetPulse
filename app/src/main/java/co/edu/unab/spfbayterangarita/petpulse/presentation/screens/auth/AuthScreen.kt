package co.edu.unab.spfbayterangarita.petpulse.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel.AuthViewModel
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetDarkGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthenticated: () -> Unit
) {
    val uiState = authViewModel.uiState.collectAsState().value
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isRegisterMode = remember { mutableStateOf(false) }

    LaunchedEffect(uiState.session) {
        if (uiState.session != null) {
            onAuthenticated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PetCream)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Pets,
            contentDescription = null,
            tint = PetGreen,
            modifier = Modifier.height(72.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isRegisterMode.value) "Crea tu cuenta" else "Bienvenido a PetPulse",
            fontWeight = FontWeight.Bold,
            color = PetDarkGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tus mascotas, registros y eventos quedan vinculados a tu cuenta.",
            color = PetTextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        uiState.errorMessage?.let { errorMessage ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = PetTextGray,
                modifier = Modifier.fillMaxWidth()
            )
        }

        uiState.successMessage?.let { successMessage ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = successMessage,
                color = PetGreen,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                if (isRegisterMode.value) {
                    authViewModel.createAccount(email.value, password.value)
                } else {
                    authViewModel.signIn(email.value, password.value)
                }
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PetGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Procesando..."
                } else if (isRegisterMode.value) {
                    "Crear cuenta"
                } else {
                    "Ingresar"
                },
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(
            onClick = {
                isRegisterMode.value = !isRegisterMode.value
            }
        ) {
            Text(
                text = if (isRegisterMode.value) {
                    "Ya tengo cuenta"
                } else {
                    "Crear una cuenta nueva"
                },
                color = PetGreen
            )
        }

        if (!isRegisterMode.value) {
            TextButton(
                onClick = {
                    authViewModel.sendPasswordReset(email.value)
                },
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Olvidé mi contraseña",
                    color = PetGreen
                )
            }
        }
    }
}
