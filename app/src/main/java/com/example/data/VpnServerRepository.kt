package com.example.data

import com.example.model.VpnProtocol
import com.example.model.VpnServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class VpnServerRepository {

    private val initialServers = listOf(
        VpnServer(
            id = "us_baltimore",
            city = "Baltimore",
            stateOrRegion = "Maryland",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "104.28.19.42",
            latencyMs = 18,
            loadPercentage = 24,
            isVirtual = false,
            isFeatured = true,
            tag = "Low Latency Hub",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_bluffdale",
            city = "Bluffdale",
            stateOrRegion = "Utah",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "198.51.100.77",
            latencyMs = 22,
            loadPercentage = 19,
            isVirtual = false,
            isFeatured = true,
            tag = "Quantum Vault",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_ashburn",
            city = "Ashburn",
            stateOrRegion = "Virginia",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "142.250.190.14",
            latencyMs = 12,
            loadPercentage = 38,
            isVirtual = false,
            isFeatured = true,
            tag = "Ultra-Fast Core",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_nyc",
            city = "New York",
            stateOrRegion = "New York",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "151.101.1.140",
            latencyMs = 15,
            loadPercentage = 42,
            isVirtual = false,
            isFeatured = false,
            tag = "Financial Route",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_chicago",
            city = "Chicago",
            stateOrRegion = "Illinois",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "199.27.76.133",
            latencyMs = 19,
            loadPercentage = 35,
            isVirtual = false,
            isFeatured = false,
            tag = "Central Transit",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_dallas",
            city = "Dallas",
            stateOrRegion = "Texas",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "172.217.4.206",
            latencyMs = 23,
            loadPercentage = 31,
            isVirtual = false,
            isFeatured = false,
            tag = "Anycast Node",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_silicon_valley",
            city = "Silicon Valley",
            stateOrRegion = "California",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "104.16.132.229",
            latencyMs = 21,
            loadPercentage = 40,
            isVirtual = false,
            isFeatured = false,
            tag = "Fiber Ring 10G",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_seattle",
            city = "Seattle",
            stateOrRegion = "Washington",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "205.251.192.0",
            latencyMs = 26,
            loadPercentage = 29,
            isVirtual = false,
            isFeatured = false,
            tag = "Pacific Gateway",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_miami",
            city = "Miami",
            stateOrRegion = "Florida",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "13.248.140.23",
            latencyMs = 24,
            loadPercentage = 33,
            isVirtual = false,
            isFeatured = false,
            tag = "South Gateway",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "us_denver",
            city = "Denver",
            stateOrRegion = "Colorado",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "162.158.0.1",
            latencyMs = 27,
            loadPercentage = 25,
            isVirtual = false,
            isFeatured = false,
            tag = "Mountain Core",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        // Virtual Privacy Locations
        VpnServer(
            id = "virt_reykjavik",
            city = "Reykjavik",
            stateOrRegion = "Capital Region",
            country = "Iceland",
            countryCode = "IS",
            flagEmoji = "🇮🇸",
            ipAddress = "185.112.82.11",
            latencyMs = 62,
            loadPercentage = 16,
            isVirtual = true,
            isFeatured = true,
            tag = "Strict Privacy",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "virt_zurich",
            city = "Zurich",
            stateOrRegion = "Canton Zurich",
            country = "Switzerland",
            countryCode = "CH",
            flagEmoji = "🇨🇭",
            ipAddress = "194.209.200.12",
            latencyMs = 58,
            loadPercentage = 22,
            isVirtual = true,
            isFeatured = true,
            tag = "Swiss Vault",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "virt_frankfurt",
            city = "Frankfurt",
            stateOrRegion = "Hesse",
            country = "Germany",
            countryCode = "DE",
            flagEmoji = "🇩🇪",
            ipAddress = "80.237.133.40",
            latencyMs = 52,
            loadPercentage = 36,
            isVirtual = true,
            isFeatured = false,
            tag = "DE-CIX Backbone",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "virt_amsterdam",
            city = "Amsterdam",
            stateOrRegion = "North Holland",
            country = "Netherlands",
            countryCode = "NL",
            flagEmoji = "🇳🇱",
            ipAddress = "185.107.56.20",
            latencyMs = 49,
            loadPercentage = 31,
            isVirtual = true,
            isFeatured = false,
            tag = "Offshore Transit",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "virt_tokyo",
            city = "Tokyo",
            stateOrRegion = "Kanto",
            country = "Japan",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            ipAddress = "133.242.0.1",
            latencyMs = 74,
            loadPercentage = 28,
            isVirtual = true,
            isFeatured = false,
            tag = "Asia Optical",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        ),
        VpnServer(
            id = "virt_toronto",
            city = "Toronto",
            stateOrRegion = "Ontario",
            country = "Canada",
            countryCode = "CA",
            flagEmoji = "🇨🇦",
            ipAddress = "192.206.151.131",
            latencyMs = 32,
            loadPercentage = 27,
            isVirtual = true,
            isFeatured = false,
            tag = "Low-Latency North",
            bandwidthCapacityGbps = 10,
            supportedProtocols = VpnProtocol.values().toList()
        )
    )

    private val _servers = MutableStateFlow(initialServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow(initialServers.first { it.id == "us_baltimore" })
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun selectServerById(id: String) {
        initialServers.find { it.id == id }?.let {
            _selectedServer.value = it
        }
    }

    fun refreshPings() {
        val updated = _servers.value.map { server ->
            // Subtle realistic jitter variance (+/- 1-3 ms)
            val delta = Random.nextInt(-2, 3)
            val newPing = (server.latencyMs + delta).coerceAtLeast(8)
            val loadDelta = Random.nextInt(-1, 2)
            val newLoad = (server.loadPercentage + loadDelta).coerceIn(10, 95)
            server.copy(latencyMs = newPing, loadPercentage = newLoad)
        }
        _servers.value = updated
        // Keep selected server synced
        _selectedServer.value = updated.find { it.id == _selectedServer.value.id } ?: updated.first()
    }

    fun getFastestServer(): VpnServer {
        return _servers.value.minByOrNull { it.latencyMs } ?: _servers.value.first()
    }
}
