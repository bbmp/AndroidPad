package com.robam.ventilator.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.robam.common.ui.dialog.IDialog
import com.robam.common.ui.helper.VerticalSpaceItemDecoration
import com.robam.ventilator.BuildConfig
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.constant.DialogConstant
import com.robam.ventilator.factory.VentilatorDialogFactory
import com.robam.ventilator.ui.adapter.RvSmartSetAdapter
import com.robam.ventilator.ui.adapter.SmartSetBean
import kotlinx.android.synthetic.main.ventilator_activity_layout_smart_setting.*
import kotlinx.android.synthetic.main.ventilator_view_layout_title.*

/**
 * 智能设置页码
 */
class SmartSettingActivity : VentilatorBaseActivity() {
    private var resetDialog: IDialog? = null
    private val mList: MutableList<SmartSetBean> = mutableListOf()

    private val mAdapter by lazy { RvSmartSetAdapter(R.layout.ventilator_item_smart_setting, null) }

    override fun getLayoutId() = R.layout.ventilator_activity_layout_smart_setting


    override fun initView() {
        showLeft()
        setCenter(R.string.ventilator_smart_setting)
        //自动换气
//        sbAir = findViewById(R.id.sb_auto_air)
//        sbAir?.setChecked(MMKVUtils.getAutoAir())
//        //油网提醒
//        sbOil = findViewById(R.id.sb_auto_oil)
//        setOnClickListener(R.id.ll_left, R.id.bt_reset)
//        sbOil?.setChecked(MMKVUtils.getOilClean())
//        sbAir?.setOnCheckedChangeListener { button, checked ->
//            MMKVUtils.setAutoAir(
//                checked
//            )
//        }
//        sbOil?.setOnCheckedChangeListener { button, checked ->
//            MMKVUtils.setOilClean(
//                checked
//            )
//        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    resources.getDimension(com.robam.common.R.dimen.dp_10).toInt()
                )
            )

            adapter = mAdapter.apply {
                //填充数据
                addListData()
                setList(mList)

                setOnItemClickListener { adapter, view, position ->
                    if (position == 0) {
                        ActivityUtils.startActivity(HolidayDateSettingActivity::class.java)
                    }
                }
                //按钮点击事件
                addChildClickViewIds(R.id.sb_mode,R.id.sb_mode_desc)
                setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.sb_mode->{
                            data[position].modeSwitch = !data[position].modeSwitch!!
                            notifyItemChanged(position)
                        }
                        R.id.sb_mode_desc->{}
                    }
                }
            }
        }
    }

    /**
     * 数据填充
     */
    private fun addListData() {
        mList.add(
            SmartSetBean(
                "假日模式",
                "7天未使用油烟机、自动开启换气（换气时间为14:00） \n每周固定 周三12:30 开启油烟机3分钟自动换气",
            )
        )
        mList.add(
            SmartSetBean(
                "油网清洗提醒功能",
                "",
            )
        )
        mList.add(
            SmartSetBean(
                "延时关机",
                "油烟机将延迟1分钟关机",
            )
        )
        mList.add(
            SmartSetBean(
                "烟灶联动",
                "暂无关联产品 \n灶具小火工作时，烟机自动匹配风量",
                modeDescSwitchVisible = true
            )
        )
        mList.add(
            SmartSetBean(
                "烟锅联动",
                "暂无关联产品 \n明火自动翻炒锅工作时开着，烟机自动匹配风量",
                modeDescSwitchVisible = true
            )
        )
        mList.add(
            SmartSetBean(
                "烟蒸烤联动",
                "暂无关联产品 \n一体机工作室开门，烟机自动匹配风量",
                modeDescSwitchVisible = true
            )
        )

    }

    override fun initData() {
        //设备名字
        tv_device_name.text = BuildConfig.MODEL

        bt_reset.setOnClickListener {
            //恢复初始提示
            resetDialog()
        }



    }


    //恢复初始
    private fun resetDialog() {
        if (null == resetDialog) {
            resetDialog = VentilatorDialogFactory.createDialogByType(
                this,
                DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON
            )
            resetDialog?.setCancelable(false)
            resetDialog?.setContentText(R.string.ventilator_smart_reset_hint)
            resetDialog?.setOKText(R.string.ventilator_reset_start)
            resetDialog?.setListeners(
                { v -> if (v.id == R.id.tv_ok); },
                R.id.tv_cancel,
                R.id.tv_ok
            )
        }
        resetDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != resetDialog && resetDialog!!.isShow) resetDialog?.dismiss()
    }
}