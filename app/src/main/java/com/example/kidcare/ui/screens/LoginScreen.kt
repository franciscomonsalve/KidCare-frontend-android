package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.AuthRepository
import com.example.kidcare.data.SessionManager
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    val context       = LocalContext.current
    val scope         = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val repository     = remember { AuthRepository(RetrofitClient.api) }

    var correo        by remember { mutableStateOf("") }
    var contrasena    by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var cargando      by remember { mutableStateOf(false) }
    var errorMsg      by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fondo superior azul degradado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(azulOscuro, azulKidCare)
                    ),
                    shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            Text("👋", fontSize = 40.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bienvenido/a",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Accede a tu cuenta KidCare",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Campo Correo
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; errorMsg = "" },
                        placeholder = { Text("tu@correo.com", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Contraseña
                    Text(
                        text = "CONTRASEÑA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it; errorMsg = "" },
                        placeholder = { Text("••••••••••", color = Color(0xFF9CA3AF)) },
                        visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verContrasena = !verContrasena }) {
                                Text(if (verContrasena) "🙈" else "👁", fontSize = 18.sp)
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

                    TextButton(onClick = { navController.navigate(Rutas.RECUPERAR_CONTRASENA) }) {
                        Text(text = "¿Olvidaste tu contraseña?", color = azulKidCare, fontSize = 14.sp)
                    }

                    if (errorMsg.isNotEmpty()) {
                        Text(
                            text = errorMsg,
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                errorMsg = ""
                                val result = repository.login(LoginRequest(correo.trim(), contrasena))
                                result.onSuccess { auth ->
                                    sessionManager.saveToken(auth.token)
                                    sessionManager.saveRol(auth.rol)
                                    sessionManager.saveEmail(auth.email)
                                    navController.navigate(
                                        if (auth.rol == "TUTOR" || auth.rol == "ADMIN") Rutas.HOME
                                        else Rutas.HOME_DELEGADO
                                    ) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                }.onFailure { e ->
                                    errorMsg = e.message ?: "Error al iniciar sesión"
                                }
                                cargando = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = !cargando && correo.isNotBlank() && contrasena.isNotBlank()
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("🔑 Ingresar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¿no tienes cuenta?",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(Rutas.REGISTRO) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
                    ) {
                        Text("Crear cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
