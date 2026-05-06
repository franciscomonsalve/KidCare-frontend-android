package com.example.kidcare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.ui.screens.AgregarMenorScreen
import com.example.kidcare.ui.screens.BitacoraScreen
import com.example.kidcare.ui.screens.ChatbotScreen
import com.example.kidcare.ui.screens.DelegadoScreen
import com.example.kidcare.ui.screens.EnlaceScreen
import com.example.kidcare.ui.screens.HomeScreen
import com.example.kidcare.ui.screens.InvitarDelegadoScreen
import com.example.kidcare.ui.screens.LoginScreen
import com.example.kidcare.ui.screens.PerfilScreen
import com.example.kidcare.ui.screens.RegistroScreen
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

    const val DELEGADOS      = "delegados/{menorId}"

    const val INVITAR_DELEGADO = "invitar_delegado/{menorId}"

    const val REGISTRO_DELEGADO = "registro_delegado"
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
            RegistroScreen(navController)

        }
        composable(Rutas.HOME) {
            HomeScreen(navController)

        }
        composable(Rutas.AGREGAR_MENOR) {
            AgregarMenorScreen(navController)
        }
        composable(Rutas.BITACORA) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId") ?: ""
            BitacoraScreen(navController)

        }
        composable(Rutas.CHATBOT) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId") ?: ""
            ChatbotScreen(navController)
        }

        composable(Rutas.ENLACE) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId") ?: ""
            EnlaceScreen(navController)

        }
        composable(Rutas.CONFIGURACION) {
            PerfilScreen(navController)
        }
        composable(Rutas.DELEGADOS) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId") ?: ""
            DelegadoScreen(navController, menorId)
        }
        composable(Rutas.INVITAR_DELEGADO) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId") ?: ""
            InvitarDelegadoScreen(navController)
        }

    }
}