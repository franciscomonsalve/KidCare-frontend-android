package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.data.model.AdminUsuarioResponse
import com.example.kidcare.data.model.CambiarRolRequest
import com.example.kidcare.data.model.CrearUsuarioAdminRequest
import com.example.kidcare.data.model.EditarUsuarioAdminRequest
import com.example.kidcare.data.AuditoriaLocal
import com.example.kidcare.navigation.Rutas
import com.example.kidcare.ui.theme.campoColores
import kotlinx.coroutines.launch

private fun fechaAdminParaApi(fecha: String): String {
    val partes = fecha.trim().split("/")
    if (partes.size != 3) return fecha
    val (dia, mes, anio) = partes
    return "${anio.padStart(4,'0')}-${mes.padStart(2,'0')}-${dia.padStart(2,'0')}"
}

@Composable
fun AdminUsuariosScreen(navController: NavController) {

    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro  = Color(0xFF1E3A8A)
    val scope       = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val usuarios = remember { mutableStateListOf<AdminUsuarioResponse>() }

    val roles = listOf("TUTOR" to 2, "ADMIN" to 1)

    var usuarioAccion         by remember { mutableStateOf<AdminUsuarioResponse?>(null) }
    var mostrarRolDialog      by remember { mutableStateOf(false) }
    var mostrarCrearDialog    by remember { mutableStateOf(false) }
    var mostrarEditarDialog   by remember { mutableStateOf(false) }
    var mostrarEliminarDialog by remember { mutableStateOf(false) }

    var nombreNuevo      by remember { mutableStateOf("") }
    var emailNuevo       by remember { mutableStateOf("") }
    var passwordNuevo    by remember { mutableStateOf("") }
    var idRolNuevo       by remember { mutableStateOf(2) }
    var errorCrearMsg    by remember { mutableStateOf("") }

    var nombreEditar by remember { mutableStateOf("") }
    var emailEditar  by remember { mutableStateOf("") }
    var idRolEditar  by remember { mutableStateOf(1) }

    var mostrarCrearMenorDialog by remember { mutableStateOf(false) }
    var usuarioParaMenor        by remember { mutableStateOf<AdminUsuarioResponse?>(null) }
    var nombreNuevoMenor        by remember { mutableStateOf("") }
    var fechaNuevoMenor         by remember { mutableStateOf(TextFieldValue("")) }
    var sexoNuevoMenor          by remember { mutableStateOf("M") }
    var emojiNuevoMenor         by remember { mutableStateOf("🧒") }
    var errorMenorMsg           by remember { mutableStateOf("") }
    val sexos   = listOf("M" to "Masculino", "F" to "Femenino")
    val emojis  = listOf("👧", "👦", "🧒", "👶")

    LaunchedEffect(Unit) {
        cargando = true
        runCatching { RetrofitClient.api.listarUsuarios() }
            .onSuccess { resp ->
                if (resp.isSuccessful) {
                    usuarios.clear(); usuarios.addAll(resp.body() ?: emptyList())
                } else { errorMsg = "No se pudo cargar la lista de usuarios." }
            }.onFailure { errorMsg = "Error de conexión." }
        cargando = false
    }

    // Dialog: cambiar rol
    if (mostrarRolDialog && usuarioAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarRolDialog = false },
            title = { Text("Cambiar rol", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Selecciona el nuevo rol para ${usuarioAccion!!.email.orEmpty()}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    roles.forEach { (nombre, idRol) ->
                        TextButton(onClick = {
                            val id = usuarioAccion!!.idUsuario
                            val emailU = usuarioAccion!!.email.orEmpty()
                            scope.launch {
                                runCatching { RetrofitClient.api.cambiarRol(id, CambiarRolRequest(idRol)) }
                                    .onSuccess { resp ->
                                        if (resp.isSuccessful) {
                                            val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                            if (idx >= 0) usuarios[idx] = usuarios[idx].copy(rol = nombre)
                                            AuditoriaLocal.registrar("MODIFICAR_ROL", "USUARIO", "Email: $emailU → $nombre")
                                        }
                                    }
                            }
                            mostrarRolDialog = false
                        }) { Text(nombre, fontSize = 16.sp) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { mostrarRolDialog = false }) { Text("Cancelar") } }
        )
    }

    // Dialog: crear usuario
    if (mostrarCrearDialog) {
        AlertDialog(
            onDismissRequest = { mostrarCrearDialog = false },
            title = { Text("Crear usuario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreNuevo, onValueChange = { nombreNuevo = it },
                        label = { Text("Nombre completo") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    OutlinedTextField(
                        value = emailNuevo, onValueChange = { emailNuevo = it },
                        label = { Text("Email") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    OutlinedTextField(
                        value = passwordNuevo, onValueChange = { passwordNuevo = it; errorCrearMsg = "" },
                        label = { Text("Contraseña") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    Text(
                        "La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un símbolo especial (ej: Admin@2024!)",
                        fontSize = 11.sp, color = Color(0xFF6B7280),
                        lineHeight = 15.sp
                    )
                    if (errorCrearMsg.isNotEmpty()) {
                        Text(errorCrearMsg, fontSize = 12.sp, color = Color(0xFFDC2626))
                    }
                    Text("ROL", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        roles.forEach { (nombre, id) ->
                            FilterChip(
                                selected = idRolNuevo == id, onClick = { idRolNuevo = id },
                                label = { Text(nombre, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = azulKidCare)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreNuevo.isBlank() || emailNuevo.isBlank() || passwordNuevo.isBlank()) return@Button
                        scope.launch {
                            runCatching {
                                RetrofitClient.api.crearUsuarioAdmin(
                                    CrearUsuarioAdminRequest(nombreNuevo, emailNuevo, passwordNuevo, idRolNuevo))
                            }.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    resp.body()?.let { usuarios.add(it) }
                                    AuditoriaLocal.registrar("CREAR", "USUARIO", "Email: $emailNuevo")
                                    mostrarCrearDialog = false
                                    nombreNuevo = ""; emailNuevo = ""; passwordNuevo = ""; idRolNuevo = 2
                                    errorCrearMsg = ""
                                } else {
                                    errorCrearMsg = "Error: verifica que el email no esté en uso y la contraseña cumpla los requisitos."
                                }
                            }.onFailure { errorCrearMsg = "Error de conexión con el servidor." }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarCrearDialog = false
                    nombreNuevo = ""; emailNuevo = ""; passwordNuevo = ""; idRolNuevo = 2; errorCrearMsg = ""
                }) { Text("Cancelar") }
            }
        )
    }

    // Dialog: editar usuario
    if (mostrarEditarDialog && usuarioAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarEditarDialog = false },
            title = { Text("Editar usuario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreEditar, onValueChange = { nombreEditar = it },
                        label = { Text("Nombre completo") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    OutlinedTextField(
                        value = emailEditar, onValueChange = { emailEditar = it },
                        label = { Text("Email") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    Text("ROL", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        roles.forEach { (nombre, id) ->
                            FilterChip(
                                selected = idRolEditar == id, onClick = { idRolEditar = id },
                                label = { Text(nombre, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = azulKidCare)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreEditar.isBlank() || emailEditar.isBlank()) return@Button
                        val id = usuarioAccion!!.idUsuario
                        scope.launch {
                            runCatching {
                                RetrofitClient.api.editarUsuarioAdmin(id,
                                    EditarUsuarioAdminRequest(nombreEditar, emailEditar, idRolEditar))
                            }.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    val rolNombre = roles.find { it.second == idRolEditar }?.first
                                    val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                    if (idx >= 0) usuarios[idx] = usuarios[idx].copy(
                                        nombreCompleto = nombreEditar, email = emailEditar,
                                        rol = rolNombre ?: usuarios[idx].rol)
                                    AuditoriaLocal.registrar("EDITAR", "USUARIO", "Email: $emailEditar")
                                }
                            }
                            mostrarEditarDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { mostrarEditarDialog = false }) { Text("Cancelar") } }
        )
    }

    // Dialog: confirmar eliminación
    if (mostrarEliminarDialog && usuarioAccion != null) {
        AlertDialog(
            onDismissRequest = { mostrarEliminarDialog = false },
            title = { Text("Eliminar usuario", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Eliminar a ${usuarioAccion!!.nombreCompleto.orEmpty()}? Esta acción es permanente e irrecuperable. Se eliminarán también sus menores huérfanos.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = usuarioAccion!!.idUsuario
                        val emailEliminado = usuarioAccion!!.email.orEmpty()
                        scope.launch {
                            runCatching { RetrofitClient.api.eliminarUsuarioAdmin(id) }
                                .onSuccess { resp ->
                                    if (resp.isSuccessful) {
                                        usuarios.removeIf { it.idUsuario == id }
                                        AuditoriaLocal.registrar("ELIMINAR", "USUARIO", "Email: $emailEliminado")
                                    }
                                }
                            mostrarEliminarDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626))
                ) { Text("Eliminar", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { mostrarEliminarDialog = false }) { Text("Cancelar") } }
        )
    }

    // Dialog: crear menor para un usuario específico
    if (mostrarCrearMenorDialog && usuarioParaMenor != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarCrearMenorDialog = false
                nombreNuevoMenor = ""; fechaNuevoMenor = TextFieldValue("")
                sexoNuevoMenor = "M"; emojiNuevoMenor = "🧒"; errorMenorMsg = ""
            },
            title = { Text("Crear menor para\n${usuarioParaMenor!!.nombreCompleto.orEmpty()}", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Selector de emoji
                    Text("ÍCONO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        emojis.forEach { emoji ->
                            val seleccionado = emoji == emojiNuevoMenor
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .then(
                                        if (seleccionado)
                                            Modifier.border(2.dp, azulKidCare, RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .background(
                                        if (seleccionado) Color(0xFFDBEAFE) else Color(0xFFF3F4F6),
                                        shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { emojiNuevoMenor = emoji },
                                    contentPadding = PaddingValues(0.dp)) {
                                    Text(emoji, fontSize = 26.sp)
                                }
                            }
                        }
                    }
                    // Nombre
                    OutlinedTextField(
                        value = nombreNuevoMenor, onValueChange = { nombreNuevoMenor = it; errorMenorMsg = "" },
                        label = { Text("Nombre del menor") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores()
                    )
                    // Fecha con auto-formato DD/MM/AAAA
                    OutlinedTextField(
                        value = fechaNuevoMenor,
                        onValueChange = { input ->
                            val digits = input.text.filter { it.isDigit() }.take(8)
                            val formatted = buildString {
                                digits.forEachIndexed { i, c ->
                                    if (i == 2 || i == 4) append('/')
                                    append(c)
                                }
                            }
                            errorMenorMsg = ""
                            fechaNuevoMenor = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
                        },
                        label = { Text("Fecha nacimiento") },
                        placeholder = { Text("DD/MM/AAAA", color = Color(0xFF9CA3AF)) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), colors = campoColores(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    // Sexo
                    Text("SEXO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280), letterSpacing = 0.6.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sexos.forEach { (codigo, etiqueta) ->
                            FilterChip(
                                selected = sexoNuevoMenor == codigo,
                                onClick = { sexoNuevoMenor = codigo },
                                label = { Text(etiqueta, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = azulKidCare)
                            )
                        }
                    }
                    if (errorMenorMsg.isNotEmpty()) {
                        Text(errorMenorMsg, fontSize = 12.sp, color = Color(0xFFDC2626))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val fechaTexto = fechaNuevoMenor.text
                        if (nombreNuevoMenor.isBlank() || fechaTexto.length < 10) {
                            errorMenorMsg = "Nombre y fecha completa (DD/MM/AAAA) son obligatorios."
                            return@Button
                        }
                        val idUsuario = usuarioParaMenor!!.idUsuario
                        val nombreUsuario = usuarioParaMenor!!.nombreCompleto.orEmpty()
                        scope.launch {
                            runCatching {
                                RetrofitClient.api.crearMenorParaUsuario(
                                    idUsuario,
                                    com.example.kidcare.data.model.MenorRequest(
                                        nombreNuevoMenor, fechaAdminParaApi(fechaTexto),
                                        sexoNuevoMenor, emojiNuevoMenor))
                            }.onSuccess { resp ->
                                if (resp.isSuccessful) {
                                    AuditoriaLocal.registrar("CREAR", "MENOR",
                                        "Nombre: $nombreNuevoMenor → Usuario: $nombreUsuario")
                                    mostrarCrearMenorDialog = false
                                    nombreNuevoMenor = ""; fechaNuevoMenor = TextFieldValue("")
                                    sexoNuevoMenor = "M"; emojiNuevoMenor = "🧒"; errorMenorMsg = ""
                                } else {
                                    errorMenorMsg = "Error al crear el menor. Verifica los datos."
                                }
                            }.onFailure { errorMenorMsg = "Error de conexión." }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarCrearMenorDialog = false
                    nombreNuevoMenor = ""; fechaNuevoMenor = TextFieldValue("")
                    sexoNuevoMenor = "M"; emojiNuevoMenor = "🧒"; errorMenorMsg = ""
                }) { Text("Cancelar") }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F5FB))) {

        // Header — gradiente estándar de la app
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.linearGradient(colors = listOf(azulOscuro, azulKidCare)))
                    .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    TextButton(onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                    ) { Text("← Volver", fontSize = 14.sp) }
                    Text("Panel Admin — Usuarios", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    Text("${usuarios.size} usuarios registrados", fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }

        // Acciones principales
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { mostrarCrearDialog = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
                ) { Text("+ Crear usuario", fontSize = 13.sp) }
            }
        }

        item {
            Button(
                onClick = { navController.navigate(Rutas.AUDITORIA) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2))
            ) { Text("📋 Ver auditoría", fontSize = 14.sp) }
        }

        if (cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = azulKidCare)
                }
            }
        } else if (errorMsg.isNotEmpty()) {
            item {
                Text(errorMsg, fontSize = 13.sp, color = Color(0xFFDC2626),
                    modifier = Modifier.padding(16.dp))
            }
        } else {
            item {
                Text("USUARIOS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280), letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }

            items(usuarios) { usuario ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .background(Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombreCompleto.orEmpty(), fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(usuario.email.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7280))
                                Text("Rol: ${usuario.rol.orEmpty()}", fontSize = 11.sp,
                                    color = azulKidCare, modifier = Modifier.padding(top = 2.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (usuario.activo) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (usuario.activo) "ACTIVO" else "INACTIVO",
                                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = if (usuario.activo) Color(0xFF059669) else Color(0xFFDC2626))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fila 1: habilitar/deshabilitar + cambiar rol
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val id = usuario.idUsuario
                                        val emailU = usuario.email.orEmpty()
                                        if (usuario.activo) {
                                            runCatching { RetrofitClient.api.deshabilitarUsuario(id) }
                                                .onSuccess { resp ->
                                                    if (resp.isSuccessful) {
                                                        val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                                        if (idx >= 0) usuarios[idx] = usuarios[idx].copy(activo = false)
                                                        AuditoriaLocal.registrar("DESHABILITAR", "USUARIO", "Email: $emailU")
                                                    }
                                                }
                                        } else {
                                            runCatching { RetrofitClient.api.habilitarUsuario(id) }
                                                .onSuccess { resp ->
                                                    if (resp.isSuccessful) {
                                                        val idx = usuarios.indexOfFirst { it.idUsuario == id }
                                                        if (idx >= 0) usuarios[idx] = usuarios[idx].copy(activo = true)
                                                        AuditoriaLocal.registrar("HABILITAR", "USUARIO", "Email: $emailU")
                                                    }
                                                }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (usuario.activo) Color(0xFFDC2626) else Color(0xFF059669))
                            ) { Text(if (usuario.activo) "Deshabilitar" else "Habilitar", fontSize = 12.sp) }

                            OutlinedButton(
                                onClick = { usuarioAccion = usuario; mostrarRolDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
                            ) { Text("Cambiar rol", fontSize = 12.sp) }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Fila 2: editar + eliminar
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    usuarioAccion = usuario
                                    nombreEditar = usuario.nombreCompleto.orEmpty()
                                    emailEditar  = usuario.email.orEmpty()
                                    idRolEditar  = roles.find { it.first == usuario.rol }?.second ?: 1
                                    mostrarEditarDialog = true
                                },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A))
                            ) { Text("Editar", fontSize = 12.sp) }

                            OutlinedButton(
                                onClick = { usuarioAccion = usuario; mostrarEliminarDialog = true },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) { Text("Eliminar", fontSize = 12.sp) }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Fila 3: crear menor
                        OutlinedButton(
                            onClick = {
                                usuarioParaMenor = usuario
                                mostrarCrearMenorDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0891B2))
                        ) { Text("+ Crear menor", fontSize = 12.sp) }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
