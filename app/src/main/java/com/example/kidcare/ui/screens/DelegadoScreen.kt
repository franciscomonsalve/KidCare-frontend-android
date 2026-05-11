package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.kidcare.data.model.DelegadoAccesoResponse
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun DelegadoScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var cargando              by remember { mutableStateOf(false) }
    var errorMsg              by remember { mutableStateOf("") }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var delegadoSeleccionado  by remember { mutableStateOf<DelegadoAccesoResponse?>(null) }

    val delegados = remember { mutableStateListOf<DelegadoAccesoResponse>() }

    LaunchedEffect(menorId) {
        val id = menorId.toIntOrNull() ?: return@LaunchedEffect
        cargando = true
        val result = runCatching { RetrofitClient.accesoApi.listarDelegados(id) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                delegados.clear()
                delegados.addAll(resp.body() ?: emptyList())
            } else {
                errorMsg = "No se pudieron cargar los delegados."
            }
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    if (mostrarDialogoEliminar && delegadoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar delegado", fontWeight = FontWeight.Bold) },
            text  = { Text("¿Estás seguro que deseas revocar el acceso de ${delegadoSeleccionado?.emailDelegado.orEmpty()}?") },
            confirmButton = {
                TextButton(onClick = {
                    val idDelegado = delegadoSeleccionado?.idDelegado ?: delegadoSeleccionado?.idAcceso ?: return@TextButton
                    scope.launch {
                        val result = runCatching { RetrofitClient.accesoApi.revocarDelegado(idDelegado) }
                        result.onSuccess { resp ->
                            if (resp.isSuccessful) {
                                delegados.remove(delegadoSeleccionado)
                            }
                        }
                        mostrarDialogoEliminar = false
                    }
                }) {
                    Text("Revocar acceso", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        item {
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
                    Text("Delegados", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("Personas autorizadas para registrar observaciones", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        // Info card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Text("ℹ️", fontSize = 16.sp)
                    Text("Los delegados pueden registrar observaciones pero no pueden compartir la bitácora con médicos ni agregar otros delegados.",
                        fontSize = 12.sp, color = Color(0xFF1E3A8A), lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Estado de carga o error
        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item {
                Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626),
                    modifier = Modifier.padding(16.dp))
            }
        } else {
            item {
                Text("DELEGADOS ACTUALES", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            if (delegados.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay delegados registrados.", fontSize = 14.sp, color = Color(0xFF9CA3AF))
                    }
                }
            }

            items(delegados) { delegado ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).background(Color(0xFFEFF6FF), shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center) { Text("👤", fontSize = 22.sp) }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(delegado.nombreDelegado.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (delegado.estado == "ACTIVO") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(delegado.estado.orEmpty(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                        color = if (delegado.estado == "ACTIVO") Color(0xFF059669) else Color(0xFFD97706))
                                }
                            }
                            Text(delegado.emailDelegado.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 2.dp))
                            delegado.duracion?.let { Text("Acceso: $it", fontSize = 12.sp, color = Color(0xFF9CA3AF)) }
                        }

                        TextButton(
                            onClick = { delegadoSeleccionado = delegado; mostrarDialogoEliminar = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626))
                        ) { Text("🗑", fontSize = 18.sp) }
                    }
                }
            }
        }

        // Botón invitar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate(Rutas.invitarDelegado(menorId.toIntOrNull() ?: 0)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
            ) { Text("+ Invitar delegado", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
