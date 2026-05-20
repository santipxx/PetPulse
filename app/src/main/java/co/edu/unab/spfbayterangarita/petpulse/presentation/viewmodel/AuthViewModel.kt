package co.edu.unab.spfbayterangarita.petpulse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import co.edu.unab.spfbayterangarita.petpulse.data.model.UserSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val session: UserSession? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _uiState.value = _uiState.value.copy(
            session = firebaseAuth.currentUser?.toUserSession(),
            isLoading = false
        )
    }

    private val _uiState = MutableStateFlow(
        AuthUiState(session = auth.currentUser?.toUserSession())
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        auth.addAuthStateListener(authListener)
    }

    fun signIn(email: String, password: String) {
        if (!validateCredentials(email, password)) return

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.localizedMessage
                            ?: "No se pudo iniciar sesión."
                    )
                }
            }
    }

    fun createAccount(email: String, password: String) {
        if (!validateCredentials(email, password)) return

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.localizedMessage
                            ?: "No se pudo crear la cuenta."
                    )
                }
            }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Ingresa tu correo para recuperar la contraseña.",
                successMessage = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                _uiState.value = if (task.isSuccessful) {
                    _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Te enviamos un enlace para recuperar tu contraseña."
                    )
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.localizedMessage
                            ?: "No se pudo enviar el correo de recuperación."
                    )
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        val errorMessage = when {
            email.isBlank() -> "Ingresa tu correo."
            password.length < MIN_PASSWORD_LENGTH -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            errorMessage = errorMessage,
            successMessage = null
        )

        return errorMessage == null
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    private fun FirebaseUser.toUserSession(): UserSession {
        return UserSession(
            uid = uid,
            email = email.orEmpty()
        )
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
