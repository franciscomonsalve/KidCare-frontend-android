package com.example.kidcare.ui.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Colores estándar accesibles para OutlinedTextField en KidCare.
 *
 * - Texto escrito:  negro (#111827) — contraste 16:1 sobre blanco
 * - Placeholder:    gris medio (#6B7280) — contraste 4.5:1
 * - Label unfocused: gris oscuro (#374151)
 * - Label focused:   color acento
 * - Borde unfocused: gris claro (#E5E7EB)
 * - Borde focused:   color acento
 */
@Composable
fun campoColores(acento: Color = AzulKidCare): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        // Texto que escribe el usuario
        focusedTextColor          = TextoPrincipal,
        unfocusedTextColor        = TextoPrincipal,
        disabledTextColor         = TextoTerciario,
        errorTextColor            = TextoPrincipal,

        // Placeholder (hint)
        focusedPlaceholderColor   = TextoPlaceholder,
        unfocusedPlaceholderColor = TextoPlaceholder,

        // Label flotante
        focusedLabelColor         = acento,
        unfocusedLabelColor       = TextoSecundario,
        disabledLabelColor        = TextoTerciario,
        errorLabelColor           = TextoError,

        // Bordes
        focusedBorderColor        = acento,
        unfocusedBorderColor      = Color(0xFFE5E7EB),
        disabledBorderColor       = Color(0xFFE5E7EB),
        errorBorderColor          = TextoError,

        // Cursor
        cursorColor               = acento,
        errorCursorColor          = TextoError,

        // Contenedor (sin fondo de relleno)
        focusedContainerColor     = Color.White,
        unfocusedContainerColor   = Color.White,
        disabledContainerColor    = Color(0xFFF9FAFB),
        errorContainerColor       = Color.White,
    )
