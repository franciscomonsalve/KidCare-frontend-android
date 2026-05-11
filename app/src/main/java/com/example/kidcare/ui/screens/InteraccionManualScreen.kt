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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.InteraccionRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun InteraccionManualScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    val idMenor = menorId.toIntOrNull() ?: 0

    val sintomasDisponibles = listOf("Fiebre", "Tos", "Dolor de cabeza", "Vómito", "Diarrea", "Irritabilidad", "Inapetencia", "Dificultad para dormir")

    var sintomasSelect by remember { mutableStateOf<Set<String>>(emptySet()) }
    var textoLibre     by remember { mutableStateOf("") }
    var cargando       by remember { mutableStateOf(false) }
    var errorMsg       by remember { mutableStateOf("") }
    var guardado       by remember { mutableStateOf(false) }

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
                TextButton(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Text("Registro manual", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.padding(start = 8.dp))
                Text("Registra observaciones sin el asistente IA", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }

        if (guardado) {
            Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Observación guardada", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { navController.navigate(Rutas.bitacora(idMenor)) { popUpTo(Rutas.HOME) } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                    ) { Text("Ver bitácora", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                }
            }
            return@Column
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // Aviso privacidad
            Box(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFFFFFBEB), shape = RoundedCornerShape(12.dp)).padding(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Text("⚠️", fontSize = 16.sp)
                    Text("No incluyas datos personales como nombres, números de documento ni direcciones.",
                        fontSize = 12.sp, color = Color(0xFF92400E), lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Síntomas predefinidos
            Text("SÍNTOMAS OBSERVADOS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 12.dp))

            sintomasDisponibles.chunked(2).forEach { fila ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { sintoma ->
                        val sel = sintomasSelect.contains(sintoma)
                        FilterChip(
                            selected = sel,
                            onClick = {
                                sintomasSelect = if (sel) sintomasSelect - sintoma else sintomasSelect + sintoma
                            },
                            label = { Text(sintoma, fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEFF6FF),
                                selectedLabelColor = azulKidCare)
                        )
                    }
                    if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto libre
            Text("DESCRIPCIÓN ADICIONAL", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280), letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = textoLibre,
                onValueChange = { textoLibre = it; errorMsg = "" },
                placeholder = { Text("Describe los síntomas observados...", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), minLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
            )

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val observaciones = buildString {
                        if (sintomasSelect.isNotEmpty()) append(sintomasSelect.joinToString(", "))
                        if (textoLibre.isNotBlank()) {
                            if (isNotEmpty()) append(". ")
                            append(textoLibre.trim())
                        }
                    }
                    if (observaciones.isBlank()) { errorMsg = "Selecciona al menos un síntoma o escribe una descripción."; return@Button }

                    scope.launch {
                        cargando = true
                        errorMsg = ""
                        val result = runCatching {
                            RetrofitClient.chatbotApi.registrarInteraccion(
                                InteraccionRequest(idMenor = idMenor, observaciones = observaciones, fallback = true)
                            )
                        }
                        result.onSuccess { resp ->
                            if (resp.isSuccessful) guardado = true
                            else errorMsg = "No se pudo guardar. Intenta de nuevo."
                        }.onFailure { errorMsg = "Error de conexión." }
                        cargando = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = (sintomasSelect.isNotEmpty() || textoLibre.isNotBlank()) && !cargando
            ) {
                if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("💾 Guardar observación", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
