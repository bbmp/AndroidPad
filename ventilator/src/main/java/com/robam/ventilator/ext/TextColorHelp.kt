package com.robam.ventilator.ext

import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.robam.ventilator.R

object TextColorHelp {

    //假日模式时间颜色
    fun setHolidayTextColor(text: String, textView: TextView) {
        val dayEnd = text.indexOf("天")//第一个出现的位置
        val timeStart = text.indexOf("气时间为")//第一个出现的位置
        val timeEnd = text.indexOf("，换气时长")//第一个出现的位置

        SpanUtils.with(textView)
            //天数颜色
            .append(text.substring(0, dayEnd))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_blue))

            .append(text.substring(dayEnd, timeStart + 4))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))

            //每周时间颜色
            .append(text.substring(timeStart + 4, timeEnd))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_blue))

            .append(text.substring(timeEnd, text.length))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))
            .create()
    }

    //延时关机时间颜色
    fun setShutdownTextColor(text: String, textView: TextView) {
        val start = text.indexOf("分钟")//第一个出现的位置

        SpanUtils.with(textView)
            .append(text.substring(0, 6))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))

            //分钟颜色
            .append(text.substring(6, start))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_blue))

            .append(text.substring(start, text.length))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))
            .create()
    }
    //烟蒸烤联动关联颜色
    fun setFanSteamTextColor(text: String, textView: TextView) {
        val start = text.indexOf("一体机")//第一个出现的位置

        SpanUtils.with(textView)
            .append(text.substring(0, 5))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))

            .append(text.substring(5, start))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_blue))

            .append(text.substring(start, text.length))
            .setForegroundColor(ColorUtils.getColor(R.color.ventilator_white_50))
            .create()
    }
}