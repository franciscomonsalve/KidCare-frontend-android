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

data class Observacion(
    val id: String,
    val contenido: String,
    val fecha: String,
    val hora: String,
    val origen: String // CHATBOT o MANUAL
)

@Composable
fun BitacoraScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    val observaciones = listOf(
        Observacion("1", "El menor presentó fiebre de 38.5°C. Un familiar lo acompañó durante la noche.", "Hoy", "14:30", "CHATBOT"),
        Observacion("2", "El menor presentó tos seca y leve congestión nasal durante la mañana.", "Ayer", "09:15", "MANUAL"),
        Observacion("3", "El menor no quiso comer durante el almuerzo. Estuvo irritable en la tarde.", "Hace 3 días", "20:00", "CHATBOT"),
        Observacion("4", "El menor durmió bien toda la noche sin interrupciones.", "Hace 4 días", "08:00", "MANUAL"),
        Observacion("5", "El menor presentó leve dolor de garganta. Se le dio agua tibia.", "Hace 5 días", "17:30", "CHATBOT"),
    )

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
                    .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
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
                        text = "Bitácora · Sofía",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "${observaciones.size} observaciones registradas",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // Métricas
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("${observaciones.size}", "Total", Color(0xFFF2F5FB)),
                    Triple("${observaciones.count { it.origen == "CHATBOT" }}", "Chatbot", Color(0xFFEFF6FF)),
                    Triple("${observaciones.count { it.origen == "MANUAL" }}", "Manual", Color(0xFFECFDF5)),
                ).forEach { (valor, label, fondo) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = valor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Lista de observaciones
        item {
            Text(
                text = "OBSERVACIONES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            )
        }

        items(observaciones) { obs ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(15.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Punto indicador
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(
                            if (obs.origen == "CHATBOT") azulKidCare else Color(0xFF059669),
                            shape = RoundedCornerShape(50)
                        )
                )

                Column(modifier = Modifier.weight(1f)) {
                    // Fecha y badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${obs.fecha} · ${obs.hora}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                        // Badge origen
                        Box(
                            modifier = Modifier
                                .background(
                                    if (obs.origen == "CHATBOT") Color(0xFFEFF6FF)
                                    else Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = obs.origen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (obs.origen == "CHATBOT") azulKidCare
                                else Color(0xFF059669)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Contenido
                    Text(
                        text = obs.contenido,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A),
                        lineHeight = 20.sp
                    )

                    // Acciones
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Editar",
                            fontSize = 12.sp,
                            color = azulKidCare,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                        Text(
                            text = "Eliminar",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}