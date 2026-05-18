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
import com.example.kidcare.data.model.CompletarRegistroRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun CompletarRegistroDelegadoScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var token            by remember { mutableStateOf("") }
    var nombreCompleto   by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmar        by remember { mutableStateOf("") }
    var verPassword      by remember { mutableStateOf(false) }
    var verConfirmar     by remember { mutableStateOf(false) }
    var cargando         by remember { mutableStateOf(false) }
    var errorMsg         by remember { mutableStateOf("") }

    val passwordsCoinciden = password == confirmar
    val formularioValido   = token.isNotBlank() && nombreCompleto.trim().length >= 3 &&
                             password.length >= 8 && passwordsCoinciden

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .background(
                    brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)),
                    shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Text("🧒", fontSize = 44.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("KidCare", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Completar registro", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Crear cuenta de delegado",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Ingresa el token que recibiste en el correo de invitación.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 17.sp
                    )

                    // Token
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it.trim(); errorMsg = "" },
                        label = { Text("Token de invitación", fontSize = 13.sp) },
                        placeholder = { Text("Pega aquí el token del correo", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )

                    // Nombre completo
                    OutlinedTextField(
                        value = nombreCompleto,
                        onValueChange = { nombreCompleto = it; errorMsg = "" },
                        label = { Text("Nombre completo", fontSize = 13.sp) },
                        placeholder = { Text("Tu nombre y apellido", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )

                    // Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMsg = "" },
                        label = { Text("Contraseña", fontSize = 13.sp) },
                        placeholder = { Text("Mínimo 8 caracteres", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (verPassword) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            TextButton(onClick = { verPassword = !verPassword }) {
                                Text(if (verPassword) "Ocultar" else "Ver", fontSize = 11.sp)
                            }
                        },
                        supportingText = {
                            Text("Debe tener al menos 8 caracteres, 1 mayúscula y 1 símbolo",
                                fontSize = 11.sp, color = Color(0xFF9CA3AF))
                        },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )

                    // Confirmar contraseña
                    OutlinedTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it; errorMsg = "" },
                        label = { Text("Confirmar contraseña", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (verConfirmar) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            TextButton(onClick = { verConfirmar = !verConfirmar }) {
                                Text(if (verConfirmar) "Ocultar" else "Ver", fontSize = 11.sp)
                            }
                        },
                        isError = confirmar.isNotBlank() && !passwordsCoinciden,
                        supportingText = {
                            if (confirmar.isNotBlank() && !passwordsCoinciden)
                                Text("Las contraseñas no coinciden", fontSize = 11.sp,
                                    color = Color(0xFFDC2626))
                        },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                    )

                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                errorMsg = ""
                                val result = runCatching {
                                    RetrofitClient.api.completarRegistroDelegado(
                                        CompletarRegistroRequest(
                                            token         = token.trim(),
                                            nombreCompleto = nombreCompleto.trim(),
                                            password      = password
                                        )
                                    )
                                }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) {
                                        navController.navigate(Rutas.LOGIN) {
                                            popUpTo(Rutas.REGISTRO_DELEGADO) { inclusive = true }
                                        }
                                    } else {
                                        errorMsg = try {
                                            val json = org.json.JSONObject(resp.errorBody()?.string() ?: "")
                                            json.optString("error", "No se pudo completar el registro.")
                                        } catch (e: Exception) { "No se pudo completar el registro." }
                                    }
                                }.onFailure { errorMsg = "Error de conexión." }
                                cargando = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = formularioValido && !cargando
                    ) {
                        if (cargando)
                            CircularProgressIndicator(modifier = Modifier.size(22.dp),
                                color = Color.White, strokeWidth = 2.dp)
                        else
                            Text("Crear cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        fontSize = 13.sp,
                        color = azulKidCare,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .let { mod ->
                                mod.then(
                                    Modifier.padding(bottom = 4.dp)
                                )
                            }
                    )
                    TextButton(
                        onClick = { navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.REGISTRO_DELEGADO) { inclusive = true }
                        }},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ir al inicio de sesión", fontSize = 13.sp, color = azulKidCare)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
