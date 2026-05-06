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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas

data class Delegado(
    val id: String,
    val nombre: String,
    val correo: String,
    val relacion: String,
    val estado: String // ACTIVO, PENDIENTE
)

@Composable
fun DelegadoScreen(navController: NavController, menorId: String = "1") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var delegadoSeleccionado   by remember { mutableStateOf<Delegado?>(null) }

    val delegados = remember {
        mutableStateListOf(
            Delegado("1", "María González", "maria@correo.cl", "Abuela", "ACTIVO"),
            Delegado("2", "Pedro Soto",     "pedro@correo.cl", "Tío",    "PENDIENTE"),
        )
    }

    // Diálogo eliminar delegado
    if (mostrarDialogoEliminar && delegadoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar delegado", fontWeight = FontWeight.Bold) },
            text  = {
                Text("¿Estás seguro que deseas eliminar a ${delegadoSeleccionado?.nombre} como delegado?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        delegados.remove(delegadoSeleccionado)
                        mostrarDialogoEliminar = false
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
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

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(azulOscuro, azulKidCare)
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        Text("← Volver", fontSize = 14.sp)
                    }
                    Text(
                        text = "Delegados",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Personas autorizadas para registrar observaciones",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // Info card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("ℹ️", fontSize = 16.sp)
                    Text(
                        text = "Los delegados pueden registrar observaciones pero no pueden compartir la bitácora con médicos ni agregar otros delegados.",
                        fontSize = 12.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Lista de delegados
        item {
            Text(
                text = "DELEGADOS ACTUALES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            )
        }

        items(delegados) { delegado ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = delegado.nombre,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            // Badge estado
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (delegado.estado == "ACTIVO") Color(0xFFDCFCE7)
                                        else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = delegado.estado,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (delegado.estado == "ACTIVO") Color(0xFF059669)
                                    else Color(0xFFD97706)
                                )
                            }
                        }
                        Text(
                            text = delegado.correo,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = delegado.relacion,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    // Botón eliminar
                    TextButton(
                        onClick = {
                            delegadoSeleccionado = delegado
                            mostrarDialogoEliminar = true
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFDC2626)
                        )
                    ) {
                        Text("🗑", fontSize = 18.sp)
                    }
                }
            }
        }

        // Botón invitar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("invitar_delegado/$menorId") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
            ) {
                Text("+ Invitar delegado", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}