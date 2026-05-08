package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.ui.viewmodel.DelegadoViewModel
import com.example.kidcare.ui.viewmodel.MenorViewModel
import com.example.kidcare.ui.viewmodel.VincularState

@Composable
fun InvitarApoderadoScreen(
    navController: NavController,
    menorViewModel: MenorViewModel = viewModel(),
    delegadoViewModel: DelegadoViewModel = viewModel()
) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro = Color(0xFF1E3A8A)

    val menores by menorViewModel.menores.collectAsState()
    val state by delegadoViewModel.state.collectAsState()

    var emailDelegado by remember { mutableStateOf("") }
    var menorSeleccionado by remember { mutableStateOf<MenorResponse?>(null) }

    LaunchedEffect(Unit) { menorViewModel.cargarMenores() }

    LaunchedEffect(state) {
        if (state is VincularState.Success) {
            delegadoViewModel.resetState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Invitar apoderado",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "El apoderado debe tener cuenta registrada",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {

            if (state is VincularState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = (state as VincularState.Error).message,
                        color = Color(0xFFB71C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Text(
                text = "CORREO DEL APODERADO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = emailDelegado,
                onValueChange = { emailDelegado = it },
                placeholder = { Text("apoderado@correo.com", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = state !is VincularState.Loading
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SELECCIONA EL MENOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (menores.isEmpty()) {
                Text(
                    text = "No tienes menores registrados",
                    fontSize = 13.sp,
                    color = Color(0xFF9CA3AF)
                )
            } else {
                menores.forEach { menor ->
                    val seleccionado = menorSeleccionado?.idMenor == menor.idMenor
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { menorSeleccionado = menor },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) Color(0xFFEFF6FF) else Color.White
                        ),
                        border = if (seleccionado)
                            CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(azulKidCare),
                                width = 2.dp
                            ) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                if (menor.sexo?.lowercase() == "femenino") "👧" else "👦",
                                fontSize = 26.sp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = menor.nombre,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                menor.fechaNacimiento?.let {
                                    Text(text = it, fontSize = 12.sp, color = Color(0xFF6B7280))
                                }
                            }
                            if (seleccionado) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(azulKidCare, shape = RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    menorSeleccionado?.let {
                        delegadoViewModel.vincular(emailDelegado.trim(), it.idMenor)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = emailDelegado.isNotBlank() &&
                        menorSeleccionado != null &&
                        state !is VincularState.Loading
            ) {
                if (state is VincularState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Dar acceso al apoderado", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
