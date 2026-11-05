package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SpeedTestManager
import com.example.data.SpeedTestState
import com.example.data.VpnServerRepository
import com.example.data.VpnSettingsRepository
import com.example.model.DnsProvider
import com.example.model.SplitTunnelMode
import com.example.model.VpnConnectionState
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.model.VpnSettings
import com.example.vpn.CipherVpnService
import com.example.vpn.VpnStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ServerFilterTab {
    ALL,
    USA_SERVERS,
    VIRTUAL,
    LOW_LATENCY
}

class VpnViewModel(
    private val serverRepository: VpnServerRepository,
    private val settingsRepository: VpnSettingsRepository,
    private val speedTestManager: SpeedTestManager = SpeedTestManager()
) : ViewModel() {

    val connectionState: StateFlow<VpnConnectionState> = VpnStateManager.connectionState
    val activeServer: StateFlow<VpnServer?> = VpnStateManager.activeServer
    val selectedServer: StateFlow<VpnServer> = serverRepository.selectedServer
    val sessionStats: StateFlow<VpnSessionStats> = VpnStateManager.sessionStats
    val settings: StateFlow<VpnSettings> = settingsRepository.settings
    val speedTestState: StateFlow<SpeedTestState> = speedTestManager.testState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterTab = MutableStateFlow(ServerFilterTab.ALL)
    val filterTab: StateFlow<ServerFilterTab> = _filterTab.asStateFlow()

    val filteredServers: StateFlow<List<VpnServer>> = combine(
        serverRepository.servers,
        _searchQuery,
        _filterTab
    ) { servers, query, tab ->
        servers.filter { server ->
            val matchesQuery = query.isBlank() ||
                server.city.contains(query, ignoreCase = true) ||
                server.stateOrRegion.contains(query, ignoreCase = true) ||
                server.country.contains(query, ignoreCase = true) ||
                server.tag.contains(query, ignoreCase = true)

            val matchesTab = when (tab) {
                ServerFilterTab.ALL -> true
                ServerFilterTab.USA_SERVERS -> !server.isVirtual && server.countryCode == "US"
                ServerFilterTab.VIRTUAL -> server.isVirtual
                ServerFilterTab.LOW_LATENCY -> server.latencyMs <= 25
            }

            matchesQuery && matchesTab
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterTab(tab: ServerFilterTab) {
        _filterTab.value = tab
    }

    fun selectServer(server: VpnServer, context: Context? = null) {
        serverRepository.selectServer(server)
        // If currently connected and context is provided, smoothly switch to new server
        if (connectionState.value == VpnConnectionState.CONNECTED && context != null) {
            connectVpn(context, server)
        }
    }

    fun toggleVpnConnection(
        context: Context,
        onPrepareRequired: (Intent) -> Unit
    ) {
        if (connectionState.value == VpnConnectionState.CONNECTED ||
            connectionState.value == VpnConnectionState.CONNECTING ||
            connectionState.value == VpnConnectionState.HANDSHAKE
        ) {
            disconnectVpn(context)
        } else {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                onPrepareRequired(prepareIntent)
            } else {
                connectVpn(context, selectedServer.value)
            }
        }
    }

    fun connectVpn(context: Context, server: VpnServer = selectedServer.value) {
        VpnStateManager.setActiveServer(server)
        val currentSettings = settings.value

        val intent = Intent(context, CipherVpnService::class.java).apply {
            action = CipherVpnService.ACTION_CONNECT
            putExtra(CipherVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(CipherVpnService.EXTRA_SERVER_CITY, server.city)
            putExtra(CipherVpnService.EXTRA_SERVER_IP, server.ipAddress)
            putExtra(CipherVpnService.EXTRA_LATENCY, server.latencyMs)
            putExtra(CipherVpnService.EXTRA_PROTOCOL, currentSettings.selectedProtocol.name)
            putExtra(CipherVpnService.EXTRA_DNS_PROVIDER, currentSettings.dnsProvider.name)
            putExtra(CipherVpnService.EXTRA_CUSTOM_DNS, currentSettings.customDnsPrimary)
            putExtra(CipherVpnService.EXTRA_MTU, currentSettings.mtuSize)
            putExtra(CipherVpnService.EXTRA_IPV6_PROTECT, currentSettings.ipv6LeakProtection)
            putExtra(CipherVpnService.EXTRA_SPLIT_ENABLED, currentSettings.splitTunnelingEnabled)
            putExtra(CipherVpnService.EXTRA_SPLIT_MODE, currentSettings.splitTunnelMode.name)
            putStringArrayListExtra(
                CipherVpnService.EXTRA_SPLIT_APPS,
                ArrayList(currentSettings.splitTunnelPackages)
            )
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            android.util.Log.e("VpnViewModel", "Error starting VPN service: ${e.message}", e)
        }
    }

    fun disconnectVpn(context: Context) {
        val intent = Intent(context, CipherVpnService::class.java).apply {
            action = CipherVpnService.ACTION_DISCONNECT
        }
        try {
            context.startService(intent)
        } catch (_: Exception) {}
        VpnStateManager.setActiveServer(null)
    }

    fun quickConnectBaltimore(context: Context, onPrepareRequired: (Intent) -> Unit) {
        serverRepository.selectServerById("us_baltimore")
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            onPrepareRequired(prepareIntent)
        } else {
            connectVpn(context, selectedServer.value)
        }
    }

    fun quickConnectBluffdale(context: Context, onPrepareRequired: (Intent) -> Unit) {
        serverRepository.selectServerById("us_bluffdale")
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            onPrepareRequired(prepareIntent)
        } else {
            connectVpn(context, selectedServer.value)
        }
    }

    fun quickConnectFastest(context: Context, onPrepareRequired: (Intent) -> Unit) {
        val fastest = serverRepository.getFastestServer()
        serverRepository.selectServer(fastest)
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            onPrepareRequired(prepareIntent)
        } else {
            connectVpn(context, fastest)
        }
    }

    fun refreshServerPings() {
        serverRepository.refreshPings()
    }

    fun updateSettings(transform: (VpnSettings) -> VpnSettings) {
        settingsRepository.updateSettings(transform)
    }

    fun runSpeedTest(server: VpnServer? = null) {
        val target = server ?: activeServer.value ?: selectedServer.value
        viewModelScope.launch {
            speedTestManager.runSpeedTest(target)
        }
    }

    fun resetSpeedTest() {
        speedTestManager.reset()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val serverRepo = VpnServerRepository()
            val settingsRepo = VpnSettingsRepository(context.applicationContext)
            return VpnViewModel(serverRepo, settingsRepo) as T
        }
    }
}
