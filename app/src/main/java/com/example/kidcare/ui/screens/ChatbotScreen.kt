package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
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
import com.example.kidcare.data.model.MensajeChatRequest
import com.example.kidcare.data.model.MensajeHistorial
import com.example.kidcare.data.model.PreguntasRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

@Composable
fun ChatbotScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val fondoChat   = Color(0xFFF5F7FF)
    val scope       = rememberCoroutineScope()
    val listState   = rememberLazyListState()

    // Etapas: 1=preguntas, 2=texto libre, 3=refinamiento, 4=confirmar
    var etapa            by remember { mutableStateOf(1) }
    var preguntas        by remember { mutableStateOf<List<String>>(emptyList()) }
    var sintomasSelect   by remember { mutableStateOf<List<String>>(emptyList()) }
    var textoDetalle     by remember { mutableStateOf("") }
    var textoRefinado    by remember { mutableStateOf("") }
    var cargando         by remember { mutableStateOf(false) }
    var errorMsg         by remember { mutableStateOf("") }
    var guardado         by remember { mutableStateOf(false) }
    var hayConexionIA    by remember { mutableStateOf(true) }

    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>() }
    var chatInput by remember { mutableStateOf("") }
    var cargandoChat by remember { mutableStateOf(false) }

    val idMenor = menorId.toIntOrNull() ?: 0

    // Carga preguntas al entrar
    LaunchedEffect(Unit) {
        if (idMenor <= 0) return@LaunchedEffect
        cargando = true
        val result = runCatching {
            RetrofitClient.chatbotApi.obtenerPreguntas(PreguntasRequest(idMenor))
        }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                preguntas = resp.body()?.preguntas ?: emptyList()
                hayConexionIA = true
            } else {
                // Fallback a preguntas predefinidas
                preguntas = listOf("Fiebre", "Tos", "Dolor de cabeza", "Vómito", "Diarrea", "Irritabilidad", "Inapetencia", "Otro")
                hayConexionIA = false
            }
        }.onFailure {
            preguntas = listOf("Fiebre", "Tos", "Dolor de cabeza", "Vómito", "Diarrea", "Irritabilidad", "Inapetencia", "Otro")
            hayConexionIA = false
        }
        cargando = false
    }

    LaunchedEffect(etapa) {
        if (etapa == 2 && chatMessages.isEmpty()) {
            chatMessages.add("Hola, soy el asistente de KidCare. Cuéntame con tus palabras: ¿cómo ha estado, desde cuándo y qué intensidad tienen los síntomas?" to true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(fondoChat)) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(Color(0xFF1E3A8A), azulKidCare)))
                .padding(top = 48.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Text("Registrar observación", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.padding(start = 8.dp))
                Text("Paso $etapa de 4", fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
            }
        }

        // Barra de progreso (4 etapas)
        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            (1..4).forEach { paso ->
                Box(modifier = Modifier.weight(1f).height(4.dp)
                    .background(if (paso <= etapa) azulKidCare else Color(0xFFE5E7EB), shape = RoundedCornerShape(2.dp)))
            }
        }

        // Aviso disclaimer
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFFFFBEB), shape = RoundedCornerShape(12.dp)).padding(11.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.Top) {
            Text("⚠️", fontSize = 17.sp)
            Text("Este asistente no emite diagnósticos clínicos. Solo registra observaciones.",
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF92400E), lineHeight = 16.sp)
        }

        // Contenido por etapa
        when {
            guardado -> {
                // Pantalla de éxito
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("✅", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Observación guardada", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("La observación ha sido registrada correctamente.", fontSize = 14.sp,
                            color = Color(0xFF6B7280), modifier = Modifier.padding(top = 8.dp))
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { navController.navigate(Rutas.bitacora(idMenor)) { popUpTo(Rutas.HOME) } },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                        ) { Text("Ver bitácora", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            etapa == 1 -> {
                // Etapa 1: Selección de síntomas con botones
                LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(16.dp)) {
                    item {
                        Text("¿Qué síntoma o situación deseas registrar?", fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 16.dp))
                    }
                    if (cargando) {
                        item { CircularProgressIndicator(color = azulKidCare) }
                    } else {
                        item {
                            preguntas.chunked(2).forEach { fila ->
                                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    fila.forEach { sintoma ->
                                        val sel = sintomasSelect.contains(sintoma)
                                        Button(
                                            onClick = {
                                                sintomasSelect = if (sel) sintomasSelect - sintoma
                                                else sintomasSelect + sintoma
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (sel) azulKidCare else Color.White,
                                                contentColor = if (sel) Color.White else Color(0xFF374151))
                                        ) { Text(sintoma, fontSize = 13.sp) }
                                    }
                                    if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Button(
                        onClick = { etapa = 2 },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = sintomasSelect.isNotEmpty()
                    ) { Text("Siguiente →", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                }
            }

            etapa == 2 -> {
                if (hayConexionIA) {
                    // Etapa 2: Chat UI
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            items(chatMessages) { (msg, isBot) ->
                                if (isBot) {
                                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth().padding(end = 32.dp)) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp).background(azulKidCare, CircleShape).padding(top = 2.dp)) {
                                            Text("🤖", fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)).padding(12.dp)) {
                                            Text(msg, fontSize = 13.sp, color = Color(0xFF0F172A), lineHeight = 18.sp)
                                        }
                                    }
                                } else {
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(start = 32.dp)) {
                                        Box(modifier = Modifier.background(azulKidCare, RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)).padding(12.dp)) {
                                            Text(msg, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                                        }
                                    }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }

                        // Input row
                        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Escribe tu respuesta…", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE5E7EB),
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    focusedContainerColor = fondoChat,
                                    unfocusedContainerColor = fondoChat
                                )
                            )
                            Button(
                                onClick = {
                                    if (chatInput.isNotBlank() && !cargandoChat) {
                                        val mensajeUsuario = chatInput
                                        chatMessages.add(mensajeUsuario to false)
                                        textoDetalle += (if (textoDetalle.isEmpty()) "" else "\n") + mensajeUsuario
                                        chatInput = ""
                                        scope.launch {
                                            cargandoChat = true
                                            val historial = chatMessages.dropLast(1).map { (msg, isBot) ->
                                                MensajeHistorial(
                                                    rol = if (isBot) "assistant" else "user",
                                                    contenido = msg
                                                )
                                            }
                                            val result = runCatching {
                                                RetrofitClient.chatbotApi.enviarMensaje(
                                                    MensajeChatRequest(
                                                        mensaje = mensajeUsuario,
                                                        sintomas = sintomasSelect,
                                                        historial = historial
                                                    )
                                                )
                                            }
                                            result.onSuccess { resp ->
                                                val texto = if (resp.isSuccessful)
                                                    resp.body()?.respuesta ?: "¿Puedes contarme más detalles?"
                                                else "¿Puedes contarme más detalles?"
                                                chatMessages.add(texto to true)
                                            }.onFailure {
                                                chatMessages.add("¿Puedes contarme más detalles?" to true)
                                            }
                                            cargandoChat = false
                                        }
                                    }
                                },
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                                enabled = !cargandoChat
                            ) {
                                if (cargandoChat)
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                else
                                    Text("➤", fontSize = 16.sp, color = Color.White)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(start = 16.dp, end = 16.dp, bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { etapa = 1 }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp)) { Text("← Atrás") }
                            Button(onClick = {
                                textoRefinado = "${sintomasSelect.joinToString(", ")}. $textoDetalle".trim()
                                etapa = 3
                            }, modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                                enabled = textoDetalle.isNotBlank()) { Text("Siguiente →") }
                        }
                    }
                } else {
                    // Etapa 2: Texto libre (manual fallback)
                    Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                        Text("Cuéntame más detalles", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 8.dp))
                        Text("Síntomas seleccionados: ${sintomasSelect.joinToString(", ")}",
                            fontSize = 12.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(bottom = 16.dp))
    
                        OutlinedTextField(
                            value = textoDetalle,
                            onValueChange = { textoDetalle = it },
                            placeholder = { Text("Describe cuándo comenzó, intensidad, etc.", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp), minLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { etapa = 1 }, modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp)) { Text("← Atrás") }
                        Button(onClick = {
                            textoRefinado = "${sintomasSelect.joinToString(", ")}. $textoDetalle".trim()
                            etapa = 3
                        }, modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                            enabled = textoDetalle.isNotBlank()) { Text("Siguiente →") }
                    }
                }
            }

            etapa == 3 -> {
                // Etapa 3: Refinamiento — revisar y editar
                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text("Revisa la observación", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 16.dp))

                    OutlinedTextField(
                        value = textoRefinado,
                        onValueChange = { textoRefinado = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), minLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azulKidCare, unfocusedBorderColor = Color(0xFFE5E7EB))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFFFFBEB), shape = RoundedCornerShape(12.dp)).padding(12.dp)) {
                        Text("No incluyas datos personales como nombres, documentos de identidad ni direcciones.",
                            fontSize = 12.sp, color = Color(0xFF92400E), lineHeight = 18.sp)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { etapa = 2 }, modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)) { Text("← Atrás") }
                    Button(onClick = { etapa = 4 }, modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = textoRefinado.isNotBlank()) { Text("Continuar →") }
                }
            }

            etapa == 4 -> {
                // Etapa 4: Confirmar y guardar
                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text("Confirmar observación", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 16.dp))

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Observación a registrar:", fontSize = 12.sp,
                                color = Color(0xFF6B7280), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(textoRefinado, fontSize = 14.sp, color = Color(0xFF0F172A), lineHeight = 22.sp)
                        }
                    }

                    if (errorMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { etapa = 3 }, modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)) { Text("← Editar") }
                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                errorMsg = ""
                                val result = runCatching {
                                    RetrofitClient.chatbotApi.registrarInteraccion(
                                        InteraccionRequest(idMenor = idMenor, observaciones = textoRefinado, origen = "CHATBOT", fallback = false)
                                    )
                                }
                                result.onSuccess { resp ->
                                    if (resp.isSuccessful) guardado = true
                                    else errorMsg = "No se pudo guardar. Intenta de nuevo."
                                }.onFailure { errorMsg = "Error de conexión." }
                                cargando = false
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                        enabled = !cargando
                    ) {
                        if (cargando) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("💾 Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
