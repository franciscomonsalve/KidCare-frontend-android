package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.navigation.Rutas

@Composable
fun HomeScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val azulTeal    = Color(0xFF0891B2)
    val context     = LocalContext.current
    val session     = remember { SessionManager(context) }

    val nombre = session.getNombreCompleto() ?: session.getEmail()?.substringBefore("@") ?: "Usuario"
    val email = session.getEmail() ?: "Usuario"
    val rol   = session.getRol() ?: ""

    val menores = remember { mutableStateListOf<MenorResponse>() }
    var menorSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var cargando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Primero intentar caché
        val cached = session.getMenores()
        if (cached.isNotEmpty()) {
            menores.clear()
            menores.addAll(cached)
            val savedId = session.getMenorSeleccionadoId()
            menorSeleccionadoId = if (savedId > 0 && cached.any { it.idMenor == savedId }) savedId
                                  else cached.first().idMenor
        }

        // Siempre refrescar desde API
        cargando = true
        val result = runCatching { RetrofitClient.api.listarMenores() }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                val lista = resp.body() ?: emptyList()
                menores.clear()
                menores.addAll(lista)
                session.saveMenores(lista)
                if (menores.isNotEmpty() && (menorSeleccionadoId == null || menores.none { it.idMenor == menorSeleccionadoId })) {
                    menorSeleccionadoId = menores.first().idMenor
                }
            }
        }
        cargando = false
    }

    val menorActual = menores.find { it.idMenor == menorSeleccionadoId }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare, azulTeal)))
                    .padding(top = 48.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Bienvenido,", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            Text("$nombre 👋", fontSize = 22.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Box(
                            modifier = Modifier.size(42.dp)
                                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                                .clickable { navController.navigate(Rutas.CONFIGURACION) },
                            contentAlignment = Alignment.Center
                        ) { Text("👤", fontSize = 22.sp) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chips menores
                    if (cargando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(menores) { menor ->
                                val seleccionado = menor.idMenor == menorSeleccionadoId
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (seleccionado) Color.White.copy(alpha = 0.92f)
                                            else Color.White.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(14.dp))
                                        .clickable {
                                            menorSeleccionadoId = menor.idMenor
                                            session.saveMenorSeleccionadoId(menor.idMenor)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(menor.emoji ?: "🧒", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(menor.nombre.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                                color = if (seleccionado) azulKidCare else Color.White)
                                            Text(menor.sexo.orEmpty(), fontSize = 11.sp,
                                                color = if (seleccionado) azulKidCare.copy(alpha = 0.7f)
                                                else Color.White.copy(alpha = 0.6f))
                                        }
                                    }
                                }
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.14f), shape = RoundedCornerShape(14.dp))
                                        .clickable { navController.navigate(Rutas.AGREGAR_MENOR) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Agregar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grid acciones
        item {
            val menorId = menorActual?.idMenor ?: 0
            Column(modifier = Modifier.padding(18.dp)) {
                Text("¿Qué quieres hacer?", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Box(
                        modifier = Modifier.weight(1f)
                            .background(brush = Brush.linearGradient(colors = listOf(azulKidCare, azulTeal)),
                                shape = RoundedCornerShape(18.dp))
                            .clickable { if (menorId > 0) navController.navigate(Rutas.chatbot(menorId)) }
                            .padding(18.dp)
                    ) {
                        Column {
                            Text("💬", fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Registrar\nobservación", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color.White, lineHeight = 18.sp)
                            Text("Con asistente IA", fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 2.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { if (menorId > 0) navController.navigate(Rutas.bitacora(menorId)) }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("📋", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ver bitácora", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A))
                                Text("Todas las observaciones", fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { if (menorId > 0) navController.navigate(Rutas.enlace(menorId)) }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("🔗", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Compartir\ncon médico", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A), lineHeight = 18.sp)
                                Text("Enlace temporal", fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { if (menorId > 0) navController.navigate(Rutas.delegados(menorId)) }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("👥", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Delegados", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A))
                                Text("Gestionar accesos", fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }

                // Historial para médico
                Spacer(modifier = Modifier.height(11.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(18.dp))
                        .clickable { if (menorId > 0) navController.navigate(Rutas.historialLista(menorId)) }
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("📄", fontSize = 22.sp)
                        Column {
                            Text("Historial médico", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A))
                            Text("Resúmenes generados para el médico", fontSize = 11.sp,
                                color = Color(0xFF9CA3AF), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }

                // Panel admin (solo para ADMIN)
                if (rol == "ADMIN") {
                    Spacer(modifier = Modifier.height(11.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(18.dp))
                            .clickable { navController.navigate(Rutas.ADMIN_USUARIOS) }
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("🛡️", fontSize = 22.sp)
                            Column {
                                Text("Panel Admin", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF92400E))
                                Text("Gestionar usuarios y auditoría", fontSize = 11.sp,
                                    color = Color(0xFFD97706), modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // Recientes (mock por ahora — se conectará en Sesión 4)
        item {
            val menorId = menorActual?.idMenor ?: 0
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recientes", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                Text("Ver todas →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = azulKidCare,
                    modifier = Modifier.clickable { if (menorId > 0) navController.navigate(Rutas.bitacora(menorId)) })
            }
        }

        if (menorActual == null && !cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👶", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Agrega tu primer menor para comenzar", fontSize = 14.sp,
                            color = Color(0xFF9CA3AF))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
