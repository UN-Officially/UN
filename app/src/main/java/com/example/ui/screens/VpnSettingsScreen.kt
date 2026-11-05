package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DnsProvider
import com.example.model.VpnProtocol
import com.example.ui.VpnViewModel
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun VpnSettingsScreen(
    viewModel: VpnViewModel,
    onBack: () -> Unit,
    onNavigateToSplitTunneling: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    var showProtocolDialog by remember { mutableStateOf(false) }
    var showDnsDialog by remember { mutableStateOf(false) }
    var showMtuDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianNavy)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("settings_back_button")
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
                    text = "VPN Settings",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Protocols, Privacy & Network Configuration",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 1: PROTOCOLS & ENCRYPTION
        SettingsSectionHeader("ADVANCED ENCRYPTION PROTOCOLS")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Protocol Selector Row
                SettingsNavRow(
                    icon = Icons.Default.Key,
                    title = "VPN Protocol",
                    subtitle = "${settings.selectedProtocol.displayName} • ${settings.selectedProtocol.cipherDescription}",
                    badge = if (settings.selectedProtocol.isRecommended) "RECOMMENDED" else null,
                    onClick = { showProtocolDialog = true },
                    testTag = "setting_protocol_row"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Quantum Resistance Key Exchange
                SettingsToggleRow(
                    icon = Icons.Default.EnhancedEncryption,
                    title = "Quantum-Resistant Key Exchange",
                    subtitle = "Kyber-1024 post-quantum lattice cryptography to prevent future decryption",
                    isChecked = settings.quantumResistanceEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(quantumResistanceEnabled = checked) }
                    },
                    testTag = "setting_quantum_toggle"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: SECURITY & LEAK PROTECTION
        SettingsSectionHeader("SECURITY & LEAK SHIELDS")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Kill Switch
                SettingsToggleRow(
                    icon = Icons.Default.Security,
                    title = "VPN Kill Switch",
                    subtitle = "Blocks all internet traffic if the VPN connection drops unexpectedly",
                    isChecked = settings.killSwitchEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(killSwitchEnabled = checked) }
                    },
                    testTag = "setting_killswitch_toggle"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // DNS Leak Protection & Provider
                SettingsNavRow(
                    icon = Icons.Default.Dns,
                    title = "DNS Leak Protection",
                    subtitle = "Current: ${settings.dnsProvider.displayName}",
                    badge = "SECURE",
                    onClick = { showDnsDialog = true },
                    testTag = "setting_dns_row"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // IPv6 Leak Protection
                SettingsToggleRow(
                    icon = Icons.Default.NetworkCheck,
                    title = "IPv6 Leak Protection",
                    subtitle = "Forces all IPv6 traffic through the encrypted tunnel to hide real IPv6",
                    isChecked = settings.ipv6LeakProtection,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(ipv6LeakProtection = checked) }
                    },
                    testTag = "setting_ipv6_toggle"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: NETWORK & ROUTING
        SettingsSectionHeader("NETWORK & ROUTING CONTROLS")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Split Tunneling
                SettingsNavRow(
                    icon = Icons.Default.AltRoute,
                    title = "Split Tunneling",
                    subtitle = if (settings.splitTunnelingEnabled)
                        "Active (${settings.splitTunnelPackages.size} apps configured)"
                    else "Disabled (All traffic routes through VPN)",
                    badge = if (settings.splitTunnelingEnabled) "ON" else "OFF",
                    onClick = onNavigateToSplitTunneling,
                    testTag = "setting_split_tunneling_row"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Bypass Local Network (LAN)
                SettingsToggleRow(
                    icon = Icons.Default.Lan,
                    title = "Bypass Local Network (LAN)",
                    subtitle = "Allow direct communication with local printers, smart devices, and Chromecast",
                    isChecked = settings.bypassLocalNetwork,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(bypassLocalNetwork = checked) }
                    },
                    testTag = "setting_bypass_lan_toggle"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Auto-Connect on Untrusted Wi-Fi
                SettingsToggleRow(
                    icon = Icons.Default.Wifi,
                    title = "Auto-Connect on Wi-Fi",
                    subtitle = "Automatically engages encrypted tunnel on public and untrusted networks",
                    isChecked = settings.autoConnectOnWifi,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(autoConnectOnWifi = checked) }
                    },
                    testTag = "setting_autoconnect_toggle"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // MTU Size
                SettingsNavRow(
                    icon = Icons.Default.Tune,
                    title = "MTU Packet Size",
                    subtitle = "${settings.mtuSize} Bytes (${if (settings.mtuSize == 1420) "Auto Recommended" else "Custom MSS"})",
                    badge = null,
                    onClick = { showMtuDialog = true },
                    testTag = "setting_mtu_row"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // PROTOCOL SELECTION DIALOG
    if (showProtocolDialog) {
        AlertDialog(
            onDismissRequest = { showProtocolDialog = false },
            title = {
                Text(
                    text = "Select VPN Protocol",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VpnProtocol.values().forEach { protocol ->
                        val isSelected = settings.selectedProtocol == protocol
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkSurfaceVariant else CardBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.updateSettings { it.copy(selectedProtocol = protocol) }
                                    showProtocolDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.updateSettings { it.copy(selectedProtocol = protocol) }
                                        showProtocolDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = protocol.displayName,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (protocol.isRecommended) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "RECOMMENDED",
                                                color = NeonGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Cipher: ${protocol.cipherDescription}",
                                        color = ElectricCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Exchange: ${protocol.keyExchange}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = protocol.summary,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProtocolDialog = false }) {
                    Text("Close", color = ElectricCyan)
                }
            },
            containerColor = CardBackground
        )
    }

    // DNS PROVIDER DIALOG
    if (showDnsDialog) {
        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            title = {
                Text(
                    text = "DNS Leak Protection",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DnsProvider.values().forEach { provider ->
                        val isSelected = settings.dnsProvider == provider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkSurfaceVariant else CardBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.updateSettings { it.copy(dnsProvider = provider) }
                                    if (provider != DnsProvider.CUSTOM) {
                                        showDnsDialog = false
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.updateSettings { it.copy(dnsProvider = provider) }
                                            if (provider != DnsProvider.CUSTOM) {
                                                showDnsDialog = false
                                            }
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = provider.displayName,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (provider.primaryIp.isNotBlank()) {
                                            Text(
                                                text = "${provider.primaryIp} / ${provider.secondaryIp}",
                                                color = ElectricCyan,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Text(
                                            text = provider.features,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (provider == DnsProvider.CUSTOM && isSelected) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = settings.customDnsPrimary,
                                        onValueChange = { newIp ->
                                            viewModel.updateSettings { it.copy(customDnsPrimary = newIp) }
                                        },
                                        label = { Text("Primary Custom DNS", color = TextMuted) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = CardBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDnsDialog = false }) {
                    Text("Done", color = ElectricCyan)
                }
            },
            containerColor = CardBackground
        )
    }

    // MTU SIZE DIALOG
    if (showMtuDialog) {
        AlertDialog(
            onDismissRequest = { showMtuDialog = false },
            title = {
                Text("Select MTU Packet Size", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(1420, "1420 Bytes", "Optimal for WireGuard and high-speed broadband"),
                        Triple(1360, "1360 Bytes", "Reduces packet fragmentation on strict LTE/5G networks"),
                        Triple(1280, "1280 Bytes", "IPv6 standard minimum for restrictive networks")
                    ).forEach { (mtu, label, desc) ->
                        val isSelected = settings.mtuSize == mtu
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkSurfaceVariant else CardBackground)
                                .border(1.dp, if (isSelected) ElectricCyan else CardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateSettings { it.copy(mtuSize = mtu) }
                                    showMtuDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = label, color = if (isSelected) ElectricCyan else TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = desc, color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMtuDialog = false }) {
                    Text("Close", color = ElectricCyan)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = ElectricCyan, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ObsidianNavy,
                checkedTrackColor = ElectricCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}

@Composable
fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String?,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = ElectricCyan, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = badge, color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}
