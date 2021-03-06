package org.chapper.chapper.data.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.insert
import com.raizlabs.android.dbflow.kotlinextensions.save
import me.annenkov.bluekitten.BluetoothSPP
import org.chapper.chapper.R
import org.chapper.chapper.data.Constants
import org.chapper.chapper.data.model.Chat
import org.chapper.chapper.data.model.Message
import org.chapper.chapper.data.repository.ChatRepository
import org.chapper.chapper.data.repository.ImageRepository
import org.chapper.chapper.data.repository.MessageRepository
import org.chapper.chapper.data.repository.SettingsRepository
import org.chapper.chapper.data.status.MessageStatus
import org.chapper.chapper.domain.Utils
import org.chapper.chapper.domain.usecase.BluetoothUseCase
import org.chapper.chapper.domain.usecase.NotificationUseCase
import org.chapper.chapper.presentation.broadcastreceiver.BluetoothStateBroadcastReceiver
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

class BluetoothService : Service() {
    private var mBtReceiverState: BluetoothStateBroadcastReceiver by Delegates.notNull()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerBluetoothStateReceiver()
        bluetoothConnectionListener(applicationContext)
        onDataReceivedListener(applicationContext)

        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("empty")
    }

    override fun onDestroy() {
        super.onDestroy()

        FlowManager.destroy()
        BluetoothUseCase.stopService()
    }

    private fun onDataReceivedListener(context: Context) {
        BluetoothFactory.sBtSPP.setOnDataReceivedListener { _, message ->
            val name = BluetoothFactory.sBtSPP.connectedDeviceName
            val address = BluetoothFactory.sBtSPP.connectedDeviceAddress

            if (message != null && name != null && address != null) {
                val chat = ChatRepository.getChat(name, address)
                val id = chat.id

                when {
                    message == Constants.TYPING ->
                        context.sendBroadcast(Intent(Constants.TYPING_TAG))

                    message == Constants.MESSAGES_READ ->
                        MessageRepository.readOutgoingMessages(id)

                    message == Constants.MESSAGE_RECEIVED ->
                        MessageRepository.receiveMessages(id)

                    message.contains(Constants.PHOTO_REQUEST) ->
                        if (SettingsRepository.getPhotoId() != message.replace(Constants.PHOTO_REQUEST, "")) {
                            BluetoothUseCase.sharePhoto(applicationContext)
                        }

                    message.contains(Constants.PHOTO_ID) -> {
                        chat.photoId = message.replace(Constants.PHOTO_ID, "")
                        chat.save()
                    }

                    message.contains(Constants.FIRST_NAME) -> {
                        val text = message.replace(Constants.FIRST_NAME, "")

                        chat.firstName = text
                        chat.save()
                    }

                    message.contains(Constants.LAST_NAME) -> {
                        val text = message.replace(Constants.LAST_NAME, "")

                        chat.lastName = text
                        chat.save()
                    }

                    message.contains(Constants.MESSAGE) -> {
                        val text = message.replace(Constants.MESSAGE, "")
                        Message(chatId = id, text = text).insert()
                        BluetoothUseCase.sendReceived()
                        if (!Utils.isForeground(applicationContext)) {
                            NotificationUseCase
                                    .sendNotification(applicationContext,
                                            id,
                                            ChatRepository.getName(chat),
                                            text)
                        }
                    }

                    message.contains(Constants.PHOTO) -> {
                        doAsync {
                            ImageRepository.saveImage(applicationContext,
                                    id,
                                    ImageRepository.jsonToBitmap(message.replace(Constants.PHOTO, ""))!!)
                        }
                    }
                }
            }
        }
    }

    private fun bluetoothConnectionListener(context: Context) {
        BluetoothFactory.sBtSPP.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String?, address: String?) {
                doAsync {
                    if (name != null && address != null)
                        addChat(context, name, address)

                    BluetoothUseCase.shareUserData()

                    if (name != null && address != null) {
                        val chat = ChatRepository.getChat(name, address)
                        BluetoothUseCase.requestPhoto(chat.photoId)
                    }

                    val intent = Intent(Constants.ACTION_CONNECTED)
                    intent.putExtra(Constants.NAME_EXTRA, name)
                    intent.putExtra(Constants.ADDRESS_EXTRA, address)
                    context.sendBroadcast(intent)
                }
            }

            override fun onDeviceDisconnected() {
                context.sendBroadcast(Intent(Constants.ACTION_DISCONNECTED))
            }

            override fun onDeviceConnectionFailed() {
                context.sendBroadcast(Intent(Constants.ACTION_CONNECTION_FAILED))
            }
        })
    }

    private fun registerBluetoothStateReceiver() {
        val listener = object : BluetoothStateBroadcastReceiver.ActionListener {
            override fun onBluetoothStatusAction() {
                BluetoothUseCase.bluetoothStatusAction()
            }
        }

        registerReceiver(listener)
    }

    private fun registerReceiver(listener: BluetoothStateBroadcastReceiver.ActionListener) {
        mBtReceiverState = BluetoothStateBroadcastReceiver(applicationContext, listener)

        registerReceiver(mBtReceiverState
                , IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun addChat(context: Context, username: String, address: String) {
        if (!ChatRepository.contains(address)) {
            val chat = Chat()
            chat.username = username
            chat.bluetoothMacAddress = address
            chat.firstName = context.getString(R.string.loading)
            chat.insert()
            Message(chatId = chat.id,
                    text = context.getString(R.string.chat_created),
                    status = MessageStatus.ACTION)
                    .insert()
        }
    }
}