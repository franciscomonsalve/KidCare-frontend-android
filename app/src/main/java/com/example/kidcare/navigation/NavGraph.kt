package com.example.kidcare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.ui.screens.LoginScreen
import com.example.kidcare.ui.screens.SplashScreen

object Rutas {
    const val SPLASH        = "splash"
    const val LOGIN         = "login"
    const val REGISTRO      = "registro"
    const val HOME          = "home"
    const val AGREGAR_MENOR = "agregar_menor"
    const val BITACORA      = "bitacora/{menorId}"
    const val CHATBOT       = "chatbot/{menorId}"
    const val ENLACE        = "enlace/{menorId}"
    const val CONFIGURACION = "configuracion"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {
        composable(Rutas.SPLASH) {
            SplashScreen(navController)
        }
        composable(Rutas.LOGIN) {
            LoginScreen(navController)
        }
        composable(Rutas.REGISTRO) {
            // RegistroScreen(navController)
        }
        composable(Rutas.HOME) {
            // HomeScreen(navController)
        }
        composable(Rutas.AGREGAR_MENOR) {
            // AgregarMenorScreen(navController)
        }
        composable(Rutas.BITACORA) {
            // BitacoraScreen(navController)
        }
        composable(Rutas.CHATBOT) {
            // ChatbotScreen(navController)
        }
        composable(Rutas.ENLACE) {
            // EnlaceScreen(navController)
        }
        composable(Rutas.CONFIGURACION) {
            // ConfiguracionScreen(navController)
        }
    }
}