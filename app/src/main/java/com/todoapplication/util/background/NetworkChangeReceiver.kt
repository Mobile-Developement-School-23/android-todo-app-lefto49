package com.todoapplication.util.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.todoapplication.TodoApp
import com.todoapplication.data.network.api.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val manager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return
        val network = manager.activeNetwork ?: return
        val info = manager.getNetworkCapabilities(network) ?: return
        if (info.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || info.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            )
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val result = TodoApp.repo.syncData()
                if (result == ResponseStatus.OK) {
                    TodoApp.preferences.edit().putBoolean("local updates", false).apply()
                }
            }
        }
    }
}