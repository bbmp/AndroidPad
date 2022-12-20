package com.robam.ventilator.ui.activity

import com.robam.common.manager.LiveDataBus
import com.robam.common.utils.MMKVUtils
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.constant.VentilatorConstant
import com.robam.ventilator.databinding.VentilatorActivityLayoutRelationDeviceBinding
import com.robam.ventilator.databinding.VentilatorActivityShutdownDelaySettingBinding
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.ext.TextColorHelp

/**
 * 延时关机
 */
class ShutdownDelaySettingActivity : VentilatorBaseActivity() {

    private lateinit var binding: VentilatorActivityShutdownDelaySettingBinding

    private var minute: String? = null

    override fun getLayoutId(): Int = R.layout.ventilator_activity_shutdown_delay_setting

    override fun setContentView(layoutResID: Int) {
        binding = VentilatorActivityShutdownDelaySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        showLeft()
        showCenter()

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSure.setOnClickListener {
            minute?.let {
                MMKVUtils.setDelayShutdownTime(minute)
                LiveDataBus.get().with(VentilatorConstant.SMART_SET, Boolean::class.java).value = true
            }
            finish()
        }
    }

    override fun initData() {

        val delayShutdownTime = MMKVUtils.getDelayShutdownTime()

        textChange(delayShutdownTime)
        //分钟选择器
        binding.mPickerMinuteLayout.apply {
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
            binding.tvShutdownDesc
        )
    }
}