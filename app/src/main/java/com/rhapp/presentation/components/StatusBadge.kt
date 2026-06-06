package com.rhapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.theme.*

fun nominaStatusColor(status: EstadoNomina): Color = when (status) {
    EstadoNomina.GENERADA -> NominaGenerada
    EstadoNomina.REVISADA -> NominaRevisada
    EstadoNomina.PAGADA   -> NominaPagada
    EstadoNomina.ANULADA  -> NominaAnulada
}

@Composable
fun StatusBadge(status: EstadoNomina, modifier: Modifier = Modifier) {
    val color = nominaStatusColor(status)
    Row(
        modifier          = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(50)),
        )
        Text(
            text       = status.label,
            color      = color,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
        )
    }
}
