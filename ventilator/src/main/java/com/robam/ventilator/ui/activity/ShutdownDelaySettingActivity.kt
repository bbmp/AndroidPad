package com.robam.ventilator.ui.activity

import com.robam.common.utils.MMKVUtils
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.ext.TextColorHelp
import kotlinx.android.synthetic.main.ventilator_activity_holiday_date_setting.btn_cancel
import kotlinx.android.synthetic.main.ventilator_activity_holiday_date_setting.btn_sure
import kotlinx.android.synthetic.main.ventilator_activity_holiday_date_setting.mPickerMinuteLayout
import kotlinx.android.synthetic.main.ventilator_activity_shutdown_delay_setting.*

/**
 * 延时关机
 */
class ShutdownDelaySettingActivity : VentilatorBaseActivity() {

    private var minute: String? = null

    override fun getLayoutId(): Int = R.layout.ventilator_activity_shutdown_delay_setting


    override fun initView() {
        showLeft()
        showCenter()

        btn_cancel.setOnClickListener { finish() }
        btn_sure.setOnClickListener {
            minute?.let {
                MMKVUtils.setDelayShutdownTime(minute)
                HomeVentilator.getInstance().shutdown.value = true
            }
            finish()
        }
    }

    override fun initData() {

        val delayShutdownTime = MMKVUtils.getDelayShutdownTime()

        textChange(delayShutdownTime)
        //分钟选择器
        mPickerMinuteLayout.apply {
            setNum(5, false, false)
            setOnPickerListener { text, position ->
                minute = text
                textChange(text)
            }

            //获取默认值的索引
            scrollToPosition(delayShutdownTime)
        }

    }

    /**
     * 描述文本
     */
    private fun textChange(delayShutdownTime: String) {
        TextColorHelp.setShutdownTextColor(
            String.format(
                context.getString(R.string.ventilator_shutdown_delay_set),
                delayShutdownTime
            ),
            tv_shutdown_desc
        )
    }
}