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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun VincularMenorScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var idMenorTexto      by remember { mutableStateOf("") }
    var cargandoBusqueda  by remember { mutableStateOf(false) }
    var cargandoVinculo   by remember { mutableStateOf(false) }
    var errorMsg          by remember { mutableStateOf("") }
    var vinculado         by remember { mutableStateOf(false) }
    var menorEncontrado   by remember { mutableStateOf<MenorResponse?>(null) }

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
                Text("Vincular menor existente", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.padding(start = 8.dp))
                Text("Solo para tutores: vincular un menor registrado en otro dispositivo", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }

        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            if (vinculado && menorEncontrado != null) {
                // Pantalla de éxito
                Spacer(modifier = Modifier.height(24.dp))
                Text("✅", fontSize = 64.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(20.dp))
                Text("Menor vinculado", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(menorEncontrado!!.emoji ?: "🧒", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(menorEncontrado!!.nombre.orEmpty(), fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A))
                        Text("Nac: ${menorEncontrado!!.fechaNacimiento.orEmpty()}", fontSize = 14.sp, color = Color(0xFF6B7280))
                        Text(menorEncontrado!!.sexo.orEmpty(), fontSize = 14.sp, color = Color(0xFF6B7280))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(Rutas.HOME) { popUpTo(0) { inclusive = false } } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Ir al inicio", fontSize = 15.sp, fontWeight = FontWeight.Bold) }

            } else {
                // Formulario de búsqueda
                Spacer(modifier = Modifier.height(16.dp))
                Text("🔗", fontSize = 56.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text("Ingresa el ID del menor para vincularlo a tu cuenta de tutor. Útil si lo registraste desde otro dispositivo o cuenta.",
                    fontSize = 14.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center,
                    lineHeight = 22.sp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(28.dp))

                Text("ID DEL MENOR", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                OutlinedTextField(
                    value = idMenorTexto,
                    onValueChange = {
                        idMenorTexto = it.filter { c -> c.isDigit() }
                        errorMsg = ""
                        if (menorEncontrado != null) menorEncontrado = null
                    },
                    placeholder = { Text("Ej: 42", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true,
                    isError = errorMsg.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB),
                        errorBorderColor = Color(0xFFDC2626))
                )

                if (errorMsg.isNotEmpty()) {
                    Text("⚠ $errorMsg", fontSize = 12.sp, color = Color(0xFFDC2626),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                }

                // Vista previa del menor encontrado
                menorEncontrado?.let { menor ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(menor.emoji ?: "🧒", fontSize = 36.sp)
                            Column {
                                Text("Menor encontrado", fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                Text(menor.nombre.orEmpty(), fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A))
                                Text("Nac: ${menor.fechaNacimiento.orEmpty()} · ${menor.sexo.orEmpty()}",
                                    fontSize = 13.sp, color = Color(0xFF6B7280))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón Buscar
                OutlinedButton(
                    onClick = {
                        val id = idMenorTexto.toIntOrNull()
                        if (id == null) { errorMsg = "Ingresa un ID válido"; return@OutlinedButton }
                        scope.launch {
                            cargandoBusqueda = true
                            errorMsg = ""
                            val result = runCatching { RetrofitClient.api.obtenerMenor(id) }
                            result.onSuccess { resp ->
                                if (resp.isSuccessful) menorEncontrado = resp.body()
                                else errorMsg = "No se encontró un menor con ese ID."
                            }.onFailure { errorMsg = "Error de conexión." }
                            cargandoBusqueda = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare),
                    enabled = idMenorTexto.isNotBlank() && !cargandoBusqueda
                ) {
                    if (cargandoBusqueda) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("🔍 Buscar menor", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Vincular (solo visible cuando se encontró un menor)
                if (menorEncontrado != null) {
                    Button(
                        onClick = {
                            val id = idMenorTexto.toIntOrNull() ?: return@Button
                            scope.launch {
                                cargandoVinculo = true
                                errorMsg = ""
                                val result = runCatching { RetrofitClient.api.vincularMenor(id) }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) vinculado = true
                                    else errorMsg = "No se pudo vincular. Intenta nuevamente."
                                }.onFailure { errorMsg = "Error de conexión." }
                                cargandoVinculo = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = !cargandoVinculo
                    ) {
                        if (cargandoVinculo) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Vincular menor", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
