package com.robam.ventilator.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robam.common.IDeviceType
import com.robam.common.bean.AccountInfo
import com.robam.common.bean.BaseResponse
import com.robam.common.bean.Device
import com.robam.common.device.Plat
import com.robam.common.http.RetrofitCallback
import com.robam.common.manager.LiveDataBus
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.constant.VentilatorConstant
import com.robam.ventilator.databinding.VentilatorActivityLayoutRelationDeviceBinding
import com.robam.ventilator.http.CloudHelper
import com.robam.ventilator.ui.adapter.RvRelationDeviceAdapter

//烟蒸烤关联设备
class RelationDeviceActivity: VentilatorBaseActivity() {

    private lateinit var binding: VentilatorActivityLayoutRelationDeviceBinding

    private val mList: MutableList<Device> = mutableListOf()

    private val mAdapter by lazy { RvRelationDeviceAdapter(R.layout.ventilator_item_layout_relation_device, null) }

    override fun getLayoutId(): Int = R.layout.ventilator_activity_layout_relation_device

    override fun setContentView(layoutResID: Int) {
        binding = VentilatorActivityLayoutRelationDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun initView() {
        showLeft()
        showCenter()
        val devideGuid = intent?.getStringExtra(VentilatorConstant.DEVICE_GUID)
        val fanSteam = intent?.getBooleanExtra(VentilatorConstant.FAN_STEAM, false)?:false
        val fanSteamGear = intent?.getBooleanExtra(VentilatorConstant.FAN_STEAM_GEAR, false)?:false

        binding.rvDevice.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = mAdapter.apply {
                relationDevice = devideGuid
                //填充数据
                addListData()
                setList(mList)

                //按钮点击事件
                addChildClickViewIds(R.id.btn_add)
                setOnItemChildClickListener { _, _, position ->
                    var guid = mList[position].guid
                    if (relationDevice != guid) {
                        //关联设备
                        CloudHelper.setLinkageConfig(this@RelationDeviceActivity,
                            Plat.getPlatform().deviceOnlySign,
                            fanSteam,
                            fanSteamGear,
                            guid,
                            mList[position].displayType,
                            BaseResponse::class.java,
                            object : RetrofitCallback<BaseResponse?> {
                                override fun onSuccess(baseResponse: BaseResponse?) {
                                    if (null != baseResponse && baseResponse.rc == 0) { //设置成功
                                        relationDevice = guid
                                        notifyDataSetChanged()
                                        //更新烟蒸烤
//                                        SmartSettingActivity.act?.updateFanStream(mList[position].displayType)
                                        LiveDataBus.get().with(VentilatorConstant.DT, String::class.java).value =
                                            mList[position].displayType
                                    }
                                }

                                override fun onFaild(err: String) {}
                            })

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