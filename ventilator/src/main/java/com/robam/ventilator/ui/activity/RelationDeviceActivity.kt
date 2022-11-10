package com.robam.ventilator.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.robam.common.IDeviceType
import com.robam.common.bean.AccountInfo
import com.robam.common.bean.Device
import com.robam.common.utils.MMKVUtils
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.ui.adapter.RvRelationDeviceAdapter
import kotlinx.android.synthetic.main.ventilator_activity_layout_relation_device.*

//烟蒸烤关联设备
class RelationDeviceActivity: VentilatorBaseActivity() {
    private val mList: MutableList<Device> = mutableListOf()

    private val mAdapter by lazy { RvRelationDeviceAdapter(R.layout.ventilator_item_layout_relation_device, null) }

    override fun getLayoutId(): Int = R.layout.ventilator_activity_layout_relation_device

    override fun initView() {
        showLeft()
        showCenter()

        rv_device.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = mAdapter.apply {
                //填充数据
                addListData()
                setList(mList)

                //按钮点击事件
                addChildClickViewIds(R.id.btn_add)
                setOnItemChildClickListener { _, _, position ->
                    var guid = mList[position].guid
                    if (relationDevice != guid) {
                        MMKVUtils.setFanSteamDevice(guid)
                        relationDevice = guid
                        notifyDataSetChanged()
                        HomeVentilator.getInstance().smartSet.value = true
                    }
                }
            }
        }
    }

    private fun addListData() {
        for (device in AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZKY))
                mList.add(device)
        }
    }

    override fun initData() {
    }
}