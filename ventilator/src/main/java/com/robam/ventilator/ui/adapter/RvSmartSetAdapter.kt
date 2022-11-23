package com.robam.ventilator.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.annotation.LayoutRes
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.robam.common.ui.view.SwitchButton
import com.robam.ventilator.R
import com.robam.ventilator.ext.TextColorHelp
import com.robam.ventilator.ext.visibleOrGone

class RvSmartSetAdapter(@LayoutRes layoutResId: Int, data: MutableList<SmartSetBean>?) :
    BaseQuickAdapter<SmartSetBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: SmartSetBean) {
        //模式名字
        holder.setText(R.id.tv_mode, item.modeName)

        //设置时间字体颜色蓝色
        when (item.modeName) {
            "假日模式" -> {
                TextColorHelp.setHolidayTextColor(item.modeDescName,holder.getView(R.id.tv_mode_desc))
            }
            "延时关机" -> {
                TextColorHelp.setShutdownTextColor(item.modeDescName,holder.getView(R.id.tv_mode_desc))
            }
            "烟蒸烤联动" -> {
                TextColorHelp.setFanSteamTextColor(item.modeDescName,holder.getView(R.id.tv_mode_desc))
            }
            else -> {
                //模式描述
                holder.setText(R.id.tv_mode_desc, item.modeDescName)
            }
        }
        //是否可点击
        holder.getView<SwitchButton>(R.id.sb_mode).isEnabled = item.enabled == true
        //模式开关
        holder.getView<SwitchButton>(R.id.sb_mode).isChecked = item.modeSwitch == true
        //模式描述开关
        holder.getView<SwitchButton>(R.id.sb_mode_desc).isEnabled = item.enabled == true
        holder.getView<SwitchButton>(R.id.sb_mode_desc).isChecked = item.modeDescSwitch == true

        //模式描述文本没内容隐藏
        holder.getView<View>(R.id.tv_mode_desc).visibleOrGone(item.modeDescName.isNotEmpty())
        //模式描述按钮是否隐藏
        holder.getView<SwitchButton>(R.id.sb_mode_desc)
            .visibleOrGone(item.modeDescSwitchVisible == true)
        //模式切换按钮关闭 描述详情文本和按钮隐藏
        holder.getView<View>(R.id.ll_mode_desc).visibleOrGone(item.modeSwitch == true)

    }


}

data class SmartSetBean(
    var enabled: Boolean? = false,
    //模式类型名字
    val modeName: String,
    //模式描述
    var modeDescName: String,
    //模式切换按钮
    var modeSwitch: Boolean? = false,
    //模式描述切换按钮
    var modeDescSwitch: Boolean? = false,
    //模式描述切换按钮是否可见
    var modeDescSwitchVisible: Boolean? = false,
)