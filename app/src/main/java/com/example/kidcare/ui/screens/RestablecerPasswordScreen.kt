package com.example.kidcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas
import com.example.kidcare.ui.viewmodel.AuthState
import com.example.kidcare.ui.viewmodel.AuthViewModel

/**
 * Pantalla de restablecimiento de contraseña — paso 2.
 *
 * El usuario ingresa el código UUID recibido en su correo y elige una nueva
 * contraseña. Al confirmar, se llama a [AuthViewModel.restablecerPassword] que
 * envía el token al backend para validarlo e invalidarlo en la misma operación.
 *
 * En caso de éxito ([AuthState.Success]) la pantalla navega a [LoginScreen] y
 * limpia toda la pila de navegación de recuperación.
 *
 * @param navController controlador de navegación para moverse entre pantallas
 * @param authViewModel ViewModel compartido que gestiona el estado de autenticación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestablecerPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var token by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var passwordsNoCoinciden by remember { mutableStateOf(false) }

    val azulKidCare = Color(0xFF4A90D9)
    val fondoGris = Color(0xFFF7F9FC)

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            navController.navigate(Rutas.LOGIN) {
                popUpTo(Rutas.LOGIN) { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva contraseña") },
                navigationIcon = {
                    IconButton(onClick = {
                        authViewModel.resetState()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulKidCare,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoGris)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Restablecer contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa el código que recibiste por correo y elige tu nueva contraseña.",
                    fontSize = 14.sp,
                    color = Color(0xFF718096),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (authState is AuthState.Error) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = Color(0xFFB71C1C),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (passwordsNoCoinciden) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = Color(0xFFB71C1C),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Código de recuperación") },
                    placeholder = { Text("Pega aquí el código del correo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = authState !is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = {
                        nuevaPassword = it
                        passwordsNoCoinciden = false
                    },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = authState !is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmarPassword,
                    onValueChange = {
                        confirmarPassword = it
                        passwordsNoCoinciden = false
                    },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = authState !is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nuevaPassword != confirmarPassword) {
                            passwordsNoCoinciden = true
                            return@Button
                        }
                        authViewModel.restablecerPassword(token.trim(), nuevaPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulKidCare),
                    enabled = authState !is AuthState.Loading
                            && token.isNotBlank()
                            && nuevaPassword.isNotBlank()
                            && confirmarPassword.isNotBlank()
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Restablecer contraseña",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
