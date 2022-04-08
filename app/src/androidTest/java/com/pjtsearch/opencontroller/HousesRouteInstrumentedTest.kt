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

package com.pjtsearch.opencontroller

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.pjtsearch.opencontroller.houses.HousesRoute
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class HousesRouteInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(
        ExperimentalTestApi::class
    )
    @Test
    fun shouldManageHouses() {
        var selectedTimes = 0
        composeTestRule.setContent {
            HousesRoute {
                selectedTimes++
            }
        }
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Add house").performClick()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Chose Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("URL").assertIsDisplayed()

        composeTestRule.onNodeWithText("Chose Icon").performClick()
        Thread.sleep(300)
        composeTestRule.onNodeWithContentDescription("ROOM").performScrollTo()
        composeTestRule.onNodeWithContentDescription("ROOM").performClick()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Set").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithContentDescription("ROOM").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").performTextInput("Test House")
        composeTestRule.onNodeWithText("URL").performTextInput("test://test")

        composeTestRule.onNodeWithText("Save").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Test House").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("ROOM").assertIsDisplayed()

//        Edit house
        composeTestRule.onNodeWithText("Test House").performMouseInput { longClick() }
        Thread.sleep(300)
        composeTestRule.onNodeWithContentDescription("Edit this house").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Name").performTextReplacement("Test House Edited")
        composeTestRule.onNodeWithText("Save").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Test House Edited").assertIsDisplayed()

//        Select House

        composeTestRule.onNodeWithText("Test House Edited").performClick()
        Thread.sleep(300)
        assertEquals(selectedTimes, 1)

//        Remove house
        composeTestRule.onNodeWithText("Test House Edited")
            .performMouseInput { longClick() }
        Thread.sleep(300)
        composeTestRule.onNodeWithContentDescription("Delete this house").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Test House Edited").assertDoesNotExist()

    }
}