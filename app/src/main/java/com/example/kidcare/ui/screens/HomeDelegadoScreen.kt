package com.example.kidcare.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.kidcare.navigation.Rutas

@Composable
fun HomeDelegadoScreen(navController: NavController) {

    val verdePrincipal = Color(0xFF0A7EA4)
    val verdeOscuro    = Color(0xFF065F7A)
    val verdeClaro     = Color(0xFF0EA5C9)

    val menorId = "1"

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
                        text = "Carmen López 👋",
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
                                text = "Delegada de Mateo",
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
                            Text("😊", fontSize = 28.sp)
                            Column {
                                Text(
                                    text = "Mateo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "10 años",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
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
                    text = "Como delegada puedes registrar y editar interacciones. Solo el tutor puede eliminarlas o generar el enlace para el médico.",
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
                            text = "7 registros",
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
                    .clickable { }
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

        // Feed interacciones
        items(
            count = 2,
            itemContent = { index ->
                val esChatbot = index == 0
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
                            text = if (esChatbot)
                                "Fiebre 38.5°, decaimiento desde ayer..."
                            else
                                "Vómitos noche, sin fiebre en ese momento...",
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
                                text = if (esChatbot) "Hoy 08:30" else "Ayer 22:15",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                    Text("›", fontSize = 18.sp, color = Color(0xFF9CA3AF))
                }
            }
        )

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
                    Triple("🚪", "Salir", false),
                ).forEach { (emoji, label, activo) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (label == "Salir") {
                                navController.navigate(Rutas.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
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