package com.example.kidcare.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun InvitarDelegadoScreen(navController: NavHostController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val verdeExito  = Color(0xFF10B981)
    val rojoError   = Color(0xFFEF4444)

    var nombreDelegado by remember { mutableStateOf("") }
    var correoDelegado by remember { mutableStateOf("") }

    // Estado para el tiempo de acceso
    val opcionesTiempo = listOf("24 h", "48 h", "1 semana", "Indefinido")
    var tiempoSeleccionado by remember { mutableStateOf("Indefinido") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Text(
                    text = "← Volver",
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Invitar Delegado", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Asigna permisos y tiempo de acceso", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // --- FORMULARIO ---
            SeccionTituloDelegado("NOMBRE DEL DELEGADO")
            OutlinedTextField(
                value = nombreDelegado,
                onValueChange = { nombreDelegado = it },
                placeholder = { Text("Carmen López") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionTituloDelegado("CORREO ELECTRÓNICO")
            OutlinedTextField(
                value = correoDelegado,
                onValueChange = { correoDelegado = it },
                placeholder = { Text("carmen@email.cl") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = azulKidCare)
            )

            // --- TIEMPO DE ACCESO ---
            SeccionTituloDelegado("TIEMPO DE ACCESO")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opcionesTiempo.forEach { tiempo ->
                    val seleccionado = tiempo == tiempoSeleccionado
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { tiempoSeleccionado = tiempo },
                        shape = RoundedCornerShape(10.dp),
                        color = if (seleccionado) Color(0xFFEFF6FF) else Color.White,
                        border = BorderStroke(1.dp, if (seleccionado) azulKidCare else Color(0xFFE5E7EB))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (tiempo == "Indefinido" && seleccionado) {
                                    Text("∞ ", color = azulKidCare, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text(
                                    text = tiempo,
                                    fontSize = 12.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (seleccionado) azulKidCare else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // --- PERMISOS ---
            SeccionTituloDelegado("PERMISOS DEL DELEGADO")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ItemPermiso("Ver bitácora del menor", true, verdeExito)
                    ItemPermiso("Registrar interacciones (chatbot y manual)", true, verdeExito)
                    ItemPermiso("Eliminar interacciones", false, rojoError)
                    ItemPermiso("Generar enlace para médico", false, rojoError)
                    ItemPermiso("Gestionar otros delegados", false, rojoError)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN DE ENVÍO ---
            Button(
                onClick = { /* Lógica de envío */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📧  ", fontSize = 18.sp)
                    Text("Enviar invitación por correo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SeccionTituloDelegado(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280),
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun ItemPermiso(texto: String, habilitado: Boolean, colorIcono: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Icono de Check o Prohibido
        Surface(
            modifier = Modifier.size(20.dp),
            color = colorIcono.copy(alpha = 0.1f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (habilitado) "✓" else "✕",
                    color = colorIcono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = if (habilitado) Color(0xFF374151) else Color(0xFF9CA3AF)
        )
    }
}