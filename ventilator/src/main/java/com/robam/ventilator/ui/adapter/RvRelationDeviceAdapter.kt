package com.robam.ventilator.ui.adapter

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.robam.common.bean.Device
import com.robam.common.utils.MMKVUtils
import com.robam.ventilator.R

class RvRelationDeviceAdapter (@LayoutRes layoutResId: Int, data: MutableList<Device>?) :
    BaseQuickAdapter<Device, BaseViewHolder>(layoutResId, data)  {

    var relationDevice: String = MMKVUtils.getFanSteamDevice()

    override fun convert(holder: BaseViewHolder, item: Device) {
        holder.setText(R.id.tv_device_name, item.getCategoryName())
        holder.setText(R.id.tv_model, item.getDisplayType())
        if (item.guid == relationDevice) {
            holder.setText(R.id.btn_add, R.string.ventilator_relationed)
            holder.setBackgroundResource(R.id.btn_add, R.drawable.ventilator_shape_button_unselected)
        } else {
            holder.setText(R.id.btn_add, R.string.ventilator_relation)
            holder.setBackgroundResource(R.id.btn_add, R.drawable.ventilator_shape_button_selected)
        }
    }
}