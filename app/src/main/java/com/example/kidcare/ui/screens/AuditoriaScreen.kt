package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.AuditoriaLocal
import com.example.kidcare.data.AuditoriaEntry
import com.example.kidcare.ui.theme.campoColores

@Composable
fun AuditoriaScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var filtroCambio  by remember { mutableStateOf("") }
    var filtroEntidad by remember { mutableStateOf("") }

    val registros by remember(filtroCambio, filtroEntidad) {
        derivedStateOf { AuditoriaLocal.filtrar(filtroCambio, filtroEntidad) }
    }

    fun colorPorAccion(accion: String): Color = when {
        accion.contains("HABILITAR", ignoreCase = true) &&
            !accion.contains("DES", ignoreCase = true) -> Color(0xFF059669)
        accion.contains("DESHABILITAR", ignoreCase = true) -> Color(0xFFDC2626)
        accion.contains("ELIMINAR", ignoreCase = true)     -> Color(0xFFDC2626)
        accion.contains("ROL", ignoreCase = true)          -> azulKidCare
        accion.contains("CREAR", ignoreCase = true)        -> Color(0xFF059669)
        accion.contains("VINCULAR", ignoreCase = true)     -> Color(0xFF0891B2)
        else                                               -> Color(0xFF6B7280)
    }

    fun fondoPorAccion(accion: String): Color = when {
        accion.contains("HABILITAR", ignoreCase = true) &&
            !accion.contains("DES", ignoreCase = true) -> Color(0xFFDCFCE7)
        accion.contains("DESHABILITAR", ignoreCase = true) -> Color(0xFFFEE2E2)
        accion.contains("ELIMINAR", ignoreCase = true)     -> Color(0xFFFEE2E2)
        accion.contains("ROL", ignoreCase = true)          -> Color(0xFFEFF6FF)
        accion.contains("CREAR", ignoreCase = true)        -> Color(0xFFDCFCE7)
        accion.contains("VINCULAR", ignoreCase = true)     -> Color(0xFFE0F2FE)
        else                                               -> Color(0xFFF3F4F6)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                    .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                    ) { Text("← Volver", fontSize = 14.sp) }
                    Text("Auditoría de sesión", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("${registros.size} acciones registradas", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        item {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                Text("FILTROS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(bottom = 10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filtroCambio,
                        onValueChange = { filtroCambio = it },
                        placeholder = { Text("Acción (CREAR, EDITAR…)", fontSize = 11.sp, color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = campoColores()
                    )
                    OutlinedTextField(
                        value = filtroEntidad,
                        onValueChange = { filtroEntidad = it },
                        placeholder = { Text("Entidad (USUARIO…)", fontSize = 11.sp, color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = campoColores()
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Muestra las acciones realizadas durante esta sesión.",
                    fontSize = 11.sp, color = Color(0xFF6B7280)
                )
            }
        }

        item {
            Text("REGISTROS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
        }

        if (registros.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Sin acciones registradas en esta sesión.", fontSize = 14.sp, color = Color(0xFF6B7280))
                }
            }
        }

        items(registros) { entry ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .background(fondoPorAccion(entry.accion), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(entry.accion, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = colorPorAccion(entry.accion))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${entry.entidad} — ${entry.descripcion}",
                            fontSize = 12.sp, color = Color(0xFF374151), fontWeight = FontWeight.Medium)
                        Text(entry.fecha, fontSize = 11.sp, color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
