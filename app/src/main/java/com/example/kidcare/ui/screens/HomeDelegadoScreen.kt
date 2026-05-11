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
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.navigation.Rutas

@Composable
fun HomeDelegadoScreen(navController: NavController) {

    val verdePrincipal = Color(0xFF0A7EA4)
    val verdeOscuro    = Color(0xFF065F7A)
    val verdeClaro     = Color(0xFF0EA5C9)

    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    val email   = session.getEmail() ?: "Delegado"
    val nombre  = session.getNombreCompleto() ?: email.substringBefore("@")

    // Cargar menores asignados al delegado
    val menores = remember { mutableStateListOf<MenorResponse>() }
    var cargandoMenores by remember { mutableStateOf(false) }
    var menorSeleccionado by remember { mutableStateOf<MenorResponse?>(null) }

    // Interacciones recientes
    val interacciones = remember { mutableStateListOf<InteraccionResponse>() }
    var cargandoInteracciones by remember { mutableStateOf(false) }

    // Cargar menores
    LaunchedEffect(Unit) {
        cargandoMenores = true
        val cached = session.getMenores()
        if (cached.isNotEmpty()) {
            menores.clear()
            menores.addAll(cached)
            menorSeleccionado = cached.firstOrNull()
        }

        val result = runCatching { RetrofitClient.api.listarMenores() }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                val lista = resp.body() ?: emptyList()
                menores.clear()
                menores.addAll(lista)
                session.saveMenores(lista)
                if (menorSeleccionado == null && lista.isNotEmpty()) {
                    menorSeleccionado = lista.first()
                }
            }
        }
        cargandoMenores = false
    }

    // Cargar interacciones cuando se selecciona un menor
    LaunchedEffect(menorSeleccionado?.idMenor) {
        val id = menorSeleccionado?.idMenor ?: return@LaunchedEffect
        cargandoInteracciones = true
        val result = runCatching { RetrofitClient.chatbotApi.listarInteracciones(id) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                interacciones.clear()
                val lista = resp.body() ?: emptyList()
                interacciones.addAll(lista.take(3)) // Mostrar solo las 3 más recientes
            }
        }
        cargandoInteracciones = false
    }

    val menorId = menorSeleccionado?.idMenor?.toString() ?: "0"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
    ) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(verdeOscuro, verdePrincipal, verdeClaro)
                        )
                    )
                    .padding(top = 48.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Acceso como",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$nombre 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge delegada
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4ADE80), shape = RoundedCornerShape(50))
                            )
                            Text(
                                text = "Delegado/a de ${menorSeleccionado?.nombre.orEmpty()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menor autorizado
                    Text(
                        text = "AUTORIZADO",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (cargandoMenores) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else if (menorSeleccionado != null) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(menorSeleccionado!!.emoji ?: "🧒", fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = menorSeleccionado!!.nombre.orEmpty(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = menorSeleccionado!!.sexo.orEmpty(),
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Aviso permisos
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("ℹ️", fontSize = 16.sp)
                Text(
                    text = "Como delegado/a puedes registrar y editar interacciones. Solo el tutor puede eliminarlas o generar el enlace para el médico.",
                    fontSize = 12.sp,
                    color = Color(0xFF1E3A8A),
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Grid acciones
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chatbot guiado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(verdePrincipal, verdeClaro)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { navController.navigate("chatbot/$menorId") }
                        .padding(16.dp)
                ) {
                    Column {
                        Text("🤖", fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chatbot\nguiado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "Asistente IA",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Bitácora
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .clickable { navController.navigate("bitacora/$menorId") }
                        .padding(16.dp)
                ) {
                    Column {
                        Text("📋", fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bitácora",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${interacciones.size} registros",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Registro manual
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .clickable { navController.navigate(Rutas.interaccionManual(menorId.toIntOrNull() ?: 0)) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("📝", fontSize = 28.sp)
                Column {
                    Text(
                        text = "Registro manual",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Sin asistente de IA",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Últimas interacciones
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimas interacciones",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Ver todas →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdePrincipal,
                    modifier = Modifier.clickable {
                        navController.navigate("bitacora/$menorId")
                    }
                )
            }
        }

        // Feed interacciones — datos reales
        if (cargandoInteracciones) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verdePrincipal, modifier = Modifier.size(24.dp))
                }
            }
        } else if (interacciones.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No hay interacciones registradas.", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                }
            }
        } else {
            items(interacciones) { obs ->
                val esChatbot = obs.tipo == "CHATBOT" || obs.origen == "CHATBOT" || obs.fallback == false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(8.dp)
                            .background(
                                if (esChatbot) verdePrincipal else Color(0xFF059669),
                                shape = RoundedCornerShape(50)
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (esChatbot) "Sesión chatbot" else "Observación manual",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = obs.observaciones.orEmpty().take(80) + if ((obs.observaciones?.length ?: 0) > 80) "..." else "",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (esChatbot) Color(0xFFEFF6FF)
                                        else Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (esChatbot) "Chatbot" else "Manual",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (esChatbot) verdePrincipal
                                    else Color(0xFF059669)
                                )
                            }
                            Text(
                                text = obs.fecha.orEmpty(),
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                    Text("›", fontSize = 18.sp, color = Color(0xFF9CA3AF))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // BottomNav delegado
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    Triple("🏠", "Inicio", true),
                    Triple("📋", "Bitácora", false),
                    Triple("💬", "Chatbot", false),
                    Triple("⚙️", "Perfil", false),
                    Triple("🚪", "Salir", false),
                ).forEach { (emoji, label, activo) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            when (label) {
                                "Salir" -> {
                                    session.clear()
                                    RetrofitClient.jwtToken = null
                                    navController.navigate(Rutas.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                "Bitácora" -> navController.navigate("bitacora/$menorId")
                                "Chatbot" -> navController.navigate("chatbot/$menorId")
                                "Perfil" -> navController.navigate(Rutas.CONFIGURACION)
                            }
                        }
                    ) {
                        Text(emoji, fontSize = 22.sp)
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                            color = if (activo) verdePrincipal else Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}