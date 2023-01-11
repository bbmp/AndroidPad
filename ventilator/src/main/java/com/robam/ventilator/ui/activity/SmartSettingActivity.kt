package com.robam.ventilator.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.util.ActivityUtils
import com.robam.common.bean.AccountInfo
import com.robam.common.bean.BaseResponse
import com.robam.common.bean.Device
import com.robam.common.device.Plat
import com.robam.common.device.subdevice.Pan
import com.robam.common.device.subdevice.Stove
import com.robam.common.http.RetrofitCallback
import com.robam.common.manager.LiveDataBus
import com.robam.common.module.IPublicPanApi
import com.robam.common.module.ModulePubliclHelper
import com.robam.common.ui.dialog.IDialog
import com.robam.common.ui.helper.VerticalSpaceItemDecoration
import com.robam.common.utils.MMKVUtils
import com.robam.steamoven.bean.SteamOven
import com.robam.ventilator.BuildConfig
import com.robam.ventilator.R
import com.robam.ventilator.base.VentilatorBaseActivity
import com.robam.ventilator.constant.DialogConstant
import com.robam.ventilator.constant.VentilatorConstant
import com.robam.ventilator.databinding.VentilatorActivityLayoutSmartSettingBinding
import com.robam.ventilator.databinding.VentilatorActivityShutdownDelaySettingBinding
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.factory.VentilatorDialogFactory
import com.robam.ventilator.http.CloudHelper
import com.robam.ventilator.request.LinkageConfigReq
import com.robam.ventilator.response.GetLinkageConfigRes
import com.robam.ventilator.ui.adapter.RvSmartSetAdapter
import com.robam.ventilator.ui.adapter.SmartSetBean

/**
 * 智能设置页码
 */
class SmartSettingActivity : VentilatorBaseActivity() {
    private lateinit var binding: VentilatorActivityLayoutSmartSettingBinding

    private var resetDialog: IDialog? = null
    private val mList: MutableList<SmartSetBean> = mutableListOf()

    private val mAdapter by lazy { RvSmartSetAdapter(R.layout.ventilator_item_smart_setting, null) }

    private var linkageConfig: LinkageConfigReq? = null

