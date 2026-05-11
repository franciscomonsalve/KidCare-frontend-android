package com.example.kidcare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidcare.R
import com.example.kidcare.data.SessionManager
import com.example.kidcare.data.api.RetrofitClient
import com.example.kidcare.navigation.Rutas
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000)
        val session = SessionManager(context)
        val token = session.getToken()
        val rol   = session.getRol()

        if (!token.isNullOrEmpty() && !rol.isNullOrEmpty()) {
            RetrofitClient.jwtToken = token
            val destino = when (rol) {
                "DELEGADO" -> Rutas.HOME_DELEGADO
                else       -> Rutas.HOME
            }
            navController.navigate(destino) {
                popUpTo(Rutas.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Rutas.LOGIN) {
                popUpTo(Rutas.SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0F1A3D), Color(0xFF2563EB))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KidCare Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "KidCare",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bitácora de salud pediátrica",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
