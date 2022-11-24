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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.get
import com.pjtsearch.opencontroller.executor.Fn
import com.pjtsearch.opencontroller.executor.Widget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * A component to display a [Widget]
 *
 * @param widget The [Widget] to display
 * @param modifier A modifier for the layout
 * @param onOpenMenu Function to be called when the an expansion menu is opened
 * @param onError Function to be called when there is an execution error
 */
@ExperimentalComposeUiApi
@Composable
fun ColumnScope.Widget(
    widget: Widget,
    modifier: Modifier = Modifier,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit
) {
    val scope = rememberCoroutineScope()

    /**
     * Calls a param that is a function
     *
     * @param paramName The name of the param to call
     * @param params The parameters to pass to the function call
     */
    fun callParam(paramName: String, vararg params: Any) {
        scope.launch(Dispatchers.IO) {
            val param = widget.params[paramName] as? Fn
            if (param != null) {
                when (val result = param(params.toList())) {
                    is Err -> onError(result.error.asThrowable())
                    else -> {}
                }
            } else {
                onError(Error("Param missing: $paramName"))
            }
        }
    }

//    Modifier for expanded widgets to set full weight
    val sizedModifier = if (widget.params["expand"] as? Boolean == true) {
        modifier.weight(1f, false)
    } else modifier
    when (widget.widgetType) {
        "reactive" -> (widget.params["observe"] as? Flow<*>)?.let { observe ->
            val state by observe.collectAsState(initial = null)
            state?.let {
                Column(sizedModifier, Arrangement.Top) {
//                        TODO: Convert to callParam
                    ((widget.params["child"] as? Fn)?.invoke(listOf(it))
                        ?.get() as? Widget)?.let { widget ->
                        this@Widget.Widget(
                            widget,
                            onError = onError,
                            onOpenMenu = onOpenMenu
                        )
                    }
                }
            }
        }
        "buttongroup" ->
            ControllerButtonGroup(
                modifier = sizedModifier,
                size = widget.params["size"] as? Int,
                buttons = widget.children.map { btn ->
//                    TODO: Use button scope
                    check(btn.widgetType == "button")
                    ButtonItemParams(
                        btn.params["text"] as? String,
                        btn.params["icon"] as? String,
                    ) {
                        GlobalScope.launch {
                            (btn.params["onClick"] as? Fn)?.invoke(listOf())
                        }
                    }
                }
            )
        "button" ->
            ControllerButton(
                sizedModifier,
                widget.params["text"] as? String,
                widget.params["icon"] as? String,
                widget.params["size"] as? Int
            ) {
                callParam("onClick")
            }
        "row" -> Row(sizedModifier, Arrangement.SpaceBetween) {
            widget.children.map {
                this@Widget.Widget(it, onError = onError, onOpenMenu = onOpenMenu)
            }
        }
        "column" -> Column(sizedModifier, Arrangement.Top) {
            widget.children.map {
                Widget(it, onError = onError, onOpenMenu = onOpenMenu)
            }
        }
        "arrowlayout" -> Column(sizedModifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                (widget.params["top"] as? Widget)?.let {
                    this@Widget.Widget(
                        it,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
            }
            Row {
                (widget.params["left"] as? Widget)?.let {
                    this@Widget.Widget(
                        it,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
                (widget.params["center"] as? Widget)?.let {
                    this@Widget.Widget(
                        it,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
                (widget.params["right"] as? Widget)?.let {
                    this@Widget.Widget(
                        it,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                (widget.params["bottom"] as? Widget)?.let {
                    this@Widget.Widget(
                        it,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
            }
        }
        "swipepad" -> ControllerSwipePad(
            modifier = sizedModifier.weight(1f, true),
            expand = widget.params["expand"] as Boolean?,
            bottomLeftContent = (widget.params["bottomLeftContent"] as? Widget)?.let {
                {
                    CompositionLocalProvider(LocalControllerButtonContext provides ControllerButtonContext.SwipePad) {
                        this@Widget.Widget(
                            it,
                            onError = onError,
                            onOpenMenu = onOpenMenu
                        )
                    }
                }
            },
            bottomRightContent = (widget.params["bottomRightContent"] as? Widget)?.let {
                {
                    CompositionLocalProvider(LocalControllerButtonContext provides ControllerButtonContext.SwipePad) {
                        this@Widget.Widget(
                            it,
                            onError = onError,
                            onOpenMenu = onOpenMenu
                        )
                    }
                }
            },
            onSwipeDown = { callParam("onSwipeDown") },
            onSwipeUp = { callParam("onSwipeUp") },
            onSwipeLeft = { callParam("onSwipeLeft") },
            onSwipeRight = { callParam("onSwipeRight") },
            onClick = { callParam("onClick") }
        )
        "space" -> Spacer(sizedModifier)
        "menubutton" ->
            ControllerButton(
                sizedModifier,
                widget.params["text"] as? String,
                widget.params["icon"] as? String,
                widget.params["size"] as? Int
            ) {
                onOpenMenu(widget.children)
            }
        "textinput" -> TextInput(
            sizedModifier,
            widget.params["text"] as? String ?: "Text input",
            widget.params["icon"] as? String,
            widget.params["size"] as? Int
        ) {
            println(it)
            callParam("onInput", it)
        }
        "numberinput" -> TextInput(
            sizedModifier,
            widget.params["text"] as? String ?: "Number input",
            widget.params["icon"] as? String,
            widget.params["size"] as? Int,
            KeyboardType.Number
        ) {
            println(it)
            callParam("onInput", it)
        }
    }
}