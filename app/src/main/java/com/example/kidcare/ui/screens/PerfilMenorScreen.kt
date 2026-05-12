package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
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
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.MenorResponse
import java.util.Calendar

@Composable
fun PerfilMenorScreen(navController: NavController, menorId: String) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    var menor    by remember { mutableStateOf<MenorResponse?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(menorId) {
        val id = menorId.toIntOrNull() ?: return@LaunchedEffect
        cargando = true
        val result = runCatching { RetrofitClient.api.obtenerMenor(id) }
        result.onSuccess { resp ->
            if (resp.isSuccessful) menor = resp.body()
            else errorMsg = "No se pudo cargar el perfil del menor."
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    // Backend devuelve formato ISO: yyyy-MM-dd
    fun calcularEdad(fechaNacimiento: String): String {
        return try {
            val partes = fechaNacimiento.split("-")
            if (partes.size < 3) return fechaNacimiento
            val cal = Calendar.getInstance()
            val hoy = Calendar.getInstance()
            cal.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
            var anios = hoy.get(Calendar.YEAR) - cal.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR)) anios--
            val meses = (hoy.get(Calendar.YEAR) * 12 + hoy.get(Calendar.MONTH)) -
                        (cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH))
            when {
                anios >= 1 -> "$anios año${if (anios != 1) "s" else ""}"
                meses >= 1 -> "$meses mes${if (meses != 1) "es" else ""}"
                else       -> "Recién nacido"
            }
        } catch (e: Exception) { fechaNacimiento }
    }

    fun formatearFecha(fecha: String): String {
        return try {
            val partes = fecha.split("-")
            if (partes.size < 3) fecha else "${partes[2]}/${partes[1]}/${partes[0]}"
        } catch (e: Exception) { fecha }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                    .padding(top = 48.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                    ) { Text("← Volver", fontSize = 14.sp) }

                    if (menor != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(menor!!.emoji ?: "🧒", fontSize = 72.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(menor!!.nombre.orEmpty(), fontSize = 26.sp, fontWeight = FontWeight.Bold,
                                color = Color.White)
                            Text(calcularEdad(menor!!.fechaNacimiento.orEmpty()), fontSize = 15.sp,
                                color = Color.White.copy(alpha = 0.75f))
                        }
                    } else {
                        Text("Perfil del menor", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        // Estado de carga
        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item {
                Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626), modifier = Modifier.padding(20.dp))
            }
        } else if (menor != null) {

            // Tarjeta de datos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("DATOS DEL MENOR", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280), letterSpacing = 0.6.sp)

                        FilaDato("Nombre", menor!!.nombre.orEmpty())
                        FilaDato("Fecha de nacimiento", formatearFecha(menor!!.fechaNacimiento.orEmpty()))
                        FilaDato("Edad", calcularEdad(menor!!.fechaNacimiento.orEmpty()))
                        FilaDato("Género", menor!!.sexo.orEmpty())
                        FilaDato("ID del menor", "#${menor!!.idMenor}")
                    }
                }
            }

            // Info sobre ID compartible
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                        Text("ℹ️", fontSize = 16.sp)
                        Column {
                            Text("ID para vincular: #${menor!!.idMenor}", fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            Text("Comparte este número con otro tutor para que pueda vincularse a ${menor!!.nombre.orEmpty()}.",
                                fontSize = 12.sp, color = Color(0xFF1E3A8A), lineHeight = 18.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontSize = 13.sp, color = Color(0xFF6B7280))
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}
