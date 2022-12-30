package com.robam.dishwasher.ui.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.ui.adapter.RvStringAdapter;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

//工作预约
public class AppointmentActivity extends DishWasherBaseActivity {
    /**
     * 小时选择
     */
    private RecyclerView mHourView;
    /**
     * 分钟选择
     */
    private RecyclerView mMinuteView;
    /**
     * 小时adapter
     */
    private RvStringAdapter mHourAdapter;
    /**
     * 分钟adapter
     */
    private RvStringAdapter mMinuteAdapter;
    /**
     * layoutManager
     */
    private PickerLayoutManager mHourManager;
    private PickerLayoutManager mMinuteManager;

    /**
     * 预约时间
     * @return
     */
    private String orderTime;
    private TextView tvTime;

    //当前模式
    private DishWasherModeBean modeBean = null;

    public int directive_offset = 20000;

    private boolean needToAppointing = false;//是否跳转到预约倒计时

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_layout_appointment;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        setLock(HomeDishWasher.getInstance().lock);
        //设置时间选择器
        mHourView = findViewById(R.id.rv_time_hour);
        mMinuteView = findViewById(R.id.rv_time_minute);
        tvTime = findViewById(R.id.tv_time);
        mHourAdapter = new RvStringAdapter();
        mMinuteAdapter = new RvStringAdapter();
        mHourManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setMaxItem(3)
                .setOnPickerListener((recyclerView, position) -> setOrderDate()).build();
        mMinuteManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setOnPickerListener((recyclerView, position) -> setOrderDate())
                .build();
        mHourView.setLayoutManager(mHourManager);
        mMinuteView.setLayoutManager(mMinuteManager);

        setOnClickListener(R.id.btn_cancel, R.id.btn_ok,R.id.ll_left);

       /* MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s.shortValue()){
                case MsgKeys.getDishWasherPower:
                    sendAppointingCommand();
                    break;
            }
        });*/

        MqttDirective.getInstance().getStrLiveData().observe(this,s->{
            if(s == null || s.trim().length() == 0){
                return ;
            }
            String[] split = s.split(MqttDirective.STR_LIVE_DATA_FLAG);
            if(split == null || split.length != 2){
                return;
            }
            if (split[0].equals(HomeDishWasher.getInstance().guid)) {
                int code = Integer.parseInt(split[1]);
                if(code == MsgKeys.getDishWasherPower){
                    sendAppointingCommand();
                }
            }
        });

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                    DishWasher dishWasher = (DishWasher) device;
                    setLock(dishWasher.StoveLock == DishWasherState.LOCK);
                    setState(dishWasher.LackSaltStatus == 1,dishWasher.LackRinseStatus == 1);
                    switch (dishWasher.powerStatus){
                        case DishWasherState.WAIT:
                        case DishWasherState.WORKING:
                        case DishWasherState.PAUSE:
                            if(dishWasher.AppointmentSwitchStatus == DishWasherState.APPOINTMENT_ON){
                                Intent intent = new Intent();
                                DishWasherModeBean newMode = modeBean.getNewMode();
                                DishWasherModelUtil.initWorkingInfo(newMode,dishWasher);
                                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                                intent.setClass(AppointmentActivity.this, AppointingActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                if(dishWasher.powerStatus == DishWasherState.WAIT){//待机状态下，无工作模式
                                    return;
                                }
                                if(dishWasher.workMode == 0){
                                    return;
                                }
                                Intent intent = new Intent();
                                DishWasherModeBean newMode = modeBean.getNewMode();
                                DishWasherModelUtil.initWorkingInfo(newMode,dishWasher);
                                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                                intent.setClass(this, WorkActivity.class);
                                startActivity(intent);
                            }
                    }
                    break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        // 生产小时
        ArrayList<String> hourData = new ArrayList<>(24);
        for (int i = 0; i <= 23; i++) {
            hourData.add((i < 10 ? "0" : "") + i + "");
        }

        // 生产分钟
        ArrayList<String> minuteData = new ArrayList<>(6);
        minuteData.add("00");
        minuteData.add("10");
        minuteData.add("20");
        minuteData.add("30");
        minuteData.add("40");
        minuteData.add("50");
        mHourAdapter.setList(hourData);
        mMinuteAdapter.setList(minuteData);
        mHourView.setAdapter(mHourAdapter);
        mMinuteView.setAdapter(mMinuteAdapter);
        modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);
        initHourAndMinScroll(hourData,minuteData);

