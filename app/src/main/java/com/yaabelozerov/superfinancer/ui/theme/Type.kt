package com.yaabelozerov.superfinancer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yaabelozerov.superfinancer.R

@OptIn(ExperimentalTextApi::class)
val bodyFontFamily = FontFamily(
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(FontVariation.weight(FontWeight.Normal.weight)),
        weight = FontWeight.Normal
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(FontVariation.weight(FontWeight.Bold.weight)),
        weight = FontWeight.Bold
    ),
)

val displayFontFamily = FontFamily(
    Font(R.font.ptserif_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.ptserif_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.ptserif_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.ptserif_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

