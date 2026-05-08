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

        // HEADER: Perfil del Usuario
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
                        color = Color.White.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "TUTOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // SECCIÓN: Cuenta
        item {
            SeccionTitulo("CUENTA")
            CardContenedor {
                val opcionesCuenta = listOf(
                    Triple("👤", "Editar perfil",       Rutas.CONFIGURACION),
                    Triple("🔒", "Cambiar contraseña",  Rutas.CAMBIAR_CONTRASENA),
                    Triple("🔔", "Notificaciones",      Rutas.CONFIGURACION),
                )
                opcionesCuenta.forEachIndexed { index, (emoji, titulo, ruta) ->
                    FilaMenu(emoji, titulo, onClick = { navController.navigate(ruta) })
                    if (index < opcionesCuenta.size - 1) DividerPersonalizado()
                }
            }
        }

        // SECCIÓN: Mis Hijos (CORREGIDA)
        item {
            SeccionTitulo("MIS HIJOS")
            CardContenedor {
                // Lista dinámica de hijos
                val misHijos = listOf(
                    Triple("👧", "Amalia", "5 años"),
                    Triple("👦", "Mateo", "10 años")
                )

                misHijos.forEachIndexed { index, (emoji, nombre, edad) ->
                    // Fila del Niño/a
                    FilaMenu(emoji, "$nombre · $edad", onClick = { /* Ir a detalle */ })

                    DividerPersonalizado()

                    // Fila de Delegados vinculada al niño
                    FilaMenu(
                        emoji = "👥",
                        titulo = "Delegados de $nombre",
                        onClick = { navController.navigate("delegados/${index + 1}") }
                    )

                    // Solo poner un separador visual fuerte si hay más hijos después
                    if (index < misHijos.size - 1) {
                        Divider(color = Color(0xFFF2F5FB), thickness = 6.dp)
                    }
                }
            }
        }

        // SECCIÓN: Soporte
        item {
            SeccionTitulo("SOPORTE")
            CardContenedor {
                val soporte = listOf("📄 Términos y condiciones", "🔐 Política de privacidad", "❓ Ayuda y soporte")
                soporte.forEachIndexed { index, texto ->
                    FilaMenu("", texto, showIcon = false)
                    if (index < soporte.size - 1) DividerPersonalizado()
                }
            }
        }

        // BOTÓN: Cerrar Sesión
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { mostrarDialogoCerrar = true },
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "🚪 Cerrar sesión",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// COMPOSABLES REUTILIZABLES PARA LIMPIEZA
@Composable
fun SeccionTitulo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280),
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
    )
}

@Composable
fun CardContenedor(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(14.dp))
    ) {
        Column(content = content)
    }
}

@Composable
fun FilaMenu(emoji: String, titulo: String, showIcon: Boolean = true, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showIcon) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF2F5FB), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 18.sp)
            }
        }
        Text(
            text = titulo,
            fontSize = 14.sp,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
        Text("›", fontSize = 20.sp, color = Color(0xFF9CA3AF))
    }
}

@Composable
fun DividerPersonalizado() {
    Divider(color = Color(0xFFF2F5FB), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
}