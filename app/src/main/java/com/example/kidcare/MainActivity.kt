package com.example.kidcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.kidcare.navigation.NavGraph
import com.example.kidcare.ui.theme.KidCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KidCareTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}