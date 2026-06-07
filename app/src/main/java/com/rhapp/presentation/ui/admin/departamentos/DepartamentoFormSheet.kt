package com.rhapp.presentation.ui.admin.departamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.DepartamentoFormState
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartamentoFormSheet(
    initial:   Departamento?,
    formState: DepartamentoFormState,
    onSave:    (DepartamentoPayload) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEdit = initial != null

    var codigo           by remember { mutableStateOf(initial?.codigo           ?: "") }
    var nombre           by remember { mutableStateOf(initial?.nombre           ?: "") }
    var descripcion      by remember { mutableStateOf(initial?.descripcion      ?: "") }
    var presupuestoAnual by remember { mutableStateOf(initial?.presupuestoAnual?.toString() ?: "") }
    var jefeIdText       by remember { mutableStateOf(initial?.jefeId?.toString()          ?: "") }
    var activo           by remember { mutableStateOf(initial?.activo           ?: true) }

    // Cerrar automáticamente al éxito
    LaunchedEffect(formState) {
        if (formState is DepartamentoFormState.Success) onDismiss()
    }

    val isSaving       = formState is DepartamentoFormState.Saving
    val codigoError    = codigo.isNotEmpty()  && codigo.isBlank()
    val nombreError    = nombre.isNotEmpty()  && nombre.length < 2
    val presupError    = presupuestoAnual.isNotEmpty() && presupuestoAnual.toDoubleOrNull() == null
    val jefeIdParsed   = jefeIdText.trimEnd().toIntOrNull()
    val jefeIdError    = jefeIdText.isNotEmpty() && jefeIdParsed == null

    val canSave = codigo.isNotBlank() &&
                  nombre.length >= 2 &&
                  !presupError &&
                  !jefeIdError &&
                  !isSaving

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor   = Surface,
        dragHandle = {
            Box(
                modifier         = Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.size(40.dp, 4.dp),
                    color    = Border,
                    shape    = MaterialTheme.shapes.extraSmall,
                ) {}
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Título
            Text(
                text       = if (isEdit) "Editar: ${initial?.nombre}" else "Nuevo departamento",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )

            // Error del formulario
            if (formState is DepartamentoFormState.Error) {
                Surface(
                    color    = Error.copy(alpha = 0.1f),
                    shape    = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text     = formState.message,
                        color    = Error,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Código
            RHTextField(
                value         = codigo,
                onValueChange = { codigo = it.uppercase() },
                label         = "Código *",
                placeholder   = "ej. TI-001",
                isError       = codigoError,
                errorMessage  = "El código no puede estar vacío",
                enabled       = !isSaving,
            )

            // Nombre
            RHTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = "Nombre *",
                placeholder   = "ej. Tecnología de la Información",
                isError       = nombreError,
                errorMessage  = "Mínimo 2 caracteres",
                enabled       = !isSaving,
            )

            // Descripción (multiline)
            OutlinedTextField(
                value         = descripcion,
                onValueChange = { descripcion = it },
                label         = { Text("Descripción") },
                placeholder   = { Text("Descripción del departamento", color = TextFaint) },
                minLines      = 2,
                maxLines      = 4,
                enabled       = !isSaving,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Accent,
                    focusedLabelColor    = Accent,
                    cursorColor          = Accent,
                    unfocusedBorderColor = Border,
                    unfocusedLabelColor  = TextSecondary,
                ),
            )

            // Presupuesto anual
            RHTextField(
                value         = presupuestoAnual,
                onValueChange = { presupuestoAnual = it },
                label         = "Presupuesto anual",
                placeholder   = "ej. 150000.00",
                isError       = presupError,
                errorMessage  = "Ingresa un número válido",
                keyboardType  = KeyboardType.Decimal,
                enabled       = !isSaving,
            )

            // ID del jefe (empleado)
            RHTextField(
                value         = jefeIdText,
                onValueChange = { jefeIdText = it },
                label         = "ID del jefe (opcional)",
                placeholder   = "ID del empleado jefe",
                isError       = jefeIdError,
                errorMessage  = "Debe ser un número entero",
                keyboardType  = KeyboardType.Number,
                enabled       = !isSaving,
            )
            if (!jefeIdError && initial?.jefeNombre != null) {
                Text(
                    text  = "Jefe actual: ${initial.jefeNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            // Toggle activo
            Surface(
                color    = Surface2,
                shape    = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            "Departamento activo",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Text(
                            "Visible y operativo en el sistema",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Switch(
                        checked         = activo,
                        onCheckedChange = { activo = it },
                        enabled         = !isSaving,
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor    = AccentOnDark,
                            checkedTrackColor    = Accent,
                            uncheckedThumbColor  = TextSecondary,
                            uncheckedTrackColor  = Surface,
                            uncheckedBorderColor = Border,
                        ),
                    )
                }
            }

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick  = { if (!isSaving) onDismiss() },
                    enabled  = !isSaving,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape    = MaterialTheme.shapes.medium,
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        onSave(
                            DepartamentoPayload(
                                codigo           = codigo.trim(),
                                nombre           = nombre.trim(),
                                descripcion      = descripcion.trim(),
                                presupuestoAnual = presupuestoAnual.toDoubleOrNull() ?: 0.0,
                                jefeId           = jefeIdText.trimEnd().toIntOrNull(),
                                activo           = activo,
                            )
                        )
                    },
                    enabled  = canSave,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = Accent,
                        contentColor           = AccentOnDark,
                        disabledContainerColor = Accent.copy(alpha = 0.4f),
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color       = AccentOnDark,
                            modifier    = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text       = if (isSaving) "Guardando..."
                                     else if (isEdit) "Guardar cambios"
                                     else "Crear departamento",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
