package com.example.vpn

import com.example.model.VpnConnectionState
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnStateManager {
    private val _connectionState = MutableStateFlow(VpnConnectionState.DISCONNECTED)
    val connectionState: StateFlow<VpnConnectionState> = _connectionState.asStateFlow()

    private val _activeServer = MutableStateFlow<VpnServer?>(null)
    val activeServer: StateFlow<VpnServer?> = _activeServer.asStateFlow()

    private val _activeProtocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val activeProtocol: StateFlow<VpnProtocol> = _activeProtocol.asStateFlow()

    private val _sessionStats = MutableStateFlow(VpnSessionStats())
    val sessionStats: StateFlow<VpnSessionStats> = _sessionStats.asStateFlow()

    fun updateState(state: VpnConnectionState) {
        _connectionState.value = state
        if (state == VpnConnectionState.DISCONNECTED) {
            _sessionStats.value = VpnSessionStats()
        }
    }

    fun setActiveServer(server: VpnServer?) {
        _activeServer.value = server
    }

    fun setActiveProtocol(protocol: VpnProtocol) {
        _activeProtocol.value = protocol
    }

    fun updateStats(stats: VpnSessionStats) {
        _sessionStats.value = stats
    }
}
