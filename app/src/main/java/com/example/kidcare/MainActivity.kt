package com.example.kidcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.data.network.ApiClient
import com.example.kidcare.data.preferences.SessionManager
import com.example.kidcare.navigation.NavGraph
import com.example.kidcare.ui.theme.KidCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restaura el token guardado para que las llamadas autenticadas funcionen al relanzar la app
        SessionManager(this).getToken()?.let { ApiClient.authToken = it }

        enableEdgeToEdge()
        setContent {
            KidCareTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
