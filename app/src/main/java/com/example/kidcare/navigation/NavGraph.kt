package com.example.kidcare.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.ui.screens.AgregarMenorScreen
import com.example.kidcare.ui.screens.BitacoraScreen
import com.example.kidcare.ui.screens.ChatbotScreen
import com.example.kidcare.ui.screens.EnlaceScreen
import com.example.kidcare.ui.screens.HomeScreen
import com.example.kidcare.ui.screens.InvitarApoderadoScreen
import com.example.kidcare.ui.screens.LoginScreen
import com.example.kidcare.ui.screens.RecuperarPasswordScreen
import com.example.kidcare.ui.screens.RegistroScreen
import com.example.kidcare.ui.screens.RestablecerPasswordScreen
import com.example.kidcare.ui.screens.SplashScreen
import com.example.kidcare.ui.viewmodel.MenorViewModel

/**
 * Rutas de navegación de la aplicación.
 *
 * Cada constante es el identificador de ruta que usa [NavHost] para registrar
 * y navegar a la pantalla correspondiente. Las rutas con parámetros usan la
 * sintaxis `{argumento}` de Navigation Compose.
 */
object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home"
    const val AGREGAR_MENOR = "agregar_menor"
    const val BITACORA = "bitacora/{menorId}"
    const val CHATBOT = "chatbot/{menorId}"
    const val ENLACE = "enlace/{menorId}"
    const val INVITAR_APODERADO = "invitar_apoderado"
    const val CONFIGURACION = "configuracion"
    const val RECUPERAR_PASSWORD = "recuperar_password"
    const val RESTABLECER_PASSWORD = "restablecer_password"
}

/**
 * Grafo de navegación principal de la aplicación.
 *
 * Define todas las pantallas y sus rutas. El punto de inicio es [Rutas.SPLASH],
 * que redirige automáticamente a [Rutas.LOGIN] o [Rutas.HOME] según si el usuario
 * ya tiene sesión activa.
 *
 * [MenorViewModel] se instancia aquí para compartirlo entre [HomeScreen] y
 * [AgregarMenorScreen] sin pérdida de estado al navegar.
 *
 * @param navController controlador de navegación; se crea uno nuevo por defecto
 */
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val menorViewModel: MenorViewModel = viewModel()

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
            HomeScreen(navController, menorViewModel)
        }
        composable(Rutas.AGREGAR_MENOR) {
            AgregarMenorScreen(navController, menorViewModel)
        }
        composable(Rutas.BITACORA) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId")?.toIntOrNull() ?: 0
            BitacoraScreen(navController, menorId)
        }
        composable(Rutas.CHATBOT) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId")?.toIntOrNull() ?: 0
            ChatbotScreen(navController, menorId)
        }
        composable(Rutas.ENLACE) { backStackEntry ->
            val menorId = backStackEntry.arguments?.getString("menorId")?.toIntOrNull() ?: 0
            EnlaceScreen(navController)
        }
        composable(Rutas.INVITAR_APODERADO) {
            InvitarApoderadoScreen(navController, menorViewModel)
        }
        composable(Rutas.CONFIGURACION) { }
        composable(Rutas.RECUPERAR_PASSWORD) {
            RecuperarPasswordScreen(navController)
        }
        composable(Rutas.RESTABLECER_PASSWORD) {
            RestablecerPasswordScreen(navController)
        }
    }
}
