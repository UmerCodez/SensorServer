/*
 *     This file is a part of SensorServer (https://www.github.com/UmerCodez/SensorServer)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     SensorServer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SensorServer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SensorServer. If not, see <https://www.gnu.org/licenses/>.
 *
 */
package github.umer0586.sensorserver.ui.screens.connections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import github.umer0586.sensorserver.ui.screens.connections.components.ConnectionItem
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@Composable
fun ConnectionsScreen(
    viewModel: ConnectionsScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ConnectionsScreen(
        state = state,
        onEvent = viewModel::onEvent
    )

}


@Composable
fun ConnectionsScreen(
    state: ConnectionScreenState,
    onEvent: (event: ConnectionsScreenEvent) -> Unit,
) {

    if (state.websocketClients.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Connections",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(state.websocketClients) { websocketClient ->
                ConnectionItem(
                    websocketClient = websocketClient,
                    onCloseClick = {
                        onEvent(ConnectionsScreenEvent.OnCloseClick(websocketClient))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ConnectionsScreenPreview() {
    SensorServerTheme {
        ConnectionsScreen(
            state = ConnectionScreenState(),
            onEvent = {}
        )
    }
}