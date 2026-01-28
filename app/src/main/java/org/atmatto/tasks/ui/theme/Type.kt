package org.atmatto.tasks.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

//val fontFamily = FontFamily(
//	Font(DeviceFontFamilyName("variable-body-large")),
//	Font(DeviceFontFamilyName("sans-serif"))
//)
//
//// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = fontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)

val baseline = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val TasksTypography = Typography(
	displayLarge = baseline.displayLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-large")))),
	displayMedium = baseline.displayMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-medium")))),
	displaySmall = baseline.displaySmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-small")))),
	headlineLarge = baseline.headlineLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-large")))),
	headlineMedium = baseline.headlineMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-medium")))),
	headlineSmall = baseline.headlineSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-small")))),
	titleLarge = baseline.titleLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-large")))),
	titleMedium = baseline.titleMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-medium")))),
	titleSmall = baseline.titleSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-small")))),
	bodyLarge = baseline.bodyLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-large")))),
	bodyMedium = baseline.bodyMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-medium")))),
	bodySmall = baseline.bodySmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-small")))),
	labelLarge = baseline.labelLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-large")))),
	labelMedium = baseline.labelMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-medium")))),
	labelSmall = baseline.labelSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-small")))),
	displayLargeEmphasized = baseline.displayLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-large-emphasized")))),
	displayMediumEmphasized = baseline.displayMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-medium-emphasized")))),
	displaySmallEmphasized = baseline.displaySmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-display-small-emphasized")))),
	headlineLargeEmphasized = baseline.headlineLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-large-emphasized")))),
	headlineMediumEmphasized = baseline.headlineMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-medium-emphasized")))),
	headlineSmallEmphasized = baseline.headlineSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-headline-small-emphasized")))),
	titleLargeEmphasized = baseline.titleLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-large-emphasized")))),
	titleMediumEmphasized = baseline.titleMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-medium-emphasized")))),
	titleSmallEmphasized = baseline.titleSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-title-small-emphasized")))),
	bodyLargeEmphasized = baseline.bodyLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-large-emphasized")))),
	bodyMediumEmphasized = baseline.bodyMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-medium-emphasized")))),
	bodySmallEmphasized = baseline.bodySmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-body-small-emphasized")))),
	labelLargeEmphasized = baseline.labelLarge.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-large-emphasized")))),
	labelMediumEmphasized = baseline.labelMedium.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-medium-emphasized")))),
	labelSmallEmphasized = baseline.labelSmall.copy(fontFamily = FontFamily(Font(DeviceFontFamilyName("variable-label-small-emphasized")))),
)
