package com.robam.ventilator.ui.adapter;


import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.CabinetWaringEnum;
import com.robam.common.IDeviceType;
import com.robam.common.bean.Device;
import com.robam.common.bean.DeviceErrorInfo;
import com.robam.common.bean.MqttDirective;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.manager.DeviceWarnInfoManager;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DishWasherModeEnum;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.constant.DishWasherWaringEnum;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.device.subdevice.Stove;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.utils.SteamDataUtil;
import com.robam.ventilator.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

public class RvProductsAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    private LifecycleOwner mOwner;


    public RvProductsAdapter(LifecycleOwner owner) {
        super(R.layout.ventilator_item_layout_device);
        this.mOwner = owner;
        //左灶关火
        addChildClickViewIds(R.id.btn_left_close, R.id.btn_right_close, R.id.btn_detail, R.id.btn_work);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {
        if (null != device) {
            baseViewHolder.setText(R.id.tv_device_name, device.getCategoryName());
            //baseViewHolder.setText(R.id.tv_model, (StringUtils.isNotBlank(device.getName()) ? device.getName() : device.getDisplayType()));
            baseViewHolder.setText(R.id.tv_model, device.getDisplayType());
            ImageView ivDevice = baseViewHolder.getView(R.id.iv_device);
            if (IDeviceType.RXDG.equals(device.dc)) {
                ivDevice.setImageResource(R.drawable.ventilator_cabinet);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    Cabinet cabinet = (Cabinet) device;
                    dealCabinetWorkView(cabinet,baseViewHolder);
                }
            } else if (IDeviceType.RRQZ.equals(device.dc)) {//灶具
                ivDevice.setImageResource(R.drawable.ventilator_stove);
                if (device.getStatus() == Device.OFFLINE) {
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    Stove stove = (Stove) device;
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (stove.leftAlarm != 0xff || stove.rightAlarm != 0xff) {  //故障
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setVisible(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
                    } else if (stove.leftLevel == 0 && stove.rightLevel == 0) {  //未工作
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else {
                        //工作状态
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                        baseViewHolder.setVisible(R.id.ventilator_group7, true);//显示灶具
                        baseViewHolder.setGone(R.id.ventilator_group6, true);
                        TextView leftClose = baseViewHolder.getView(R.id.btn_left_close);
                        TextView rightClose = baseViewHolder.getView(R.id.btn_right_close);

                        if (stove.leftLevel != 0) {
                            leftClose.setBackgroundResource(R.drawable.ventilator_shape_button_selected);
                            leftClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white));
                        } else {
                            leftClose.setBackgroundResource(R.drawable.ventilator_shape_button_unselected);
                            leftClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white_50));
                        }
                        if (stove.rightLevel != 0) {
                            rightClose.setBackgroundResource(R.drawable.ventilator_shape_button_selected);
                            rightClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white));
                        } else {
                            rightClose.setBackgroundResource(R.drawable.ventilator_shape_button_unselected);
                            rightClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white_50));
                        }
                    }
                }
            } else if (IDeviceType.RZKY.equals(device.dc)) { //一体机
                ivDevice.setImageResource(R.drawable.ventilator_steam);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    SteamOven steamOven = (SteamOven) device;
                    dealSteamWorkView(steamOven,baseViewHolder);
                }
            } else if (IDeviceType.RXWJ.equals(device.dc)) { //洗碗机
                ivDevice.setImageResource(R.drawable.ventilator_dishwasher);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    DishWasher dishWasher = (DishWasher) device;
                    dealDishWasherWorkFinish(dishWasher,baseViewHolder);
                }
            } else if (IDeviceType.RZNG.equals(device.dc)) {//无人锅
                ivDevice.setImageResource(R.drawable.ventilator_pan);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    Pan pan = (Pan) device;
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (pan.sysytemStatus == 0 || pan.sysytemStatus == 1) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else if (pan.sysytemStatus == 3) { //低电量
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setVisible(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_low_battery);
                    } else {
                        //故障
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setVisible(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
                    }
                }
            } else if (IDeviceType.RYYJ.equals(device.dc)) {//油烟机
                ivDevice.setImageResource(R.drawable.ventilator_ventilator);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setVisible(R.id.btn_detail, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (device.getWorkStatus() == 0) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else if (device.getWorkStatus() != 0) {
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                    } else {
                        //故障
                    }
                }
            }
        }

    }



    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
        super.onBindViewHolder(baseViewHolder, position);
