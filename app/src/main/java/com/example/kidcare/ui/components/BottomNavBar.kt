package com.example.kidcare.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kidcare.navigation.Rutas

data class ItemNav(
    val ruta: String,
    val emoji: String,
    val etiqueta: String
)

@Composable
fun BottomNavBar(navController: NavController, menorId: String = "1") {

    val azulKidCare = Color(0xFF2563EB)

    val items = listOf(
        ItemNav("home",              "🏠", "Inicio"),
        ItemNav("bitacora/$menorId", "📋", "Bitácora"),
        ItemNav("chatbot/$menorId",  "💬", "Chatbot"),
        ItemNav(Rutas.CONFIGURACION, "👤", "Perfil"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val seleccionado = rutaActual == item.ruta ||
                    (item.ruta == "home" && rutaActual == Rutas.HOME) ||
                    (item.ruta.startsWith("bitacora") && rutaActual?.startsWith("bitacora") == true) ||
                    (item.ruta.startsWith("chatbot") && rutaActual?.startsWith("chatbot") == true) ||
                    (item.ruta == Rutas.CONFIGURACION && rutaActual == Rutas.CONFIGURACION)

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    navController.navigate(item.ruta) {
                        popUpTo(Rutas.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Text(item.emoji, fontSize = 20.sp)
                },
                label = {
                    Text(
                        text = item.etiqueta,
                        fontSize = 10.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionado) azulKidCare else Color(0xFF9CA3AF)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = azulKidCare,
                    unselectedIconColor = Color(0xFF9CA3AF),
                    indicatorColor = Color(0xFFEFF6FF)
                )
            )
        }
    }
}