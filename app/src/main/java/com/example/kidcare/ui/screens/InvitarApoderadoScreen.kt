package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.ui.viewmodel.DesvincularState
import com.example.kidcare.ui.viewmodel.DelegadoViewModel
import com.example.kidcare.ui.viewmodel.MenorViewModel
import com.example.kidcare.ui.viewmodel.VincularState

/**
 * Pantalla para gestionar el apoderado asignado a un menor.
 *
 * Flujo:
 * 1. El tutor selecciona un menor de su lista.
 * 2. Se consulta el apoderado actual del menor:
 *    - Si tiene apoderado: muestra sus datos + botón "Revocar acceso".
 *    - Si no tiene: muestra formulario para invitar (email + fecha límite opcional).
 *
 * Regla de negocio: solo un apoderado por menor a la vez.
 *
 * @param navController    controlador de navegación
 * @param menorViewModel   provee la lista de menores del tutor
 * @param delegadoViewModel gestiona las operaciones de vinculación/revocación
 */
@Composable
fun InvitarApoderadoScreen(
    navController: NavController,
    menorViewModel: MenorViewModel = viewModel(),
    delegadoViewModel: DelegadoViewModel = viewModel()
) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val rojoError   = Color(0xFFDC2626)

    val menores          by menorViewModel.menores.collectAsState()
    val vincularState    by delegadoViewModel.vincularState.collectAsState()
    val desvincularState by delegadoViewModel.desvincularState.collectAsState()
    val delegadoActual   by delegadoViewModel.delegadoActual.collectAsState()
    val cargandoDelegado by delegadoViewModel.cargandoDelegado.collectAsState()

    var menorSeleccionado by remember { mutableStateOf<MenorResponse?>(null) }
    var emailDelegado     by remember { mutableStateOf("") }
    var fechaLimite       by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) { menorViewModel.cargarMenores() }

    // Al seleccionar un menor, cargar su apoderado actual y limpiar formulario + errores previos
    LaunchedEffect(menorSeleccionado) {
        delegadoViewModel.resetVincularState()
        delegadoViewModel.resetDesvincularState()
        menorSeleccionado?.let {
            emailDelegado = ""
            fechaLimite = TextFieldValue("")
            delegadoViewModel.cargarDelegado(it.idMenor)
        } ?: delegadoViewModel.limpiarDelegado()
    }

    // Vincular exitoso → volver
    LaunchedEffect(vincularState) {
        if (vincularState is VincularState.Success) {
            delegadoViewModel.resetVincularState()
            navController.popBackStack()
        }
    }

    // Desvincular exitoso → resetear estado (pantalla muestra formulario de invitación)
    LaunchedEffect(desvincularState) {
        if (desvincularState is DesvincularState.Success) {
            delegadoViewModel.resetDesvincularState()
        }
    }

    val fechaTexto = fechaLimite.text
    val fechaCompleta = fechaTexto.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    val fechaValida = if (fechaCompleta) {
        val p = fechaTexto.split("/")
        val d = p[0].toIntOrNull() ?: 0
        val m = p[1].toIntOrNull() ?: 0
        val a = p[2].toIntOrNull() ?: 0
        d in 1..31 && m in 1..12 && a in 2025..2035
    } else true // vacío o incompleto = sin límite, no bloquear

    val operacionEnCurso = vincularState is VincularState.Loading ||
            desvincularState is DesvincularState.Loading

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
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                TextButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gestionar apoderado", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "Solo un apoderado por menor a la vez",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {

            // Errores de operación
            val errorMsg = when {
                vincularState is VincularState.Error -> (vincularState as VincularState.Error).message
                desvincularState is DesvincularState.Error -> (desvincularState as DesvincularState.Error).message
                else -> null
            }
            errorMsg?.let {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(it, color = Color(0xFFB71C1C), fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp))
                }
            }

            // ── Selector de menor ─────────────────────────────────────────────
            EtiquetaCampo("SELECCIONA UN MENOR")
            if (menores.isEmpty()) {
                Text("No tienes menores registrados", fontSize = 13.sp, color = Color(0xFF9CA3AF))
            } else {
                menores.forEach { menor ->
                    val seleccionado = menorSeleccionado?.idMenor == menor.idMenor
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable(enabled = !operacionEnCurso) {
                                menorSeleccionado = if (seleccionado) null else menor
                            },
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
                            Text(if (menor.sexo?.lowercase() == "femenino") "👧" else "👦", fontSize = 26.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(menor.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                menor.fechaNacimiento?.let {
                                    Text(it, fontSize = 12.sp, color = Color(0xFF6B7280))
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

            // ── Panel de apoderado (solo si hay menor seleccionado) ───────────
            menorSeleccionado?.let { menor ->
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                Spacer(modifier = Modifier.height(20.dp))

                when {
                    cargandoDelegado -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = azulKidCare,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Consultando apoderado...", fontSize = 13.sp, color = Color(0xFF6B7280))
                        }
                    }

                    delegadoActual != null -> {
                        // ── Apoderado actual ──────────────────────────────────
                        EtiquetaCampo("APODERADO ACTUAL")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilaDato("Nombre", delegadoActual!!.nombreCompleto)
                                FilaDato("Email", delegadoActual!!.email)
                                val expDisplay = delegadoActual!!.fechaExpiracion
                                    ?.let { iso -> iso.split("-").let { p -> "${p[2]}/${p[1]}/${p[0]}" } }
                                    ?: "Permanente"
                                FilaDato("Acceso hasta", expDisplay)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { delegadoViewModel.desvincular(menor.idMenor) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = rojoError),
                            enabled = !operacionEnCurso
                        ) {
                            if (desvincularState is DesvincularState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Revocar acceso", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        // ── Sin apoderado: formulario de invitación ───────────
                        EtiquetaCampo("INVITAR APODERADO")
                        Text(
                            "El usuario debe tener cuenta registrada en KidCare",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        EtiquetaCampo("CORREO DEL APODERADO")
                        OutlinedTextField(
                            value = emailDelegado,
                            onValueChange = { emailDelegado = it },
                            placeholder = { Text("apoderado@correo.com", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            enabled = !operacionEnCurso
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EtiquetaCampo("ACCESO HASTA (opcional)")
                        OutlinedTextField(
                            value = fechaLimite,
                            onValueChange = { input ->
                                val digits = input.text.filter { it.isDigit() }.take(8)
                                val formatted = buildString {
                                    digits.forEachIndexed { i, c ->
                                        if (i == 2 || i == 4) append('/')
                                        append(c)
                                    }
                                }
                                fechaLimite = TextFieldValue(
                                    text = formatted,
                                    selection = TextRange(formatted.length)
                                )
                            },
                            placeholder = { Text("DD/MM/AAAA  (dejar vacío = permanente)", color = Color(0xFF9CA3AF)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = fechaTexto.isNotEmpty() && !fechaValida,
                            supportingText = {
                                if (fechaTexto.isNotEmpty() && !fechaValida) {
                                    Text("Fecha inválida (día 01–31, mes 01–12, año 2025–2035)",
                                        color = Color(0xFFB71C1C))
                                }
                            },
                            enabled = !operacionEnCurso
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                val expBackend = if (fechaCompleta && fechaValida) {
                                    val p = fechaTexto.split("/")
                                    "${p[2]}-${p[1]}-${p[0]}"
                                } else null
                                delegadoViewModel.vincular(emailDelegado.trim(), menor.idMenor, expBackend)
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                            enabled = emailDelegado.isNotBlank() &&
                                    (fechaTexto.isEmpty() || (fechaCompleta && fechaValida)) &&
                                    !operacionEnCurso
                        ) {
                            if (vincularState is VincularState.Loading) {
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
        }
    }
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280),
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("$etiqueta:", fontSize = 13.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
        Text(valor, fontSize = 13.sp, color = Color(0xFF0F172A))
    }
}