        //默认
        //setOrderDate();



    }

    /**
     * 初始化小时与分钟 RecyclerView 组件显示位置
     * @param hourData
     * @param minuteData
     */
    private void initHourAndMinScroll(ArrayList<String> hourData, ArrayList<String> minuteData){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int hourScrollP;
        int minScrollP;
        if(min >= 50){
            hourScrollP = hour >= 23 ? 0 : hour + 1;
            minScrollP = 0;
        }else{
            hourScrollP = hour;
            minScrollP = (min / 10)+1;
        }

        mHourView.scrollToPosition(hourScrollP);
        mMinuteView.scrollToPosition(minScrollP);
        String curOrderTime = hourData.get(hourScrollP)+":"+minuteData.get(minScrollP);
        if((hour >= 23 && min >= 50) || (hour == hourScrollP && min == minScrollP)){
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint2), curOrderTime ));
        }{
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint3), curOrderTime ));
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel)
            finish();
        else if (id == R.id.btn_ok) { //确认预约
            //startAppointing();
            startSetResult();
        }else if(id == R.id.ll_left){
            finish();
        }
    }


    private void startSetResult(){
        DishWasher curDevice = getCurDevice();
        if(curDevice == null){
            return;
        }
        if(curDevice.AppointmentSwitchStatus == DishWasherState.APPOINTMENT_ON){
            //修改预约时间
        }else{
            //设置预约时间
            Intent result = new Intent();
            result.putExtra(DishWasherConstant.APPOINTMENT_RESULT,tvTime.getText().toString());
            setResult(RESULT_OK,result);
            finish();
        }

    }

    /**
     * 开始预约
     */
    private void startAppointing(){
        DishWasher curDevice = getCurDevice();
        if(!DishWasherCommandHelper.checkDishWasherState(this,curDevice)){
            return;
        }
        sendSetPowerStateCommand();
//        if((curDevice.powerStatus == DishWasherState.OFF) || HomeDishWasher.getInstance().isTurnOff){
//            sendSetPowerStateCommand();
//        }else {
//            sendAppointingCommand();
//        }
    }

    /**
     * 发送点亮屏幕命令
     */
    private void sendSetPowerStateCommand(){
        Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherPower);
        map.put(DishWasherConstant.PowerMode,1);
        DishWasherCommandHelper.getInstance().sendCommonMsg(map,MsgKeys.setDishWasherPower + directive_offset);
    }

    /**
     * 发送预约命令
     */
    private void sendAppointingCommand(){
        try {
            long appointingTime = getAppointingTimeMin(tvTime.getText().toString());

            Map map = DishWasherCommandHelper.getModelMap(MsgKeys.setDishWasherWorkMode, modeBean.code,(short) 1, (int)appointingTime);
            map.put(DishWasherConstant.AutoVentilation, 0);
            map.put(DishWasherConstant.EnhancedDrySwitch, 0);
            map.put(DishWasherConstant.ArgumentNumber, 1);
            map.put(DishWasherConstant.ADD_AUX, modeBean.auxCode);
            DishWasherCommandHelper.getInstance().sendCommonMsg(map,MsgKeys.setDishWasherWorkMode + directive_offset);

            HomeDishWasher.getInstance().workHours = modeBean.time;
            HomeDishWasher.getInstance().orderWorkTime = (int) appointingTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取预约执行时间
     * @param timeText
     * @return 预约执行时间（单位：分钟）
     * @throws ParseException
     */
    private long getAppointingTimeMin(String timeText) throws ParseException {
        String time = timeText.substring("次日".length()).trim()+":00";
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //HH:24小时制  hh:12小时制
        String curTimeStr = dateFormat.format(curTime);
        String curTimeText = curTimeStr.substring("yyyy-MM-dd".length()).trim();
        if(time.compareTo(curTimeText) > 0){//今日
            String orderTimeStr = curTimeStr.split(" ")[0].trim() + " " + time;
            Date orderTime = dateFormat.parse(orderTimeStr);
            long timeDur = (orderTime.getTime() - curTime.getTime())/60/1000;
            if((orderTime.getTime() - curTime.getTime())% 60 != 0){
                return timeDur + 1;
            }
            return timeDur;
        }else{//次日
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(curTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            String destTime = dateFormat.format(calendar.getTime());
            String orderTimeStr = destTime.split(" ")[0].trim() + " " + time;
            Date orderTime = dateFormat.parse(orderTimeStr);
            long timeDur = (orderTime.getTime() - curTime.getTime())/60/1000;
            if((orderTime.getTime() - curTime.getTime())% 60 != 0){
                return timeDur + 1;
            }
            return timeDur;
        }
    }


    /**
     * 设置下方提示的开始时间
     */
    private void setOrderDate() {
        String hour = mHourAdapter.getItem(mHourManager.getPickedPosition());
        String minute = mMinuteAdapter.getItem(mMinuteManager.getPickedPosition());
        orderTime = hour + ":" + minute;
        if (DateUtil.compareTime(DateUtil.getCurrentTime(DateUtil.PATTERN), orderTime, DateUtil.PATTERN) >= 0) {
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint2), orderTime ));
        } else {
            tvTime.setText(String.format(getString(R.string.dishwasher_work_order_hint3), orderTime ));
        }
    }


}