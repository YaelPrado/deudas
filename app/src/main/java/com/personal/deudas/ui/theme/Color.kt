package com.personal.deudas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda

val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    error = Color(0xFFB91C1C),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF450A0A),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF1F5F9),
    inversePrimary = Color(0xFF5EEAD4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE9EEF4),
    surfaceContainerHighest = Color(0xFFE2E8F0),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF115E59),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFFCBD5E1),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFFFCD34D),
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF0B1220),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFF0F766E),
    surfaceContainerLowest = Color(0xFF080E1A),
    surfaceContainerLow = Color(0xFF0F172A),
    surfaceContainer = Color(0xFF131E33),
    surfaceContainerHigh = Color(0xFF1E293B),
    surfaceContainerHighest = Color(0xFF273449),
)

/** Par fondo/texto para chips y montos con significado (tipo y estado de la deuda). */
@Immutable
data class StatusColors(val container: Color, val content: Color)

@Composable
fun TipoDeuda.colors(): StatusColors {
    val dark = isSystemInDarkTheme()
    return when (this) {
        TipoDeuda.ME_DEBEN ->
            if (dark) StatusColors(Color(0xFF14532D), Color(0xFFBBF7D0)) else StatusColors(Color(0xFFDCFCE7), Color(0xFF166534))
        TipoDeuda.DEBO ->
            if (dark) StatusColors(Color(0xFF7C2D12), Color(0xFFFED7AA)) else StatusColors(Color(0xFFFFEDD5), Color(0xFF9A3412))
    }
}

@Composable
fun EstadoDeuda.colors(): StatusColors {
    val dark = isSystemInDarkTheme()
    return when (this) {
        EstadoDeuda.PENDIENTE ->
            if (dark) StatusColors(Color(0xFF334155), Color(0xFFE2E8F0)) else StatusColors(Color(0xFFE2E8F0), Color(0xFF334155))
        EstadoDeuda.PARCIAL ->
            if (dark) StatusColors(Color(0xFF78350F), Color(0xFFFDE68A)) else StatusColors(Color(0xFFFEF3C7), Color(0xFF92400E))
        EstadoDeuda.PAGADA ->
            if (dark) StatusColors(Color(0xFF14532D), Color(0xFFBBF7D0)) else StatusColors(Color(0xFFDCFCE7), Color(0xFF166534))
    }
}
