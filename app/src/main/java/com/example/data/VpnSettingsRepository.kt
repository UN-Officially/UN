package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DnsProvider
import com.example.model.SplitTunnelMode
import com.example.model.VpnProtocol
import com.example.model.VpnSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VpnSettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cipher_vpn_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    private fun loadSettings(): VpnSettings {
        val protocolName = prefs.getString("protocol", VpnProtocol.WIREGUARD.name) ?: VpnProtocol.WIREGUARD.name
        val selectedProtocol = try {
            VpnProtocol.valueOf(protocolName)
        } catch (_: Exception) {
            VpnProtocol.WIREGUARD
        }

        val dnsName = prefs.getString("dns_provider", DnsProvider.CLOUDFLARE.name) ?: DnsProvider.CLOUDFLARE.name
        val dnsProvider = try {
            DnsProvider.valueOf(dnsName)
        } catch (_: Exception) {
            DnsProvider.CLOUDFLARE
        }

        val splitModeName = prefs.getString("split_mode", SplitTunnelMode.BYPASS_VPN.name) ?: SplitTunnelMode.BYPASS_VPN.name
        val splitMode = try {
            SplitTunnelMode.valueOf(splitModeName)
        } catch (_: Exception) {
            SplitTunnelMode.BYPASS_VPN
        }

        val packages = prefs.getStringSet("split_packages", emptySet()) ?: emptySet()

        return VpnSettings(
            killSwitchEnabled = prefs.getBoolean("kill_switch", true),
            splitTunnelingEnabled = prefs.getBoolean("split_tunneling_enabled", false),
            splitTunnelMode = splitMode,
            splitTunnelPackages = packages,
            selectedProtocol = selectedProtocol,
            dnsProvider = dnsProvider,
            customDnsPrimary = prefs.getString("custom_dns_primary", "1.1.1.1") ?: "1.1.1.1",
            customDnsSecondary = prefs.getString("custom_dns_secondary", "1.0.0.1") ?: "1.0.0.1",
            ipv6LeakProtection = prefs.getBoolean("ipv6_leak_protection", true),
            mtuSize = prefs.getInt("mtu_size", 1420),
            autoConnectOnWifi = prefs.getBoolean("auto_connect_wifi", true),
            bypassLocalNetwork = prefs.getBoolean("bypass_local_network", true),
            quantumResistanceEnabled = prefs.getBoolean("quantum_resistance", true)
        )
    }

    fun updateSettings(transform: (VpnSettings) -> VpnSettings) {
        val updated = transform(_settings.value)
        _settings.value = updated
        prefs.edit().apply {
            putBoolean("kill_switch", updated.killSwitchEnabled)
            putBoolean("split_tunneling_enabled", updated.splitTunnelingEnabled)
            putString("split_mode", updated.splitTunnelMode.name)
            putStringSet("split_packages", updated.splitTunnelPackages)
            putString("protocol", updated.selectedProtocol.name)
            putString("dns_provider", updated.dnsProvider.name)
            putString("custom_dns_primary", updated.customDnsPrimary)
            putString("custom_dns_secondary", updated.customDnsSecondary)
            putBoolean("ipv6_leak_protection", updated.ipv6LeakProtection)
            putInt("mtu_size", updated.mtuSize)
            putBoolean("auto_connect_wifi", updated.autoConnectOnWifi)
            putBoolean("bypass_local_network", updated.bypassLocalNetwork)
            putBoolean("quantum_resistance", updated.quantumResistanceEnabled)
            apply()
        }
    }
}
