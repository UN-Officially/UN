package com.example.ui.screens

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SplitTunnelMode
import com.example.ui.VpnViewModel
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class SplitTunnelAppItem(
    val packageName: String,
    val appName: String,
    val category: String
)

@Composable
fun SplitTunnelingScreen(
    viewModel: VpnViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Curated standard apps & installed apps list
    val defaultApps = remember {
        listOf(
            SplitTunnelAppItem("com.android.chrome", "Google Chrome", "Web Browser"),
            SplitTunnelAppItem("org.mozilla.firefox", "Firefox Browser", "Web Browser"),
            SplitTunnelAppItem("com.netflix.mediaclient", "Netflix", "Streaming Video"),
            SplitTunnelAppItem("com.spotify.music", "Spotify", "Audio Streaming"),
            SplitTunnelAppItem("com.google.android.youtube", "YouTube", "Video"),
            SplitTunnelAppItem("com.whatsapp", "WhatsApp Messenger", "Communication"),
            SplitTunnelAppItem("org.telegram.messenger", "Telegram", "Secure Messaging"),
            SplitTunnelAppItem("com.paypal.android.p2pmobile", "PayPal", "Finance & Banking"),
            SplitTunnelAppItem("com.chase.sig.android", "Chase Mobile", "Finance & Banking"),
            SplitTunnelAppItem("com.valvesoftware.android.steam.community", "Steam Mobile", "Gaming"),
            SplitTunnelAppItem("com.discord", "Discord", "Communication")
        )
    }

    val filteredApps = defaultApps.filter {
        searchQuery.isBlank() ||
            it.appName.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true) ||
            it.packageName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianNavy)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("split_tunneling_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Split Tunneling",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage Application Routing",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Enable Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = "Split Tunneling",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Split Tunneling",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose which apps use VPN encryption",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = settings.splitTunnelingEnabled,
                        onCheckedChange = { isEnabled ->
                            viewModel.updateSettings { it.copy(splitTunnelingEnabled = isEnabled) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianNavy,
                            checkedTrackColor = ElectricCyan
                        )
                    )
                }

                if (settings.splitTunnelingEnabled) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardBorder)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode Toggle: Bypass vs Only
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ModeSelectorButton(
                            title = "Bypass VPN",
                            description = "Selected apps bypass VPN",
                            isSelected = settings.splitTunnelMode == SplitTunnelMode.BYPASS_VPN,
                            onClick = {
                                viewModel.updateSettings { it.copy(splitTunnelMode = SplitTunnelMode.BYPASS_VPN) }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ModeSelectorButton(
                            title = "Only VPN",
                            description = "Only selected use VPN",
                            isSelected = settings.splitTunnelMode == SplitTunnelMode.ONLY_VPN,
                            onClick = {
                                viewModel.updateSettings { it.copy(splitTunnelMode = SplitTunnelMode.ONLY_VPN) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Apps Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search installed applications...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apps List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
                val isChecked = settings.splitTunnelPackages.contains(app.packageName)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBackground)
                        .border(
                            1.dp,
                            if (isChecked) ElectricCyan.copy(alpha = 0.5f) else CardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            val currentSet = settings.splitTunnelPackages.toMutableSet()
                            if (isChecked) {
                                currentSet.remove(app.packageName)
                            } else {
                                currentSet.add(app.packageName)
                            }
                            viewModel.updateSettings { it.copy(splitTunnelPackages = currentSet) }
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = app.appName,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.appName,
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${app.category} • ${app.packageName}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val currentSet = settings.splitTunnelPackages.toMutableSet()
                                if (checked) {
                                    currentSet.add(app.packageName)
                                } else {
                                    currentSet.remove(app.packageName)
                                }
                                viewModel.updateSettings { it.copy(splitTunnelPackages = currentSet) }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ElectricCyan,
                                checkmarkColor = ObsidianNavy,
                                uncheckedColor = TextMuted
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ModeSelectorButton(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkSurfaceVariant else CardBackground)
            .border(
                1.dp,
                if (isSelected) ElectricCyan else CardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = if (isSelected) ElectricCyan else TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
