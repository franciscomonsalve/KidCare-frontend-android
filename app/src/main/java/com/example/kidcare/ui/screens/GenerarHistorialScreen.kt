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
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.GenerarHistorialRequest
import com.example.kidcare.data.model.HistorialResponse
import com.example.kidcare.data.model.InteraccionResponse
import kotlinx.coroutines.launch

@Composable
fun GenerarHistorialScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    val idMenor = menorId.toIntOrNull() ?: 0

    var cargandoInteracciones by remember { mutableStateOf(false) }
    var cargandoGenerar       by remember { mutableStateOf(false) }
    var errorMsg              by remember { mutableStateOf("") }
    var resumenGenerado       by remember { mutableStateOf<HistorialResponse?>(null) }

    val interacciones = remember { mutableStateListOf<InteraccionResponse>() }
    var seleccionadas by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(menorId) {
        if (idMenor <= 0) return@LaunchedEffect
        cargandoInteracciones = true
        val result = runCatching { RetrofitClient.chatbotApi.listarInteracciones(idMenor) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                interacciones.clear()
                interacciones.addAll(resp.body() ?: emptyList())
            }
        }.onFailure { errorMsg = "Error al cargar interacciones." }
        cargandoInteracciones = false
    }

    // Dialog resumen generado
    resumenGenerado?.let { historial ->
        AlertDialog(
            onDismissRequest = { resumenGenerado = null },
            title = { Text("Resumen generado", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Fecha: ${historial.fecha}", fontSize = 12.sp, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(historial.resumen.orEmpty(), fontSize = 14.sp, color = Color(0xFF0F172A), lineHeight = 22.sp)
                }
            },
            confirmButton = {
                Button(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)) {
                    Text("Finalizar")
                }
            }
        )
    }

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB)).padding(padding)) {

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
                        Text("Generar resumen médico", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, modifier = Modifier.padding(start = 8.dp))
                        Text("Selecciona las observaciones a incluir", fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                    }
                }
            }

            if (cargandoInteracciones) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = azulKidCare)
                    }
                }
            } else {
                item {
                    Text("SELECCIONA OBSERVACIONES", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
                }

                items(interacciones) { obs ->
                    val obsId = obs.id.orEmpty()
                    val sel = obsId.isNotEmpty() && seleccionadas.contains(obsId)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(if (sel) Color(0xFFEFF6FF) else Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Checkbox(
                                checked = sel,
                                onCheckedChange = { checked ->
                                    if (obsId.isNotEmpty()) {
                                        seleccionadas = if (checked) seleccionadas + obsId else seleccionadas - obsId
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = azulKidCare)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(obs.fecha.orEmpty(), fontSize = 11.sp, color = Color(0xFF6B7280))
                                val obsText = obs.observaciones.orEmpty()
                                Text(obsText.take(120) + if (obsText.length > 120) "..." else "",
                                    fontSize = 13.sp, color = Color(0xFF0F172A))
                            }
                        }
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    item { Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626), modifier = Modifier.padding(16.dp)) }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (seleccionadas.isEmpty()) { errorMsg = "Selecciona al menos una observación."; return@Button }
                            scope.launch {
                                cargandoGenerar = true
                                errorMsg = ""
                                val result = runCatching {
                                    RetrofitClient.historialApi.generarHistorial(
                                        GenerarHistorialRequest(idMenor = idMenor, idInteracciones = seleccionadas.filter { it.isNotEmpty() }.toList())
                                    )
                                }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) resumenGenerado = resp.body()
                                    else errorMsg = "No se pudo generar el resumen."
                                }.onFailure { errorMsg = "Error de conexión." }
                                cargandoGenerar = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = seleccionadas.isNotEmpty() && !cargandoGenerar
                    ) {
                        if (cargandoGenerar) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("🤖 Generar resumen con IA", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
