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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas
import com.example.kidcare.ui.viewmodel.AuthState
import com.example.kidcare.ui.viewmodel.AuthViewModel

/**
 * Pantalla de registro de nuevo usuario (solo rol TUTOR).
 *
 * El rol DELEGADO no se puede elegir al registrarse — es asignado posteriormente
 * por el tutor desde la pantalla de invitar apoderado.
 *
 * Requiere nombre, apellidos, correo y contraseña (mínimo 8 caracteres,
 * una mayúscula y un símbolo especial). Teléfono es opcional.
 *
 * @param navController controlador de navegación
 * @param authViewModel ViewModel compartido de autenticación
 */
@Composable
fun RegistroScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var nombre      by remember { mutableStateOf("") }
    var apellidos   by remember { mutableStateOf("") }
    var telefono    by remember { mutableStateOf("+569") }
    var correo      by remember { mutableStateOf("") }
    var contrasena  by remember { mutableStateOf("") }
    var confirmar   by remember { mutableStateOf("") }
    var verContrasena  by remember { mutableStateOf(false) }
    var verConfirmar   by remember { mutableStateOf(false) }
    var aceptoTerminos by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            navController.navigate(Rutas.HOME) {
                popUpTo(Rutas.LOGIN) { inclusive = true }
            }
        }
    }

    // Validaciones de contraseña
    val tieneMinimo    = contrasena.length >= 8
    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneSimbolo   = contrasena.any { !it.isLetterOrDigit() }
    val passwordValida = tieneMinimo && tieneMayuscula && tieneSimbolo
    val passwordsMatch = contrasena == confirmar || confirmar.isEmpty()

    val canRegister = aceptoTerminos &&
            nombre.isNotBlank() &&
            apellidos.isNotBlank() &&
            correo.isNotBlank() &&
            passwordValida &&
            contrasena == confirmar &&
            authState !is AuthState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text("← Volver", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Crear cuenta", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "Registro para padres y tutores",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {

            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color(0xFFB71C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Nombre y Apellidos (fila doble)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("NOMBRE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Nombre", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = authState !is AuthState.Loading
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("APELLIDOS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        placeholder = { Text("Apellidos", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = authState !is AuthState.Loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Correo
            Text("CORREO ELECTRÓNICO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("tu@correo.com", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Teléfono (opcional)
            Text("TELÉFONO (opcional)", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("+56 9 1234 5678", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Contraseña
            Text("CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                placeholder = { Text("Mínimo 8 caracteres", color = Color(0xFF9CA3AF)) },
                visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verContrasena = !verContrasena }) {
                        Text(if (verContrasena) "🙈" else "👁", fontSize = 18.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = authState !is AuthState.Loading
            )

            // Indicadores de requisitos de contraseña
            if (contrasena.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        RequisitoClave("Mínimo 8 caracteres", tieneMinimo)
                        RequisitoClave("Al menos una mayúscula", tieneMayuscula)
                        RequisitoClave("Al menos un símbolo (!@#\$%...)", tieneSimbolo)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Confirmar contraseña
            Text("CONFIRMAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = confirmar,
                onValueChange = { confirmar = it },
                placeholder = { Text("Repite tu contraseña", color = Color(0xFF9CA3AF)) },
                visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verConfirmar = !verConfirmar }) {
                        Text(if (verConfirmar) "🙈" else "👁", fontSize = 18.sp)
                    }
                },
                isError = confirmar.isNotEmpty() && !passwordsMatch,
                supportingText = {
                    if (confirmar.isNotEmpty() && !passwordsMatch) {
                        Text("Las contraseñas no coinciden", color = Color(0xFFB71C1C))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Términos y condiciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F9FF), shape = RoundedCornerShape(12.dp))
                    .padding(13.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it },
                    colors = CheckboxDefaults.colors(checkedColor = azulKidCare)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acepto los términos y condiciones y autorizo el tratamiento de datos conforme a la Ley 19.628",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val nombreCompleto = "${nombre.trim()} ${apellidos.trim()}"
                    authViewModel.registro(nombreCompleto, correo, contrasena, telefono.ifBlank { null })
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = canRegister
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Crear cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Muestra un requisito de contraseña con indicador verde/rojo según si se cumple. */
@Composable
private fun RequisitoClave(texto: String, cumplido: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(if (cumplido) "✓" else "✗", fontSize = 12.sp, color = if (cumplido) Color(0xFF16A34A) else Color(0xFFDC2626))
        Text(texto, fontSize = 11.sp, color = if (cumplido) Color(0xFF16A34A) else Color(0xFF6B7280))
    }
}
