package org.chapper.chapper.presentation.screen.devicelist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.chapper.chapper.R
import org.chapper.chapper.data.model.Device

class DeviceListAdapter(private val mDevices: ArrayList<Device>, private val listener: OnItemClickListener) : RecyclerView.Adapter<DeviceHolder>() {
    interface OnItemClickListener {
        fun onItemClick(device: Device)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_device, parent, false)
        return DeviceHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        val device = mDevices[position]
        holder.bind(device, listener)
    }

    override fun getItemCount(): Int = mDevices.size

    fun addDevice(device: Device) {
        if (!mDevices.contains(device)) {
            mDevices.add(device)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        mDevices.clear()
        notifyDataSetChanged()
    }
}