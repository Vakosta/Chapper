package org.chapper.chapper.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.select
import org.chapper.chapper.data.model.Settings
import org.chapper.chapper.domain.usecase.BluetoothUseCase
import java.util.*

object SettingsRepository {
    fun update() {
        val settings = (select from Settings::class).querySingle()!!
        settings.save()
    }

    fun getPhotoId(): String = (select from Settings::class).querySingle()!!.photoId

    private fun generatePhotoId() {
        val settings = (select from Settings::class).querySingle()!!
        settings.photoId = UUID.randomUUID().toString()
        settings.save()
    }

    fun getAddress(context: Context): String = BluetoothUseCase.getBluetoothAddress(context)

    fun getUsername(): String = BluetoothUseCase.getBluetoothName()

    fun getName(): String = "${getFirstName()} ${getLastName()}"

    fun getProfilePhoto(context: Context): Bitmap? = ImageRepository.getImage(context, "profile")

    fun isFirstStart(): Boolean = (select from Settings::class).querySingle()!!.isFirstStart

    fun getFirstName(): String = (select from Settings::class).querySingle()!!.firstName

    fun getLastName(): String = (select from Settings::class).querySingle()!!.lastName

    fun isSendByEnter(): Boolean = (select from Settings::class).querySingle()!!.isSendByEnter

    fun getFirstCharsName(): String {
        val firstName = getFirstName()
        val lastName = getLastName()
        val username = getUsername()
        return try {
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                "${firstName[0]}${lastName[0]}"
            } else {
                "${username[0]}"
            }
        } catch (e: Exception) {
            "..."
        }
    }

    fun setUsername(username: String) {
        BluetoothUseCase.setBluetoothName(username)
    }

    fun setProfilePhoto(context: Context, image: Bitmap) {
        ImageRepository.saveImage(context, "profile", image)
        generatePhotoId()
    }

    fun setFirstStart(isFirstStart: Boolean) {
        val settings = (select from Settings::class).querySingle()!!
        settings.isFirstStart = isFirstStart
        settings.save()
    }

    fun setFirstName(firstName: String) {
        val settings = (select from Settings::class).querySingle()!!
        settings.firstName = firstName
        settings.save()
    }

    fun setLastName(lastName: String) {
        val settings = (select from Settings::class).querySingle()!!
        settings.lastName = lastName
        settings.save()
    }

    fun setSendByEnter(sendByEnter: Boolean) {
        val settings = (select from Settings::class).querySingle()!!
        settings.isSendByEnter = sendByEnter
        settings.save()
    }
}