package com.robam.ventilator.ui.activity

import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.R
import kotlinx.android.synthetic.main.ventilator_activity_holiday_date_setting.*

/**
 * 假日模式
 */
class HolidayDateSettingActivity : VentilatorBaseActivity() {

    override fun getLayoutId(): Int = R.layout.ventilator_activity_holiday_date_setting


    override fun initView() {
        showLeft()
        showCenter()

        btn_cancel.setOnClickListener { finish() }
        btn_sure.setOnClickListener { finish() }
    }

    override fun initData() {
        //天
        mPickerDayLayout.setNum(7, false, false)
        //周
        mPickerWeekLayout.setWeek()
        //小时
        mPickerHourLayout.setNum(24)
        //分钟
        mPickerMinuteLayout.setNum(60)

    }
}