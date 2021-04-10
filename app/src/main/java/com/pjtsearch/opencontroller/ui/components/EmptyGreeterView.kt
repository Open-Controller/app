package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.unit.sp
import com.pjtsearch.opencontroller.components.LargeButton
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.theme.typography

@Composable
fun EmptyGreeterView(onRevealMenu: () -> Unit, onAddHome: () -> Unit) =
    Column {
        Column(
            Modifier.weight(1f).align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                Icons.Outlined.Villa, "OpenController logo",
                Modifier.align(Alignment.CenterHorizontally).size(250.dp),
                MaterialTheme.colors.onSurface.copy(0.2f)
            )
            Text(
                "Welcome to OpenController",
                Modifier.align(Alignment.CenterHorizontally),
                MaterialTheme.colors.onSurface.copy(0.4f),
                style = typography.h4,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Row(Modifier.weight(0.5f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
    }