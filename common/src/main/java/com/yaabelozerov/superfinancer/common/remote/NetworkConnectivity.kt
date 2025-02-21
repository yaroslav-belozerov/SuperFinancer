package com.yaabelozerov.superfinancer.common.remote

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal val networkRequest: NetworkRequest = NetworkRequest.Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()

internal object NetworkCallback : ConnectivityManager.NetworkCallback() {
    val isConnected = MutableStateFlow(false)

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isConnected.update { true }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isConnected.update { false }
    }
}