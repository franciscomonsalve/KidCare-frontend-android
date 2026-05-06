package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RecuperarContrasenaScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var correo           by remember { mutableStateOf("") }
    var correoError      by remember { mutableStateOf(false) }
    var correoEnviado    by remember { mutableStateOf(false) }
    var codigoIngresado  by remember { mutableStateOf("") }
    var codigoVerificado by remember { mutableStateOf(false) }
    var contrasenaNueva  by remember { mutableStateOf("") }
    var confirmarNueva   by remember { mutableStateOf("") }
    var verNueva         by remember { mutableStateOf(false) }
    var verConfirmar     by remember { mutableStateOf(false) }
    var cambioFinalizado by remember { mutableStateOf(false) }

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
                    text = "Recuperar contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = when {
                        cambioFinalizado -> "Proceso completado"
                        codigoVerificado -> "Paso 3: Nueva contraseña"
                        correoEnviado    -> "Paso 2: Verificar código"
                        else             -> "Paso 1: Ingresa tu correo"
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        // Indicador de pasos
        if (!cambioFinalizado) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair("1", "Correo"),
                    Pair("2", "Código"),
                    Pair("3", "Contraseña")
                ).forEachIndexed { index, (num, label) ->
                    val activo = when (index) {
                        0 -> true
                        1 -> correoEnviado
                        2 -> codigoVerificado
                        else -> false
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    if (activo) azulKidCare else Color(0xFFE5E7EB),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                num,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activo) Color.White else Color(0xFF9CA3AF)
                            )
                        }
                        Text(
                            label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activo) azulKidCare else Color(0xFF9CA3AF)
                        )
                    }
                    if (index < 2) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(Color(0xFFE5E7EB))
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when {

                // ─── PASO FINAL: Éxito ────────────────────────────────────────
                cambioFinalizado -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("✅", fontSize = 64.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Contraseña actualizada",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Ya puedes iniciar sesión con tu nueva contraseña.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                    ) {
                        Text("Ir al inicio de sesión",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ─── PASO 3: Nueva contraseña ─────────────────────────────────
                codigoVerificado -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("🔐", fontSize = 56.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Crea tu nueva contraseña",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("NUEVA CONTRASEÑA", fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("CONFIRMAR CONTRASEÑA", fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = confirmarNueva,
                        onValueChange = { confirmarNueva = it },
                        placeholder = { Text("Repite la contraseña", color = Color(0xFF9CA3AF)) },
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
                            focusedBorderColor = azulKidCare,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    if (confirmarNueva.isNotBlank() && contrasenaNueva != confirmarNueva) {
                        Text("⚠ Las contraseñas no coinciden", fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { cambioFinalizado = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = contrasenaNueva.length >= 8 &&
                                contrasenaNueva == confirmarNueva
                    ) {
                        Text("Actualizar contraseña",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ─── PASO 2: Código de verificación ───────────────────────────
                correoEnviado -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("📧", fontSize = 64.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Revisa tu correo",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Ingresa el código de 6 dígitos\nque enviamos a $correo",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 28.dp)
                    )

                    Text("CÓDIGO DE VERIFICACIÓN", fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = codigoIngresado,
                        onValueChange = { if (it.length <= 6) codigoIngresado = it },
                        placeholder = { Text("000000", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { codigoVerificado = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = codigoIngresado.length == 6
                    ) {
                        Text("Verificar código",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { correoEnviado = false }) {
                        Text("← Cambiar correo", color = azulKidCare,
                            fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ─── PASO 1: Ingresar correo ──────────────────────────────────
                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("🔑", fontSize = 56.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingresa tu correo registrado y te enviaremos un código para restablecer tu contraseña.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    Text("CORREO ELECTRÓNICO", fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; correoError = false },
                        placeholder = { Text("tu@correo.com", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = correoError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            errorBorderColor = Color(0xFFDC2626)
                        )
                    )

                    if (correoError) {
                        Text("⚠ Ingresa un correo válido", fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (correo.isBlank() || !correo.contains("@")) {
                                correoError = true
                            } else {
                                correoEnviado = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = correo.isNotBlank()
                    ) {
                        Text("Enviar código", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}