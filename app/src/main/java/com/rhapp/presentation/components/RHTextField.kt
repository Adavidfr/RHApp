package com.rhapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import com.rhapp.theme.*

@Composable
fun RHTextField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    modifier:      Modifier = Modifier,
    placeholder:   String   = "",
    isError:       Boolean  = false,
    errorMessage:  String?  = null,
    isPassword:    Boolean  = false,
    keyboardType:  KeyboardType = KeyboardType.Text,
    imeAction:     ImeAction    = ImeAction.Next,
    enabled:       Boolean      = true,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val visualTransformation = if (isPassword && !passwordVisible)
        PasswordVisualTransformation() else VisualTransformation.None

    Column(modifier = modifier) {
        OutlinedTextField(
            value                = value,
            onValueChange        = onValueChange,
            label                = { Text(label) },
            placeholder          = { Text(placeholder, color = TextFaint) },
            isError              = isError,
            visualTransformation = visualTransformation,
            keyboardOptions      = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction    = imeAction,
            ),
            enabled    = enabled,
            singleLine = true,
            modifier   = Modifier.fillMaxWidth(),
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Accent,
                focusedLabelColor    = Accent,
                cursorColor          = Accent,
                unfocusedBorderColor = Border,
                unfocusedLabelColor  = TextSecondary,
                errorBorderColor     = Error,
                errorLabelColor      = Error,
            ),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = TextSecondary,
                        )
                    }
                }
            } else null,
        )
        if (isError && errorMessage != null) {
            Text(
                text  = errorMessage,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}