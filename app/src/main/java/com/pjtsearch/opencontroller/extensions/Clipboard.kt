package com.pjtsearch.opencontroller.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun copy(label: String, text: String, ctx: Context) =
    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText(label, text))