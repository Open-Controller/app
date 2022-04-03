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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.text.diff.CommandVisitor
import org.apache.commons.text.diff.StringsComparator


@ExperimentalComposeUiApi
@Composable
fun TextInput(modifier: Modifier = Modifier, text: String, icon: String, size: Int, onInput: (Map<String, Any>) -> Unit) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    var isOpen by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    DisposableEffect(isOpen) {
        if (isOpen) {
            scope.launch {
                delay(50)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        } else {
            runCatching { focusRequester.freeFocus() }
            keyboardController?.hide()
        }
        onDispose {  }
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
