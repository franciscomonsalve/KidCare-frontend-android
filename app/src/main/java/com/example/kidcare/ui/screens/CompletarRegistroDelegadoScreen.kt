package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.kidcare.ui.theme.campoColores
import kotlinx.coroutines.launch

@Composable
fun CompletarRegistroDelegadoScreen(navController: NavController) {

    val azulKidCare  = Color(0xFF2563EB)
    val azulOscuro   = Color(0xFF1E3A8A)
    val negro        = Color(0xFF111827)
    val scope        = rememberCoroutineScope()

    var tieneCuenta    by remember { mutableStateOf(false) }
    var token          by remember { mutableStateOf("") }
    var nombreCompleto by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var confirmar      by remember { mutableStateOf("") }
    var verPassword    by remember { mutableStateOf(false) }
    var verConfirmar   by remember { mutableStateOf(false) }
    var cargando       by remember { mutableStateOf(false) }
    var errorMsg       by remember { mutableStateOf("") }
    var exitoso        by remember { mutableStateOf(false) }

    val passwordsCoinciden = password == confirmar
    val formularioValido = if (tieneCuenta) {
        token.isNotBlank()
    } else {
        token.isNotBlank() && nombreCompleto.trim().length >= 3 &&
        password.length >= 8 && passwordsCoinciden
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
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
            Spacer(modifier = Modifier.height(6.dp))
            Text("KidCare", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Acceso por invitación", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        "Aceptar invitación",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = negro
                    )

                    // Toggle Soy nuevo / Ya tengo cuenta
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(false to "Soy nuevo usuario", true to "Ya tengo cuenta").forEach { (valor, label) ->
                            val seleccionado = tieneCuenta == valor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (seleccionado) Color.White else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = if (seleccionado) 1.dp else 0.dp,
                                        color = if (seleccionado) Color(0xFFE5E7EB) else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { tieneCuenta = valor; errorMsg = "" }
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    fontSize = 12.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (seleccionado) negro else Color(0xFF6B7280)
                                )
                            }
                        }
                    }

                    // Instrucción contextual
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            if (tieneCuenta)
                                "Tu cuenta KidCare existente quedará vinculada al menor. No necesitas crear contraseña nueva."
                            else
                                "Se creará una cuenta nueva con el correo al que te enviaron la invitación.",
                            fontSize = 12.sp,
                            color = negro,
                            lineHeight = 17.sp
                        )
                    }

                    // Campo token
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it.trim(); errorMsg = "" },
                        label = { Text("Token de invitación", fontSize = 13.sp, color = negro) },
                        placeholder = { Text("Pega aquí el token del correo", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = campoColores()
                    )

                    // Campos solo para nuevo usuario
                    if (!tieneCuenta) {
                        OutlinedTextField(
                            value = nombreCompleto,
                            onValueChange = { nombreCompleto = it; errorMsg = "" },
                            label = { Text("Nombre completo", fontSize = 13.sp, color = negro) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = campoColores()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMsg = "" },
                            label = { Text("Contraseña", fontSize = 13.sp, color = negro) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (verPassword) VisualTransformation.None
                                                   else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                TextButton(onClick = { verPassword = !verPassword }) {
                                    Text(if (verPassword) "Ocultar" else "Ver",
                                        fontSize = 11.sp, color = azulKidCare)
                                }
                            },
                            supportingText = {
                                Text("Mínimo 8 caracteres, 1 mayúscula y 1 símbolo",
                                    fontSize = 11.sp, color = Color(0xFF6B7280))
                            },
                            colors = campoColores()
                        )

                        OutlinedTextField(
                            value = confirmar,
                            onValueChange = { confirmar = it; errorMsg = "" },
                            label = { Text("Confirmar contraseña", fontSize = 13.sp, color = negro) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (verConfirmar) VisualTransformation.None
                                                   else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                TextButton(onClick = { verConfirmar = !verConfirmar }) {
                                    Text(if (verConfirmar) "Ocultar" else "Ver",
                                        fontSize = 11.sp, color = azulKidCare)
                                }
                            },
                            isError = confirmar.isNotBlank() && !passwordsCoinciden,
                            supportingText = {
                                if (confirmar.isNotBlank() && !passwordsCoinciden)
                                    Text("Las contraseñas no coinciden",
                                        fontSize = 11.sp, color = Color(0xFFDC2626))
                            },
                            colors = campoColores()
                        )
                    }

                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                    }

                    if (exitoso) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                if (tieneCuenta) "✅ Cuenta vinculada. Inicia sesión para continuar."
                                else "✅ Cuenta creada. Inicia sesión para continuar.",
                                fontSize = 13.sp,
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                errorMsg = ""
                                val result = runCatching {
                                    RetrofitClient.api.completarRegistroDelegado(
                                        CompletarRegistroRequest(
                                            token          = token.trim(),
                                            nombreCompleto = if (tieneCuenta) "" else nombreCompleto.trim(),
                                            password       = if (tieneCuenta) "" else password
                                        )
                                    )
                                }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) {
                                        exitoso = true
                                    } else {
                                        errorMsg = try {
                                            val json = org.json.JSONObject(
                                                resp.errorBody()?.string() ?: "")
                                            json.optString("error", "No se pudo completar.")
                                        } catch (e: Exception) { "No se pudo completar." }
                                    }
                                }.onFailure { errorMsg = "Error de conexión." }
                                cargando = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = formularioValido && !cargando && !exitoso
                    ) {
                        if (cargando)
                            CircularProgressIndicator(modifier = Modifier.size(22.dp),
                                color = Color.White, strokeWidth = 2.dp)
                        else
                            Text(
                                if (tieneCuenta) "Vincular mi cuenta" else "Crear cuenta",
                                fontSize = 15.sp, fontWeight = FontWeight.Bold
                            )
                    }

                    TextButton(
                        onClick = {
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(Rutas.REGISTRO_DELEGADO) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Ir al inicio de sesión",
                            fontSize = 13.sp,
                            color = negro,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
