package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.ui.viewmodel.MenorState
import com.example.kidcare.ui.viewmodel.MenorViewModel

/**
 * Pantalla para crear un nuevo perfil de menor.
 *
 * Solicita nombre, fecha de nacimiento y sexo. Al confirmar llama a
 * [MenorViewModel.crearMenor]; en caso de éxito ([MenorState.Success])
 * regresa a [HomeScreen], que recarga automáticamente la lista de menores.
 *
 * Solo accesible para usuarios con rol TUTOR o ADMIN.
 *
 * @param navController controlador de navegación
 * @param menorViewModel ViewModel compartido con [HomeScreen]
 */
@Composable
fun AgregarMenorScreen(
    navController: NavController,
    menorViewModel: MenorViewModel = viewModel()
) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro = Color(0xFF1E3A8A)

    var nombre by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    var sexo by remember { mutableStateOf("") }

    val crearState by menorViewModel.crearState.collectAsState()

    LaunchedEffect(crearState) {
        if (crearState is MenorState.Success) {
            menorViewModel.resetCrearState()
            navController.popBackStack()
        }
    }

    val fechaTexto = fechaNacimiento.text
    val fechaCompleta = fechaTexto.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    val fechaValida = if (fechaCompleta) {
        val p = fechaTexto.split("/")
        val dia = p[0].toIntOrNull() ?: 0
        val mes = p[1].toIntOrNull() ?: 0
        val anio = p[2].toIntOrNull() ?: 0
        dia in 1..31 && mes in 1..12 && anio in 2000..2026
    } else false

    val puedeGuardar = nombre.isNotBlank() &&
            fechaValida &&
            sexo.isNotBlank() &&
            crearState !is MenorState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare))
                )
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text("← Volver", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Agregar menor",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Ingresa los datos del niño o niña",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {

            if (crearState is MenorState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = (crearState as MenorState.Error).message,
                        color = Color(0xFFB71C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Nombre
            Label("NOMBRE COMPLETO")
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Ej: María González", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = crearState !is MenorState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de nacimiento — cursor forzado al final para evitar inserciones en posición incorrecta
            Label("FECHA DE NACIMIENTO")
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { input ->
                    val digits = input.text.filter { it.isDigit() }.take(8)
                    val formatted = buildString {
                        digits.forEachIndexed { i, c ->
                            if (i == 2 || i == 4) append('/')
                            append(c)
                        }
                    }
                    fechaNacimiento = TextFieldValue(
                        text = formatted,
                        selection = TextRange(formatted.length)
                    )
                },
                placeholder = { Text("DD/MM/AAAA  (ej: 15/05/2020)", color = Color(0xFF9CA3AF)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = fechaTexto.isNotEmpty() && !fechaValida,
                supportingText = {
                    if (fechaTexto.isNotEmpty() && !fechaValida) {
                        val msg = if (!fechaCompleta)
                            "Formato requerido: DD/MM/AAAA"
                        else
                            "Fecha inválida (día 01–31, mes 01–12, año 2000–2026)"
                        Text(msg, color = Color(0xFFB71C1C))
                    }
                },
                enabled = crearState !is MenorState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sexo
            Label("SEXO")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Masculino", "Femenino").forEach { opcion ->
                    val seleccionado = sexo == opcion
                    Button(
                        onClick = { sexo = opcion },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) azulKidCare else Color(0xFFE5E7EB),
                            contentColor = if (seleccionado) Color.White else Color(0xFF374151)
                        ),
                        enabled = crearState !is MenorState.Loading
                    ) {
                        Text(
                            text = if (opcion == "Masculino") "👦 Masculino" else "👧 Femenino",
                            fontSize = 13.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón guardar
            Button(
                onClick = {
                    val p = fechaNacimiento.text.split("/")
                    val fechaBackend = "${p[2]}-${p[1]}-${p[0]}"
                    menorViewModel.crearMenor(nombre.trim(), fechaBackend, sexo.lowercase())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = puedeGuardar
            ) {
                if (crearState is MenorState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar menor",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280),
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
