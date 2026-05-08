package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.data.network.ApiClient
import com.example.kidcare.data.preferences.SessionManager
import com.example.kidcare.navigation.Rutas
import com.example.kidcare.ui.viewmodel.MenorViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    menorViewModel: MenorViewModel = viewModel()
) {
    val azulKidCare = Color(0xFF2563EB)
    val azulOscuro = Color(0xFF1E3A8A)
    val azulTeal = Color(0xFF0891B2)

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userEmail = sessionManager.getEmail() ?: "Usuario"
    val userRol = sessionManager.getRol() ?: ""
    val esTutor = userRol.equals("TUTOR", ignoreCase = true)

    val menores by menorViewModel.menores.collectAsState()
    val loading by menorViewModel.loading.collectAsState()

    var tabSeleccionado by remember { mutableStateOf(0) }
    var menorSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var menorAEliminar by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { menorViewModel.cargarMenores() }

    LaunchedEffect(menores) {
        if (menorSeleccionadoId == null && menores.isNotEmpty()) {
            menorSeleccionadoId = menores.first().idMenor
        }
    }

    val menorActual = menores.find { it.idMenor == menorSeleccionadoId } ?: menores.firstOrNull()
    val menorId = menorActual?.idMenor?.toString() ?: "0"

    if (menorAEliminar != null) {
        AlertDialog(
            onDismissRequest = { menorAEliminar = null },
            title = { Text("Eliminar menor") },
            text = { Text("¿Estás seguro de que quieres eliminar este perfil? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    menorViewModel.eliminarMenor(menorAEliminar!!)
                    if (menorSeleccionadoId == menorAEliminar) menorSeleccionadoId = null
                    menorAEliminar = null
                }) { Text("Eliminar", color = Color(0xFFDC2626)) }
            },
            dismissButton = {
                TextButton(onClick = { menorAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    icon = { Text("🏠", fontSize = 22.sp) },
                    label = { Text("Inicio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = azulKidCare,
                        selectedTextColor = azulKidCare,
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("bitacora/$menorId") },
                    icon = { Text("📋", fontSize = 22.sp) },
                    label = { Text("Bitácora", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = azulKidCare,
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("chatbot/$menorId") },
                    icon = { Text("💬", fontSize = 22.sp) },
                    label = { Text("Chatbot", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = azulKidCare,
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 3,
                    onClick = { tabSeleccionado = 3 },
                    icon = { Text("👤", fontSize = 22.sp) },
                    label = { Text("Perfil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = azulKidCare,
                        selectedTextColor = azulKidCare,
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
            }
        }
    ) { innerPadding ->
        when (tabSeleccionado) {
            0 -> InicioTab(
                innerPadding = innerPadding,
                navController = navController,
                menorViewModel = menorViewModel,
                azulKidCare = azulKidCare,
                azulOscuro = azulOscuro,
                azulTeal = azulTeal,
                userEmail = userEmail,
                esTutor = esTutor,
                menores = menores,
                loading = loading,
                menorSeleccionadoId = menorSeleccionadoId,
                menorActual = menorActual,
                menorId = menorId,
                onSeleccionarMenor = { menorSeleccionadoId = it },
                onEliminarMenor = { menorAEliminar = it }
            )
            3 -> PerfilTab(
                innerPadding = innerPadding,
                userEmail = userEmail,
                userRol = userRol,
                azulKidCare = azulKidCare,
                onInvitarApoderado = { navController.navigate(Rutas.INVITAR_APODERADO) },
                onCerrarSesion = {
                    sessionManager.clearSession()
                    ApiClient.authToken = null
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun InicioTab(
    innerPadding: PaddingValues,
    navController: NavController,
    menorViewModel: MenorViewModel,
    azulKidCare: Color,
    azulOscuro: Color,
    azulTeal: Color,
    userEmail: String,
    esTutor: Boolean,
    menores: List<com.example.kidcare.data.model.MenorResponse>,
    loading: Boolean,
    menorSeleccionadoId: Int?,
    menorActual: com.example.kidcare.data.model.MenorResponse?,
    menorId: String,
    onSeleccionarMenor: (Int) -> Unit,
    onEliminarMenor: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
            .padding(innerPadding)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(azulOscuro, azulKidCare, azulTeal)
                        )
                    )
                    .padding(top = 48.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bienvenido,",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${userEmail.substringBefore("@")} 👋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userEmail.first().uppercaseChar().toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(start = 4.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(menores) { menor ->
                                val seleccionado = menor.idMenor == menorSeleccionadoId
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (seleccionado) Color.White.copy(alpha = 0.92f)
                                            else Color.White.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onSeleccionarMenor(menor.idMenor) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (menor.sexo?.lowercase() == "femenino") "👧" else "👦",
                                            fontSize = 22.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = menor.nombre,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (seleccionado) azulKidCare else Color.White
                                        )
                                        if (esTutor) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "×",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (seleccionado) Color(0xFFDC2626)
                                                else Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.clickable { onEliminarMenor(menor.idMenor) }
                                            )
                                        }
                                    }
                                }
                            }

                            if (menores.isEmpty()) {
                                item {
                                    Text(
                                        text = "Sin menores registrados",
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            if (esTutor) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.White.copy(alpha = 0.14f),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { navController.navigate(Rutas.AGREGAR_MENOR) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "+ Agregar",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "¿Qué quieres hacer?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                brush = Brush.linearGradient(colors = listOf(azulKidCare, azulTeal)),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { navController.navigate("chatbot/$menorId") }
                            .padding(18.dp)
                    ) {
                        Column {
                            Text("💬", fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Registrar\nobservación",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "Con asistente IA",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { navController.navigate("bitacora/$menorId") }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("📋", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ver bitácora",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = menorActual?.nombre ?: "—",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(18.dp))
                                .clickable { navController.navigate("enlace/$menorId") }
                                .padding(18.dp)
                        ) {
                            Column {
                                Text("🔗", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Compartir\ncon médico",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A),
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "Enlace temporal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun PerfilTab(
    innerPadding: PaddingValues,
    userEmail: String,
    userRol: String,
    azulKidCare: Color,
    onInvitarApoderado: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val rolColor = if (userRol.equals("TUTOR", ignoreCase = true))
        Color(0xFF2563EB) else Color(0xFF0891B2)
    val rolLabel = when (userRol.uppercase()) {
        "TUTOR" -> "Tutor principal"
        "DELEGADO" -> "Cuidador delegado"
        "ADMIN" -> "Administrador"
        else -> userRol
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
            .padding(innerPadding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .background(azulKidCare, shape = RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userEmail.first().uppercaseChar().toString(),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userEmail.substringBefore("@"),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Text(
            text = userEmail,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .background(rolColor.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = rolLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = rolColor
            )
        }

        if (userRol.equals("DELEGADO", ignoreCase = true)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Text("⚠️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tienes acceso limitado. No puedes agregar ni eliminar perfiles de menores.",
                        fontSize = 12.sp,
                        color = Color(0xFF92400E),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(color = Color(0xFFE5E7EB))

        Spacer(modifier = Modifier.height(24.dp))

        if (userRol.equals("TUTOR", ignoreCase = true)) {
            OutlinedButton(
                onClick = onInvitarApoderado,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
            ) {
                Text("👥  Invitar apoderado", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
