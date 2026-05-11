package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.GenerarTokenRequest
import com.example.kidcare.data.model.TokenMedicoResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EnlaceScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    val idMenor = menorId.toIntOrNull() ?: 0

    var cargando          by remember { mutableStateOf(false) }
    var errorMsg          by remember { mutableStateOf("") }
    var tokenGenerado     by remember { mutableStateOf<TokenMedicoResponse?>(null) }
    var segundos          by remember { mutableStateOf(20 * 60) } // 20 minutos
    var canalSeleccionado by remember { mutableStateOf("QR") }

    val enlaceGenerado = tokenGenerado != null

    // Countdown timer
    LaunchedEffect(enlaceGenerado) {
        if (enlaceGenerado) {
            while (segundos > 0) {
                delay(1000)
                segundos--
            }
        }
    }

    val minutos = segundos / 60
    val segs    = segundos % 60
    val timerDisplay = "${String.format("%02d", minutos)}:${String.format("%02d", segs)}"
    val expirado = segundos <= 0

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Text(if (enlaceGenerado) "Token generado" else "Compartir con médico",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.padding(start = 8.dp))
                Text(if (enlaceGenerado) "Válido por 20 minutos" else "Enlace temporal seguro",
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }

        if (!enlaceGenerado) {
            // Vista antes de generar
            Column(modifier = Modifier.padding(20.dp)) {

                Box(modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(14.dp)).padding(16.dp)) {
                    Column {
                        Text("¿Cómo compartir?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("El médico podrá ver la bitácora del menor por 20 minutos. El sistema verifica la proximidad geográfica.",
                            fontSize = 13.sp, color = Color(0xFF6B7280), lineHeight = 20.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("CANAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 10.dp))

                // Opción QR
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "QR") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "QR") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center) { Text("📱", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Código QR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("El médico escanea desde su celular", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                        if (canalSeleccionado == "QR") {
                            Box(modifier = Modifier.size(20.dp).background(azulKidCare, shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center) {
                                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opción Email
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "EMAIL") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "EMAIL") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center) { Text("✉️", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Correo electrónico", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("Enviar al correo del médico", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            cargando = true
                            errorMsg = ""
                            val result = runCatching {
                                RetrofitClient.accesoApi.generarTokenMedico(
                                    GenerarTokenRequest(
                                        idMenor = idMenor,
                                        latitud = 0.0,
                                        longitud = 0.0
                                    )
                                )
                            }
                            result.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    tokenGenerado = resp.body()
                                    segundos = 20 * 60
                                } else {
                                    errorMsg = "No se pudo generar el token. Intenta nuevamente."
                                }
                            }.onFailure { errorMsg = "Error de conexión." }
                            cargando = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled = !cargando && idMenor > 0
                ) {
                    if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Generar enlace temporal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Solo puede existir un enlace activo por hijo a la vez.", fontSize = 12.sp,
                    color = Color(0xFF9CA3AF), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

        } else {
            // Vista después de generar
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                Box(modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(18.dp)).padding(24.dp),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Muestra este token al médico", fontSize = 13.sp, color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 16.dp))

                        // Token como texto (QR real requeriría dependencia adicional)
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color(0xFFF2F5FB), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), shape = RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tokenGenerado?.token ?: "", fontSize = 14.sp, fontFamily = FontFamily.Monospace,
                                color = Color(0xFF374151), textAlign = TextAlign.Center, lineHeight = 22.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(timerDisplay, fontSize = 36.sp, fontWeight = FontWeight.Bold,
                            color = if (expirado) Color(0xFFDC2626) else azulKidCare,
                            fontFamily = FontFamily.Monospace)
                        Text(if (expirado) "Enlace expirado" else "tiempo restante",
                            fontSize = 12.sp, color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(14.dp)).padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estado", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Box(modifier = Modifier.background(
                                if (expirado) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                                Text(if (expirado) "EXPIRADO" else "ACTIVO", fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (expirado) Color(0xFFDC2626) else Color(0xFFD97706))
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Canal", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Text("QR", fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val token = tokenGenerado?.token
                        if (token.isNullOrEmpty()) { navController.popBackStack(); return@OutlinedButton }
                        scope.launch {
                            runCatching { RetrofitClient.accesoApi.revocarTokenMedico(token) }
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) { Text("Invalidar enlace", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
