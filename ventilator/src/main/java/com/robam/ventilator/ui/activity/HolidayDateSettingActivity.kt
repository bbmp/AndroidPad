package com.robam.ventilator.ui.activity

import com.robam.common.manager.LiveDataBus
import com.robam.common.utils.MMKVUtils
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.constant.VentilatorConstant
import com.robam.ventilator.databinding.VentilatorActivityHolidayDateSettingBinding
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.ext.TextColorHelp

/**
 * 假日模式
 */
class HolidayDateSettingActivity : VentilatorBaseActivity() {

    /**
     * 天数
     */
    private lateinit var day: String

    /**
     * 周
     */
    private lateinit var week: String

    /**
     * 小时
     */
    private lateinit var hour: String

    /**
     * 分钟
     */
    private lateinit var minute: String

    private lateinit var binding: VentilatorActivityHolidayDateSettingBinding

    override fun getLayoutId(): Int = R.layout.ventilator_activity_holiday_date_setting

    override fun setContentView(layoutResID: Int) {
        binding = VentilatorActivityHolidayDateSettingBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun initView() {

        showLeft()
        showCenter()

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSure.setOnClickListener {
            MMKVUtils.setHolidayDay(day)
            MMKVUtils.setHolidayWeekTime("$week$hour:$minute")
            LiveDataBus.get().with(VentilatorConstant.SMART_SET, Boolean::class.java).value = true
//            HomeVentilator.getInstance().holidayDay = day
//            HomeVentilator.getInstance().weekTime = "$week$hour:$minute"
            finish()
        }
    }

    override fun initData() {
        //获取默认值
        day = MMKVUtils.getHolidayDay()
        val weekTime = MMKVUtils.getHolidayWeekTime()
        week = weekTime.substring(0, 2)
        hour = weekTime.substring(2, 4)
        minute = weekTime.substring(5, 7)

        textChange(day)
        //天
        binding.mPickerDayLayout.apply {
            setNum(7, false, false)
            setOnPickerListener { text, position ->
                day = text
                textChange(day)
            }
            //获取默认值的索引
            scrollToPosition(day)
        }

        //周
        binding.mPickerWeekLayout.apply {
            setWeek()
            setOnPickerListener { text, position ->
                week = text
                textChange(day)
            }
            //获取默认值的索引
            scrollToPosition(week)
        }
        //小时
        binding.mPickerHourLayout.apply {
            setNum(24)
            setOnPickerListener { text, position ->
                hour = text
                textChange(day)
            }
            //获取默认值的索引
            scrollToPosition(hour)
        }
        //分钟
        binding.mPickerMinuteLayout.apply {
            setNum(60)
            setOnPickerListener { text, position ->
                minute = text
                textChange(day)
            }
            //获取默认值的索引
            scrollToPosition(minute)
        }

    }

    /**
     * 描述文本
     */
    private fun textChange(day: String) {
        TextColorHelp.setHolidayTextColor(
            String.format(
                getString(R.string.ventilator_holiday_desc_set),
                day,
                "$week$hour:$minute"
            ),
            binding.tvModeDesc
        )
    }
}