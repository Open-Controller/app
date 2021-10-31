package com.pjtsearch.opencontroller.houses

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settingsDataStore
import kotlinx.coroutines.flow.map

// FIXME: Figure this out
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun HousesRoute(
    isExpandedScreen: Boolean,
    onHouseSelected: (HouseRef) -> Unit
) {
    val ctx = LocalContext.current
    val houseRefs = ctx.settingsDataStore.data.map {
        it.houseRefsList
    }.collectAsState(initial = listOf())
    Column(modifier = Modifier.systemBarsPadding()) {
        houseRefs.value.map {
            FilledTonalButton(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = { onHouseSelected(it) }
            ) {
                Text(it.displayName)
            }
        }
    }
}
