package com.robam.ventilator.ext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.robam.common.ui.helper.PickerLayoutManager
import com.robam.ventilator.R
import com.robam.ventilator.ui.adapter.RvStringAdapter

class PickerTimeLayout : LinearLayout {

    private var mHourManager: PickerLayoutManager? = null

    private val mAdapter = RvStringAdapter()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.time_layout, this)
        mHourManager = PickerLayoutManager.Builder(context)
            .setScale(0.5f)
            .setMaxItem(3)
            .build()
        findViewById<RecyclerView>(R.id.rv_time).apply {
            layoutManager = mHourManager
            adapter = mAdapter
        }
    }

    /**
     * 设置自定义列表内容
     */
    fun setData(list: MutableList<String>) {
        mAdapter.setList(list)
    }

    /**
     * 获取列表数据
     */
    fun getData(): MutableList<String> {
        return mAdapter.data
    }

    /**
     * 滚动到指定位置
     */
    fun scrollToPosition(position: Int) {
        mHourManager?.scrollToPosition(position)
    }

    /**
     * 根据文本内容滚动到指定位置
     */
    fun scrollToPosition(text: String) {
        val index = getData().indexOfFirst { it == text }
        if (index != -1) {
            scrollToPosition(index)
        }
    }

    /**
     * 24小时数据
     */
    fun setHour() {
        findViewById<RecyclerView>(R.id.rv_time).apply {
            layoutManager = mHourManager
            adapter = mAdapter
        }

        // 生产小时
        val hourData = ArrayList<String>(24)
        for (i in 0..23) {
            hourData.add((if (i < 10) "0" else "") + i + "")
        }
        mAdapter.setList(hourData)
    }

    /**
     * 设置数据
     * @param hasPreZero 小于10时前缀是否有0
     * @param firstContainsZero 第一位是从0还是1开始 true是从0开始
     */
    fun setNum(count: Int, hasPreZero: Boolean? = true, firstContainsZero: Boolean? = true) {
        // 生产小时
        val data = ArrayList<String>(count)
        for (i in 0 until count) {
            data.add((if (i < 10 && hasPreZero == true) "0" else "") + if (firstContainsZero == true) i else (i + 1).toString() + "")
        }
        mAdapter.setList(data)
    }

    /**
     * 一周数据
     */
    fun setWeek() {
        val week = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        mAdapter.setList(week.toMutableList())
    }

    /**
     * 选中回调数据
     */
    fun setOnPickerListener(callback: (text: String, position: Int) -> Unit) {
        mHourManager?.setOnPickerListener { recyclerView, position ->
            callback.invoke(mAdapter.data[position], position)
        }
    }
}