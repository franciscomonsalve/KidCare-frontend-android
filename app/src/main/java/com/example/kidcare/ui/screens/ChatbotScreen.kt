package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.ui.viewmodel.ChatbotViewModel
import kotlinx.coroutines.launch

/** Representa un mensaje en la interfaz de chat con su autor (usuario o sistema). */
data class Mensaje(val texto: String, val esUsuario: Boolean)

/**
 * Pantalla de registro de observaciones en formato de chat.
 *
 * Permite al usuario escribir una observación sobre el menor y enviarla al
 * chatbot-service mediante [ChatbotViewModel.guardarObservacion]. También carga
 * el historial previo de observaciones para el menor al iniciar la pantalla.
 *
 * @param navController controlador de navegación
 * @param menorId identificador del menor al que se registra la observación
 * @param chatbotViewModel ViewModel que gestiona el envío y carga de observaciones
 */
@Composable
fun ChatbotScreen(
    navController: NavController,
    menorId: Int = 0,
    chatbotViewModel: ChatbotViewModel = viewModel()
) {
    val azulKidCare = Color(0xFF2563EB)
    val fondoChat = Color(0xFFF5F7FF)

    var inputTexto by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val guardado by chatbotViewModel.guardado.collectAsState()

    val mensajes = remember {
        mutableStateListOf(
            Mensaje(
                "Hola 👋 Voy a ayudarte a registrar una observación. ¿Qué síntoma o situación quieres registrar hoy?",
                esUsuario = false
            )
        )
    }

    LaunchedEffect(guardado) {
        if (guardado == true) {
            chatbotViewModel.resetGuardado()
            mensajes.add(Mensaje("✅ Observación guardada correctamente.", esUsuario = false))
            scope.launch { listState.animateScrollToItem(mensajes.size - 1) }
        } else if (guardado == false) {
            chatbotViewModel.resetGuardado()
            mensajes.add(Mensaje("⚠️ No se pudo guardar. Verifica tu conexión.", esUsuario = false))
            scope.launch { listState.animateScrollToItem(mensajes.size - 1) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoChat)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(Color(0xFF1E3A8A), azulKidCare)))
                .padding(top = 48.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text("← Volver", fontSize = 14.sp)
                }
                Text(
                    text = "Registrar observación",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Asistente de registro",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(true, false, false).forEach { activo ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            if (activo) azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFFFFBEB), shape = RoundedCornerShape(12.dp))
                .padding(11.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("⚠️", fontSize = 17.sp)
            Text(
                text = "Este asistente no emite diagnósticos clínicos. Solo registra observaciones.",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF92400E),
                lineHeight = 16.sp
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(mensajes) { mensaje ->
                if (mensaje.esUsuario) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .background(azulKidCare, shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                                .padding(11.dp)
                                .fillMaxWidth(0.78f)
                        ) {
                            Text(text = mensaje.texto, fontSize = 13.sp, color = Color.White, lineHeight = 20.sp)
                        }
                    }
                } else {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(azulKidCare, shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White, shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                .padding(11.dp)
                                .fillMaxWidth(0.78f)
                        ) {
                            Text(
                                text = mensaje.texto,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            OutlinedTextField(
                value = inputTexto,
                onValueChange = { inputTexto = it },
                placeholder = { Text("Escribe tu observación...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedBorderColor = azulKidCare
                )
            )

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(azulKidCare, shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        val texto = inputTexto.trim()
                        if (texto.isNotBlank()) {
                            mensajes.add(Mensaje(texto, esUsuario = true))
                            inputTexto = ""
                            // Save to API
                            chatbotViewModel.guardarObservacion(menorId, texto)
                            scope.launch {
                                kotlinx.coroutines.delay(300)
                                listState.animateScrollToItem(mensajes.size - 1)
                            }
                        }
                    }
                ) {
                    Text("→", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
