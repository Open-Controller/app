package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.pjtsearch.opencontroller_lib_proto.TextInputAction

@Composable
fun TextInput(onInput: (TextInputAction) -> Unit) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    BasicTextField(value, {
        if (it.text.length > value.text.length) {
            onInput(TextInputAction.newBuilder().setChar(it.text[it.text.length - 1].toString()).build())
        } else if (it.text.length < value.text.length) {
            onInput(TextInputAction.newBuilder().setBackspace(true).build())
        }
        value = it
    })
}
