package org.chapper.chapper.data.model

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.annotation.Unique
import com.raizlabs.android.dbflow.kotlinextensions.eq
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import org.chapper.chapper.data.MessageStatus
import org.chapper.chapper.data.database.AppDatabase
import org.chapper.chapper.data.repository.MessageRepository
import java.util.*

@Table(database = AppDatabase::class)
data class Chat(
        @PrimaryKey
        @Unique
        var id: String = UUID.randomUUID().toString(),

        @Column
        var photo: String = "",

        @Column
        var firstName: String = "",

        @Column
        var lastName: String = "",

        @Column
        var username: String = "",

        @Column
        var bluetoothMacAddress: String = ""
) {
    fun getLastMessage(): Message = MessageRepository.getMessages(id).lastOrNull() ?: Message()
    val newMessagesNumber: Int = (select from Message::class where
            (Message_Table.status eq MessageStatus.INCOMING_UNREAD))
            .count().toInt()
}