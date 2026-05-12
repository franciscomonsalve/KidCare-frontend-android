package com.example.kidcare.ui.screens

import android.widget.Toast
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
fun PerfilScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val context     = LocalContext.current
    val session     = remember { SessionManager(context) }

    // Datos reales del usuario desde sesión
    val nombreUsuario = session.getNombreCompleto() ?: session.getEmail()?.substringBefore("@") ?: "Usuario"
    val emailUsuario  = session.getEmail() ?: ""
    val rolUsuario    = session.getRol() ?: "TUTOR"

    // Menores cacheados en sesión
    val menores = remember { mutableStateListOf<MenorResponse>() }
    var cargando by remember { mutableStateOf(false) }

    // Cargar menores: primero intentar caché, luego API
    LaunchedEffect(Unit) {
        val cached = session.getMenores()
        if (cached.isNotEmpty()) {
            menores.clear()
            menores.addAll(cached)
        } else {
            cargando = true
            val result = runCatching { RetrofitClient.api.listarMenores() }
            result.onSuccess { resp ->
                if (resp.isSuccessful) {
                    val lista = resp.body() ?: emptyList()
                    menores.clear()
                    menores.addAll(lista)
                    session.saveMenores(lista)
                }
            }
            cargando = false
        }
    }

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
                        session.clear()
                        RetrofitClient.jwtToken = null
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

        // HEADER: Perfil del Usuario — datos reales
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
                        text = nombreUsuario,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = emailUsuario,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = rolUsuario,
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
                FilaMenu("👤", "Editar perfil", onClick = {
                    Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
                })
                DividerPersonalizado()
                FilaMenu("🔒", "Cambiar contraseña", onClick = {
                    navController.navigate(Rutas.CAMBIAR_CONTRASENA)
                })
                DividerPersonalizado()
                FilaMenu("🔔", "Notificaciones", onClick = {
                    Toast.makeText(context, "Próximamente disponible", Toast.LENGTH_SHORT).show()
                })
            }
        }

        // SECCIÓN: Mis Hijos — datos reales desde API/caché
        item {
            SeccionTitulo("MIS HIJOS")
            CardContenedor {
                if (cargando) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = azulKidCare, modifier = Modifier.size(24.dp))
                    }
                } else if (menores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No hay menores registrados.", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                    }
                } else {
                    menores.forEachIndexed { index, menor ->
                        FilaMenu(
                            emoji = menor.emoji ?: "🧒",
                            titulo = "${menor.nombre.orEmpty()} · ${menor.sexo.orEmpty()}",
                            onClick = { navController.navigate(Rutas.perfilMenor(menor.idMenor)) }
                        )

                        DividerPersonalizado()

                        // Fila de Delegados vinculada al niño
                        FilaMenu(
                            emoji = "👥",
                            titulo = "Delegados de ${menor.nombre.orEmpty()}",
                            onClick = { navController.navigate(Rutas.delegados(menor.idMenor)) }
                        )

                        // Solo poner un separador visual fuerte si hay más hijos después
                        if (index < menores.size - 1) {
                            HorizontalDivider(color = Color(0xFFF2F5FB), thickness = 6.dp)
                        }
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
    HorizontalDivider(color = Color(0xFFF2F5FB), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
}