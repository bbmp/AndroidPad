package com.robam.ventilator.ui.adapter

import android.view.View
import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.robam.common.ui.view.SwitchButton
import com.robam.ventilator.R
import com.robam.ventilator.ext.visibleOrGone

class RvSmartSetAdapter(@LayoutRes layoutResId: Int, data: MutableList<SmartSetBean>?) :
    BaseQuickAdapter<SmartSetBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: SmartSetBean) {
        holder.setText(R.id.tv_mode, item.modeName)
        holder.setText(R.id.tv_mode_desc, item.modeDescName)

        holder.getView<SwitchButton>(R.id.sb_mode).isChecked = item.modeSwitch == true
        holder.getView<SwitchButton>(R.id.sb_mode_desc).isChecked = item.modeDescSwitch == true

        //模式描述没内容隐藏
        holder.getView<View>(R.id.tv_mode_desc).visibleOrGone(item.modeDescName.isNotEmpty())
        holder.getView<SwitchButton>(R.id.sb_mode_desc).visibleOrGone(item.modeDescSwitchVisible == true)
        //模式切换按钮关闭 描述详情隐藏
        holder.getView<View>(R.id.ll_mode_desc).visibleOrGone(item.modeSwitch == true)

    }


}

data class SmartSetBean(
    //模式类型名字
    val modeName: String,
    //模式描述
    val modeDescName: String,
    //模式切换按钮
    var modeSwitch: Boolean?=false,
    //模式描述切换按钮
    var modeDescSwitch: Boolean?=false,
    //模式描述切换按钮是否可见
    var modeDescSwitchVisible: Boolean?=false,
)