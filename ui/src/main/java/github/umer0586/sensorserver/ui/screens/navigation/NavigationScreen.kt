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
package github.umer0586.sensorserver.ui.screens.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import github.umer0586.sensorserver.data.model.http.HttpServerState
import github.umer0586.sensorserver.ui.R
import github.umer0586.sensorserver.ui.TouchScreenActivity
import github.umer0586.sensorserver.ui.screens.about.AboutScreen
import github.umer0586.sensorserver.ui.screens.connections.ConnectionsScreen
import github.umer0586.sensorserver.ui.screens.navigation.components.NavDrawerHeader
import github.umer0586.sensorserver.ui.screens.navigation.components.TestInWebBrowserContent
import github.umer0586.sensorserver.ui.screens.sensors.SensorsScreen
import github.umer0586.sensorserver.ui.screens.server.ServerScreen
import github.umer0586.sensorserver.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch


sealed class NavItem(val pageIndex: Int, val imageVector: ImageVector, val label: String){
    data object Server: NavItem(0, Icons.Default.Home, "Server")
    data object Connections: NavItem(1, Icons.Default.Share, "Connections")
    data object Sensors: NavItem(2, Icons.AutoMirrored.Filled.List, "Sensors")
}

private val bottomNavItems = listOf(
    NavItem.Server,
    NavItem.Connections,
    NavItem.Sensors
)

@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavigationScreen(
        state = state,
        onEvent = viewModel::onEvent
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationScreen(
    state: NavigationScreenState,
    onEvent: (NavigationScreenEvent) -> Unit
) {

    var selectedNavItemIndex by remember { mutableIntStateOf(bottomNavItems.indexOf(NavItem.Server)) }
    var showAboutDetails by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showTestInWebBrowser by remember { mutableStateOf(false) }


    val context = LocalContext.current


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState{3}

    val snackbarHostState = remember { SnackbarHostState() }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ){

                    Spacer(Modifier.height(20.dp))

                    Icon(
                        modifier = Modifier.clickable {
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Menu"
                    )

                    Spacer(Modifier.height(12.dp))

                    NavDrawerHeader(
                        appVersion = getAppVersion(context)
                    )

                    Spacer(Modifier.height(20.dp))


                    HorizontalDivider()


                    NavigationDrawerItem(
                        label = { Text("Test in Web browser") },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_http_24dp),
                                contentDescription = null
                            )
                        },
                        badge = {
                            if (state.httpServerState is HttpServerState.Running) {
                                Badge{
                                    Text("Running")
                                }
                            }
                        },
                        selected = false,
                        onClick = {
                            showTestInWebBrowser = true
                        }
                    )


                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = {
                            showSettings = true
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Touch Screen") },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_touch_24dp),
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            context.startActivity(Intent(context, TouchScreenActivity::class.java))
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("About") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = {
                            showAboutDetails = true
                        }
                    )

                }
            }
        }
    ) {

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (selectedNavItemIndex) {
                                bottomNavItems.indexOf(NavItem.Server) -> "Websocket Server"
                                bottomNavItems.indexOf(NavItem.Sensors) -> "Available Sensors"
                                bottomNavItems.indexOf(NavItem.Connections) -> "Connections"
                                else -> ""
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { navItem ->
                        NavigationBarItem(
                            selected = bottomNavItems.indexOf(navItem) == selectedNavItemIndex,
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (navItem is NavItem.Connections && state.connectionsCount > 0) {
                                            Badge {
                                                Text(state.connectionsCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = when(navItem){
                                            is NavItem.Connections -> ImageVector.vectorResource(R.drawable.ic_network_node_24dp)
                                            else -> navItem.imageVector
                                        },
                                        contentDescription = null
                                    )
                                }
                            },
                            label = { Text(text = navItem.label) },
                            onClick = {
                                selectedNavItemIndex = bottomNavItems.indexOf(navItem)
                                scope.launch {
                                    when(navItem){
                                        NavItem.Server -> pagerState.scrollToPage(navItem.pageIndex)
                                        NavItem.Connections -> pagerState.scrollToPage(navItem.pageIndex)
                                        NavItem.Sensors -> pagerState.scrollToPage(navItem.pageIndex)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                state = pagerState,
                beyondViewportPageCount = 3,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> ServerScreen(
                        onError = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        }
                    )

                    1 -> ConnectionsScreen()
                    2 -> SensorsScreen()
                }
            }

        }
    }

    if (showSettings) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showSettings = false },
        ) {
            SettingsScreen()
        }

    }

    if(showAboutDetails){
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showAboutDetails = false },
        ) {
            AboutScreen()
        }
    }

    if(showTestInWebBrowser){
        ModalBottomSheet(
            onDismissRequest = { showTestInWebBrowser = false },
        ) {
            TestInWebBrowserContent(
                httpServerState = state.httpServerState,
                onStartClick = {
                    onEvent(NavigationScreenEvent.OnStartHttpServerClick)
                },
                onStopClick = {
                    onEvent(NavigationScreenEvent.OnStopHttpServerClick)
                }
            )
        }
    }
}



private fun getAppVersion(context: Context) : String{
    val versionName = try {
        context.applicationContext.packageManager
            .getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "Unknown"
    }
    return versionName
}