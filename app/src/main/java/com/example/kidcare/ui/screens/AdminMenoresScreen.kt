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
import com.example.kidcare.data.AuditoriaLocal
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.ui.theme.campoColores
import kotlinx.coroutines.launch

@Composable
fun AdminMenoresScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val menores = remember { mutableStateListOf<MenorResponse>() }

    val sexos = listOf("M" to "Masculino", "F" to "Femenino")

    var menorAccion           by remember { mutableStateOf<MenorResponse?>(null) }
    var mostrarEditarDialog   by remember { mutableStateOf(false) }
    var mostrarEliminarDialog by remember { mutableStateOf(false) }
    var mostrarVincularDialog by remember { mutableStateOf(false) }

    var nombreEditar by remember { mutableStateOf("") }
    var fechaEditar  by remember { mutableStateOf("") }
    var sexoEditar   by remember { mutableStateOf("M") }
    var emojiEditar  by remember { mutableStateOf("") }

    var idUsuarioVincular by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cargando = true
        runCatching { RetrofitClient.api.listarMenoresAdmin() }
            .onSuccess { resp ->
                if (resp.isSuccessful) {
                    menores.clear(); menores.addAll(resp.body() ?: emptyList())
                } else { errorMsg = "No se pudo cargar la lista de menores." }
            }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    // Dialog: editar menor
    if (mostrarEditarDialog && menorAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarEditarDialog = false },
            title = { Text("Editar menor", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreEditar, onValueChange = { nombreEditar = it },
                        label = { Text("Nombre") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    OutlinedTextField(
                        value = fechaEditar, onValueChange = { fechaEditar = it },
                        label = { Text("Fecha nacimiento (yyyy-MM-dd)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    OutlinedTextField(
                        value = emojiEditar, onValueChange = { emojiEditar = it },
                        label = { Text("Emoji (opcional)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    Text("SEXO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sexos.forEach { (codigo, etiqueta) ->
                            FilterChip(
                                selected = sexoEditar == codigo, onClick = { sexoEditar = codigo },
                                label = { Text(etiqueta, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = azulKidCare)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreEditar.isBlank() || fechaEditar.isBlank()) return@Button
                        val id = menorAccion!!.idMenor
                        scope.launch {
                            runCatching {
                                RetrofitClient.api.editarMenorAdmin(id,
                                    MenorRequest(nombreEditar, fechaEditar, sexoEditar,
                                        emojiEditar.ifBlank { null }))
                            }.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    val idx = menores.indexOfFirst { it.idMenor == id }
                                    if (idx >= 0) menores[idx] = menores[idx].copy(
                                        nombre = nombreEditar, fechaNacimiento = fechaEditar,
                                        sexo = sexoEditar, emoji = emojiEditar.ifBlank { null })
                                    AuditoriaLocal.registrar("EDITAR", "MENOR", "Nombre: $nombreEditar")
                                }
                            }
                            mostrarEditarDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { mostrarEditarDialog = false }) { Text("Cancelar") } }
        )
    }

    // Dialog: confirmar eliminación
    if (mostrarEliminarDialog && menorAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarEliminarDialog = false },
            title = { Text("Eliminar menor", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Eliminar a ${menorAccion!!.nombre.orEmpty()}? Esta acción es permanente e irrecuperable.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = menorAccion!!.idMenor
                        val nombreMenor = menorAccion!!.nombre.orEmpty()
                        scope.launch {
                            runCatching { RetrofitClient.api.eliminarMenorAdmin(id) }
                                .onSuccess { resp ->
                                    if (resp.isSuccessful) {
                                        menores.removeIf { it.idMenor == id }
                                        AuditoriaLocal.registrar("ELIMINAR", "MENOR", "Nombre: $nombreMenor")
                                    }
                                }
                            mostrarEliminarDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626))
                ) { Text("Eliminar", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { mostrarEliminarDialog = false }) { Text("Cancelar") } }
        )
    }

    // Dialog: vincular usuario a menor
    if (mostrarVincularDialog && menorAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarVincularDialog = false; idUsuarioVincular = "" },
            title = { Text("Vincular usuario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresa el ID del usuario a vincular con ${menorAccion!!.nombre.orEmpty()}:")
                    OutlinedTextField(
                        value = idUsuarioVincular,
                        onValueChange = { idUsuarioVincular = it.filter { c -> c.isDigit() } },
                        label = { Text("ID del usuario") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idUsuario = idUsuarioVincular.toIntOrNull() ?: return@Button
                        val idMenor = menorAccion!!.idMenor
                        val nombreMenor = menorAccion!!.nombre.orEmpty()
                        scope.launch {
                            runCatching { RetrofitClient.api.vincularUsuarioMenorAdmin(idMenor, idUsuario) }
                                .onSuccess { resp ->
                                    if (resp.isSuccessful)
                                        AuditoriaLocal.registrar("VINCULAR", "USUARIO_MENOR",
                                            "Menor: $nombreMenor → Usuario #$idUsuario")
                                }
                            mostrarVincularDialog = false; idUsuarioVincular = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Vincular") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarVincularDialog = false; idUsuarioVincular = "" }) { Text("Cancelar") }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header — mismo gradiente estándar de la app
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
                    Text("Panel Admin — Menores", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("${menores.size} menores registrados", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
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
                Text("MENORES", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            if (menores.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay menores registrados.", fontSize = 14.sp, color = Color(0xFF6B7280))
                    }
                }
            }

            items(menores) { menor ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Text(menor.emoji ?: "🧒", fontSize = 20.sp) }
                                Column {
                                    Text(menor.nombre.orEmpty(), fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(
                                        "Nacimiento: ${menor.fechaNacimiento.orEmpty()}  •  ${menor.sexo.orEmpty()}",
                                        fontSize = 12.sp, color = Color(0xFF6B7280))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    menorAccion  = menor
                                    nombreEditar = menor.nombre.orEmpty()
                                    fechaEditar  = menor.fechaNacimiento.orEmpty()
                                    sexoEditar   = menor.sexo ?: "M"
                                    emojiEditar  = menor.emoji.orEmpty()
                                    mostrarEditarDialog = true
                                },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A))
                            ) { Text("Editar", fontSize = 12.sp) }

                            OutlinedButton(
                                onClick = { menorAccion = menor; mostrarEliminarDialog = true },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) { Text("Eliminar", fontSize = 12.sp) }

                            OutlinedButton(
                                onClick = { menorAccion = menor; mostrarVincularDialog = true },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
                            ) { Text("Vincular", fontSize = 12.sp) }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
