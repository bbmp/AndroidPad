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
import com.robam.common.IDeviceType;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DishWasherModeEnum;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.constant.DishWasherWaringEnum;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.device.subdevice.Stove;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.utils.SteamDataUtil;
import com.robam.ventilator.R;

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
            baseViewHolder.setText(R.id.tv_model, device.getDisplayType());
            ImageView ivDevice = baseViewHolder.getView(R.id.iv_device);
            if (IDeviceType.RXDG.equals(device.dc)) {
                ivDevice.setImageResource(R.drawable.ventilator_cabinet);
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
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
                    if (stove.leftLevel == 0 && stove.rightLevel == 0) {  //未工作
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
                    baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_check_connect_status);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if(device.faultId != 0 && DishWasherWaringEnum.match(device.faultId).getCode() != DishWasherWaringEnum.E10.getCode()){//故障
                        baseViewHolder.setText(R.id.tv_hint, R.string.ventilator_product_failure);
                        baseViewHolder.setVisible(R.id.btn_detail,true);
                        baseViewHolder.setGone(R.id.layout_work,true);
                    }else{
                        DishWasher dishWasher = (DishWasher) device;
                        dealDishWasherWorkFinish(dishWasher,baseViewHolder);
                    }
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
                    if (pan.sysytemStatus == 0) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else if (pan.sysytemStatus == 3) { //低电量
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                        baseViewHolder.setText(R.id.tv_mode, R.string.ventilator_low_battery);
                        baseViewHolder.setText(R.id.btn_work, R.string.ventilator_detail);
                    } else {
                        //故障
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
        MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(dishWasher.guid);
        boolean isWork = false;
        if((dishWasher.powerStatus == DishWasherState.WORKING  ||
                dishWasher.powerStatus == DishWasherState.PAUSE) &&
                dishWasher.remainingWorkingTime > 0){
            isWork = true;
        }
        if(isWork){
            baseViewHolder.setGone(R.id.layout_offline, true);
            baseViewHolder.setVisible(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.ventilator_group7, true);
            baseViewHolder.setVisible(R.id.ventilator_group6, true);
            baseViewHolder.setText(R.id.tv_mode, DishWasherEnum.match(dishWasher.workMode));
            baseViewHolder.setText(R.id.tv_time, getSpan(dishWasher.remainingWorkingTime * 60));
            if (dishWasher.getWorkStatus() == 2) //工作中
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_pause);
            else if (dishWasher.getWorkStatus() == 3) //暂停中
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_continue);
            else
                baseViewHolder.setGone(R.id.btn_work, true);
        }else{
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
     * @return
     */
    private SpannableString getSpan(int remainTime){
        String time = TimeUtils.secToHourMinUp(remainTime);
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
        boolean isWork  = false;
        boolean workFinish = false;
        if(steamOven.powerState != SteamStateConstant.POWER_STATE_OFF &&
                (steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_PREHEAT_PAUSE ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_WORKING ||
                        steamOven.workState == SteamStateConstant.WORK_STATE_WORKING_PAUSE)){
            isWork =  true;
        }
        if(!isWork){
            MqttDirective.WorkState workState = MqttDirective.getInstance().getWorkState(steamOven.guid);
            if(workState != null && workState.isEnd()){
                workFinish = true;
            }
            baseViewHolder.setVisible(R.id.layout_offline, true);
            baseViewHolder.setGone(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.btn_detail, true);
            baseViewHolder.setText(R.id.tv_hint, workFinish ? SteamDataUtil.getModelName(steamOven.guid,steamOven.workMode,steamOven.recipeId) : "轻松烹饪\n智享厨房");
        }else{
            baseViewHolder.setGone(R.id.layout_offline, true);
            baseViewHolder.setVisible(R.id.layout_work, true);
            baseViewHolder.setGone(R.id.ventilator_group7, true);
            baseViewHolder.setVisible(R.id.ventilator_group6, true);

            baseViewHolder.setText(R.id.tv_mode, SteamDataUtil.getModelName(steamOven));
            baseViewHolder.setText(R.id.tv_time, DateUtil.secForMatTime3(steamOven.totalRemainSeconds) + "min");

            if (steamOven.getWorkStatus() == 2 || steamOven.getWorkStatus() == 4) //预热中和工作中
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_pause);
            else if (steamOven.getWorkStatus() == 3 || steamOven.getWorkStatus() == 5) //暂停中
                baseViewHolder.setText(R.id.btn_work, R.string.ventilator_continue);
            else
                baseViewHolder.setGone(R.id.btn_work, true);
        }

    }

    /**
     * 处理消毒柜工作展示
     * @param cabinet
     * @param baseViewHolder
     */
    private void dealCabinetWorkView(Cabinet cabinet, BaseViewHolder baseViewHolder){
        boolean isWork = false;
        boolean workFinish = false;
        if((cabinet.workMode == CabinetConstant.FUN_DISINFECT || cabinet.workMode == CabinetConstant.FUN_CLEAN
           || cabinet.workMode == CabinetConstant.FUN_DRY || cabinet.workMode == CabinetConstant.FUN_FLUSH
            || cabinet.workMode == CabinetConstant.FUN_SMART) && cabinet.remainingModeWorkTime != 0 ){
            isWork = true;
        }
        baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
        baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
        if(!isWork){
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
            baseViewHolder.setText(R.id.tv_time, DateUtil.secForMatTime3(cabinet.remainingModeWorkTime) + "min");
            baseViewHolder.setText(R.id.btn_work, R.string.ventilator_cabinet_completion);
        }
    }
}
