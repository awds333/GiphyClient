package io.demo.fedchenko.giphyclient.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager


class NetworkStateBroadcastReceiver(private val onConnected: () -> Unit, private val onDisconnected: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context
            ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnected

        if(isConnected)
            onConnected()
        else
            onDisconnected()
    }
}