package com.rhapp.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.presentation.components.RHButton
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.AuthViewModel
import com.rhapp.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (isStaff: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user.isStaff)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg  = (uiState as? AuthUiState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 100.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Encabezado
            Text(
                text       = "RH App",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Control de Recursos Humanos",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Inicia sesión para continuar",
                color = TextFaint,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(48.dp))

            // Tarjeta del formulario
            Surface(
                shape          = MaterialTheme.shapes.large,
                color          = Surface,
                tonalElevation = 0.dp,
                modifier       = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Banner de error
                    if (errorMsg != null) {
                        Surface(
                            color    = Error.copy(alpha = 0.1f),
                            shape    = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text     = errorMsg,
                                color    = Error,
                                style    = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    RHTextField(
                        value         = username,
                        onValueChange = { username = it; viewModel.clearError() },
                        label         = "Usuario",
                        placeholder   = "nombre de usuario",
                        enabled       = !isLoading,
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(16.dp))

                    RHTextField(
                        value         = password,
                        onValueChange = { password = it; viewModel.clearError() },
                        label         = "Contraseña",
                        placeholder   = "••••••••",
                        isPassword    = true,
                        enabled       = !isLoading,
                        keyboardType  = KeyboardType.Password,
                        imeAction     = ImeAction.Done,
                    )
                    Spacer(Modifier.height(28.dp))

                    RHButton(
                        text      = "Iniciar sesión",
                        onClick   = { viewModel.login(username, password) },
                        isLoading = isLoading,
                        enabled   = username.isNotBlank() && password.isNotBlank(),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Nota informativa — no hay registro público en RRHH
            Surface(
                color    = Surface,
                shape    = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text     = "El acceso es gestionado por el administrador del sistema.",
                    color    = TextFaint,
                    style    = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}