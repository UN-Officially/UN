package com.example.model

enum class VpnProtocol(
    val displayName: String,
    val cipherDescription: String,
    val keyExchange: String,
    val summary: String,
    val isRecommended: Boolean = false
) {
    WIREGUARD(
        displayName = "WireGuard",
        cipherDescription = "ChaCha20-Poly1305 AEAD",
        keyExchange = "Curve25519 ECDH / Noise IK",
        summary = "Ultra-low latency, modern cryptographic suite, minimal CPU overhead for maximum browsing speed.",
        isRecommended = true
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN (UDP)",
        cipherDescription = "AES-256-GCM / SHA-512",
        keyExchange = "RSA-4096 / TLS 1.3",
        summary = "High-speed datagram protocol engineered for smooth 4K streaming, gaming, and rapid downloads."
    ),
    OPENVPN_TCP(
        displayName = "OpenVPN (TCP)",
        cipherDescription = "AES-256-GCM / Port 443",
        keyExchange = "Diffie-Hellman 4096-bit",
        summary = "Reliable bypass over HTTPS port 443 for restrictive public Wi-Fi, school/work firewalls, and deep packet inspection."
    ),
    IKEV2_IPSEC(
        displayName = "IKEv2 / IPsec",
        cipherDescription = "AES-GCM-256 / MOBIKE",
        keyExchange = "Diffie-Hellman Group 20",
        summary = "Uninterrupted mobile handshakes when switching seamlessly between cellular networks and Wi-Fi."
    ),
    STEALTH_QUANTUM(
        displayName = "Quantum-Stealth",
        cipherDescription = "Kyber-1024 + ChaCha20",
        keyExchange = "Post-Quantum ML-KEM Lattice",
        summary = "Quantum-resistant encryption layered with heuristic traffic scrambling to defeat DPI censorship."
    )
}

enum class DnsProvider(
    val displayName: String,
    val primaryIp: String,
    val secondaryIp: String,
    val features: String
) {
    CLOUDFLARE("Cloudflare 1.1.1.1", "1.1.1.1", "1.0.0.1", "Privacy-first zero-logging DNS"),
    QUAD9("Quad9 Threat Block", "9.9.9.9", "149.112.112.112", "Blocks malware, spyware, & phishing"),
    ADGUARD("AdGuard DNS", "94.140.14.14", "94.140.15.15", "Filters intrusive mobile ads & web trackers"),
    GOOGLE("Google Public DNS", "8.8.8.8", "8.8.4.4", "Global fast Anycast resolving"),
    CUSTOM("Custom Secure DNS", "", "", "User-defined DNS server addresses")
}

enum class SplitTunnelMode {
    BYPASS_VPN,   // Selected apps bypass the VPN
    ONLY_VPN      // Only selected apps use the VPN
}

data class VpnSettings(
    val killSwitchEnabled: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val splitTunnelMode: SplitTunnelMode = SplitTunnelMode.BYPASS_VPN,
    val splitTunnelPackages: Set<String> = emptySet(),
    val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val dnsProvider: DnsProvider = DnsProvider.CLOUDFLARE,
    val customDnsPrimary: String = "1.1.1.1",
    val customDnsSecondary: String = "1.0.0.1",
    val ipv6LeakProtection: Boolean = true,
    val mtuSize: Int = 1420,
    val autoConnectOnWifi: Boolean = true,
    val bypassLocalNetwork: Boolean = true,
    val quantumResistanceEnabled: Boolean = true
)

data class VpnServer(
    val id: String,
    val city: String,
    val stateOrRegion: String,
    val country: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val latencyMs: Int,
    val loadPercentage: Int,
    val isVirtual: Boolean,
    val isFeatured: Boolean,
    val tag: String,
    val bandwidthCapacityGbps: Int = 10,
    val supportedProtocols: List<VpnProtocol> = VpnProtocol.values().toList()
)

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    HANDSHAKE,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

data class VpnSessionStats(
    val durationSeconds: Long = 0,
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val totalBytesReceived: Long = 0,
    val totalBytesSent: Long = 0,
    val currentPingMs: Int = 0,
    val jitterMs: Int = 1,
    val virtualIp: String = "10.8.0.2",
    val activeCipher: String = "ChaCha20-Poly1305"
)
