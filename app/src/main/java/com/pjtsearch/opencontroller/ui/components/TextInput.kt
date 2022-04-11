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

package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.text.diff.CommandVisitor
import org.apache.commons.text.diff.StringsComparator


/**
 * A component for a button that opens a text input dialog
 *
 * @param modifier A modifier for the layout
 * @param text The text for the button
 * @param icon The icon for the button
 * @param size The size of the button
 * @param onInput Function to be called when a character is inputted or deleted
 */
@ExperimentalComposeUiApi
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    text: String,
    icon: String,
    size: Int,
    onInput: (Map<String, Any>) -> Unit
) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    var isOpen by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    DisposableEffect(isOpen) {
        if (isOpen) {
//            Focus the input and open keyboard on open
            scope.launch {
                delay(50)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        } else {
            runCatching { focusRequester.freeFocus() }
            keyboardController?.hide()
        }
        onDispose { }
    }

    ControllerButton(
        onClick = { isOpen = true },
        text = text,
        icon = icon,
        modifier = modifier,
        size = size
    )
    if (isOpen) {
        AlertDialog(
            title = { Text(text) },
            text = {
                OutlinedTextField(value, {
                    val comp = StringsComparator(value.text, it.text).script
                    comp.visit(object : CommandVisitor<Char> {
                        override fun visitInsertCommand(added: Char?) {
                            onInput(mapOf(Pair("char", added!!.toString())))
                        }

                        override fun visitKeepCommand(char: Char?) {}

                        override fun visitDeleteCommand(delted: Char?) {
                            onInput(mapOf(Pair("backspace", true)))
                        }
                    })
                    value = it
                }, Modifier.focusRequester(focusRequester))
            },
            confirmButton = {
                Button(onClick = { isOpen = false }) {
                    Text("Close")
                }
            },
            onDismissRequest = { isOpen = false }
        )
    }
}
