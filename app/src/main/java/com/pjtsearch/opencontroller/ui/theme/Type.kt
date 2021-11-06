package com.pjtsearch.opencontroller.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pjtsearch.opencontroller.R

val LexendDeca = FontFamily(
    Font(R.font.lexend_deca_regular)
)


// Set of Material typography styles to start with
val typography = Typography(
    displayLarge = Typography().displayMedium.copy(fontFamily = LexendDeca),
    displayMedium = Typography().displayMedium.copy(fontFamily = LexendDeca),
    displaySmall = Typography().displaySmall.copy(fontFamily = LexendDeca),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = LexendDeca),
    headlineMedium = TextStyle(
        fontFamily = LexendDeca,
        fontSize = 38.sp
    ),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = LexendDeca),
    titleLarge = Typography().titleLarge.copy(fontFamily = LexendDeca),
    titleMedium = Typography().titleMedium.copy(fontFamily = LexendDeca),
    titleSmall = Typography().titleSmall.copy(fontFamily = LexendDeca),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = LexendDeca),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = LexendDeca),
    bodySmall = Typography().bodySmall.copy(fontFamily = LexendDeca),
    labelLarge = Typography().labelLarge.copy(fontFamily = LexendDeca),
    labelMedium = Typography().labelMedium.copy(fontFamily = LexendDeca),
    labelSmall = Typography().labelSmall.copy(fontFamily = LexendDeca),
//    FIXME: Readd
//    h1 = Typography().h1.copy(fontFamily = LexendDeca),
//    h2 = Typography().h2.copy(fontFamily = LexendDeca),
//    h3 = Typography().h3.copy(fontFamily = LexendDeca),
//    h4 = Typography().h4.copy(fontFamily = LexendDeca),
//    h5 = TextStyle(
//        fontFamily = LexendDeca,
//        fontWeight = FontWeight.Bold,
//        fontSize = 25.sp
//    ),
//    h6 = Typography().h6.copy(fontFamily = LexendDeca),
//    subtitle1 = TextStyle(
//            fontFamily = LexendDeca,
//            fontWeight = FontWeight.Medium,
//            fontSize = 17.sp
//    ),
//    subtitle2 = Typography().subtitle2.copy(fontFamily = LexendDeca),
//    body1 = Typography().body1.copy(fontFamily = LexendDeca),
//    body2 = Typography().body2.copy(fontFamily = LexendDeca),
//    button = Typography().button.copy(fontFamily = LexendDeca),
//    caption = Typography().caption.copy(fontFamily = LexendDeca),
//    overline = Typography().overline.copy(fontFamily = LexendDeca)
)