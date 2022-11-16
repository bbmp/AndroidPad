package com.robam.ventilator.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.util.ActivityUtils
import com.robam.common.bean.AccountInfo
import com.robam.common.device.subdevice.Pan
import com.robam.common.device.subdevice.Stove
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
import com.robam.ventilator.device.HomeVentilator
import com.robam.ventilator.factory.VentilatorDialogFactory
import com.robam.ventilator.ui.adapter.RvSmartSetAdapter
import com.robam.ventilator.ui.adapter.SmartSetBean
import kotlinx.android.synthetic.main.ventilator_activity_layout_smart_setting.*

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

                //按钮点击事件
                addChildClickViewIds(R.id.sb_mode, R.id.sb_mode_desc, R.id.tv_mode_desc)
                setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.sb_mode -> {
                            data[position].modeSwitch = !data[position].modeSwitch!!
                            notifyItemChanged(position)
                            //切换开关保存本地
                            saveModeValue(position, data[position].modeSwitch == true)
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
                                "烟蒸烤联动" -> ActivityUtils.startActivity(RelationDeviceActivity::class.java)
                            }
                        }
                    }
                }
            }
            //关闭动画,防止闪烁
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        //假日模式设置
        HomeVentilator.getInstance().smartSet.observe(this) {
            if (it) {
                //选择时间后确认返回页面刷新
                mList.clear()
                addListData()
                mAdapter.setList(mList)
            }
        }
        //查询烟锅联动开关
        val iPublicPanApi = ModulePubliclHelper.getModulePublic(
            IPublicPanApi::class.java, IPublicPanApi.PAN_PUBLIC
        )
        iPublicPanApi?.queryFanPan()
    }

    /**
     * 开关状态本地保存
     */
    private fun saveModeValue(position: Int, onOff: Boolean) {
        when (position) {
            0 -> {
                MMKVUtils.setHoliday(onOff)
                HomeVentilator.getInstance().holiday = onOff
            }
            1 -> MMKVUtils.setOilClean(onOff)
            2 -> MMKVUtils.setDelayShutdown(onOff)
            3 -> {
                MMKVUtils.setFanStove(onOff)
                if (!onOff) { //烟灶联动关闭
                    HomeVentilator.getInstance().stopLevelCountDown()
                    HomeVentilator.getInstance().stopA6CountDown()
                }
            }
            4 -> MMKVUtils.setFanPan(onOff)
            5 -> MMKVUtils.setFanSteam(onOff)
        }
    }
    /**
     *  烟灶锅，一体机自动匹配风量
     */
    private fun saveModedescValue(modeName: String, onOff: Boolean) {
        when (modeName) {
            "烟灶联动" -> MMKVUtils.setFanStoveGear(onOff)
            "烟锅联动" -> MMKVUtils.setFanPanGear(onOff)
            "烟蒸烤联动" -> MMKVUtils.setFanSteamGear(onOff)
        }
    }

    /**
     * 数据填充
     */
    private fun addListData() {

        mList.add(
            SmartSetBean(
                "假日模式",
                String.format(
                    context.getString(R.string.ventilator_holiday_desc_set),
                    MMKVUtils.getHolidayDay(),
                    MMKVUtils.getHolidayWeekTime()
                ),
                MMKVUtils.getHoliday()
            )
        )
        mList.add(
            SmartSetBean(
                "油网清洗提醒功能",
                "",
                MMKVUtils.getOilClean()
            )
        )
        mList.add(
            SmartSetBean(
                "延时关机",
                String.format(
                    context.getString(R.string.ventilator_shutdown_delay_set),
                    MMKVUtils.getDelayShutdownTime()
                ),
                MMKVUtils.getDelayShutdown()
            )
        )
        //灶
        val stoveList =
            AccountInfo.getInstance().deviceList.filter { it is Stove }.map { it.displayType }
        if (stoveList.isNotEmpty()) {
            val stoveListDevice =
                "关联产品:${stoveList.toString().substring(1,stoveList.toString().length-1)}"
            mList.add(
                SmartSetBean(
                    "烟灶联动",
                    "$stoveListDevice \n灶具小火工作时，烟机自动匹配风量",
                    MMKVUtils.getFanStove(),
                    MMKVUtils.getFanStoveGear(),
                    true
                )
            )
        }

        //锅
        val panList =
            AccountInfo.getInstance().deviceList.filter { it is Pan }.map { it.displayType }
        if (panList.isNotEmpty()) {

            val panListDevice = "关联产品:${panList.toString().substring(1,panList.toString().length-1)}"
            mList.add(
                SmartSetBean(
                    "烟锅联动",
                    "$panListDevice \n明火自动翻炒锅工作时开着，烟机自动匹配风量",
                    MMKVUtils.getFanPan(),
                    MMKVUtils.getFanPanGear(),
                    true
                )
            )
        }

        //一体机
        var steamOvenListDevice: String? = null

        for (device in AccountInfo.getInstance().deviceList) {
            if (device is SteamOven) {
                if (MMKVUtils.getFanSteamDevice() == device.guid) { //已关联
                    steamOvenListDevice = "关联产品:${device.displayType}"
                    break
                } else
                    steamOvenListDevice = "暂无关联产品"
            }
        }

         steamOvenListDevice?.apply {

            mList.add(
                SmartSetBean(
                    "烟蒸烤联动",
                    "$steamOvenListDevice \n一体机工作室开门，烟机自动匹配风量",
                    MMKVUtils.getFanSteam(),
                    MMKVUtils.getFanSteamGear(),
                    true
                )
            )
        }
    }

    override fun initData() {
        //设备名字
        tv_device_name.text = "油烟机" + BuildConfig.MODEL

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