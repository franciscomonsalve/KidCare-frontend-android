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
fun PerfilScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var mostrarDialogoCerrar by remember { mutableStateOf(false) }

    // Diálogo cerrar sesión
    if (mostrarDialogoCerrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrar = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text  = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrar = false
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cerrar sesión", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
    ) {

        // Header con avatar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(azulOscuro, azulKidCare)
                        )
                    )
                    .padding(top = 48.dp, bottom = 28.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Carlos Rodríguez",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "carlos@correo.com",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "TUTOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Sección cuenta
        item {
            Text(
                text = "CUENTA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
            ) {
                Column {
                    listOf(
                        Triple("👤", "Editar perfil", true),
                        Triple("🔒", "Cambiar contraseña", true),
                        Triple("🔔", "Notificaciones", true),
                    ).forEach { (emoji, titulo, mostrarChevron) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF2F5FB), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 18.sp)
                            }
                            Text(
                                text = titulo,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }
                        if (titulo != "Notificaciones") {
                            Divider(color = Color(0xFFF2F5FB), thickness = 1.dp)
                        }
                    }
                }
            }
        }

        // Sección menores
        item {
            Text(
                text = "MIS HIJOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
            ) {
                Column {
                    listOf(
                        Pair("👧", "Sofía · 5 años"),
                        Pair("👦", "Mateo · 3 años"),
                    ).forEach { (emoji, nombre) ->
                        // Sofía
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👧", fontSize = 18.sp)
                            }
                            Text(
                                text = "Amalia · 5 años",
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }

                        Divider(color = Color(0xFFF2F5FB), thickness = 1.dp)

// Delegados de Sofía
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("delegados/1") }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👥", fontSize = 18.sp)
                            }
                            Text(
                                text = "Delegados de Amalia",
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }

                        Divider(color = Color(0xFFF2F5FB), thickness = 1.dp)

                        // Mateo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👦", fontSize = 18.sp)
                            }
                            Text(
                                text = "Mateo · 10 años",
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }

                        Divider(color = Color(0xFFF2F5FB), thickness = 1.dp)

                        // Delegados de Mateo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("delegados/2") }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👥", fontSize = 18.sp)
                            }
                            Text(
                                text = "Delegados de Mateo",
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }
                    }
                }
            }
        }

        // Sección soporte
        item {
            Text(
                text = "SOPORTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
            ) {
                Column {
                    listOf(
                        "📄 Términos y condiciones",
                        "🔐 Política de privacidad",
                        "❓ Ayuda y soporte",
                    ).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )
                            Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
                        }
                        if (item != "❓ Ayuda y soporte") {
                            Divider(color = Color(0xFFF2F5FB), thickness = 1.dp)
                        }
                    }
                }
            }
        }

        // Botón cerrar sesión
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFFEF2F2), shape = RoundedCornerShape(14.dp))
                    .clickable { mostrarDialogoCerrar = true }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚪 Cerrar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}