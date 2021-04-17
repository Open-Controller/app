package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.pjtsearch.opencontroller_lib_proto.Icon
import com.pjtsearch.opencontroller_lib_proto.Size
import com.pjtsearch.opencontroller_lib_proto.TextInputAction
import org.apache.commons.text.diff.CommandVisitor
import org.apache.commons.text.diff.StringsComparator

@Composable
fun TextInput(modifier: Modifier = Modifier, text: String, icon: Icon, size: Size, onInput: (TextInputAction) -> Unit) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    var showDialog by remember { mutableStateOf(false) }

    OpenControllerButton(
        onClick = { showDialog = true },
        text = text,
        icon = icon,
        modifier = Modifier,
        size = size
    )
    if (showDialog) {
        AlertDialog(
            shape = MaterialTheme.shapes.small,
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("Input Text")
            },
            confirmButton = {

            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    },
                ) {
                    Text("Close")
                }
            },
            text = {
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
                })
            },
        )
    }
}
