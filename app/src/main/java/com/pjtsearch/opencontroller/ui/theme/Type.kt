package com.pjtsearch.opencontroller.ui.theme

import androidx.compose.material.Typography
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
    h1 = Typography().h1.copy(fontFamily = LexendDeca),
    h2 = Typography().h2.copy(fontFamily = LexendDeca),
    h3 = Typography().h3.copy(fontFamily = LexendDeca),
    h4 = Typography().h4.copy(fontFamily = LexendDeca),
    h5 = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),
    h6 = Typography().h6.copy(fontFamily = LexendDeca),
    subtitle1 = TextStyle(
            fontFamily = LexendDeca,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp
    ),
    subtitle2 = Typography().subtitle2.copy(fontFamily = LexendDeca),
    body1 = Typography().body1.copy(fontFamily = LexendDeca),
    body2 = Typography().body2.copy(fontFamily = LexendDeca),
    button = Typography().button.copy(fontFamily = LexendDeca),
    caption = Typography().caption.copy(fontFamily = LexendDeca),
    overline = Typography().overline.copy(fontFamily = LexendDeca)
)