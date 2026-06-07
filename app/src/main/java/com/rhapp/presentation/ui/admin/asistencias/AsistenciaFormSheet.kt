package com.rhapp.presentation.ui.admin.asistencias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EstadoAsistencia
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.AsistenciaFormState
import com.rhapp.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciaFormSheet(
    empleados:    List<Empleado>,
    fechaDefault: String,
    formState:    AsistenciaFormState,
    onSave:       (com.rhapp.domain.model.AsistenciaPayload) -> Unit,
    onDismiss:    () -> Unit,
) {
    var empleadoSel     by remember { mutableStateOf<Empleado?>(null) }
    var empExpanded     by remember { mutableStateOf(false) }
    var fecha           by remember { mutableStateOf(fechaDefault) }
    var horaEntrada     by remember { mutableStateOf("") }
    var observaciones   by remember { mutableStateOf("") }

    var estadoSel      by remember { mutableStateOf(EstadoAsistencia.PRESENTE) }
    var estadoExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(formState) {
        if (formState is AsistenciaFormState.Success) onDismiss()
    }

    val isSaving    = formState is AsistenciaFormState.Saving
    val fechaError  = fecha.isNotEmpty() && !Regex("""\d{4}-\d{2}-\d{2}""").matches(fecha)
    val horaError   = horaEntrada.isNotEmpty() && !Regex("""\d{2}:\d{2}""").matches(horaEntrada)
    val canSave     = empleadoSel != null && !fechaError && !horaError && !isSaving

    val estadoColor: @Composable () -> androidx.compose.ui.graphics.Color = {
        when (estadoSel) {
            EstadoAsistencia.PRESENTE          -> StatusActivo
            EstadoAsistencia.AUSENTE           -> Error
            EstadoAsistencia.LICENCIA          -> StatusLicencia
            EstadoAsistencia.RETARDO           -> Warning
            EstadoAsistencia.SALIDA_ANTICIPADA -> TextSecondary
        }
    }

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor   = Surface,
        dragHandle = {
            Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(40.dp, 4.dp), color = Border, shape = MaterialTheme.shapes.extraSmall) {}
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
            Text("Registrar asistencia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)

            if (formState is AsistenciaFormState.Error) {
                Surface(color = Error.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                    Text(formState.message, color = Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                }
            }

            // Dropdown Empleado
            ExposedDropdownMenuBox(expanded = empExpanded, onExpandedChange = { if (!isSaving) empExpanded = it }) {
                OutlinedTextField(
                    value         = empleadoSel?.let { "${it.nombre} ${it.apellido}" } ?: "",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Empleado *") },
                    placeholder   = { Text("Selecciona un empleado", color = TextFaint) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                    isError       = empleadoSel == null && fecha.isNotBlank(),
                    enabled       = !isSaving,
                    modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent, focusedLabelColor = Accent,
                        cursorColor = Accent, unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                    ),
                )
                ExposedDropdownMenu(expanded = empExpanded, onDismissRequest = { empExpanded = false }, containerColor = Surface) {
                    if (empleados.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay empleados activos", color = TextSecondary) }, onClick = {})
                    } else {
                        empleados.forEach { e ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("${e.nombre} ${e.apellido}", color = if (empleadoSel?.id == e.id) Accent else TextPrimary, fontWeight = FontWeight.SemiBold)
                                        Text("# ${e.numeroEmpleado} · ${e.departamentoNombre}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = { empleadoSel = e; empExpanded = false },
                            )
                        }
                    }
                }
            }

            // Fecha y hora
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RHTextField(
                    value = fecha, onValueChange = { fecha = it },
                    label = "Fecha *", placeholder = "YYYY-MM-DD",
                    isError = fechaError, errorMessage = "Formato YYYY-MM-DD",
                    enabled = !isSaving, modifier = Modifier.weight(1f),
                )
                RHTextField(
                    value = horaEntrada, onValueChange = { horaEntrada = it },
                    label = "Hora entrada", placeholder = "HH:MM",
                    isError = horaError, errorMessage = "Formato HH:MM",
                    enabled = !isSaving, modifier = Modifier.weight(1f),
                )
            }

            // Dropdown Estado
            ExposedDropdownMenuBox(
                expanded = estadoExpanded,
                onExpandedChange = { if (!isSaving) estadoExpanded = it },
            ) {
                val color = estadoColor()
                OutlinedTextField(
                    value = estadoSel.label, onValueChange = {},
                    readOnly = true, label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                    enabled = !isSaving,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = color, focusedLabelColor = color,
                        unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                    ),
                )
                ExposedDropdownMenu(expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }, containerColor = Surface) {
                    EstadoAsistencia.entries.forEach { e ->
                        val c = when (e) {
                            EstadoAsistencia.PRESENTE          -> StatusActivo
                            EstadoAsistencia.AUSENTE           -> Error
                            EstadoAsistencia.LICENCIA          -> StatusLicencia
                            EstadoAsistencia.RETARDO           -> Warning
                            EstadoAsistencia.SALIDA_ANTICIPADA -> TextSecondary
                        }
                        DropdownMenuItem(
                            text = { Text(e.label, color = if (estadoSel == e) c else TextPrimary, fontWeight = if (estadoSel == e) FontWeight.Bold else FontWeight.Normal) },
                            onClick = { estadoSel = e; estadoExpanded = false },
                        )
                    }
                }
            }

            // Observaciones
            OutlinedTextField(
                value = observaciones, onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                placeholder = { Text("Notas adicionales...", color = TextFaint) },
                minLines = 2, maxLines = 3, enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent, focusedLabelColor = Accent,
                    cursorColor = Accent, unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                ),
            )

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (!isSaving) onDismiss() }, enabled = !isSaving,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = MaterialTheme.shapes.medium,
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        onSave(
                            com.rhapp.domain.model.AsistenciaPayload(
                                empleadoId  = empleadoSel!!.id,
                                fecha       = fecha,
                                status      = estadoSel.value,
                                horaEntrada = horaEntrada.ifBlank { null },
                                observaciones = observaciones,
                            )
                        )
                    },
                    enabled = canSave,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent, contentColor = AccentOnDark,
                        disabledContainerColor = Accent.copy(alpha = 0.4f),
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (isSaving) { CircularProgressIndicator(color = AccentOnDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                    Text(if (isSaving) "Registrando..." else "Registrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
