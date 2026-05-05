package com.example.kidcare.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.navigation.Rutas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.kidcare.R




@Composable
fun LoginScreen(navController: NavController) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val azulKidCare = Color(0xFF4A90D9)
    val azulClaro   = Color(0xFF6BB8F0)
    val fondoGris   = Color(0xFFF7F9FC)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGris),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KidCare Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            //Text(
            //    text = "KidCare",
            //    fontSize = 28.sp,
            //    fontWeight = FontWeight.Bold,
            //    color = Color(0xFF2D3748)
            //)

            Text(
                text = "Bitácora de salud pediátrica",
                fontSize = 14.sp,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Campo correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón iniciar sesión
            Button(
                onClick = { navController.navigate(Rutas.HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulKidCare)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón registro
            OutlinedButton(
                onClick = { navController.navigate(Rutas.REGISTRO) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = azulKidCare)
            ) {
                Text(
                    text = "Crear cuenta nueva",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = azulKidCare,
                    fontSize = 14.sp
                )
            }
        }
    }
}