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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CambiarContrasenaScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var contrasenaActual    by remember { mutableStateOf("") }
    var contrasenaNueva     by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var verActual    by remember { mutableStateOf(false) }
    var verNueva     by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    var cambioExitoso by remember { mutableStateOf(false) }

    // Validaciones
    val contrasenaValida = contrasenaNueva.length >= 8
    val contrasenasCoinciden = contrasenaNueva == confirmarContrasena
    val formularioValido = contrasenaActual.isNotBlank() &&
            contrasenaValida &&
            contrasenasCoinciden

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
                    text = "Cambiar contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Actualiza tu contraseña de acceso",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            if (cambioExitoso) {
                // Pantalla de éxito
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔐", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Contraseña actualizada",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                        Text(
                            text = "Tu contraseña fue cambiada exitosamente.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF059669)
                            )
                        ) {
                            Text("Volver al perfil", fontWeight = FontWeight.Bold)
                        }
                    }
                }

            } else {
                // Formulario
                // Contraseña actual
                Text(
                    text = "CONTRASEÑA ACTUAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = contrasenaActual,
                    onValueChange = { contrasenaActual = it },
                    placeholder = { Text("••••••••", color = Color(0xFF9CA3AF)) },
                    visualTransformation = if (verActual) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { verActual = !verActual }) {
                            Text(if (verActual) "🙈" else "👁", fontSize = 18.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulKidCare,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Nueva contraseña
                Text(
                    text = "NUEVA CONTRASEÑA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = contrasenaNueva,
                    onValueChange = { contrasenaNueva = it },
                    placeholder = { Text("Mínimo 8 caracteres", color = Color(0xFF9CA3AF)) },
                    visualTransformation = if (verNueva) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { verNueva = !verNueva }) {
                            Text(if (verNueva) "🙈" else "👁", fontSize = 18.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulKidCare,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                // Indicador de seguridad
                if (contrasenaNueva.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            contrasenaNueva.length >= 8,
                            contrasenaNueva.any { it.isUpperCase() },
                            contrasenaNueva.any { it.isDigit() }
                        ).forEachIndexed { index, cumple ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .background(
                                        if (cumple) Color(0xFF059669) else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                    Text(
                        text = when {
                            contrasenaNueva.length < 8 -> "Mínimo 8 caracteres"
                            !contrasenaNueva.any { it.isUpperCase() } -> "Agrega una mayúscula"
                            !contrasenaNueva.any { it.isDigit() } -> "Agrega un número"
                            else -> "✓ Contraseña segura"
                        },
                        fontSize = 11.sp,
                        color = if (contrasenaValida) Color(0xFF059669) else Color(0xFF9CA3AF),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confirmar contraseña
                Text(
                    text = "CONFIRMAR CONTRASEÑA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    placeholder = { Text("Repite la nueva contraseña", color = Color(0xFF9CA3AF)) },
                    visualTransformation = if (verConfirmar) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { verConfirmar = !verConfirmar }) {
                            Text(if (verConfirmar) "🙈" else "👁", fontSize = 18.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (!contrasenasCoinciden && confirmarContrasena.isNotBlank())
                            Color(0xFFDC2626) else azulKidCare,
                        unfocusedBorderColor = if (!contrasenasCoinciden && confirmarContrasena.isNotBlank())
                            Color(0xFFDC2626) else Color(0xFFE5E7EB)
                    )
                )

                // Error contraseñas no coinciden
                if (!contrasenasCoinciden && confirmarContrasena.isNotBlank()) {
                    Text(
                        text = "⚠ Las contraseñas no coinciden",
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { cambioExitoso = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled = formularioValido
                ) {
                    Text("Actualizar contraseña", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}