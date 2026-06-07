package com.rhapp.presentation.ui.admin.puestos

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
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.PuestoFormState
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuestoFormSheet(
    initial:       Puesto?,
    formState:     PuestoFormState,
    departamentos: List<Departamento>,
    onSave:        (PuestoPayload) -> Unit,
    onDismiss:     () -> Unit,
) {
    val isEdit = initial != null

    var codigo        by remember { mutableStateOf(initial?.codigo       ?: "") }
    var titulo        by remember { mutableStateOf(initial?.titulo       ?: "") }
    var descripcion   by remember { mutableStateOf(initial?.descripcion  ?: "") }
    var requisitos    by remember { mutableStateOf(initial?.requisitos   ?: "") }
    var salarioBase   by remember { mutableStateOf(initial?.salarioBase?.toString()   ?: "") }
    var salarioMaximo by remember { mutableStateOf(initial?.salarioMaximo?.toString() ?: "") }
    var activo        by remember { mutableStateOf(initial?.activo       ?: true) }

    // Departamento seleccionado
    val depInicial = departamentos.firstOrNull { it.id == initial?.departamentoId }
    var depSeleccionado by remember { mutableStateOf(depInicial) }
    var depDropdownOpen by remember { mutableStateOf(false) }

    // Cerrar al éxito
    LaunchedEffect(formState) {
        if (formState is PuestoFormState.Success) onDismiss()
    }

    val isSaving       = formState is PuestoFormState.Saving
    val codigoError    = codigo.isNotEmpty()  && codigo.isBlank()
    val tituloError    = titulo.isNotEmpty()  && titulo.length < 2
    val baseError      = salarioBase.isNotEmpty()   && salarioBase.toDoubleOrNull() == null
    val maximoError    = salarioMaximo.isNotEmpty() && salarioMaximo.toDoubleOrNull() == null
    val rangoError     = !baseError && !maximoError &&
                         salarioBase.isNotBlank() && salarioMaximo.isNotBlank() &&
                         (salarioMaximo.toDouble() < salarioBase.toDouble())

    val canSave = codigo.isNotBlank() &&
                  titulo.length >= 2 &&
                  !baseError && !maximoError && !rangoError &&
                  depSeleccionado != null &&
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
            // Título del sheet
            Text(
                text       = if (isEdit) "Editar: ${initial?.titulo}" else "Nuevo puesto",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )

            // Error del formulario
            if (formState is PuestoFormState.Error) {
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
                placeholder   = "ej. DEV-SR",
                isError       = codigoError,
                errorMessage  = "El código no puede estar vacío",
                enabled       = !isSaving,
            )

            // Título
            RHTextField(
                value         = titulo,
                onValueChange = { titulo = it },
                label         = "Título del cargo *",
                placeholder   = "ej. Desarrollador Senior",
                isError       = tituloError,
                errorMessage  = "Mínimo 2 caracteres",
                enabled       = !isSaving,
            )

            // Descripción (multiline)
            OutlinedTextField(
                value         = descripcion,
                onValueChange = { descripcion = it },
                label         = { Text("Descripción") },
                placeholder   = { Text("Descripción del puesto", color = TextFaint) },
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

            // Requisitos (multiline)
            OutlinedTextField(
                value         = requisitos,
                onValueChange = { requisitos = it },
                label         = { Text("Requisitos") },
                placeholder   = { Text("Ej. 3 años de experiencia, inglés avanzado", color = TextFaint) },
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

            // Rango de salarios (fila)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RHTextField(
                    value         = salarioBase,
                    onValueChange = { salarioBase = it },
                    label         = "Salario base",
                    placeholder   = "0.00",
                    isError       = baseError,
                    errorMessage  = "Número inválido",
                    keyboardType  = KeyboardType.Decimal,
                    enabled       = !isSaving,
                    modifier      = Modifier.weight(1f),
                )
                RHTextField(
                    value         = salarioMaximo,
                    onValueChange = { salarioMaximo = it },
                    label         = "Salario máximo",
                    placeholder   = "0.00",
                    isError       = maximoError || rangoError,
                    errorMessage  = if (rangoError) "Debe ser ≥ base" else "Número inválido",
                    keyboardType  = KeyboardType.Decimal,
                    enabled       = !isSaving,
                    modifier      = Modifier.weight(1f),
                )
            }

            // Dropdown de Departamento
            ExposedDropdownMenuBox(
                expanded          = depDropdownOpen,
                onExpandedChange  = { if (!isSaving) depDropdownOpen = it },
            ) {
                OutlinedTextField(
                    value         = depSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Departamento *") },
                    placeholder   = { Text("Selecciona un departamento", color = TextFaint) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = depDropdownOpen) },
                    enabled       = !isSaving,
                    isError       = depSeleccionado == null && titulo.isNotBlank(),
                    modifier      = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        focusedLabelColor    = Accent,
                        cursorColor          = Accent,
                        unfocusedBorderColor = Border,
                        unfocusedLabelColor  = TextSecondary,
                        errorBorderColor     = Error,
                    ),
                )
                ExposedDropdownMenu(
                    expanded         = depDropdownOpen,
                    onDismissRequest = { depDropdownOpen = false },
                    containerColor   = Surface,
                ) {
                    if (departamentos.isEmpty()) {
                        DropdownMenuItem(
                            text    = { Text("No hay departamentos activos", color = TextSecondary) },
                            onClick = {},
                        )
                    } else {
                        departamentos.forEach { dep ->
                            DropdownMenuItem(
                                text    = { Text(dep.nombre, color = TextPrimary) },
                                onClick = {
                                    depSeleccionado = dep
                                    depDropdownOpen = false
                                },
                                leadingIcon = {
                                    if (depSeleccionado?.id == dep.id) {
                                        Text("✓", color = Accent, fontWeight = FontWeight.Bold)
                                    }
                                },
                            )
                        }
                    }
                }
            }
            if (depSeleccionado == null && titulo.isNotBlank()) {
                Text(
                    "Selecciona un departamento",
                    color = Error,
                    style = MaterialTheme.typography.bodySmall,
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
                            "Puesto activo",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Text(
                            "Disponible para asignar empleados",
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
                            PuestoPayload(
                                codigo         = codigo.trim(),
                                titulo         = titulo.trim(),
                                descripcion    = descripcion.trim(),
                                requisitos     = requisitos.trim(),
                                salarioBase    = salarioBase.toDoubleOrNull()   ?: 0.0,
                                salarioMaximo  = salarioMaximo.toDoubleOrNull() ?: 0.0,
                                departamentoId = depSeleccionado!!.id,
                                activo         = activo,
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
                                     else "Crear puesto",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
