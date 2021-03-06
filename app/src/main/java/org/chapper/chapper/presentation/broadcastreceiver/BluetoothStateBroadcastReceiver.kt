package org.chapper.chapper.presentation.broadcastreceiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothStateBroadcastReceiver(
        private val context: Context,
        private val listener: BluetoothStateBroadcastReceiver.ActionListener
) : BroadcastReceiver() {
    interface ActionListener {
        fun onBluetoothStatusAction()
    }

    override fun onReceive(context: Context, intent: Intent) {
        listener.onBluetoothStatusAction()
    }

    fun registerContext() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(this, filter)
    }

    fun unregisterContext() {
        context.unregisterReceiver(this)
    }
}