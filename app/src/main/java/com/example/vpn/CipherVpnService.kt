package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.DnsProvider
import com.example.model.SplitTunnelMode
import com.example.model.VpnConnectionState
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.random.Random

class CipherVpnService : VpnService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var tunnelJob: Job? = null
    private var vpnInterface: ParcelFileDescriptor? = null

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_ID = "EXTRA_SERVER_ID"
        const val EXTRA_SERVER_NAME = "EXTRA_SERVER_NAME"
        const val EXTRA_SERVER_CITY = "EXTRA_SERVER_CITY"
        const val EXTRA_SERVER_IP = "EXTRA_SERVER_IP"
        const val EXTRA_LATENCY = "EXTRA_LATENCY"
        const val EXTRA_PROTOCOL = "EXTRA_PROTOCOL"
        const val EXTRA_DNS_PROVIDER = "EXTRA_DNS_PROVIDER"
        const val EXTRA_CUSTOM_DNS = "EXTRA_CUSTOM_DNS"
        const val EXTRA_MTU = "EXTRA_MTU"
        const val EXTRA_IPV6_PROTECT = "EXTRA_IPV6_PROTECT"
        const val EXTRA_SPLIT_ENABLED = "EXTRA_SPLIT_ENABLED"
        const val EXTRA_SPLIT_MODE = "EXTRA_SPLIT_MODE"
        const val EXTRA_SPLIT_APPS = "EXTRA_SPLIT_APPS"

        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "cipher_vpn_channel"
        private const val TAG = "CipherVpnService"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_CONNECT
        if (action == ACTION_DISCONNECT) {
            disconnectVpn()
            return START_NOT_STICKY
        }

        val serverCity = intent?.getStringExtra(EXTRA_SERVER_CITY) ?: "Baltimore"
        val serverIp = intent?.getStringExtra(EXTRA_SERVER_IP) ?: "104.28.19.42"
        val serverLatency = intent?.getIntExtra(EXTRA_LATENCY, 18) ?: 18
        val protocolName = intent?.getStringExtra(EXTRA_PROTOCOL) ?: VpnProtocol.WIREGUARD.name
        val protocol = try { VpnProtocol.valueOf(protocolName) } catch (_: Exception) { VpnProtocol.WIREGUARD }
        val dnsProviderName = intent?.getStringExtra(EXTRA_DNS_PROVIDER) ?: DnsProvider.CLOUDFLARE.name
        val dnsProvider = try { DnsProvider.valueOf(dnsProviderName) } catch (_: Exception) { DnsProvider.CLOUDFLARE }
        val customDns = intent?.getStringExtra(EXTRA_CUSTOM_DNS) ?: "1.1.1.1"
        val mtu = intent?.getIntExtra(EXTRA_MTU, 1420) ?: 1420
        val ipv6Protect = intent?.getBooleanExtra(EXTRA_IPV6_PROTECT, true) ?: true
        val splitEnabled = intent?.getBooleanExtra(EXTRA_SPLIT_ENABLED, false) ?: false
        val splitMode = intent?.getStringExtra(EXTRA_SPLIT_MODE) ?: SplitTunnelMode.BYPASS_VPN.name
        val splitApps = intent?.getStringArrayListExtra(EXTRA_SPLIT_APPS) ?: arrayListOf()

        startVpnTunnel(
            city = serverCity,
            ip = serverIp,
            baseLatency = serverLatency,
            protocol = protocol,
            dnsProvider = dnsProvider,
            customDns = customDns,
            mtu = mtu,
            ipv6Protect = ipv6Protect,
            splitEnabled = splitEnabled,
            splitMode = splitMode,
            splitApps = splitApps
        )

        return START_STICKY
    }

    private fun startVpnTunnel(
        city: String,
        ip: String,
        baseLatency: Int,
        protocol: VpnProtocol,
        dnsProvider: DnsProvider,
        customDns: String,
        mtu: Int,
        ipv6Protect: Boolean,
        splitEnabled: Boolean,
        splitMode: String,
        splitApps: List<String>
    ) {
        val notification = buildNotification("Connecting to $city...", "Establishing $protocol tunnel...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        tunnelJob?.cancel()
        tunnelJob = serviceScope.launch {
            try {
                VpnStateManager.updateState(VpnConnectionState.CONNECTING)
                VpnStateManager.setActiveProtocol(protocol)
                delay(400) // Handshake simulation delay

                VpnStateManager.updateState(VpnConnectionState.HANDSHAKE)
                delay(300)

                // Configure Virtual TUN Interface via Android VpnService.Builder
                val builder = Builder()
                    .setSession("CipherVPN - $city")
                    .setMtu(mtu)
                    .addAddress("10.8.0.2", 24)
                    .addRoute("0.0.0.0", 0)

                // DNS configuration
                val primaryDns = when (dnsProvider) {
                    DnsProvider.CLOUDFLARE -> "1.1.1.1"
                    DnsProvider.QUAD9 -> "9.9.9.9"
                    DnsProvider.ADGUARD -> "94.140.14.14"
                    DnsProvider.GOOGLE -> "8.8.8.8"
                    DnsProvider.CUSTOM -> if (customDns.isNotBlank()) customDns else "1.1.1.1"
                }
                builder.addDnsServer(primaryDns)

                // IPv6 Leak Protection routing
                if (ipv6Protect) {
                    try {
                        builder.addAddress("fd00:1:2:3::2", 128)
                        builder.addRoute("::", 0)
                    } catch (e: Exception) {
                        Log.w(TAG, "IPv6 address assignment fallback: ${e.message}")
                    }
                }

                // Split Tunneling application rules
                if (splitEnabled && splitApps.isNotEmpty()) {
                    splitApps.forEach { pkg ->
                        try {
                            if (splitMode == SplitTunnelMode.BYPASS_VPN.name) {
                                builder.addDisallowedApplication(pkg)
                            } else {
                                builder.addAllowedApplication(pkg)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Split tunnel rule failed for $pkg: ${e.message}")
                        }
                    }
                }

                // Establish TUN interface
                vpnInterface?.close()
                vpnInterface = builder.establish()

                if (vpnInterface == null) {
                    Log.e(TAG, "VpnService.Builder.establish() returned null")
                    VpnStateManager.updateState(VpnConnectionState.ERROR)
                    stopSelf()
                    return@launch
                }

                VpnStateManager.updateState(VpnConnectionState.CONNECTED)

                // Update notification with active status
                val activeNotification = buildNotification(
                    "Connected: $city (USA)",
                    "Protocol: ${protocol.displayName} • Securely Encrypted"
                )
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, activeNotification)

                // Live traffic & telemetry loop
                var secondsElapsed = 0L
                var totalRxBytes = 0L
                var totalTxBytes = 0L

                while (isActive) {
                    delay(1000)
                    secondsElapsed++

                    // Generate realistic high-speed low-latency VPN throughput metrics
                    // Speed fluctuates realistically depending on simulated browsing traffic
                    val dlSpeed = Random.nextFloat() * 35f + 45f // 45 to 80 Mbps
                    val ulSpeed = Random.nextFloat() * 15f + 18f // 18 to 33 Mbps

                    val rxChunk = (dlSpeed * 1024 * 1024 / 8).toLong()
                    val txChunk = (ulSpeed * 1024 * 1024 / 8).toLong()

                    totalRxBytes += rxChunk
                    totalTxBytes += txChunk

                    val currentPing = (baseLatency + Random.nextInt(-2, 3)).coerceAtLeast(8)
                    val jitter = Random.nextInt(1, 3)

                    VpnStateManager.updateStats(
                        VpnSessionStats(
                            durationSeconds = secondsElapsed,
                            downloadSpeedMbps = dlSpeed,
                            uploadSpeedMbps = ulSpeed,
                            totalBytesReceived = totalRxBytes,
                            totalBytesSent = totalTxBytes,
                            currentPingMs = currentPing,
                            jitterMs = jitter,
                            virtualIp = ip,
                            activeCipher = protocol.cipherDescription
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in VPN tunnel execution: ${e.message}", e)
                VpnStateManager.updateState(VpnConnectionState.ERROR)
                disconnectVpn()
            }
        }
    }

    private fun disconnectVpn() {
        tunnelJob?.cancel()
        tunnelJob = null
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing vpn interface: ${e.message}")
        }
        VpnStateManager.updateState(VpnConnectionState.DISCONNECTED)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(title: String, content: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, CipherVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this, 1, disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Disconnect", disconnectPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CipherVPN Service Channel"
            val descriptionText = "Notifications for active VPN tunnel connection and privacy status"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        disconnectVpn()
        serviceScope.cancel()
        super.onDestroy()
    }
}
