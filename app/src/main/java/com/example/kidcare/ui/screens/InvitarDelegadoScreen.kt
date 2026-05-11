package com.example.kidcare.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.kidcare.data.model.InvitacionRequest
import kotlinx.coroutines.launch

@Composable
fun InvitarDelegadoScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val verdeExito  = Color(0xFF10B981)
    val rojoError   = Color(0xFFEF4444)
    val scope       = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var correoDelegado    by remember { mutableStateOf("") }
    var tiempoSeleccionado by remember { mutableStateOf("Indefinido") }
    var cargando          by remember { mutableStateOf(false) }
    var errorMsg          by remember { mutableStateOf("") }

    val opcionesTiempo = listOf("24 h", "48 h", "1 semana", "Indefinido")

    fun mapearDuracion(opcion: String) = when (opcion) {
        "24 h"     -> "24H"
        "48 h"     -> "48H"
        "1 semana" -> "1_SEMANA"
        else       -> "INDEFINIDO"
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F5FB))
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text("← Volver", color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.clickable { navController.popBackStack() })
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Invitar Delegado", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Asigna permisos y tiempo de acceso", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                SeccionTituloDelegado("CORREO ELECTRÓNICO DEL DELEGADO")
                OutlinedTextField(
                    value = correoDelegado,
                    onValueChange = { correoDelegado = it; errorMsg = "" },
                    placeholder = { Text("carmen@email.cl") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
                )

                SeccionTituloDelegado("TIEMPO DE ACCESO")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcionesTiempo.forEach { tiempo ->
                        val seleccionado = tiempo == tiempoSeleccionado
                        Surface(
                            modifier = Modifier.weight(1f).height(44.dp)
                                .clickable { tiempoSeleccionado = tiempo },
                            shape = RoundedCornerShape(10.dp),
                            color = if (seleccionado) Color(0xFFEFF6FF) else Color.White,
                            border = BorderStroke(1.dp, if (seleccionado) azulKidCare else Color(0xFFE5E7EB))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(tiempo, fontSize = 12.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (seleccionado) azulKidCare else Color.DarkGray)
                            }
                        }
                    }
                }

                SeccionTituloDelegado("PERMISOS DEL DELEGADO")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ItemPermiso("Ver bitácora del menor", true, verdeExito)
                        ItemPermiso("Registrar interacciones (chatbot y manual)", true, verdeExito)
                        ItemPermiso("Eliminar interacciones", false, rojoError)
                        ItemPermiso("Generar enlace para médico", false, rojoError)
                        ItemPermiso("Gestionar otros delegados", false, rojoError)
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val idMenorInt = menorId.toIntOrNull()
                        if (idMenorInt == null) { errorMsg = "ID de menor no válido."; return@Button }
                        if (!Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(correoDelegado)) {
                            errorMsg = "Ingresa un correo válido."
                            return@Button
                        }
                        scope.launch {
                            cargando = true
                            errorMsg = ""
                            val result = runCatching {
                                RetrofitClient.api.enviarInvitacion(
                                    InvitacionRequest(
                                        emailDelegado = correoDelegado.trim(),
                                        idMenor = idMenorInt,
                                        duracion = mapearDuracion(tiempoSeleccionado)
                                    )
                                )
                            }
                            result.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    snackbarHostState.showSnackbar("Invitación enviada a $correoDelegado")
                                    navController.popBackStack()
                                } else {
                                    errorMsg = "No se pudo enviar. Verifica el correo o intenta de nuevo."
                                }
                            }.onFailure { errorMsg = "Error de conexión." }
                            cargando = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled = correoDelegado.isNotBlank() && !cargando
                ) {
                    if (cargando) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    else Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📧  ", fontSize = 18.sp)
                        Text("Enviar invitación por correo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SeccionTituloDelegado(texto: String) {
    Text(text = texto, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
}

@Composable
fun ItemPermiso(texto: String, habilitado: Boolean, colorIcono: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(20.dp), color = colorIcono.copy(alpha = 0.1f),
            shape = RoundedCornerShape(6.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = if (habilitado) "✓" else "✕", color = colorIcono,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = texto, fontSize = 13.sp,
            color = if (habilitado) Color(0xFF374151) else Color(0xFF9CA3AF))
    }
}
