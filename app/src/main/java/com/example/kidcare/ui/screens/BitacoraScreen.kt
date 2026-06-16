package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.SessionManager
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.EditarInteraccionRequest
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun BitacoraScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()
    val context     = LocalContext.current
    val session     = remember { SessionManager(context) }

    val todosLosMenores = remember { session.getMenores() }

    var idMenorActual by remember { mutableStateOf(menorId.toIntOrNull() ?: 0) }
    val menorInfo    = todosLosMenores.find { it.idMenor == idMenorActual }
    val nombreMenor  = menorInfo?.nombre.orEmpty().ifEmpty { "Menor" }
    val emojiMenor   = menorInfo?.emoji ?: "🧒"

    var cargando    by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf("") }

    // Diálogo cambiar menor
    var mostrarCambiarMenor by remember { mutableStateOf(false) }

    // Diálogo editar
    var mostrarEditar      by remember { mutableStateOf(false) }
    var interaccionEditada by remember { mutableStateOf<InteraccionResponse?>(null) }
    var textoEditado       by remember { mutableStateOf("") }

    // Diálogo eliminar
    var mostrarEliminar    by remember { mutableStateOf(false) }
    var interaccionEliminar by remember { mutableStateOf<InteraccionResponse?>(null) }

    val interacciones = remember { mutableStateListOf<InteraccionResponse>() }

    LaunchedEffect(idMenorActual) {
        val id = idMenorActual.takeIf { it > 0 } ?: return@LaunchedEffect
        interacciones.clear()
        cargando = true
        val result = runCatching { RetrofitClient.chatbotApi.listarInteracciones(id) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                interacciones.clear()
                interacciones.addAll(resp.body() ?: emptyList())
            } else { errorMsg = "No se pudieron cargar las observaciones." }
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    // Diálogo cambiar menor
    if (mostrarCambiarMenor && todosLosMenores.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { mostrarCambiarMenor = false },
            title = { Text("Cambiar menor", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Selecciona el menor:", fontSize = 13.sp, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.height(4.dp))
                    todosLosMenores.forEach { m ->
                        val seleccionado = m.idMenor == idMenorActual
                        TextButton(
                            onClick = {
                                if (m.idMenor != null) idMenorActual = m.idMenor
                                mostrarCambiarMenor = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(m.emoji ?: "🧒", fontSize = 20.sp, modifier = Modifier.padding(end = 10.dp))
                                Text(
                                    m.nombre.orEmpty(),
                                    fontSize = 15.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (seleccionado) azulKidCare else Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { mostrarCambiarMenor = false }) { Text("Cancelar") } }
        )
    }

    // Diálogo editar
    if (mostrarEditar && interaccionEditada != null) {
        AlertDialog(
            onDismissRequest = { mostrarEditar = false },
            title = { Text("Editar observación", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = textoEditado,
                    onValueChange = { textoEditado = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(onClick = {
                    val id = interaccionEditada?.id ?: return@Button
                    scope.launch {
                        val result = runCatching {
                            RetrofitClient.chatbotApi.editarInteraccion(id, EditarInteraccionRequest(textoEditado))
                        }
                        result.onSuccess { resp ->
                            if (resp.isSuccessful) {
                                val idx = interacciones.indexOfFirst { it.id == id }
                                if (idx >= 0) {
                                    interacciones[idx] = interacciones[idx].copy(observaciones = textoEditado)
                                }
                            }
                        }
                        mostrarEditar = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditar = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo eliminar
    if (mostrarEliminar && interaccionEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarEliminar = false },
            title = { Text("Eliminar observación", fontWeight = FontWeight.Bold) },
            text  = { Text("¿Eliminar esta observación? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = interaccionEliminar?.id ?: return@TextButton
                    scope.launch {
                        val result = runCatching { RetrofitClient.chatbotApi.eliminarInteraccion(id) }
                        result.onSuccess { resp ->
                            if (resp.isSuccessful) interacciones.removeIf { it.id == id }
                        }
                        mostrarEliminar = false
                    }
                }) { Text("Eliminar", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEliminar = false }) { Text("Cancelar") }
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
                    .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                    ) { Text("← Volver", fontSize = 14.sp) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) { Text(emojiMenor, fontSize = 20.sp) }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Bitácora de $nombreMenor", fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White)
                                Text("${interacciones.size} observaciones", fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        if (todosLosMenores.size > 1) {
                            TextButton(
                                onClick = { mostrarCambiarMenor = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.85f))
                            ) { Text("Cambiar ↕", fontSize = 12.sp) }
                        }
                    }
                }
            }
        }

        // Métricas
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("${interacciones.size}", "Total", Color(0xFFF2F5FB)),
                    Triple("${interacciones.count { it.origen?.uppercase() == "CHATBOT" }}", "Chatbot", Color(0xFFEFF6FF)),
                    Triple("${interacciones.count { it.origen?.uppercase() != "CHATBOT" && it.origen != null }}", "Manual", Color(0xFFECFDF5)),
                ).forEach { (valor, label, _) ->
                    Box(modifier = Modifier.weight(1f).background(Color.White, shape = RoundedCornerShape(12.dp)).padding(12.dp),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text(label, fontSize = 11.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        // Botones nueva observación
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { if (idMenorActual > 0) navController.navigate(Rutas.chatbot(idMenorActual)) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) {
                    Text("💬 Con asistente IA", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { if (idMenorActual > 0) navController.navigate(Rutas.interaccionManual(idMenorActual)) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("✏️ Manual", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item { Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626), modifier = Modifier.padding(16.dp)) }
        } else {
            item {
                Text("OBSERVACIONES", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            if (interacciones.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay observaciones registradas.", fontSize = 14.sp, color = Color(0xFF6B7280))
                    }
                }
            }

            items(interacciones) { obs ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.padding(top = 4.dp).size(8.dp)
                        .background(
                            if (obs.origen?.uppercase() == "CHATBOT") azulKidCare else Color(0xFF059669),
                            shape = RoundedCornerShape(50)))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(obs.fecha.orEmpty(), fontSize = 11.sp, color = Color(0xFF6B7280))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (obs.origen?.uppercase() == "CHATBOT") Color(0xFFEFF6FF) else Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(obs.origen.orEmpty(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = if (obs.origen?.uppercase() == "CHATBOT") azulKidCare else Color(0xFF059669))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(obs.observaciones.orEmpty(), fontSize = 13.sp, color = Color(0xFF0F172A), lineHeight = 20.sp)

                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Editar", fontSize = 12.sp, color = azulKidCare, fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    interaccionEditada = obs
                                    textoEditado = obs.observaciones.orEmpty()
                                    mostrarEditar = true
                                })
                            if (obs.origen?.uppercase() != "CHATBOT") {
                                Text("Eliminar", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        interaccionEliminar = obs
                                        mostrarEliminar = true
                                    })
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
