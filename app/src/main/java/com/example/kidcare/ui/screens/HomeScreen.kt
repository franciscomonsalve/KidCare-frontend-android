package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.kidcare.navigation.Rutas

@Composable
fun HomeScreen(navController: NavHostController) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // --- HEADER AZUL ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(top = 56.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Bienvenido,", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Text("Carlos 👋", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }

                            // EL BOTÓN QUE PEDISTE: Avatar que lleva al Perfil
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                                    .clickable {
                                        // Navega a la pantalla de Perfil (Configuración)
                                        navController.navigate(Rutas.CONFIGURACION)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Fila de Niños (Amalia, Mateo, Agregar)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { KidCardMini("👧", "Amalia", "5 años", true) }
                            item { KidCardMini("👦", "Mateo", "10 años", false) }
                            item { AddKidCard(onClick = {
                                    navController.navigate(Rutas.AGREGAR_MENOR)
                                })
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN: ¿QUÉ QUIERES HACER? ---
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("¿Qué quieres hacer?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard("💬", "Registrar observación", "Con asistente IA", azulKidCare, Modifier.weight(1f))
                        ActionCard("📋", "Ver bitácora", "12 registros", Color.White, Modifier.weight(1f), textColor = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card inferior de compartir
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🔗", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Compartir con médico", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Enlace temporal", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN: RECIENTES ---
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recientes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Ver todas →", color = azulKidCare, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(3) { index ->
                val titulo = listOf("Fiebre registrada", "Tos seca leve", "Inapetencia")[index]
                val hora = listOf("Hoy · 14:30", "Ayer · 09:15", "Ayer · 20:00")[index]
                val colorDot = if(index == 0) Color.Blue else if(index == 1) Color.Green else Color.Red

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(colorDot, CircleShape))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(hora, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// --- COMPONENTES DEL HOME ---

@Composable
fun KidCardMini(emoji: String, nombre: String, edad: String, seleccionado: Boolean) {
    Card(
        modifier = Modifier.width(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) Color.White else Color.White.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Text(nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(seleccionado) Color.Blue else Color.White)
            Text(edad, fontSize = 11.sp, color = if(seleccionado) Color.Gray else Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AddKidCard(onClick: () -> Unit) {

        Card(
            modifier = Modifier
                .width(110.dp)
                .height(110.dp) // Asegúrate de darle un alto para que se vea igual a las otras
                .clickable { onClick() }, // 2. Agregamos el .clickable para que responda al toque
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(), // fillMaxSize para centrar bien
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("+", fontSize = 24.sp, color = Color.White)
                Text("Agregar", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }


@Composable
fun ActionCard(emoji: String, titulo: String, sub: String, fondo: Color, modifier: Modifier, textColor: Color = Color.White) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(40.dp).background(if(fondo == Color.White) Color(0xFFF2F5FB) else Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
            Text(sub, fontSize = 11.sp, color = if(textColor == Color.White) Color.White.copy(alpha = 0.8f) else Color.Gray)
        }

        @Composable
        fun AddKidCard(onClick: () -> Unit) { // 1. Agrega el parámetro aquí
            Card(
                modifier = Modifier
                    .width(110.dp)
                    .height(110.dp) // o el alto que tengas
                    .clickable { onClick() }, // 2. Agrega el .clickable aquí
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "+", fontSize = 28.sp, color = Color.White)
                    Text(text = "Agregar", fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}