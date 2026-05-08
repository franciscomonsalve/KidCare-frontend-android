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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas

@Composable
fun HomeScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val azulTeal    = Color(0xFF0891B2)

    data class MenorItem(
        val id: String,
        val nombre: String,
        val edad: String,
        val emoji: String,
        val observaciones: Int
    )

    var menorSeleccionado by remember { mutableStateOf("1") }

    val menores = listOf(
        MenorItem("1", "Sofía", "5 años", "👧", 12),
        MenorItem("2", "Mateo", "3 años", "👦", 8),
    )

    val menor = menores.find { it.id == menorSeleccionado } ?: menores.first()

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
                            colors = listOf(azulOscuro, azulKidCare, azulTeal)
                        )
                    )
                    .padding(top = 48.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bienvenido,",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Carlos 👋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Avatar perfil clickeable
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                )
                                .clickable {
                                    navController.navigate(Rutas.CONFIGURACION)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chips menores
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(menores) { m ->
                            val seleccionado = m.id == menorSeleccionado
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (seleccionado) Color.White.copy(alpha = 0.92f)
                                        else Color.White.copy(alpha = 0.14f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { menorSeleccionado = m.id }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(m.emoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = m.nombre,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (seleccionado) azulKidCare
                                            else Color.White
                                        )
                                        Text(
                                            text = m.edad,
                                            fontSize = 11.sp,
                                            color = if (seleccionado)
                                                azulKidCare.copy(alpha = 0.7f)
                                            else Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        // Botón agregar menor
                        item {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.14f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        navController.navigate(Rutas.AGREGAR_MENOR)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("+", fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Agregar", fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grid acciones
        item {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "¿Qué quieres hacer?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {

                    // Registrar observación (grande izquierda)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(azulKidCare, azulTeal)
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { navController.navigate("chatbot/${menor.id}") }
                            .padding(18.dp)
                    ) {
                        Column {
                            Text("💬", fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Registrar\nobservación",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "Con asistente IA",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Columna derecha con 3 acciones
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {

                        // Ver bitácora
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { navController.navigate("bitacora/${menor.id}") }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("📋", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ver bitácora",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${menor.observaciones} registros",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // Compartir con médico
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { navController.navigate("enlace/${menor.id}") }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("🔗", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Compartir\ncon médico",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A),
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "Enlace temporal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // Delegados
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { navController.navigate("delegados/${menor.id}") }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("👥", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Delegados",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Gestionar accesos",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recientes
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recientes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Ver todas →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulKidCare,
                    modifier = Modifier.clickable {
                        navController.navigate("bitacora/${menor.id}")
                    }
                )
            }
        }

        items(listOf(
            Triple("💬", "Fiebre registrada", "Hoy · 14:30"),
            Triple("📝", "Tos seca leve", "Ayer · 09:15"),
            Triple("💬", "Inapetencia", "Hace 3 días"),
        )) { (emoji, titulo, fecha) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 5.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .clickable { navController.navigate("bitacora/${menor.id}") }
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (emoji == "💬") azulKidCare else Color(0xFF059669),
                            shape = RoundedCornerShape(50)
                        )
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = fecha,
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text("›", fontSize = 18.sp, color = Color(0xFF9CA3AF))
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}