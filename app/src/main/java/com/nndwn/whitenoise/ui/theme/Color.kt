package com.nndwn.whitenoise.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val DarkNavyGray = Color(0xFF121219)
val CharcoalDarkGray = Color(0xFF262626)
val MediumDarkGray = Color(0xFF414141)
val White = Color(0xFFF5F5FF)


/** Convert an unsigned ARGB Long to Compose [Color]. */
fun Long.toComposeColor(): Color = Color(this.toInt())

/** Convert a Compose [Color] to unsigned ARGB Long for storage. */
fun Color.toArgbLong(): Long = this.toArgb().toUInt().toLong()
