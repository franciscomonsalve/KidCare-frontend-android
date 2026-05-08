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

@Composable
fun RegistroScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var verContrasena by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro = Color(0xFF1E3A8A)

    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf("TUTOR") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            navController.navigate(Rutas.HOME) {
                popUpTo(Rutas.LOGIN) { inclusive = true }
            }
        }
    }

    val passwordsMatch = contrasena == confirmar || confirmar.isEmpty()
    val canRegister = aceptoTerminos &&
            nombreCompleto.isNotBlank() &&
            correo.isNotBlank() &&
            contrasena.length >= 8 &&
            contrasena == confirmar &&
            authState !is AuthState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare))
                )
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
                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Registro para padres y tutores",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Step indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 22.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(azulKidCare, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text("Cuenta", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = azulKidCare)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                }
                Text("Términos", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                }
                Text("Listo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {

            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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

            Text(
                text = "TIPO DE CUENTA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("TUTOR" to "Tutor / Padre", "DELEGADO" to "Apoderado").forEach { (rol, label) ->
                    val seleccionado = rolSeleccionado == rol
                    Button(
                        onClick = { rolSeleccionado = rol },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) azulKidCare else Color(0xFFE5E7EB),
                            contentColor = if (seleccionado) Color.White else Color(0xFF374151)
                        )
                    ) {
                        Text(label, fontSize = 13.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NOMBRE COMPLETO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = nombreCompleto,
                onValueChange = { nombreCompleto = it },
                placeholder = { Text("Tu nombre completo", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CORREO ELECTRÓNICO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
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

            Text(
                text = "CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
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

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CONFIRMAR CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
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
                onClick = { authViewModel.registro(nombreCompleto, correo, contrasena, rolSeleccionado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = canRegister
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
