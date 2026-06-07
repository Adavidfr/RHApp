package com.rhapp.presentation.ui.admin.nominas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhapp.domain.model.Nomina
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NominaDetailSheet(
    nomina:    Nomina,
    onDismiss: () -> Unit,
) {
    val color = nominaEstadoColor(nomina.estado)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Cabecera
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column {
                    Text("Detalle de nómina", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(nomina.empleadoNombre, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(
                        "Período: ${nomina.mes.toString().padStart(2, '0')}/${nomina.anio}",
                        style = MaterialTheme.typography.bodySmall, color = TextFaint,
                    )
                }
                Surface(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small) {
                    Text(
                        nomina.estado.label, color = color, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            HorizontalDivider(color = Border)

            // Desglose financiero
            Text("Desglose salarial", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)

            NominaFinancialRow("Salario base",         nomina.salarioBase,           isIncome = true)
            NominaFinancialRow("Bono",                 nomina.bono,                  isIncome = true)
            NominaFinancialRow("Descuento aportes",    -nomina.descuentoAportes,     isIncome = false)
            NominaFinancialRow("Descuento impuestos",  -nomina.descuentoImpuestos,   isIncome = false)

            HorizontalDivider(color = Border, thickness = 0.5.dp)

            // Total
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Salario neto", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "S/ ${"%.2f".format(nomina.salarioNeto)}",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = NominaPagada,
                    fontSize   = 22.sp,
                )
            }

            // Fechas
            if (nomina.fechaGeneracion.isNotBlank() || nomina.fechaPago != null) {
                HorizontalDivider(color = Border, thickness = 0.5.dp)
                Text("Fechas", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                if (nomina.fechaGeneracion.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Generada", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(nomina.fechaGeneracion.take(10), style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                nomina.fechaPago?.let { fp ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pagada", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(fp.take(10), style = MaterialTheme.typography.bodySmall, color = NominaPagada, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Button(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = TextSecondary),
                shape    = MaterialTheme.shapes.medium,
            ) { Text("Cerrar") }
        }
    }
}

@Composable
private fun NominaFinancialRow(label: String, amount: Double, isIncome: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text       = if (isIncome) "+ S/ ${"%.2f".format(amount)}" else "- S/ ${"%.2f".format(-amount)}",
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = if (isIncome) NominaPagada else Error,
        )
    }
}
