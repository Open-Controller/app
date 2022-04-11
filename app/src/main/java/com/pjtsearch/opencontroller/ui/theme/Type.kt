/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.pjtsearch.opencontroller.R

/** The Lexend Deca font family */
val LexendDeca = FontFamily(
    Font(R.font.lexend_deca_regular)
)


/**
 * Set of Material typography styles with Lexend Deca
 */
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
    labelSmall = Typography().labelSmall.copy(fontFamily = LexendDeca)
)