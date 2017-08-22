package org.chapper.chapper.presentation.screen.intro

import agency.tango.materialintroscreen.SlideFragment
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import org.chapper.chapper.R
import org.chapper.chapper.data.repository.SettingsRepository
import org.chapper.chapper.presentation.util.BluetoothHelper

class RegisterSlide : SlideFragment() {
    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var username: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_slide, container, false)

        firstName = view.findViewById<EditText>(R.id.first_name)
        lastName = view.findViewById<EditText>(R.id.last_name)
        username = view.findViewById<EditText>(R.id.username)

        if (BluetoothHelper.bluetoothAdapter != null) {
            username!!.setText(BluetoothHelper.bluetoothName)
        } else {
            username!!.hint = getString(R.string.bluetooth_not_available)
            username!!.inputType = InputType.TYPE_NULL
        }

        return view
    }

    override fun backgroundColor(): Int = R.color.color_secondary

    override fun buttonsColor(): Int = R.color.color_accent

    override fun canMoveFurther(): Boolean {
        return !firstName!!.text.isEmpty()
                && !lastName!!.text.isEmpty()
                && !username!!.text.isEmpty()
    }

    override fun cantMoveFurtherErrorMessage(): String = getString(R.string.fill_in_all_fields)

    override fun onPause() {
        super.onPause()

        saveData()
    }

    private fun saveData() {
        SettingsRepository.setFirstStart(false)
        SettingsRepository.setFirstName(firstName!!.text.toString())
        SettingsRepository.setLastName(lastName!!.text.toString())
        BluetoothAdapter.getDefaultAdapter().name = username!!.text.toString()
    }
}