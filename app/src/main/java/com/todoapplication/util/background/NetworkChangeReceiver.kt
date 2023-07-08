package com.todoapplication.util.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.todoapplication.data.network.api.ResponseStatus
import com.todoapplication.data.repository.TodoItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Is used for observing the user's connectivity status.
 */
class NetworkChangeReceiver @Inject constructor(
    private val repo: TodoItemsRepository,
    private val preferences: SharedPreferences
) : BroadcastReceiver() {
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
                val result = repo.syncData()
                if (result == ResponseStatus.OK) {
                    preferences.edit().putBoolean("local updates", false).apply()
                }
            }
        }
    }
}