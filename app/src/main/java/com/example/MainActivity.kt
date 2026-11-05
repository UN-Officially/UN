package com.example

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VpnViewModel
import com.example.ui.screens.ConnectScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.screens.SplitTunnelingScreen
import com.example.ui.screens.VpnSettingsScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class AppNavDestination {
    CONNECT,
    SERVERS,
    SPEED_TEST,
    SETTINGS,
    SPLIT_TUNNELING
}

class MainActivity : ComponentActivity() {

    private val viewModel: VpnViewModel by viewModels {
        VpnViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CipherVpnApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CipherVpnApp(viewModel: VpnViewModel) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppNavDestination.CONNECT) }

    // Launcher for system VPN permission (VpnService.prepare)
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.connectVpn(context)
        }
    }

    // Launcher for Android 13+ Notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianNavy,
        bottomBar = {
            if (currentDestination != AppNavDestination.SPLIT_TUNNELING) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextWhite,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Connect",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Connect",
                                fontSize = 11.sp,
                                fontWeight = if (currentDestination == AppNavDestination.CONNECT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = currentDestination == AppNavDestination.CONNECT,
                        onClick = { currentDestination = AppNavDestination.CONNECT },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNavy,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_connect")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Servers",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Servers",
                                fontSize = 11.sp,
                                fontWeight = if (currentDestination == AppNavDestination.SERVERS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = currentDestination == AppNavDestination.SERVERS,
                        onClick = { currentDestination = AppNavDestination.SERVERS },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNavy,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_servers")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed Test",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Speed Test",
                                fontSize = 11.sp,
                                fontWeight = if (currentDestination == AppNavDestination.SPEED_TEST) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = currentDestination == AppNavDestination.SPEED_TEST,
                        onClick = { currentDestination = AppNavDestination.SPEED_TEST },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNavy,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_speed_test")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Settings",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Settings",
                                fontSize = 11.sp,
                                fontWeight = if (currentDestination == AppNavDestination.SETTINGS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = currentDestination == AppNavDestination.SETTINGS,
                        onClick = { currentDestination = AppNavDestination.SETTINGS },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNavy,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.CONNECT -> {
                    ConnectScreen(
                        viewModel = viewModel,
                        onNavigateToServers = { currentDestination = AppNavDestination.SERVERS },
                        onNavigateToSettings = { currentDestination = AppNavDestination.SETTINGS },
                        onNavigateToSpeedTest = { currentDestination = AppNavDestination.SPEED_TEST },
                        onPrepareRequired = { intent -> vpnPrepareLauncher.launch(intent) }
                    )
                }
                AppNavDestination.SERVERS -> {
                    ServerListScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppNavDestination.CONNECT }
                    )
                }
                AppNavDestination.SPEED_TEST -> {
                    SpeedTestScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppNavDestination.CONNECT }
                    )
                }
                AppNavDestination.SETTINGS -> {
                    VpnSettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppNavDestination.CONNECT },
                        onNavigateToSplitTunneling = { currentDestination = AppNavDestination.SPLIT_TUNNELING }
                    )
                }
                AppNavDestination.SPLIT_TUNNELING -> {
                    SplitTunnelingScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppNavDestination.SETTINGS }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}

