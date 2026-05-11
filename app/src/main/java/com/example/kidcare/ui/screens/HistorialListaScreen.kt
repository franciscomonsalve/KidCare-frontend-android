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
import com.example.kidcare.data.model.HistorialResponse
import com.example.kidcare.navigation.Rutas

@Composable
fun HistorialListaScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    val idMenor = menorId.toIntOrNull() ?: 0

    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val historiales = remember { mutableStateListOf<HistorialResponse>() }

    LaunchedEffect(menorId) {
        if (idMenor <= 0) return@LaunchedEffect
        cargando = true
        val result = runCatching { RetrofitClient.historialApi.listarHistorial(idMenor) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                historiales.clear()
                historiales.addAll(resp.body() ?: emptyList())
            } else { errorMsg = "No se pudo cargar el historial." }
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    Scaffold(
        floatingActionButton = {
            if (idMenor > 0) {
                FloatingActionButton(
                    onClick = { navController.navigate(Rutas.generarHistorial(idMenor)) },
                    containerColor = azulKidCare
                ) { Text("+ Generar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            }
        }
    ) { padding ->
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
                        Text("Historial médico", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, modifier = Modifier.padding(start = 8.dp))
                        Text("${historiales.size} resúmenes generados", fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
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
            } else if (historiales.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📄", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Aún no hay resúmenes generados.", fontSize = 14.sp, color = Color(0xFF9CA3AF))
                            Text("Toca el botón + para generar el primero.", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                        }
                    }
                }
            } else {
                item {
                    Text("RESÚMENES", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
                }

                items(historiales) { h ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .background(Color.White, shape = RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(h.fecha.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7280))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (h.generadoPorIA) Color(0xFFEFF6FF) else Color(0xFFF3F4F6),
                                            shape = RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(if (h.generadoPorIA) "IA" else "Manual", fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (h.generadoPorIA) azulKidCare else Color(0xFF6B7280))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val resumenTexto = h.resumen.orEmpty()
                            Text(resumenTexto.take(200) + if (resumenTexto.length > 200) "..." else "",
                                fontSize = 13.sp, color = Color(0xFF0F172A), lineHeight = 20.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
