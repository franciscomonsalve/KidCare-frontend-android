package com.example.kidcare.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.kidcare.data.SessionManager
import com.example.kidcare.data.calcularDV
import com.example.kidcare.data.formatearRut
import com.example.kidcare.data.soloLetrasReg
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.GenerarTokenRequest
import com.example.kidcare.data.model.TokenMedicoResponse
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@SuppressLint("MissingPermission")
private suspend fun obtenerCoordenadas(client: FusedLocationProviderClient): Pair<String, String>? =
    suspendCancellableCoroutine { cont ->
        val cts = CancellationTokenSource()
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                cont.resume(loc?.let { "${it.latitude}" to "${it.longitude}" })
            }
            .addOnFailureListener { cont.resume(null) }
        cont.invokeOnCancellation { cts.cancel() }
    }

@Composable
fun EnlaceScreen(navController: NavController, menorId: String = "") {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val context     = LocalContext.current
    val scope       = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val idMenor        = menorId.toIntOrNull() ?: 0
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val nombreTutor    = remember { SessionManager(context).getNombreCompleto() ?: "el tutor" }

    var cargando             by remember { mutableStateOf(false) }
    var obtenendoGps         by remember { mutableStateOf(false) }
    var errorMsg             by remember { mutableStateOf("") }
    var tokenGenerado        by remember { mutableStateOf<TokenMedicoResponse?>(null) }
    var segundos             by remember { mutableStateOf(20 * 60) }
    var canalSeleccionado    by remember { mutableStateOf("QR") }

    // Datos del médico
    var nombreMedicoInput    by remember { mutableStateOf("") }
    var apellidosMedicoInput by remember { mutableStateOf("") }
    var rutMedicoInput       by remember { mutableStateOf("") }

    // Selección de observaciones
    val observacionesMenor = remember { mutableStateListOf<com.example.kidcare.data.model.InteraccionResponse>() }
    var seleccionadasIds   by remember { mutableStateOf(setOf<String>()) }
    var cargandoObs        by remember { mutableStateOf(false) }
    var mostrarSeleccion   by remember { mutableStateOf(false) }

    // Disclaimer
    var mostrarDisclaimer    by remember { mutableStateOf(false) }

    // Derivados del RUT
    val rutRaw       = rutMedicoInput.replace(".", "").replace("-", "")
    val rutCuerpo    = if (rutRaw.length >= 2) rutRaw.dropLast(1) else rutRaw
    val dvCalculado  = if (rutCuerpo.length in 6..8) calcularDV(rutCuerpo) else ""
    val rutValido    = rutRaw.length in 8..9 && rutCuerpo.all { it.isDigit() } &&
                       dvCalculado == rutRaw.last().uppercase().toString()
    val rutError     = rutRaw.length >= 8 && !rutValido

    val enlaceGenerado = tokenGenerado != null

    LaunchedEffect(idMenor) {
        if (idMenor <= 0) return@LaunchedEffect
        cargandoObs = true
        runCatching { RetrofitClient.chatbotApi.listarInteracciones(idMenor) }
            .onSuccess { resp ->
                if (resp.isSuccessful) {
                    val lista = resp.body() ?: emptyList()
                    observacionesMenor.clear()
                    observacionesMenor.addAll(lista)
                    seleccionadasIds = seleccionadasIds + lista.mapNotNull { it.id }
                }
            }
        cargandoObs = false
    }

    LaunchedEffect(enlaceGenerado) {
        if (enlaceGenerado) {
            while (segundos > 0) {
                delay(1000)
                segundos--
            }
        }
    }

    val minutos      = segundos / 60
    val segs         = segundos % 60
    val timerDisplay = "${String.format("%02d", minutos)}:${String.format("%02d", segs)}"
    val expirado     = segundos <= 0

    suspend fun ubicarYGenerar() {
        obtenendoGps = true
        errorMsg = ""
        val coords = obtenerCoordenadas(locationClient)
        obtenendoGps = false
        if (coords == null) {
            errorMsg = "No se pudo obtener la ubicación. Asegúrate de tener el GPS activo."
            return
        }
        cargando = true
        val result = runCatching {
            RetrofitClient.accesoApi.generarTokenMedico(
                GenerarTokenRequest(
                    idMenor        = idMenor,
                    nombreMedico   = "$nombreMedicoInput $apellidosMedicoInput".trim(),
                    rutMedico      = rutMedicoInput,
                    latitudPadre   = coords.first,
                    longitudPadre  = coords.second,
                    observacionIds = if (seleccionadasIds.size < observacionesMenor.size)
                                         seleccionadasIds.toList() else null
                )
            )
        }
        result.onSuccess { resp ->
            if (resp.isSuccessful) {
                tokenGenerado = resp.body()
                segundos = 20 * 60
            } else {
                errorMsg = "No se pudo generar el token. Intenta nuevamente."
            }
        }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION]   == true ||
                        permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            scope.launch { ubicarYGenerar() }
        } else {
            errorMsg = "Se requiere permiso de ubicación para verificar la proximidad con el médico."
        }
    }

    fun ejecutarGeneracion() {
        errorMsg = ""
        val tieneFino   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED
        val tieneGrueso = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (tieneFino || tieneGrueso) {
            scope.launch { ubicarYGenerar() }
        } else {
            permisosLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    fun onGenerarClick() {
        errorMsg = ""
        mostrarDisclaimer = true
    }

    val camposCompletos = nombreMedicoInput.trim().length >= 2 &&
                         apellidosMedicoInput.trim().length >= 2 &&
                         rutValido &&
                         (observacionesMenor.isEmpty() || seleccionadasIds.isNotEmpty())

    // ─── Disclaimer dialog ────────────────────────────────────────────────────
    if (mostrarDisclaimer) {
        AlertDialog(
            onDismissRequest = { mostrarDisclaimer = false },
            containerColor   = Color.White,
            shape            = RoundedCornerShape(18.dp),
            title = {
                Column {
                    Text("⚠️", fontSize = 28.sp, modifier = Modifier.padding(bottom = 6.dp))
                    Text(
                        "Aviso de responsabilidad",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Estimado/a $nombreTutor,",
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)
                    )
                    Text(
                        "Estás a punto de compartir información médica sensible de un menor a tu cargo con:",
                        fontSize = 13.sp, color = Color(0xFF374151), lineHeight = 19.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Médico: $nombreMedicoInput $apellidosMedicoInput",
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Text("RUT: $rutMedicoInput",
                                fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                    Text(
                        "Al continuar confirmas que:",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf(
                            "Eres el responsable legal de este menor.",
                            "Los datos del médico ingresados son correctos.",
                            "Aceptas plena responsabilidad por el acceso que estás autorizando.",
                            "KidCare no verifica la identidad del médico en tiempo real."
                        ).forEach { punto ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", fontSize = 13.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                                Text(punto, fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 18.sp)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF7ED), shape = RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            "El enlace expirará en 20 minutos y solo puede usarse una vez.",
                            fontSize = 11.sp, color = Color(0xFF92400E), lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDisclaimer = false; ejecutarGeneracion() },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    shape  = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirmar y generar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDisclaimer = false },
                    shape   = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar", fontSize = 13.sp)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                TextButton(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) { Text("← Volver", fontSize = 14.sp) }
                Text(
                    if (enlaceGenerado) "Token generado" else "Compartir con médico",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    if (enlaceGenerado) "Válido por 20 minutos" else "Enlace temporal seguro",
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }

        if (!enlaceGenerado) {
            // ─── VISTA PREVIA A GENERAR ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
                ) {
                    Column {
                        Text("¿Cómo compartir?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(
                            "El médico podrá ver la bitácora del menor por 20 minutos. " +
                            "El sistema verifica la proximidad geográfica.",
                            fontSize = 13.sp, color = Color(0xFF6B7280), lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ─── Datos del médico ─────────────────────────────────────────
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🩺", fontSize = 16.sp)
                            Text("Datos del médico",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Text(
                            "Ingresa los datos del profesional con quien compartes la bitácora. " +
                            "Esta información quedará registrada.",
                            fontSize = 12.sp, color = Color(0xFF6B7280), lineHeight = 17.sp
                        )
                        OutlinedTextField(
                            value         = nombreMedicoInput,
                            onValueChange = {
                                val f = soloLetrasReg.replace(it, "")
                                if (f.length <= 25) nombreMedicoInput = f
                            },
                            label         = { Text("Nombre(s)", fontSize = 13.sp) },
                            placeholder   = { Text("Ej: Juan Carlos", fontSize = 13.sp) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(10.dp),
                            isError       = nombreMedicoInput.isNotEmpty() && nombreMedicoInput.trim().length < 2,
                            supportingText = {
                                if (nombreMedicoInput.isNotEmpty() && nombreMedicoInput.trim().length < 2)
                                    Text("Mínimo 2 caracteres", color = Color(0xFFDC2626), fontSize = 11.sp)
                                else
                                    Text("${nombreMedicoInput.length}/25", fontSize = 11.sp,
                                        color = Color(0xFF9CA3AF), modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End)
                            }
                        )
                        OutlinedTextField(
                            value         = apellidosMedicoInput,
                            onValueChange = {
                                val f = soloLetrasReg.replace(it, "")
                                if (f.length <= 35) apellidosMedicoInput = f
                            },
                            label         = { Text("Apellidos", fontSize = 13.sp) },
                            placeholder   = { Text("Ej: Pérez González", fontSize = 13.sp) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(10.dp),
                            isError       = apellidosMedicoInput.isNotEmpty() && apellidosMedicoInput.trim().length < 2,
                            supportingText = {
                                if (apellidosMedicoInput.isNotEmpty() && apellidosMedicoInput.trim().length < 2)
                                    Text("Mínimo 2 caracteres", color = Color(0xFFDC2626), fontSize = 11.sp)
                                else
                                    Text("${apellidosMedicoInput.length}/35", fontSize = 11.sp,
                                        color = Color(0xFF9CA3AF), modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End)
                            }
                        )
                        OutlinedTextField(
                            value         = rutMedicoInput,
                            onValueChange = { nuevo ->
                                val raw = nuevo.replace(".", "").replace("-", "")
                                    .uppercase().filter { it.isDigit() || it == 'K' }.take(9)
                                rutMedicoInput = if (raw.length >= 2) formatearRut(raw) else raw
                            },
                            label          = { Text("RUT del médico", fontSize = 13.sp) },
                            placeholder    = { Text("Ej: 12.345.678-9", fontSize = 13.sp) },
                            modifier       = Modifier.fillMaxWidth(),
                            singleLine     = true,
                            shape          = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            isError        = rutError,
                            supportingText = {
                                when {
                                    rutError ->
                                        Text("RUT inválido. Dígito verificador incorrecto.",
                                            color = Color(0xFFDC2626), fontSize = 11.sp)
                                    rutValido ->
                                        Text("RUT válido ✓", color = Color(0xFF059669), fontSize = 11.sp)
                                    dvCalculado.isNotEmpty() ->
                                        Text("Dígito verificador calculado: $dvCalculado",
                                            color = Color(0xFF2563EB), fontSize = 11.sp)
                                    else ->
                                        Text("Ingresa el RUT sin puntos ni guión",
                                            color = Color(0xFF9CA3AF), fontSize = 11.sp)
                                }
                            }
                        )
                        Row(
                            modifier             = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF7ED), shape = RoundedCornerShape(8.dp))
                                .padding(9.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Text("⚠️", fontSize = 12.sp)
                            Text(
                                "La identidad del médico no es verificada automáticamente. " +
                                "Solo se valida proximidad GPS.",
                                fontSize = 11.sp, color = Color(0xFF92400E), lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ─── Selección de observaciones ──────────────────────────────
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {

                        // Cabecera colapsable
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { mostrarSeleccion = !mostrarSeleccion },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📋", fontSize = 16.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Observaciones a compartir",
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(
                                    when {
                                        cargandoObs -> "Cargando..."
                                        observacionesMenor.isEmpty() -> "Sin observaciones registradas"
                                        seleccionadasIds.size == observacionesMenor.size ->
                                            "Todas (${observacionesMenor.size})"
                                        else -> "${seleccionadasIds.size} de ${observacionesMenor.size} seleccionadas"
                                    },
                                    fontSize = 12.sp, color = Color(0xFF6B7280)
                                )
                            }
                            Text(if (mostrarSeleccion) "▲" else "▼",
                                fontSize = 12.sp, color = Color(0xFF6B7280))
                        }

                        if (mostrarSeleccion) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFE5E7EB))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Botones Todas / Ninguna
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { seleccionadasIds = seleccionadasIds + observacionesMenor.mapNotNull { it.id } },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) { Text("Seleccionar todas", fontSize = 12.sp) }
                                TextButton(
                                    onClick = { seleccionadasIds = emptySet() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) { Text("Ninguna", fontSize = 12.sp) }
                            }

                            if (cargandoObs) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp),
                                        color = azulKidCare, strokeWidth = 2.dp)
                                }
                            } else if (observacionesMenor.isEmpty()) {
                                Text("No hay observaciones registradas aún.",
                                    fontSize = 13.sp, color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(vertical = 8.dp))
                            } else {
                                observacionesMenor.forEach { obs ->
                                    val id = obs.id ?: return@forEach
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .clickable {
                                                seleccionadasIds = if (id in seleccionadasIds)
                                                    seleccionadasIds - id else seleccionadasIds + id
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = id in seleccionadasIds,
                                            onCheckedChange = { checked ->
                                                seleccionadasIds = if (checked)
                                                    seleccionadasIds + id else seleccionadasIds - id
                                            }
                                        )
                                        Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                                            Text(obs.fecha.orEmpty(), fontSize = 11.sp, color = Color(0xFF6B7280))
                                            Text(
                                                obs.observaciones.orEmpty().take(80) +
                                                    if ((obs.observaciones?.length ?: 0) > 80) "…" else "",
                                                fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }

                            if (seleccionadasIds.isEmpty() && observacionesMenor.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Selecciona al menos una observación para continuar.",
                                    fontSize = 12.sp, color = Color(0xFFDC2626))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aviso GPS
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                        Text("📍", fontSize = 14.sp)
                        Text(
                            "Se usará tu ubicación GPS para que el médico pueda verificar la proximidad. " +
                            "Asegúrate de tener el GPS activo.",
                            fontSize = 12.sp, color = Color(0xFF1E3A8A), lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("CANAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280),
                    letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 10.dp))

                // Opción QR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "QR") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "QR") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center) { Text("📱", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Código QR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("El médico escanea desde su celular", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                        if (canalSeleccionado == "QR") {
                            Box(modifier = Modifier.size(20.dp).background(azulKidCare, shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center) {
                                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opción Email
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = if (canalSeleccionado == "EMAIL") 2.dp else 1.dp,
                            color = if (canalSeleccionado == "EMAIL") azulKidCare else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEFF6FF), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center) { Text("✉️", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Correo electrónico", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("Enviar al correo del médico", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick  = { onGenerarClick() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(13.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled  = !cargando && !obtenendoGps && idMenor > 0 && camposCompletos
                ) {
                    when {
                        obtenendoGps -> {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Obteniendo ubicación...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        cargando -> {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        }
                        else -> {
                            Text("Generar enlace temporal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Solo puede existir un enlace activo por hijo a la vez.",
                    fontSize = 12.sp, color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

        } else {
            // ─── VISTA POST-GENERACIÓN ────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(18.dp))
                    .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Muestra este token al médico", fontSize = 13.sp, color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF2F5FB), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), shape = RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tokenGenerado?.token ?: "", fontSize = 14.sp, fontFamily = FontFamily.Monospace,
                                color = Color(0xFF374151), textAlign = TextAlign.Center, lineHeight = 22.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(timerDisplay, fontSize = 36.sp, fontWeight = FontWeight.Bold,
                            color = if (expirado) Color(0xFFDC2626) else azulKidCare,
                            fontFamily = FontFamily.Monospace)
                        Text(
                            if (expirado) "Enlace expirado" else "tiempo restante",
                            fontSize = 12.sp, color = Color(0xFF9CA3AF),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estado", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Box(modifier = Modifier
                                .background(
                                    if (expirado) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    if (expirado) "EXPIRADO" else "ACTIVO",
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = if (expirado) Color(0xFFDC2626) else Color(0xFFD97706)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Canal", fontSize = 13.sp, color = Color(0xFF6B7280))
                            Text("QR", fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                        if (nombreMedicoInput.isNotBlank()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Médico", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(
                                    "$nombreMedicoInput $apellidosMedicoInput",
                                    fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val token = tokenGenerado?.token
                        if (token.isNullOrEmpty()) { navController.popBackStack(); return@OutlinedButton }
                        scope.launch {
                            runCatching { RetrofitClient.accesoApi.revocarTokenMedico(token) }
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(13.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) { Text("Invalidar enlace", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
