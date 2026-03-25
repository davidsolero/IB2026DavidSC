package com.iberdrola.practicas2026.davidsc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun IB2026DavidSCTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}


val IberdrolaGreen = Color(0xFF366459) // verde corporativo Iberdrola


val StatusPendientepagofondo = Color(0xFFEFBEBE) // Pagada
val StatusPendientepagotexto= Color(0xFF883933)



val StatusPagadofondo  = Color(0xFFB0DCC6)
val StatusPagadotexto   = Color(0xFF065B30)