//        if (getItemViewType(position) == ProductMutiItem.DEVICE) {
//            ProductMutiItem productMutiItem = getItem(position);
//            Device device = productMutiItem.getDevice();
//            device.observe(mOwner, new Observer<Device>() {
//                @Override
//                public void onChanged(Device device) {
//                    if (IDeviceType.RYYJ.equals(device.dc)) {//灶具
//                        if (device.getStatus() == Device.OFFLINE) {
//                            baseViewHolder.setVisible(R.id.layout_offline, true);
//                            baseViewHolder.setGone(R.id.layout_work, true);
//                            baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
//                            baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
//                        } else {
//                            //在线
//                            baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
//                            baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
//                            if (device.getWorkStatus() == 0) {  //未工作
//                                baseViewHolder.setVisible(R.id.layout_offline, true);
//                                baseViewHolder.setGone(R.id.layout_work, true);
//                                baseViewHolder.setGone(R.id.btn_detail, true);
//                                baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
//                            } else {
//                                if (device.getWorkStatus() != 0) { //工作状态
//                                    baseViewHolder.setGone(R.id.layout_offline, true);
//                                    baseViewHolder.setVisible(R.id.layout_work, true);
//                                    baseViewHolder.setVisible(R.id.ventilator_group7, true);//显示灶具
//                                    baseViewHolder.setGone(R.id.ventilator_group6, true);
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }
    }


    /**
     * 洗碗机工作展示
     * @param dishWasher
     * @return
     */
    private void dealDishWasherWorkFinish(DishWasher dishWasher,BaseViewHolder baseViewHolder){
        if(dishWasher.faultId != 0 && DishWasherWaringEnum.match(dishWasher.faultId).getCode() != DishWasherWaringEnum.E0.getCode()){//故障
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setVisible(R.id.btn_detail, true);
            return;
        }
        MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(dishWasher.guid);
        boolean isWork = false;
        boolean isAppoint = false;//是否正在预约
        if((dishWasher.powerStatus == DishWasherState.WORKING  ||
                dishWasher.powerStatus == DishWasherState.PAUSE) &&
                dishWasher.remainingWorkingTime > 0){
            isWork = true;
            if(dishWasher.AppointmentRemainingTime  > 0){
                isAppoint = true;
            }
        }
        if(isWork){
            baseViewHolder.setGone(R.id.layout_offline, true);
            baseViewHolder.setVisible(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.ventilator_group7, true);
            baseViewHolder.setVisible(R.id.ventilator_group6, true);
            baseViewHolder.setText(R.id.tv_mode, DishWasherEnum.match(dishWasher.workMode));
            if(isAppoint){
                baseViewHolder.setText(R.id.tv_time, getStartTimePrompt(dishWasher.AppointmentRemainingTime * 60,dishWasher.dc));
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_appoint_finish);
            }else{
                baseViewHolder.setText(R.id.tv_time, getSpan(dishWasher.remainingWorkingTime * 60,true));
                if (dishWasher.getWorkStatus() == 2) //工作中
                    baseViewHolder.setText(R.id.btn_work, R.string.ventilator_pause);
                else if (dishWasher.getWorkStatus() == 3) //暂停中
                    baseViewHolder.setText(R.id.btn_work, R.string.ventilator_continue);
                else
                    baseViewHolder.setGone(R.id.btn_work, true);
            }
        }else{
            preDishwasherTimeValue = "";
            preDishwasherTimeMil = 0;
            boolean isWorkFinish = false;
            if(workState != null && workState.isFinish()) {
                isWorkFinish = true;
            }
            //String modelName = DishWasherEnum.match(dishWasher.workMode);
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.btn_detail, true);
            baseViewHolder.setText(R.id.tv_hint, isWorkFinish ? "清洁完成":"会洗锅的\n洗碗机");
        }
    }


    /**
     * 获取时间Spannable
     * @param remainTime 剩余工作时间，单位秒
     * @param isRound 是否四舍五入
     * @return
     */
    private SpannableString getSpan(int remainTime,boolean isRound){
        String time = TimeUtils.secToHourMinUp(remainTime);
        if(remainTime >= 60*60*10){//超过10小时，只显示小时数据
            int minIndex = time.indexOf("min");
            int hourIndex = time.indexOf("h");
            if(minIndex > 0 && hourIndex != 0){
                try{
                    if(isRound){
                        time = (Integer.parseInt(time.substring(0,hourIndex)) + 1) +"h";
                    }else{
                        time = time.substring(0,hourIndex)+"h";//暂时不加1
                    }
                }catch (NumberFormatException e){}
            }else{
                try{
                    time = time.substring(0,hourIndex) +"h";
                }catch (NumberFormatException e){}
            }
        }
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 处理一体机工作展示
     * @param steamOven
     * @param baseViewHolder
     * @return
     */
    private void dealSteamWorkView(SteamOven steamOven,BaseViewHolder baseViewHolder){
        if(steamOven.faultId != 0){//故障
            String deviceTypeId = DeviceUtils.getDeviceTypeId(steamOven.guid);
            DeviceErrorInfo deviceErrorInfo = DeviceWarnInfoManager.getInstance().getDeviceErrorInfo(IDeviceType.RZKY, deviceTypeId, steamOven.faultId);
            if(deviceErrorInfo != null){
                baseViewHolder.setVisible(R.id.layout_offline, true);
                baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
                baseViewHolder.setGone(R.id.layout_work, true);
                baseViewHolder.setVisible(R.id.btn_detail, true);
                return;
            }
        }
        boolean isWork  = false;
        boolean workFinish = false;
        boolean isAppoint = false;
        if(steamOven.powerState != SteamStateConstant.POWER_STATE_OFF &&
                (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_WORKING ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_WORKING_PAUSE ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_APPOINTMENT)){
            isWork =  true;
            isAppoint = steamOven.workState == SteamStateConstant.WORK_STATE_APPOINTMENT;
        }
        if(!isWork){
            preSteamTimeValue = "";
            preSteamTimeMil = 0;
            MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(steamOven.guid);
            if(workState != null && workState.isFinish()){
                workFinish = true;
            }
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.btn_detail, true);
            String contentValue = "轻松烹饪\n智享厨房";
            if(workFinish){
                String  workName = SteamDataUtil.getModelName(steamOven.guid,steamOven.workMode,steamOven.recipeId);
                if(StringUtils.isNotBlank(workName)){
                    contentValue = workName;
                }
            }
            baseViewHolder.setText(R.id.tv_hint,contentValue);
        }else{
            String modelName = SteamDataUtil.getModelName(steamOven);
            if(StringUtils.isBlank(modelName) || SteamModeEnum.CHUGOU.getName().equals(modelName)){//未查询到工作模式或者除垢模式
                baseViewHolder.setVisible(R.id.layout_offline, true);
                baseViewHolder.setGone(R.id.layout_work, true);
                baseViewHolder.setGone(R.id.btn_detail, true);
                String contentValue = StringUtils.isBlank(modelName) ? "轻松烹饪\n智享厨房" : modelName;
                baseViewHolder.setText(R.id.tv_hint,contentValue);
            }else{
                baseViewHolder.setGone(R.id.layout_offline, true);
                baseViewHolder.setVisible(R.id.layout_work, true);
                baseViewHolder.setGone(R.id.ventilator_group7, true);
                baseViewHolder.setVisible(R.id.ventilator_group6, true);
                baseViewHolder.setText(R.id.tv_mode, modelName);
                if(isAppoint){
                    baseViewHolder.setText(R.id.tv_time, getStartTimePrompt(steamOven.orderLeftTime,steamOven.dc));
                    baseViewHolder.setText(R.id.btn_work, R.string.ventilator_start_cook);
                }else{
                    baseViewHolder.setText(R.id.tv_time, DateUtil.secForMatTime3(getTotalTime(steamOven)) + "min");
                    //baseViewHolder.setText(R.id.tv_time, DateUtil.secForMatTime3(steamOven.totalRemainSeconds) + "min");
                    if (steamOven.getWorkStatus() == 2 || steamOven.getWorkStatus() == 4) //预热中和工作中
                        baseViewHolder.setText(R.id.btn_work, R.string.ventilator_pause);
                    else if (steamOven.getWorkStatus() == 3 || steamOven.getWorkStatus() == 5) //暂停中
                        baseViewHolder.setText(R.id.btn_work, R.string.ventilator_continue);
                    else
                        baseViewHolder.setGone(R.id.btn_work, true);
                }
            }
        }

    }

    /**
     * 处理消毒柜工作展示
     * @param cabinet
     * @param baseViewHolder
     */
    private void dealCabinetWorkView(Cabinet cabinet, BaseViewHolder baseViewHolder){

        if(cabinet.faultId != 255  &&
                CabinetWaringEnum.match(cabinet.faultId).getCode() != CabinetWaringEnum.E255.getCode()){
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setVisible(R.id.btn_detail, true);
            return;
        }

        //if((cabinet.smartCruising == 1 || cabinet.pureCruising == 1) && cabinet.remainingModeWorkTime <= 0){//去往智能巡航页面
        if(cabinet.smartCruising == 1 || cabinet.pureCruising == 1){
            baseViewHolder.setVisible(R.id.tv_mode,true);
            baseViewHolder.setGone(R.id.layout_offline, true);
            baseViewHolder.setVisible(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.ventilator_group7, true);
            baseViewHolder.setVisible(R.id.ventilator_group6, true);
            baseViewHolder.setText(R.id.tv_mode, cabinet.smartCruising == 1?"智能":"净存");
            baseViewHolder.setText(R.id.tv_time, "巡航中");
            baseViewHolder.setText(R.id.btn_work, R.string.ventilator_cabinet_completion);
            return;
        }
        boolean isWork = false;
        boolean workFinish = false;
        boolean isAppoint = cabinet.remainingAppointTime > 0;
        if(cabinet.workMode == CabinetConstant.FUN_DISINFECT || cabinet.workMode == CabinetConstant.FUN_CLEAN
           || cabinet.workMode == CabinetConstant.FUN_DRY || cabinet.workMode == CabinetConstant.FUN_FLUSH
            || cabinet.workMode == CabinetConstant.FUN_SMART){
            isWork = true;
        }

        if(!isWork){
            preCabinetTimeValue = "";
            preCabinetTimeMil = 0;
            MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(cabinet.guid);
            if(workState != null && workState.isFinish()){
               workFinish = true;
            }
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.btn_detail, true);
            baseViewHolder.setText(R.id.tv_hint, workFinish ? "消毒完成":"消杀洁净\n智享厨房");
        }else{
            baseViewHolder.setVisible(R.id.tv_mode,true);
            baseViewHolder.setGone(R.id.layout_offline, true);
            baseViewHolder.setVisible(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.ventilator_group7, true);
            baseViewHolder.setVisible(R.id.ventilator_group6, true);
            baseViewHolder.setText(R.id.tv_mode, CabinetEnum.match(cabinet.workMode));
            if(isAppoint){
                baseViewHolder.setText(R.id.tv_time, getStartTimePrompt(cabinet.remainingAppointTime*60,cabinet.dc));
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_appoint_finish);
            }else{
                //baseViewHolder.setText(R.id.tv_time, DateUtil.secForMatTime3(cabinet.remainingModeWorkTime) + "min");
                baseViewHolder.setText(R.id.tv_time, getSpan(cabinet.remainingModeWorkTime,true));
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_cabinet_completion);
            }

        }
    }

    /**
     *
     * @param remainingAppointTime  剩余预约时间，单位 - 秒
     * @return
     */
    private String getStartTimePrompt(int remainingAppointTime,String deviceType){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.SECOND,remainingAppointTime);
        int totalHour = calendar.get(Calendar.HOUR_OF_DAY);
        int totalMin = calendar.get(Calendar.MINUTE);
        int totalDay = calendar.get(Calendar.DAY_OF_MONTH);
        String time = (totalHour <= 9 ? ("0" + totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0" + totalMin) : totalMin);



        // 问题：返回的剩余时间单位是分钟，若剩余时间为2分钟，本地时间是 12:02,计算结果为12：04，
        // 若10秒后本地时间为12：03,这时剩余时间仍然为2分钟，计算结果为12:05
        //兼容性处理 以下逻辑为了屏蔽时间来回切换的情况
        if(IDeviceType.RXDG.equals(deviceType)){//消毒柜
            if(StringUtils.isBlank(preCabinetTimeValue)){
                preCabinetTimeValue = time;
                preCabinetTimeMil = calendar.getTimeInMillis();
            }
            if(time.compareTo(preCabinetTimeValue) != 0){
                if(Math.abs(preCabinetTimeMil - calendar.getTimeInMillis()) <= max_error_dur){//只相差一分钟的情况下，生效
                    time = preCabinetTimeValue;
                }
            }
        }else if(IDeviceType.RXWJ.equals(deviceType)){//洗碗机
            if(StringUtils.isBlank(preDishwasherTimeValue)){
                preDishwasherTimeValue = time;
                preDishwasherTimeMil = calendar.getTimeInMillis();
            }
            if(time.compareTo(preDishwasherTimeValue) != 0){
                if(Math.abs(preDishwasherTimeMil - calendar.getTimeInMillis()) <= max_error_dur){//只相差一分钟的情况下，生效
                    time = preDishwasherTimeValue;
                }
            }
        }else if(IDeviceType.RZKY.equals(deviceType)){//一体机
            if(StringUtils.isBlank(preSteamTimeValue)){
                preSteamTimeValue = time;
                preSteamTimeMil = totalMin;
            }
            if(time.compareTo(preSteamTimeValue) != 0){
                if(Math.abs(preSteamTimeMil - calendar.getTimeInMillis()) <= max_error_dur){//只相差一分钟的情况下，生效
                    time = preSteamTimeValue;
                }
            }
        }

        if (day != totalDay) {
            return "次日" + time+ "启动";
        } else {
            return time+ "启动";
        }
    }

    /**
     * 上一次消毒柜显示时间(如 12:30)
     */
    private String preCabinetTimeValue = "";
    /**
     * 上次消毒柜显示分钟数（如：30）
     */
    private long preCabinetTimeMil;
    /**
     * 上一次洗碗机显示时间(如 12:30)
     */
    private String preDishwasherTimeValue = "";
    /**
     * 上次洗碗机分钟数（如：30）
     */
    private long preDishwasherTimeMil;
    /**
     * 上一次一体机显示时间(如 12:30)
     */
    private String preSteamTimeValue = "";
    /**
     * 上次一体机显示分钟数（如：30）
     */
    private long preSteamTimeMil;

    /**
     * 获取剩余运行时间
     * @param steamOven
     * @return
     */
    private int getTotalTime(SteamOven steamOven){
        if(steamOven == null){
            return 0;
        }
        int totalTime = 0;
        int startIndex = steamOven.curSectionNbr;
        if(startIndex <= 0){
            startIndex = 1;
        }
        for(int i = startIndex ; i <= 3;i++){
            if(i == 1){
                totalTime += steamOven.restTimeH * 256 + steamOven.restTime;//设置的工作时间 (秒)
            }else if(i == 2){
                totalTime += steamOven.restTimeH2 * 256 + steamOven.restTime2;//设置的工作时间 (秒)
            }else if(i == 3){
                totalTime += steamOven.restTimeH3 * 256 + steamOven.restTime3;//设置的工作时间 (秒)
            }
        }
        return totalTime;
    }

    private final int max_error_dur = 60*1000;//1分钟


}
