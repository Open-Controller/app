package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ExitToApp
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
import com.pjtsearch.opencontroller_lib_proto.House

@Composable
fun HomeGreeterView(house: House, onRevealMenu: () -> Unit, onExitHome: () -> Unit) =
    GreeterLayout(
        {
            Icon(
                Icons.Outlined.Villa, "OpenController logo",
                Modifier.align(Alignment.CenterHorizontally).size(250.dp),
            )
            Text(
                "Welcome to ${house.displayName}",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        {
            LargeButton(modifier = Modifier.fillMaxHeight().weight(1f), onClick = onRevealMenu, icon = {
                Icon(Icons.Outlined.ArrowDownward, "Open Homes Menu", Modifier.size(40.dp))
            }) {
                Text("Select Controller", style = typography.subtitle1, textAlign = TextAlign.Center)
            }
            LargeButton(modifier = Modifier.fillMaxHeight().weight(1f), onClick = onExitHome, icon = {
                Icon(Icons.Outlined.ExitToApp, "Add Home", Modifier.size(40.dp))
            }) {
                Text("Exit Home", style = typography.subtitle1, textAlign = TextAlign.Center)
            }
        }
    )