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
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.RecuperarRequest
import com.example.kidcare.data.model.RestablecerRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun RecuperarContrasenaScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var correo           by remember { mutableStateOf("") }
    var correoError      by remember { mutableStateOf(false) }
    var correoEnviado    by remember { mutableStateOf(false) }
    var token            by remember { mutableStateOf("") }
    var contrasenaNueva  by remember { mutableStateOf("") }
    var confirmarNueva   by remember { mutableStateOf("") }
    var verNueva         by remember { mutableStateOf(false) }
    var verConfirmar     by remember { mutableStateOf(false) }
    var cambioFinalizado by remember { mutableStateOf(false) }
    var cargando         by remember { mutableStateOf(false) }
    var errorMsg         by remember { mutableStateOf("") }

    val pasoActual = when {
        cambioFinalizado -> 3
        correoEnviado    -> 2
        else             -> 1
    }

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
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Text(
                    text = "Recuperar contraseña",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = when (pasoActual) {
                        1 -> "Paso 1: Ingresa tu correo"
                        2 -> "Paso 2: Token y nueva contraseña"
                        else -> "Proceso completado"
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
                listOf("1" to "Correo", "2" to "Restablecer").forEachIndexed { index, (num, label) ->
                    val activo = pasoActual > index
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
                            Text(num, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = if (activo) Color.White else Color(0xFF9CA3AF))
                        }
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = if (activo) azulKidCare else Color(0xFF9CA3AF))
                    }
                    if (index < 1) {
                        Box(modifier = Modifier.weight(1f).height(2.dp).background(Color(0xFFE5E7EB)))
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when {

                // ─── PASO FINAL: Éxito ─────────────────────────────────────────
                cambioFinalizado -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("✅", fontSize = 64.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Contraseña actualizada", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("Ya puedes iniciar sesión con tu nueva contraseña.",
                        fontSize = 14.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center,
                        lineHeight = 22.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { navController.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                    ) { Text("Ir al inicio de sesión", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                }

                // ─── PASO 2: Token + nueva contraseña ─────────────────────────
                correoEnviado -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("📧", fontSize = 56.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Revisa tu correo", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("Te enviamos un enlace con un token de recuperación a $correo. Cópialo aquí.",
                        fontSize = 14.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center,
                        lineHeight = 22.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 20.dp))

                    Text("TOKEN DE RECUPERACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it.trim(); errorMsg = "" },
                        placeholder = { Text("Pega el token del correo", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("NUEVA CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = contrasenaNueva,
                        onValueChange = { contrasenaNueva = it.filter { c -> !c.isWhitespace() }; errorMsg = "" },
                        placeholder = { Text("Mínimo 8 caracteres", color = Color(0xFF9CA3AF)) },
                        visualTransformation = if (verNueva) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verNueva = !verNueva }) {
                                Text(if (verNueva) "🙈" else "👁", fontSize = 18.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("CONFIRMAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = confirmarNueva,
                        onValueChange = { confirmarNueva = it.filter { c -> !c.isWhitespace() }; errorMsg = "" },
                        placeholder = { Text("Repite la contraseña", color = Color(0xFF9CA3AF)) },
                        visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                Text(if (verConfirmar) "🙈" else "👁", fontSize = 18.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
                    )

                    if (confirmarNueva.isNotBlank() && contrasenaNueva != confirmarNueva) {
                        Text("⚠ Las contraseñas no coinciden", fontSize = 12.sp,
                            color = Color(0xFFDC2626), modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }

                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, fontSize = 12.sp, color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                errorMsg = ""
                                val result = runCatching {
                                    RetrofitClient.api.restablecerPassword(
                                        RestablecerRequest(token = token, nuevaPassword = contrasenaNueva)
                                    )
                                }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) {
                                        cambioFinalizado = true
                                    } else {
                                        errorMsg = "Token inválido o expirado. Verifica el correo."
                                    }
                                }.onFailure {
                                    errorMsg = "Error de conexión. Inténtalo de nuevo."
                                }
                                cargando = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = token.isNotBlank() && contrasenaNueva.length >= 8 &&
                                contrasenaNueva == confirmarNueva && !cargando
                    ) {
                        if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Actualizar contraseña", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { correoEnviado = false }) {
                        Text("← Cambiar correo", color = azulKidCare, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ─── PASO 1: Ingresar correo ───────────────────────────────────
                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("🔑", fontSize = 56.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ingresa tu correo registrado y te enviaremos un enlace para restablecer tu contraseña.",
                        fontSize = 14.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center,
                        lineHeight = 22.sp, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(28.dp))

                    Text("CORREO ELECTRÓNICO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; correoError = false; errorMsg = "" },
                        placeholder = { Text("tu@correo.com", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                        isError = correoError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB),
                            errorBorderColor = Color(0xFFDC2626))
                    )

                    if (correoError) {
                        Text("⚠ Correo inválido. Ej: nombre@correo.com", fontSize = 12.sp, color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }
                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, fontSize = 12.sp, color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (!Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(correo)) {
                                correoError = true
                            } else {
                                scope.launch {
                                    cargando = true
                                    errorMsg = ""
                                    val result = runCatching {
                                        RetrofitClient.api.recuperarPassword(RecuperarRequest(correo.trim()))
                                    }
                                    result.onSuccess { resp ->
                                        if (resp.isSuccessful) {
                                            correoEnviado = true
                                        } else {
                                            errorMsg = "No encontramos una cuenta con ese correo."
                                        }
                                    }.onFailure {
                                        errorMsg = "Error de conexión. Inténtalo de nuevo."
                                    }
                                    cargando = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = correo.isNotBlank() && !cargando
                    ) {
                        if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Enviar enlace de recuperación", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
