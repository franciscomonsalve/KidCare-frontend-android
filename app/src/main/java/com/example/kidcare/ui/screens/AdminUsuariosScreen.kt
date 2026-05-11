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
import com.example.kidcare.data.model.AdminUsuarioResponse
import com.example.kidcare.data.model.CambiarRolRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun AdminUsuariosScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val usuarios = remember { mutableStateListOf<AdminUsuarioResponse>() }

    // Dialogs
    var usuarioAccion       by remember { mutableStateOf<AdminUsuarioResponse?>(null) }
    var mostrarRolDialog    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cargando = true
        val result = runCatching { RetrofitClient.api.listarUsuarios() }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                usuarios.clear()
                usuarios.addAll(resp.body() ?: emptyList())
            } else { errorMsg = "No se pudo cargar la lista de usuarios." }
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    // Dialog cambiar rol
    if (mostrarRolDialog && usuarioAccion != null) {
        val roles = listOf("TUTOR" to 1, "DELEGADO" to 2, "ADMIN" to 3)
        AlertDialog(
            onDismissRequest = { mostrarRolDialog = false },
            title = { Text("Cambiar rol", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Selecciona el nuevo rol para ${usuarioAccion!!.email.orEmpty()}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    roles.forEach { (nombre, idRol) ->
                        TextButton(onClick = {
                            val id = usuarioAccion!!.idUsuario
                            scope.launch {
                                runCatching { RetrofitClient.api.cambiarRol(id, CambiarRolRequest(idRol)) }
                                    .onSuccess { resp ->
                                        if (resp.isSuccessful) {
                                            val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                            if (idx >= 0) usuarios[idx] = usuarios[idx].copy(rol = nombre)
                                        }
                                    }
                            }
                            mostrarRolDialog = false
                        }) { Text(nombre, fontSize = 16.sp) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarRolDialog = false }) { Text("Cancelar") }
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
                    Text("Panel Admin — Usuarios", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("${usuarios.size} usuarios registrados", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        // Botón auditoría
        item {
            Button(
                onClick = { navController.navigate(Rutas.AUDITORIA) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
            ) { Text("📋 Ver auditoría", fontSize = 14.sp) }
        }

        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item { Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626), modifier = Modifier.padding(16.dp)) }
        } else {
            item {
                Text("USUARIOS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            items(usuarios) { usuario ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombreCompleto.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A))
                                Text(usuario.email.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7280))
                            }
                            Box(modifier = Modifier
                                .background(
                                    if (usuario.activo) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(if (usuario.activo) "ACTIVO" else "INACTIVO", fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (usuario.activo) Color(0xFF059669) else Color(0xFFDC2626))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Habilitar / Deshabilitar
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val id = usuario.idUsuario
                                        if (usuario.activo) {
                                            runCatching { RetrofitClient.api.deshabilitarUsuario(id) }
                                                .onSuccess { resp ->
                                                    if (resp.isSuccessful) {
                                                        val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                                        if (idx >= 0) usuarios[idx] = usuarios[idx].copy(activo = false)
                                                    }
                                                }
                                        } else {
                                            runCatching { RetrofitClient.api.habilitarUsuario(id) }
                                                .onSuccess { resp ->
                                                    if (resp.isSuccessful) {
                                                        val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                                        if (idx >= 0) usuarios[idx] = usuarios[idx].copy(activo = true)
                                                    }
                                                }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (usuario.activo) Color(0xFFDC2626) else Color(0xFF059669))
                            ) { Text(if (usuario.activo) "Deshabilitar" else "Habilitar", fontSize = 12.sp) }

                            // Cambiar rol
                            OutlinedButton(
                                onClick = { usuarioAccion = usuario; mostrarRolDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
                            ) { Text("Rol: ${usuario.rol.orEmpty()}", fontSize = 12.sp) }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
