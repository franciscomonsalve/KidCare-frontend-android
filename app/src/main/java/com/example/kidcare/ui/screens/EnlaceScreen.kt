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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

/**
 * Pantalla de generación de enlace/QR para compartir acceso al menor.
 *
 * Permite al tutor generar un enlace o código QR de duración limitada (25 minutos)
 * para compartir con un apoderado o profesional de salud. El canal puede ser
 * QR, enlace directo u otro método configurable. El countdown se gestiona
 * localmente con `delay` dentro de una corrutina.
 *
 * @param navController controlador de navegación
 */
@Composable
fun EnlaceScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var enlaceGenerado by remember { mutableStateOf(false) }
    var segundos       by remember { mutableStateOf(25 * 60) } // 25 minutos
    var canalSeleccionado by remember { mutableStateOf("QR") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(azulOscuro, azulKidCare)
                    )
                )
                .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("← Volver", fontSize = 14.sp)
                }
                Text(
                    text = if (enlaceGenerado) "QR generado" else "Compartir con médico",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = if (enlaceGenerado) "Válido por 25 minutos · Sofía"
                    else "Enlace temporal seguro · Sofía",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        if (!enlaceGenerado) {
            // Vista antes de generar
            Column(modifier = Modifier.padding(20.dp)) {

                // Info card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "¿Cómo compartir?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "El médico podrá ver la bitácora de Sofía por 25 minutos desde la consulta.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECCIONA EL CANAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Opción QR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "QR") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "QR") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📱", fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Código QR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "El médico escanea desde su celular",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        if (canalSeleccionado == "QR") {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(azulKidCare, shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opción Email
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "EMAIL") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "EMAIL") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✉️", fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Correo electrónico",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Enviar al correo del médico",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { enlaceGenerado = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) {
                    Text("Generar enlace temporal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Solo puede existir un enlace activo por hijo a la vez.",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        } else {
            // Vista después de generar — QR + timer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // QR simulado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(18.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Muestra este código al médico",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // QR placeholder
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .background(Color(0xFFF2F5FB), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▓▒░▓▒░\n▒░▓▒░▓\n░▓▒░▓▒\n▓▒░▓▒░\n▒░▓▒░▓",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                color = Color(0xFF374151)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Timer
                        Text(
                            text = timerDisplay,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (expirado) Color(0xFFDC2626) else azulKidCare,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Text(
                            text = if (expirado) "Enlace expirado" else "tiempo restante",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info del enlace
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estado", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (expirado) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (expirado) "EXPIRADO" else "PENDIENTE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (expirado) Color(0xFFDC2626) else Color(0xFFD97706)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Canal", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Text("QR", fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón invalidar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFCA5A5), Color(0xFFFCA5A5))
                        )
                    )
                ) {
                    Text("Invalidar enlace", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}