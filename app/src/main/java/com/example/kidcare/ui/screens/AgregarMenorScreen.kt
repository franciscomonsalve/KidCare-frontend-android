package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas

@Composable
fun AgregarMenorScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var nombre        by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var genero        by remember { mutableStateOf("") }
    var alergias      by remember { mutableStateOf("") }
    var condiciones   by remember { mutableStateOf("") }
    var emojiSeleccionado by remember { mutableStateOf("👧") }

    val emojis = listOf("👧", "👦", "🧒", "👶")
    val generos = listOf("Femenino", "Masculino", "Otro")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
            .verticalScroll(rememberScrollState())
    ) {

        // Header
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
                    text = "Agregar hijo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Completa los datos del menor",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // Selector de emoji
            Text(
                text = "ELIGE UN ÍCONO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                emojis.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (emoji == emojiSeleccionado) Color(0xFFEFF6FF)
                                else Color.White,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .then(
                                if (emoji == emojiSeleccionado)
                                    Modifier.background(
                                        Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { emojiSeleccionado = emoji }) {
                            Text(emoji, fontSize = 28.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nombre
            Text(
                text = "NOMBRE DEL MENOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Sofía", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fecha de nacimiento
            Text(
                text = "FECHA DE NACIMIENTO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                placeholder = { Text("DD/MM/AAAA", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Género
            Text(
                text = "GÉNERO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                generos.forEach { g ->
                    FilterChip(
                        selected = genero == g,
                        onClick = { genero = g },
                        label = { Text(g, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEFF6FF),
                            selectedLabelColor = azulKidCare
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alergias
            Text(
                text = "ALERGIAS CONOCIDAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = alergias,
                onValueChange = { alergias = it },
                placeholder = { Text("Ej: Polen, mariscos... (opcional)", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Condiciones médicas
            Text(
                text = "CONDICIONES MÉDICAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = condiciones,
                onValueChange = { condiciones = it },
                placeholder = { Text("Ej: Asma, diabetes... (opcional)", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulKidCare,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón guardar
            Button(
                onClick = { navController.navigate(Rutas.HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = nombre.isNotBlank() && fechaNacimiento.isNotBlank() && genero.isNotBlank()
            ) {
                Text("Guardar menor", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}