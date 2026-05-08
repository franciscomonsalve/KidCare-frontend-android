package com.example.kidcare.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.kidcare.navigation.Rutas

@Composable
fun VincularMenorScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var codigoVinculacion by remember { mutableStateOf("") }
    var vinculado         by remember { mutableStateOf(false) }
    var error             by remember { mutableStateOf(false) }

    // Datos del menor encontrado (simulado)
    val menorEncontrado = remember { mutableStateOf<Triple<String, String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
            .verticalScroll(rememberScrollState())
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
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
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
                    text = "Vincular menor existente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Ingresa el código de vinculación del menor",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (vinculado) {
                // Pantalla de éxito
                Spacer(modifier = Modifier.height(24.dp))
                Text("✅", fontSize = 64.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Menor vinculado",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                menorEncontrado.value?.let { (nombre, edad, emoji) ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = edad,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.HOME) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) {
                    Text("Ir al inicio", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

            } else {
                // Formulario
                Spacer(modifier = Modifier.height(16.dp))

                Text("🔗", fontSize = 56.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ingresa el código de vinculación que el tutor principal te compartió para agregar al menor a tu cuenta.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Info box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("ℹ️", fontSize = 16.sp)
                        Text(
                            text = "El código de vinculación lo genera el tutor principal desde su perfil. Tiene una validez de 24 horas.",
                            fontSize = 12.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "CÓDIGO DE VINCULACIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = codigoVinculacion,
                    onValueChange = {
                        codigoVinculacion = it.uppercase()
                        error = false
                        // Simular búsqueda cuando el código tiene 8 caracteres
                        if (it.length == 8) {
                            menorEncontrado.value = Triple("Sofía", "5 años", "👧")
                        } else {
                            menorEncontrado.value = null
                        }
                    },
                    placeholder = { Text("Ej: KC-AB1234", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = error,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulKidCare,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        errorBorderColor = Color(0xFFDC2626)
                    )
                )

                if (error) {
                    Text(
                        text = "⚠ Código inválido o expirado",
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }

                // Vista previa del menor encontrado
                menorEncontrado.value?.let { (nombre, edad, emoji) ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(emoji, fontSize = 36.sp)
                            Column {
                                Text(
                                    text = "Menor encontrado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                                Text(
                                    text = nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = edad,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (codigoVinculacion.length < 6) {
                            error = true
                        } else {
                            menorEncontrado.value = Triple("Sofía", "5 años", "👧")
                            vinculado = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled = codigoVinculacion.length >= 6
                ) {
                    Text("Vincular menor", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}