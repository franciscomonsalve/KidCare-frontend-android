package com.example.kidcare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.ui.screens.*

object Rutas {
    const val SPLASH               = "splash"
    const val LOGIN                = "login"
    const val REGISTRO             = "registro"
    const val HOME                 = "home"
    const val AGREGAR_MENOR        = "agregar_menor"
    const val BITACORA             = "bitacora/{menorId}"
    const val CHATBOT              = "chatbot/{menorId}"
    const val ENLACE               = "enlace/{menorId}"
    const val CONFIGURACION        = "configuracion"
    const val DELEGADOS            = "delegados/{menorId}"
    const val INVITAR_DELEGADO     = "invitar_delegado/{menorId}"
    const val REGISTRO_DELEGADO    = "registro_delegado"
    const val CAMBIAR_CONTRASENA   = "cambiar_contrasena"
    const val RECUPERAR_CONTRASENA = "recuperar_contrasena"
    const val HOME_DELEGADO        = "home_delegado"
    const val VINCULAR_MENOR       = "vincular_menor"

    // Nuevas rutas
    const val PERFIL_MENOR        = "perfil_menor/{menorId}"
    const val INTERACCION_MANUAL  = "interaccion_manual/{menorId}"
    const val GENERAR_HISTORIAL   = "generar_historial/{menorId}"
    const val HISTORIAL_LISTA     = "historial_lista/{menorId}"
    const val ADMIN_USUARIOS      = "admin_usuarios"
    const val AUDITORIA           = "auditoria"

    fun bitacora(menorId: Int)        = "bitacora/$menorId"
    fun chatbot(menorId: Int)         = "chatbot/$menorId"
    fun enlace(menorId: Int)          = "enlace/$menorId"
    fun delegados(menorId: Int)       = "delegados/$menorId"
    fun invitarDelegado(menorId: Int) = "invitar_delegado/$menorId"
    fun perfilMenor(menorId: Int)     = "perfil_menor/$menorId"
    fun interaccionManual(menorId: Int) = "interaccion_manual/$menorId"
    fun generarHistorial(menorId: Int)  = "generar_historial/$menorId"
    fun historialLista(menorId: Int)    = "historial_lista/$menorId"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) { SplashScreen(navController) }
        composable(Rutas.LOGIN) { LoginScreen(navController) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController) }
        composable(Rutas.HOME) { HomeScreen(navController) }
        composable(Rutas.HOME_DELEGADO) { HomeDelegadoScreen(navController) }

        composable(Rutas.AGREGAR_MENOR) { AgregarMenorScreen(navController) }
        composable(Rutas.VINCULAR_MENOR) { VincularMenorScreen(navController) }

        composable(Rutas.PERFIL_MENOR) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            PerfilMenorScreen(navController, menorId)
        }

        composable(Rutas.BITACORA) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            BitacoraScreen(navController, menorId)
        }

        composable(Rutas.CHATBOT) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            ChatbotScreen(navController, menorId)
        }

        composable(Rutas.ENLACE) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            EnlaceScreen(navController, menorId)
        }

        composable(Rutas.DELEGADOS) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            DelegadoScreen(navController, menorId)
        }

        composable(Rutas.INVITAR_DELEGADO) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            InvitarDelegadoScreen(navController, menorId)
        }

        composable(Rutas.INTERACCION_MANUAL) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            InteraccionManualScreen(navController, menorId)
        }

        composable(Rutas.HISTORIAL_LISTA) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            HistorialListaScreen(navController, menorId)
        }

        composable(Rutas.GENERAR_HISTORIAL) { backStack ->
            val menorId = backStack.arguments?.getString("menorId") ?: ""
            GenerarHistorialScreen(navController, menorId)
        }

        composable(Rutas.CONFIGURACION) { PerfilScreen(navController) }
        composable(Rutas.CAMBIAR_CONTRASENA) { CambiarContrasenaScreen(navController) }
        composable(Rutas.RECUPERAR_CONTRASENA) { RecuperarContrasenaScreen(navController) }

        composable(Rutas.ADMIN_USUARIOS) { AdminUsuariosScreen(navController) }
        composable(Rutas.AUDITORIA) { AuditoriaScreen(navController) }
    }
}
