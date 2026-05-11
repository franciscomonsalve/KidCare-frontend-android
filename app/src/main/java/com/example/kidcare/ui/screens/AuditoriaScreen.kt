package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.AuditoriaResponse
import kotlinx.coroutines.launch

@Composable
fun AuditoriaScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val registros = remember { mutableStateListOf<AuditoriaResponse>() }

    var filtroCambio  by remember { mutableStateOf("") }
    var filtroEntidad by remember { mutableStateOf("") }

    fun colorPorCambio(cambio: String?): Color = when {
        cambio?.contains("HABILITAR", ignoreCase = true) == true   -> Color(0xFF059669)
        cambio?.contains("DESHABILITAR", ignoreCase = true) == true -> Color(0xFFDC2626)
        cambio?.contains("ROL", ignoreCase = true) == true          -> azulKidCare
        else                                                         -> Color(0xFF6B7280)
    }

    fun fondoPorCambio(cambio: String?): Color = when {
        cambio?.contains("HABILITAR", ignoreCase = true) == true   -> Color(0xFFDCFCE7)
        cambio?.contains("DESHABILITAR", ignoreCase = true) == true -> Color(0xFFFEE2E2)
        cambio?.contains("ROL", ignoreCase = true) == true          -> Color(0xFFEFF6FF)
        else                                                         -> Color(0xFFF3F4F6)
    }

    fun buscar() {
        scope.launch {
            cargando = true
            errorMsg = ""
            val result = runCatching {
                RetrofitClient.api.consultarAuditoria(
                    cambio = filtroCambio.takeIf { it.isNotBlank() },
                    entidad = filtroEntidad.takeIf { it.isNotBlank() }
                )
            }
            result.onSuccess { resp ->
                if (resp.isSuccessful) {
                    registros.clear()
                    registros.addAll(resp.body() ?: emptyList())
                } else { errorMsg = "No se pudo cargar la auditoría." }
            }.onFailure { errorMsg = "Error de conexión." }
            cargando = false
        }
    }

    LaunchedEffect(Unit) { buscar() }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                    .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                    ) { Text("← Volver", fontSize = 14.sp) }
                    Text("Auditoría del sistema", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("${registros.size} registros encontrados", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        // Filtros
        item {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                Text("FILTROS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filtroCambio,
                        onValueChange = { filtroCambio = it },
                        placeholder = { Text("Cambio", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )
                    OutlinedTextField(
                        value = filtroEntidad,
                        onValueChange = { filtroEntidad = it },
                        placeholder = { Text("Entidad", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { buscar() }, modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)) {
                    Text("Buscar", fontSize = 14.sp)
                }
            }
        }

        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item { Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626), modifier = Modifier.padding(16.dp)) }
        } else {
            item {
                Text("REGISTROS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            if (registros.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay registros de auditoría.", fontSize = 14.sp, color = Color(0xFF9CA3AF))
                    }
                }
            }

            items(registros) { registro ->
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
                                .background(fondoPorCambio(registro.cambio), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(registro.cambio.orEmpty(), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = colorPorCambio(registro.cambio))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Admin: ${registro.emailAdmin.orEmpty()}", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text("Entidad: ${registro.entidad.orEmpty()} #${registro.idEntidad ?: "-"}",
                                fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text(registro.fecha.orEmpty(), fontSize = 11.sp, color = Color(0xFF9CA3AF),
                                modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
