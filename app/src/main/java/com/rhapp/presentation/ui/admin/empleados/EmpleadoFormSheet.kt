package com.rhapp.presentation.ui.admin.empleados

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rhapp.domain.model.*
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.EmpleadoFormState
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoFormSheet(
    initial:   Empleado?,
    puestos:   List<Puesto>,
    formState: EmpleadoFormState,
    onSave:    (EmpleadoPayload) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEdit = initial != null

    var nombre          by remember { mutableStateOf(initial?.nombre          ?: "") }
    var apellido        by remember { mutableStateOf(initial?.apellido        ?: "") }
    var numeroEmpleado  by remember { mutableStateOf(initial?.numeroEmpleado  ?: "") }
    var cedula          by remember { mutableStateOf(initial?.cedula          ?: "") }
    var email           by remember { mutableStateOf(initial?.email           ?: "") }
    var telefono        by remember { mutableStateOf(initial?.telefono        ?: "") }
    var direccion       by remember { mutableStateOf(initial?.direccion       ?: "") }
    var fechaNacimiento by remember { mutableStateOf(initial?.fechaNacimiento ?: "") }
    var fechaIngreso    by remember { mutableStateOf("") }   // fecha de ingreso a la empresa
    var salario         by remember { mutableStateOf(initial?.salarioActual?.toString() ?: "") }
    var supervisorText  by remember { mutableStateOf(initial?.supervisorId?.toString() ?: "") }

    // Dropdowns
    var puestoSel       by remember { mutableStateOf(puestos.firstOrNull { it.id == initial?.puestoId }) }
    var puestoExpanded  by remember { mutableStateOf(false) }

    var estadoSel      by remember { mutableStateOf(initial?.estado ?: EstadoEmpleado.ACTIVO) }
    var estadoExpanded by remember { mutableStateOf(false) }

    var contratoSel      by remember { mutableStateOf(initial?.tipoContrato ?: TipoContrato.INDEFINIDO) }
    var contratoExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(formState) {
        if (formState is EmpleadoFormState.Success) onDismiss()
    }

    val isSaving      = formState is EmpleadoFormState.Saving
    val salarioVal    = salario.toDoubleOrNull()
    val supId         = supervisorText.trimEnd().toIntOrNull()
    val salarioError  = salario.isNotEmpty() && salarioVal == null
    val supError      = supervisorText.isNotEmpty() && supId == null
    val fechaNacError = fechaNacimiento.isNotEmpty() &&
                        !Regex("""\d{4}-\d{2}-\d{2}""").matches(fechaNacimiento)
    val fechaIngError = fechaIngreso.isNotEmpty() &&
                        !Regex("""\d{4}-\d{2}-\d{2}""").matches(fechaIngreso)

    // Edad auto-calculada desde fechaNacimiento
    val edadCalculada: Int = remember(fechaNacimiento) {
        runCatching {
            val parts = fechaNacimiento.split("-")
            val anioNac = parts[0].toInt()
            val mesNac  = parts[1].toInt()
            val diaNac  = parts[2].toInt()
            val hoy = java.util.Calendar.getInstance()
            var edad = hoy.get(java.util.Calendar.YEAR) - anioNac
            if (hoy.get(java.util.Calendar.MONTH) + 1 < mesNac ||
                (hoy.get(java.util.Calendar.MONTH) + 1 == mesNac && hoy.get(java.util.Calendar.DAY_OF_MONTH) < diaNac)) {
                edad--
            }
            edad
        }.getOrDefault(0)
    }

    val canSave = nombre.isNotBlank() && apellido.isNotBlank() && cedula.isNotBlank() &&
                  email.isNotBlank() && !salarioError && !supError && !fechaNacError && !fechaIngError &&
                  fechaIngreso.isNotBlank() && puestoSel != null && !isSaving

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
            Text(
                text       = if (isEdit) "Editar: ${initial?.nombre} ${initial?.apellido}" else "Nuevo empleado",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )

            // Error global
            if (formState is EmpleadoFormState.Error) {
                Surface(color = Error.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                    Text(formState.message, color = Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                }
            }

            // ── Datos personales ──────────────────────────────────
            Text("Datos personales", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)

            RHTextField(
                value = numeroEmpleado, onValueChange = { numeroEmpleado = it.uppercase() },
                label = "Código / N° de empleado *", placeholder = "ej. EMP002",
                enabled = !isSaving,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RHTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = "Nombre *", placeholder = "ej. Juan",
                    enabled = !isSaving, modifier = Modifier.weight(1f),
                )
                RHTextField(
                    value = apellido, onValueChange = { apellido = it },
                    label = "Apellido *", placeholder = "ej. Pérez",
                    enabled = !isSaving, modifier = Modifier.weight(1f),
                )
            }

            RHTextField(
                value = cedula, onValueChange = { cedula = it },
                label = "Cédula *", placeholder = "ej. 001-123456-0001X",
                enabled = !isSaving,
            )

            RHTextField(
                value = fechaNacimiento, onValueChange = { fechaNacimiento = it },
                label = "Fecha de nacimiento *", placeholder = "YYYY-MM-DD",
                isError = fechaNacError, errorMessage = "Formato: YYYY-MM-DD",
                enabled = !isSaving,
            )

            // Edad auto-calculada (solo informativo)
            if (edadCalculada > 0) {
                Surface(
                    color    = Surface2,
                    shape    = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Edad calculada", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("$edadCalculada años", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Accent)
                    }
                }
            }

            RHTextField(
                value = fechaIngreso, onValueChange = { fechaIngreso = it },
                label = "Fecha de ingreso *", placeholder = "YYYY-MM-DD",
                isError = fechaIngError, errorMessage = "Formato: YYYY-MM-DD",
                enabled = !isSaving,
            )

            // ── Contacto ──────────────────────────────────────────
            Text("Contacto", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)

            RHTextField(
                value = email, onValueChange = { email = it },
                label = "Email *", placeholder = "empleado@empresa.com",
                keyboardType = KeyboardType.Email, enabled = !isSaving,
            )

            RHTextField(
                value = telefono, onValueChange = { telefono = it },
                label = "Teléfono", placeholder = "ej. +505 8888-8888",
                keyboardType = KeyboardType.Phone, enabled = !isSaving,
            )

            OutlinedTextField(
                value = direccion, onValueChange = { direccion = it },
                label = { Text("Dirección") },
                placeholder = { Text("Dirección del empleado", color = TextFaint) },
                minLines = 2, maxLines = 3, enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent, focusedLabelColor = Accent,
                    cursorColor = Accent, unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                ),
            )

            // ── Datos laborales ───────────────────────────────────
            Text("Datos laborales", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)

            RHTextField(
                value = salario, onValueChange = { salario = it },
                label = "Salario actual", placeholder = "ej. 25000.00",
                isError = salarioError, errorMessage = "Número inválido",
                keyboardType = KeyboardType.Decimal, enabled = !isSaving,
            )

            // Dropdown Puesto
            ExposedDropdownMenuBox(expanded = puestoExpanded, onExpandedChange = { if (!isSaving) puestoExpanded = it }) {
                OutlinedTextField(
                    value = puestoSel?.titulo ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Puesto *") },
                    placeholder = { Text("Selecciona un puesto", color = TextFaint) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = puestoExpanded) },
                    isError = puestoSel == null && nombre.isNotBlank(),
                    enabled = !isSaving,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent, focusedLabelColor = Accent,
                        cursorColor = Accent, unfocusedBorderColor = Border,
                        unfocusedLabelColor = TextSecondary, errorBorderColor = Error,
                    ),
                )
                ExposedDropdownMenu(expanded = puestoExpanded, onDismissRequest = { puestoExpanded = false }, containerColor = Surface) {
                    if (puestos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay puestos activos", color = TextSecondary) }, onClick = {})
                    } else {
                        puestos.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.titulo, color = if (puestoSel?.id == p.id) Accent else TextPrimary) },
                                onClick = { puestoSel = p; puestoExpanded = false },
                                leadingIcon = {
                                    if (puestoSel?.id == p.id) Text("✓", color = Accent, fontWeight = FontWeight.Bold)
                                },
                            )
                        }
                    }
                }
            }

            // Fila: Estado + Tipo Contrato
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Dropdown Estado
                ExposedDropdownMenuBox(
                    expanded = estadoExpanded,
                    onExpandedChange = { if (!isSaving) estadoExpanded = it },
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        value = estadoSel.label, onValueChange = {},
                        readOnly = true, label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                        enabled = !isSaving,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent, focusedLabelColor = Accent,
                            unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                        ),
                    )
                    ExposedDropdownMenu(expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }, containerColor = Surface) {
                        EstadoEmpleado.entries.forEach { e ->
                            DropdownMenuItem(
                                text = { Text(e.label, color = if (estadoSel == e) Accent else TextPrimary) },
                                onClick = { estadoSel = e; estadoExpanded = false },
                            )
                        }
                    }
                }

                // Dropdown Tipo Contrato
                ExposedDropdownMenuBox(
                    expanded = contratoExpanded,
                    onExpandedChange = { if (!isSaving) contratoExpanded = it },
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        value = contratoSel.label, onValueChange = {},
                        readOnly = true, label = { Text("Contrato") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = contratoExpanded) },
                        enabled = !isSaving,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent, focusedLabelColor = Accent,
                            unfocusedBorderColor = Border, unfocusedLabelColor = TextSecondary,
                        ),
                    )
                    ExposedDropdownMenu(expanded = contratoExpanded, onDismissRequest = { contratoExpanded = false }, containerColor = Surface) {
                        TipoContrato.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.label, color = if (contratoSel == t) Accent else TextPrimary) },
                                onClick = { contratoSel = t; contratoExpanded = false },
                            )
                        }
                    }
                }
            }

            // Supervisor (opcional)
            RHTextField(
                value = supervisorText, onValueChange = { supervisorText = it },
                label = "ID del supervisor (opcional)",
                placeholder = "ID del empleado supervisor",
                isError = supError, errorMessage = "Debe ser un número entero",
                keyboardType = KeyboardType.Number, enabled = !isSaving,
            )
            if (!supError && initial?.supervisorNombre != null) {
                Text("Supervisor actual: ${initial.supervisorNombre}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // ── Botones ───────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (!isSaving) onDismiss() },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = MaterialTheme.shapes.medium,
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        onSave(EmpleadoPayload(
                            numeroEmpleado  = numeroEmpleado.trim(),
                            cedula          = cedula.trim(),
                            nombre          = nombre.trim(),
                            apellido        = apellido.trim(),
                            email           = email.trim(),
                            telefono        = telefono.trim(),
                            direccion       = direccion.trim(),
                            fechaNacimiento = fechaNacimiento.trim(),
                            fechaIngreso    = fechaIngreso.trim(),
                            edad            = edadCalculada,
                            salarioActual   = salarioVal ?: 0.0,
                            tipoContrato    = contratoSel.value,
                            estado          = estadoSel.value,
                            puestoId        = puestoSel!!.id,
                            departamentoId  = puestoSel!!.departamentoId,
                            supervisorId    = supId,
                        ))
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
                        CircularProgressIndicator(color = AccentOnDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text       = if (isSaving) "Guardando..." else if (isEdit) "Guardar cambios" else "Crear empleado",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
