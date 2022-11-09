package com.robam.ventilator.ui.activity

import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity

//烟蒸烤关联设备
class RelationDeviceActivity: VentilatorBaseActivity() {

    override fun getLayoutId(): Int = R.layout.ventilator_activity_layout_relation_device

    override fun initView() {
        showLeft()
        showCenter()
    }

    override fun initData() {
    }
}