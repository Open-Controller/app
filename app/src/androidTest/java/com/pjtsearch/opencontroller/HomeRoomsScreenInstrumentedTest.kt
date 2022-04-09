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
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.Room
import com.pjtsearch.opencontroller.home.HomeRoomsScreen
import com.pjtsearch.opencontroller.home.HouseLoadingState
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class HomeRoomsScreenInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldLoad() {
        composeTestRule.setContent {
            HomeRoomsScreen(
                houseLoadingState = HouseLoadingState.Loading(null),
                onExit = {},
                onReload = {},
                onSelectController = {}
            )
        }
        composeTestRule.onAllNodesWithText("Loading").assertCountEquals(5)
    }

    @Test
    fun shouldShowError() {
        var reloadedTimes = 0
        composeTestRule.setContent {
            HomeRoomsScreen(
                houseLoadingState = HouseLoadingState.Error(Error("Test Error"), null),
                onExit = {},
                onReload = { reloadedTimes++ },
                onSelectController = {}
            )
        }
        composeTestRule.onNodeWithText("Error Loading: Test Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reload").performClick()
        Thread.sleep(300)
        assertEquals(1, reloadedTimes)
    }

    @Test
    fun shouldBeLoaded() {
        var selected = listOf<Pair<String, String>>()
        composeTestRule.setContent {
            HomeRoomsScreen(
                houseLoadingState = HouseLoadingState.Loaded(
                    House(
                        id = "test",
                        displayName = "Test",
                        rooms = listOf(
                            Room(
                                id = "testRoom",
                                displayName = "Test Room",
                                icon = "ROOM",
                                controllers = listOf(
                                    Controller(
                                        id = "controller1",
                                        displayName = "Controller 1",
                                        brandColor = "#000000",
                                        displayInterface = null
                                    ),
                                    Controller(
                                        id = "controller2",
                                        displayName = "Controller 2",
                                        brandColor = "#000000",
                                        displayInterface = null
                                    )
                                )
                            )
                        )
                    )
                ),
                onExit = {},
                onReload = {},
                onSelectController = { selected = selected + it }
            )
        }
        composeTestRule.onNodeWithText("Test Room").performClick()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Controller 1").performClick()
        Thread.sleep(300)
        assertEquals(listOf("testRoom" to "controller1"), selected)
        composeTestRule.onNodeWithText("Controller 2").performClick()
        Thread.sleep(300)
        assertEquals(
            listOf("testRoom" to "controller1", "testRoom" to "controller2"),
            selected
        )
    }
}