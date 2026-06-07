package com.rhapp.presentation.ui.admin.nominas

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
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.NominaPayload
import com.rhapp.presentation.components.RHTextField
import com.rhapp.presentation.viewmodel.NominaFormState
import com.rhapp.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NominaFormSheet(
    empleados:  List<Empleado>,
    formState:  NominaFormState,
    onSave:     (NominaPayload) -> Unit,
    onDismiss:  () -> Unit,
) {
    val currentYear  = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    var empleadoSel      by remember { mutableStateOf<Empleado?>(null) }
    var empExpanded      by remember { mutableStateOf(false) }
    var mesText          by remember { mutableStateOf(currentMonth.toString()) }
    var anioText         by remember { mutableStateOf(currentYear.toString()) }
    var salarioBase      by remember { mutableStateOf("") }
    var bono             by remember { mutableStateOf("0.0") }
    var descAportes      by remember { mutableStateOf("0.0") }
    var descImpuestos    by remember { mutableStateOf("0.0") }

    LaunchedEffect(formState) {
        if (formState is NominaFormState.Success) onDismiss()
    }

    val isSaving     = formState is NominaFormState.Saving
    val mesVal       = mesText.toIntOrNull()
    val anioVal      = anioText.toIntOrNull()
    val salarioVal   = salarioBase.toDoubleOrNull()
    val bonoVal      = bono.toDoubleOrNull() ?: 0.0
    val descAVal     = descAportes.toDoubleOrNull() ?: 0.0
    val descIVal     = descImpuestos.toDoubleOrNull() ?: 0.0
    val mesError     = mesText.isNotEmpty() && (mesVal == null || mesVal !in 1..12)
    val anioError    = anioText.isNotEmpty() && (anioVal == null || anioVal < 2000)
    val salarioError = salarioBase.isNotEmpty() && salarioVal == null

    // Vista previa del salario neto
    val salarioNeto  = (salarioVal ?: 0.0) + bonoVal - descAVal - descIVal

    val canSave = empleadoSel != null && !mesError && !anioError && !salarioError &&
                  mesVal != null && anioVal != null && salarioVal != null && !isSaving

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
            Text("Generar nómina", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)

            if (formState is NominaFormState.Error) {
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
                    isError       = empleadoSel == null && salarioBase.isNotBlank(),
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
                                        Text("# ${e.numeroEmpleado} · ${e.puestoTitulo}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = { empleadoSel = e; empExpanded = false },
                            )
                        }
                    }
                }
            }

            // Mes y Año
            Text("Período", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RHTextField(
                    value = mesText, onValueChange = { mesText = it },
                    label = "Mes (1-12) *", placeholder = "ej. 6",
                    isError = mesError, errorMessage = "Mes inválido (1-12)",
                    keyboardType = KeyboardType.Number, enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                )
                RHTextField(
                    value = anioText, onValueChange = { anioText = it },
                    label = "Año *", placeholder = "ej. $currentYear",
                    isError = anioError, errorMessage = "Año inválido",
                    keyboardType = KeyboardType.Number, enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                )
            }

            // Datos financieros
            Text("Detalle salarial", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)
            RHTextField(
                value = salarioBase, onValueChange = { salarioBase = it },
                label = "Salario base *", placeholder = "ej. 25000.00",
                isError = salarioError, errorMessage = "Número inválido",
                keyboardType = KeyboardType.Decimal, enabled = !isSaving,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RHTextField(
                    value = bono, onValueChange = { bono = it },
                    label = "Bono", placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal, enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                )
                RHTextField(
                    value = descAportes, onValueChange = { descAportes = it },
                    label = "Desc. aportes", placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal, enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                )
            }
            RHTextField(
                value = descImpuestos, onValueChange = { descImpuestos = it },
                label = "Desc. impuestos", placeholder = "0.00",
                keyboardType = KeyboardType.Decimal, enabled = !isSaving,
            )

            // Vista previa del neto
            if (salarioVal != null) {
                Surface(color = Accent.copy(alpha = 0.08f), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Salario neto estimado", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(
                            "S/ ${"%.2f".format(salarioNeto)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (salarioNeto >= 0) NominaPagada else Error,
                        )
                    }
                }
            }

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
                        onSave(NominaPayload(
                            empleadoId          = empleadoSel!!.id,
                            mes                 = mesVal!!,
                            anio                = anioVal!!,
                            salarioBase         = salarioVal!!,
                            bono                = bonoVal,
                            descuentoAportes    = descAVal,
                            descuentoImpuestos  = descIVal,
                        ))
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
                    Text(if (isSaving) "Generando..." else "Generar nómina", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
