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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

// Convierte DD/MM/AAAA → yyyy-MM-dd (formato que espera el backend LocalDate)
private fun convertirFechaParaApi(fecha: String): String {
    val partes = fecha.trim().split("/")
    if (partes.size != 3) return fecha
    val (dia, mes, anio) = partes
    return "${anio.padStart(4, '0')}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}"
}

@Composable
fun AgregarMenorScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val context     = LocalContext.current
    val scope       = rememberCoroutineScope()

    var nombre            by remember { mutableStateOf("") }
    var fechaNacimiento   by remember { mutableStateOf("") }
    var genero            by remember { mutableStateOf("") }
    var emojiSeleccionado by remember { mutableStateOf("👧") }
    var cargando          by remember { mutableStateOf(false) }
    var errorMsg          by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val emojis = listOf("👧", "👦", "🧒", "👶")
    val generos = listOf("Femenino", "Masculino", "Otro")

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar registro", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("¿Deseas registrar al siguiente menor?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("$emojiSeleccionado  $nombre", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Nacimiento: $fechaNacimiento", fontSize = 14.sp, color = Color(0xFF6B7280))
                    Text("Género: $genero", fontSize = 14.sp, color = Color(0xFF6B7280))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacion = false
                        scope.launch {
                            cargando = true
                            errorMsg = ""
                            val result = runCatching {
                                RetrofitClient.api.crearMenor(
                                    MenorRequest(
                                        nombre = nombre.trim(),
                                        fechaNacimiento = convertirFechaParaApi(fechaNacimiento),
                                        sexo = genero,
                                        emoji = emojiSeleccionado
                                    )
                                )
                            }
                            result.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    navController.navigate(Rutas.HOME) {
                                        popUpTo(Rutas.HOME) { inclusive = false }
                                    }
                                } else {
                                    val codigo = resp.code()
                                    errorMsg = when (codigo) {
                                        400  -> "Datos inválidos. Verifica el formato de la fecha (DD/MM/AAAA)."
                                        401  -> "Sesión expirada. Vuelve a iniciar sesión."
                                        else -> "Error $codigo al guardar. Intenta nuevamente."
                                    }
                                }
                            }.onFailure { e ->
                                errorMsg = "Error de conexión: ${e.message}"
                            }
                            cargando = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
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
                Text("Agregar hijo", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.padding(start = 8.dp))
                Text("Completa los datos del menor", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // Selector de emoji
            Text("ELIGE UN ÍCONO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                emojis.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (emoji == emojiSeleccionado) Color(0xFFEFF6FF) else Color.White,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { emojiSeleccionado = emoji }) {
                            Text(emoji, fontSize = 28.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nombre
            Text("NOMBRE DEL MENOR", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMsg = "" },
                placeholder = { Text("Sofía", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fecha de nacimiento
            Text("FECHA DE NACIMIENTO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it; errorMsg = "" },
                placeholder = { Text("Ej: 15/03/2020", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Género
            Text("GÉNERO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                generos.forEach { g ->
                    FilterChip(
                        selected = genero == g,
                        onClick = { genero = g },
                        label = { Text(g, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEFF6FF),
                            selectedLabelColor = azulKidCare)
                    )
                }
            }

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { navController.navigate(Rutas.VINCULAR_MENOR) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
            ) {
                Text("🔗 Vincular menor existente", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { mostrarConfirmacion = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = nombre.isNotBlank() && fechaNacimiento.isNotBlank() && genero.isNotBlank() && !cargando
            ) {
                if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Guardar menor", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
