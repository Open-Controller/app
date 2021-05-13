package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import com.pjtsearch.opencontroller.components.LocalDialogOpener
import com.pjtsearch.opencontroller_lib_proto.Icon
import com.pjtsearch.opencontroller_lib_proto.Size
import com.pjtsearch.opencontroller_lib_proto.TextInputAction
import kotlinx.coroutines.delay
import org.apache.commons.text.diff.CommandVisitor
import org.apache.commons.text.diff.StringsComparator

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun TextInput(modifier: Modifier = Modifier, text: String, icon: Icon, size: Size, onInput: (TextInputAction) -> Unit) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    val openDialog = LocalDialogOpener.current

    OpenControllerButton(
        onClick = {
            openDialog { state ->
                val focusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current

                DisposableEffect(state.currentValue) {
                    if (state.isVisible) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    } else {
                        runCatching { focusRequester.freeFocus() }
                        keyboardController?.hide()
                    }
                    onDispose {  }
                }
                Text(text, style = MaterialTheme.typography.subtitle1)
                TextField(value, {
                    val comp = StringsComparator(value.text, it.text).script
                    comp.visit(object : CommandVisitor<Char> {
                      override fun visitInsertCommand(added: Char?) {
                          onInput(TextInputAction.newBuilder().setChar(added!!.toString()).build())
                      }

                      override fun visitKeepCommand(char: Char?) {}

                      override fun visitDeleteCommand(delted: Char?) {
                          onInput(TextInputAction.newBuilder().setBackspace(true).build())
                      }
                    })
                    value = it
                }, Modifier.focusRequester(focusRequester))
            }
        },
        text = text,
        icon = icon,
        modifier = modifier,
        size = size
    )
}
