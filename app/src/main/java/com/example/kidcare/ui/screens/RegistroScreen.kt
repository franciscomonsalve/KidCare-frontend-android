package com.example.kidcare.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.AuthRepository
import com.example.kidcare.data.SessionManager
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.RegistroRequest
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.launch

data class Pais(val nombre: String, val bandera: String, val codigo: String, val maxDigitos: Int)

val PAISES = listOf(
    Pais("Chile",          "🇨🇱", "+56",  9),
    Pais("Argentina",      "🇦🇷", "+54", 10),
    Pais("Colombia",       "🇨🇴", "+57", 10),
    Pais("Perú",           "🇵🇪", "+51",  9),
    Pais("México",         "🇲🇽", "+52", 10),
    Pais("Ecuador",        "🇪🇨", "+593", 9),
    Pais("Bolivia",        "🇧🇴", "+591", 8),
    Pais("Uruguay",        "🇺🇾", "+598", 9),
    Pais("Paraguay",       "🇵🇾", "+595", 9),
    Pais("Venezuela",      "🇻🇪", "+58", 10),
    Pais("Brasil",         "🇧🇷", "+55", 11),
    Pais("España",         "🇪🇸", "+34",  9),
    Pais("Estados Unidos", "🇺🇸", "+1",  10),
)

@Composable
fun RegistroScreen(navController: NavController) {
    var verContrasena  by remember { mutableStateOf(false) }
    var verConfirmar   by remember { mutableStateOf(false) }
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)

    val context        = LocalContext.current
    val scope          = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val repository     = remember { AuthRepository(RetrofitClient.api) }

    var correo      by remember { mutableStateOf("") }
    var contrasena  by remember { mutableStateOf("") }
    var confirmar   by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }
    var nombre    by remember { mutableStateOf("") }
    var numeroCelular    by remember { mutableStateOf("") }
    var paisSeleccionado by remember { mutableStateOf(PAISES[0]) }
    var mostrarSelectorPais by remember { mutableStateOf(false) }
    var cargando  by remember { mutableStateOf(false) }
    var errorMsg  by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .verticalScroll(rememberScrollState())
    ) {

        // Header azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(azulOscuro, azulKidCare)
                    )
                )
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                // Botón volver
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("← Volver", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Registro para padres y tutores",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Indicador de pasos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 22.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Paso 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(azulKidCare, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text("Cuenta", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = azulKidCare)
            }

            // Línea
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(Color(0xFFE5E7EB))
            )

            // Paso 2
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                }
                Text("Términos", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
            }

            // Línea
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(Color(0xFFE5E7EB))
            )

            // Paso 3
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                }
                Text("Listo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
            }
        }

        // Formulario
        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            // Nombre completo
            Text(
                text = "NOMBRE COMPLETO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMsg = "" },
                placeholder = { Text("María González", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

// Teléfono
            Text(
                text = "NÚMERO DE TELÉFONO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { mostrarSelectorPais = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF374151))
                ) {
                    Text("${paisSeleccionado.bandera} ${paisSeleccionado.codigo}", fontSize = 14.sp)
                    Text(" ▼", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                }
                OutlinedTextField(
                    value = numeroCelular,
                    onValueChange = { nueva ->
                        val soloDigitos = nueva.filter { it.isDigit() }
                        if (soloDigitos.length <= paisSeleccionado.maxDigitos) numeroCelular = soloDigitos
                    },
                    placeholder = { Text("9 1234 5678", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulKidCare,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )
            }

            if (mostrarSelectorPais) {
                AlertDialog(
                    onDismissRequest = { mostrarSelectorPais = false },
                    title = { Text("Selecciona tu país", fontWeight = FontWeight.Bold) },
                    text = {
                        LazyColumn {
                            items(PAISES) { pais ->
                                TextButton(
                                    onClick = {
                                        paisSeleccionado = pais
                                        numeroCelular = ""
                                        mostrarSelectorPais = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "${pais.bandera}  ${pais.nombre}  (${pais.codigo})",
                                        modifier = Modifier.fillMaxWidth(),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {}
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            // Correo
            Text(
                text = "CORREO ELECTRÓNICO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorMsg = "" },
                placeholder = { Text("tu@correo.com", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Contraseña
            Text(
                text = "CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it.filter { c -> !c.isWhitespace() }; errorMsg = "" },
                placeholder = { Text("Mínimo 8 caracteres", color = Color(0xFF9CA3AF)) },
                visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verContrasena = !verContrasena }) {
                        Text(if (verContrasena) "🙈" else "👁", fontSize = 18.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            // Confirmar contraseña
            Text(
                text = "CONFIRMAR CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = confirmar,
                onValueChange = { confirmar = it.filter { c -> !c.isWhitespace() }; errorMsg = "" },
                placeholder = { Text("Repite tu contraseña", color = Color(0xFF9CA3AF)) },
                visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verConfirmar = !verConfirmar }) {
                        Text(if (verConfirmar) "🙈" else "👁", fontSize = 18.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            // Términos y condiciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F9FF), shape = RoundedCornerShape(12.dp))
                    .padding(13.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it },
                    colors = CheckboxDefaults.colors(checkedColor = azulKidCare)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acepto los términos y condiciones y autorizo el tratamiento de datos conforme a la Ley 19.628",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMsg.isNotEmpty()) {
                Text(
                    text = errorMsg,
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón crear cuenta
            Button(
                onClick = {
                    if (contrasena != confirmar) {
                        errorMsg = "Las contraseñas no coinciden"
                        return@Button
                    }
                    scope.launch {
                        cargando = true
                        errorMsg = ""
                        val result = repository.registro(
                            RegistroRequest(
                                nombreCompleto = nombre.trim(),
                                email = correo.trim(),
                                password = contrasena,
                                telefono = numeroCelular.ifBlank { null }?.let { "${paisSeleccionado.codigo}$it" },
                                aceptaTerminos = aceptoTerminos
                            )
                        )
                        result.onSuccess { auth ->
                            sessionManager.saveToken(auth.token)
                            sessionManager.saveRol(auth.rol)
                            sessionManager.saveEmail(auth.email)
                            navController.navigate(Rutas.HOME) {
                                popUpTo(Rutas.REGISTRO) { inclusive = true }
                            }
                        }.onFailure { e ->
                            errorMsg = e.message ?: "Error al crear la cuenta"
                        }
                        cargando = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                enabled = aceptoTerminos && !cargando && nombre.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
