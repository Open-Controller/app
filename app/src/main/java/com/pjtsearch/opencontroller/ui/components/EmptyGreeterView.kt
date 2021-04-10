package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Villa
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.GreeterLayout
import com.pjtsearch.opencontroller.components.LargeButton
import com.pjtsearch.opencontroller.ui.theme.typography

@Composable
fun EmptyGreeterView(onRevealMenu: () -> Unit, onAddHome: () -> Unit) =
    GreeterLayout(
        {
            Icon(
                Icons.Outlined.Villa, "OpenController logo",
                Modifier.align(Alignment.CenterHorizontally).size(250.dp),
            )
            Text(
                "Welcome to OpenController",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        {
            LargeButton(modifier = Modifier.fillMaxHeight().weight(1f), onClick = onRevealMenu, icon = {
                Icon(Icons.Outlined.ArrowDownward, "Open Homes Menu", Modifier.size(40.dp))
            }) {
                Text("Select Home", style = typography.subtitle1, textAlign = TextAlign.Center)
            }
            LargeButton(modifier = Modifier.fillMaxHeight().weight(1f), onClick = onAddHome, icon = {
                Icon(Icons.Outlined.Add, "Add Home", Modifier.size(40.dp))
            }) {
                Text("Add Home", style = typography.subtitle1, textAlign = TextAlign.Center)
            }
        }
    )