    override fun setContentView(layoutResID: Int) {
        binding = VentilatorActivityLayoutSmartSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun getLayoutId() = R.layout.ventilator_activity_layout_smart_setting

    //烟锅联动状态查询
    //查询烟锅联动开关
    private var iPublicPanApi = ModulePubliclHelper.getModulePublic(
        IPublicPanApi::class.java, IPublicPanApi.PAN_PUBLIC
    )

    override fun initView() {
        showLeft()
        setCenter(R.string.ventilator_smart_setting)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SmartSettingActivity)
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    resources.getDimension(com.robam.common.R.dimen.dp_10).toInt()
                )
            )

            adapter = mAdapter.apply {
                //填充数据
                addListData()
                setList(mList)

                //按钮点击事件
                addChildClickViewIds(R.id.sb_mode, R.id.sb_mode_desc, R.id.tv_mode_desc)
                setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.sb_mode -> {
                            data[position].modeSwitch = !data[position].modeSwitch!!
                            notifyItemChanged(position)
                            //切换开关保存本地
                            saveModeValue(data[position].modeName, data[position].modeSwitch == true)
                        }
                        R.id.sb_mode_desc -> {
                            data[position].modeDescSwitch = !data[position].modeDescSwitch!!
                            //切换保存本地
                            saveModedescValue(data[position].modeName, data[position].modeDescSwitch == true)
                        }
                        R.id.tv_mode_desc -> {
                            when (data[position].modeName) {
                                "假日模式" -> ActivityUtils.startActivity(HolidayDateSettingActivity::class.java)
                                "延时关机" -> ActivityUtils.startActivity(ShutdownDelaySettingActivity::class.java)
                                "烟蒸烤联动" -> {
                                    intent = Intent(this@SmartSettingActivity, RelationDeviceActivity::class.java)
                                    intent.putExtra(VentilatorConstant.DEVICE_GUID, linkageConfig?.targetGuid)
                                    intent.putExtra(VentilatorConstant.FAN_STEAM, linkageConfig?.enabled?:false)
                                    intent.putExtra(VentilatorConstant.FAN_STEAM_GEAR, linkageConfig?.doorOpenEnabled?:false)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
            //关闭动画,防止闪烁
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        //假日模式设置
        LiveDataBus.get().with(
            VentilatorConstant.SMART_SET,
                  Boolean::class.java
        ).observe(this) {
            if (it) {
                //选择时间后确认返回页面刷新
                mList.clear()
                addListData()
                mAdapter.setList(mList)
            }
        }
        LiveDataBus.get().with(
            VentilatorConstant.DT,
            String::class.java
        ).observe(
            this
        ) {
            getFanSteamLinkage()
        }
    }

    /**
     * 开关状态本地保存
     */
    private fun saveModeValue(modeName: String, onOff: Boolean) {
        when (modeName) {
            "假日模式" -> {
                MMKVUtils.setHoliday(onOff)
//                HomeVentilator.getInstance().holiday = onOff
            }
            "油网清洗提醒功能" -> MMKVUtils.setOilClean(onOff)
            "延时关机" -> MMKVUtils.setDelayShutdown(onOff)
            "烟灶联动" -> {
                MMKVUtils.setFanStove(onOff)
                if (!onOff) { //烟灶联动关闭
                    HomeVentilator.getInstance().stopLevelCountDown()
                    HomeVentilator.getInstance().stopA6CountDown()
                } else
                    updateFanStove()
            }
            "烟锅联动" -> {
                for (device in AccountInfo.getInstance().deviceList) { //查找锅
                    if (device is Pan) {
                        var pan: Pan = device

                        if (onOff) {
                            pan.fanPan = pan.fanPan.or(0x02)
                            updateFanPan(pan.fanPan)
                        } else //关闭时风量也关闭
                            pan.fanPan = pan.fanPan.and(0xF9)
                        pan.let { iPublicPanApi?.setFanPan(it.fanPan) }  //设置烟锅联动
                        break
                    }
                }

            }
            "烟蒸烤联动" -> {
                MMKVUtils.setFanSteam(onOff)
                CloudHelper.setLinkageConfig(this@SmartSettingActivity,
                    Plat.getPlatform().deviceOnlySign,
                    onOff,
                    linkageConfig?.doorOpenEnabled?:false,
                    linkageConfig?.targetGuid,
                    linkageConfig?.targetDeviceName,
                    BaseResponse::class.java,
                    object : RetrofitCallback<BaseResponse?> {
                        override fun onSuccess(baseResponse: BaseResponse?) {
                            if (null != baseResponse && baseResponse.rc == 0) { //设置成功

                            }
                        }

                        override fun onFaild(err: String) {}
                    })
            }
        }
    }
    /**
     *  烟灶锅，一体机自动匹配风量
     */
    private fun saveModedescValue(modeName: String, onOff: Boolean) {
        when (modeName) {
            "烟灶联动" -> MMKVUtils.setFanStoveGear(onOff)
            "烟锅联动" -> {
                for (device in AccountInfo.getInstance().deviceList) { //查找锅
                    if (device is Pan) {
                        var pan: Pan = device
                        if (onOff) {
                            pan.fanPan = pan.fanPan.or(0x04)
                        } else //关闭
                            pan.fanPan = pan.fanPan.and(0xFB)
                        pan.let { iPublicPanApi?.setFanPan(it.fanPan) }  //设置烟锅联动
                    }
                }

            }
            "烟蒸烤联动" -> {
                MMKVUtils.setFanSteamGear(onOff)
                CloudHelper.setLinkageConfig(this@SmartSettingActivity,
                    Plat.getPlatform().deviceOnlySign,
                    linkageConfig?.enabled?:true,
                    onOff,
                    linkageConfig?.targetGuid,
                    linkageConfig?.targetDeviceName,
                    BaseResponse::class.java,
                    object : RetrofitCallback<BaseResponse?> {
                        override fun onSuccess(baseResponse: BaseResponse?) {
                            if (null != baseResponse && baseResponse.rc == 0) { //设置成功

                            }
                        }

                        override fun onFaild(err: String) {}
                    })
            }
        }
    }

    /**
     * 数据填充
     */
    private fun addListData() {

        mList.add(
            SmartSetBean(
                true,
                "假日模式",
                String.format(
                    getString(R.string.ventilator_holiday_desc_set),
                    MMKVUtils.getHolidayDay(),
                    MMKVUtils.getHolidayWeekTime()
                ),
                MMKVUtils.getHoliday()
            )
        )
        mList.add(
            SmartSetBean(
                true,
                "油网清洗提醒功能",
                "",
                MMKVUtils.getOilClean()
            )
        )
        mList.add(
            SmartSetBean(
                true,
                "延时关机",
                String.format(
                    getString(R.string.ventilator_shutdown_delay_set),
                    MMKVUtils.getDelayShutdownTime()
                ),
                MMKVUtils.getDelayShutdown()
            )
        )
        //灶
        for (device in AccountInfo.getInstance().deviceList) {
            if (device is Stove) {
                val stove: Stove = device
                val stoveListDevice =
                    "关联产品:${stove.displayType}"
                mList.add(
                    SmartSetBean(
                        true,
                        "烟灶联动",
                        "$stoveListDevice \n灶具小火工作时，烟机自动匹配风量",
                        MMKVUtils.getFanStove(),
                        MMKVUtils.getFanStoveGear(),
                        true
                    )
                )

                break
            }
        }
        //锅
        for (device in AccountInfo.getInstance().deviceList) {
            if (device is Pan) {
                val pan: Pan = device
                val panListDevice = "关联产品:${pan.displayType}"
                val fanPan: Int = pan.fanPan and 0x02 shr 1
                val fanPanGear: Int = pan.fanPan and 0x04 shr 2
                mList.add(
                    SmartSetBean(
                        pan.status == Device.ONLINE,
                        "烟锅联动",
                        "$panListDevice \n明火自动翻炒锅工作时开着，烟机自动匹配风量",
                        fanPan === 1,
                        fanPanGear === 1,
                        true
                    )
                )

                break
            }
        }

        //一体机
        var relationDevice: String? = null
        var enabled = false //是否有一体机
        for (device in AccountInfo.getInstance().deviceList) {
            if (device is SteamOven) {
                enabled = true
                if (linkageConfig?.targetGuid == device.guid) { //已关联
                    relationDevice = "${device.displayType}"
                    break
                } else
                    relationDevice = "去关联"
            }
        }

        if (enabled) {
            relationDevice?.apply {

                mList.add(
                    SmartSetBean(
                        true,
                        "烟蒸烤联动",
                        "关联产品:$relationDevice \n一体机工作室开门，烟机自动匹配风量",
                        linkageConfig?.enabled ?: false,
                        linkageConfig?.doorOpenEnabled ?: false,
                        true
                    )
                )
            }
        }
    }
    //更新烟蒸烤联动
    fun updateFanStream(relationDevice: String?) { //关联设备
        for (smartBean in mList) {
            if (smartBean.modeName == "烟蒸烤联动") {
                smartBean.modeDescName = "关联产品:$relationDevice\n一体机工作室开门，烟机自动匹配风量"
                smartBean.modeSwitch = linkageConfig?.enabled ?: false
                smartBean.modeDescSwitch = linkageConfig?.doorOpenEnabled ?: false
                mAdapter?.notifyDataSetChanged()
                break
            }
        }
    }
    //更新烟灶联动
    fun updateFanStove() {
        for (smartBean in mList) {
            if (smartBean.modeName == "烟灶联动") {
                smartBean.modeSwitch = MMKVUtils.getFanStove()
                smartBean.modeDescSwitch = MMKVUtils.getFanStoveGear()
                mAdapter?.notifyDataSetChanged()
                break
            }
        }
    }
    //更新烟锅联动
    fun updateFanPan(fanPan: Int) {
        for (smartBean in mList) {
            if (smartBean.modeName == "烟锅联动") {
                val fanPan: Int = fanPan and 0x02 shr 1
                val fanPanGear: Int = fanPan and 0x04 shr 2
                smartBean.modeSwitch = (fanPan === 1)
                smartBean.modeDescSwitch = (fanPanGear === 1)
                mAdapter?.notifyDataSetChanged()
                break
            }
        }
    }

    override fun initData() {
        //设备名字
        binding.tvDeviceName.text = "油烟机" + BuildConfig.MODEL

        binding.btReset.setOnClickListener {
            //恢复初始提示
            resetDialog()
        }
        getFanSteamLinkage()
    }

    private fun getFanSteamLinkage() {
        //查询烟蒸烤联动状态
        val userInfo = AccountInfo.getInstance().user.value

        CloudHelper.getLinkageConfig(this, Plat.getPlatform().deviceOnlySign,
            userInfo?.id ?: 0,
            GetLinkageConfigRes::class.java, object : RetrofitCallback<GetLinkageConfigRes?> {
                override fun onSuccess(getLinkageConfigRes: GetLinkageConfigRes?) {
                    if (getLinkageConfigRes?.payload != null) {
                        linkageConfig = getLinkageConfigRes.payload
                        var relationDevice = "去关联"
                        if (linkageConfig!!.targetDeviceName != null)
                            relationDevice = "${linkageConfig!!.targetDeviceName}"
                        updateFanStream(relationDevice)
                    }
                }

                override fun onFaild(err: String) {}

            })
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
                { v -> if (v.id == R.id.tv_ok) {
                        MMKVUtils.resetSmartSet()
                        mList.clear()
                        addListData()
                        mAdapter.setList(mList)
                    }
                },
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