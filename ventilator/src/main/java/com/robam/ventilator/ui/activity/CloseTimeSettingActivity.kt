package com.robam.ventilator.ui.activity

import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.R
import kotlinx.android.synthetic.main.ventilator_activity_holiday_date_setting.*

/**
 * 延时关机
 */
class CloseTimeSettingActivity : VentilatorBaseActivity() {

    override fun getLayoutId(): Int = R.layout.ventilator_activity_close_time_setting


    override fun initView() {
        showLeft()
        showCenter()

        btn_cancel.setOnClickListener { finish() }
        btn_sure.setOnClickListener { finish() }
    }

    override fun initData() {
        //分钟
        mPickerMinuteLayout.setNum(60)

    }
}