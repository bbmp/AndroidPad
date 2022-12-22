package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.view.CancelRadioButton;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherAuxBean;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherAuxEnum;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherEnum;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.dishwasher.util.DishWasherModelUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ModeSelectActivity extends DishWasherBaseActivity {
    private RadioGroup radioGroup;
    //模式
    private TextView tvMode;
    private TextView tvTime, tvTemp, tvTempUnit;
    private RadioButton rButton1, rButton2, rButton3, rButton4;
    private TextView tvStartHint;
    private TextView tvAuxPrompt;
    //当前模式
    private DishWasherModeBean modeBean = null;

    public int directive_offset = 10000;
    public static final int START_P = 22;
    private TextView btStart;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        setRight(R.string.dishwasher_appointment);
        showCenter();
        showRightCenter();
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);
        radioGroup = findViewById(R.id.rg_aux);
        tvMode = findViewById(R.id.tv_mode);
        tvTime = findViewById(R.id.tv_time);
        tvTemp = findViewById(R.id.tv_temp);
        tvTempUnit = findViewById(R.id.tv_temp_unit);
        rButton1 = findViewById(R.id.rb_button1);
        rButton2 = findViewById(R.id.rb_button2);
        rButton3 = findViewById(R.id.rb_button3);
        rButton4 = findViewById(R.id.rb_button4);
        tvStartHint = findViewById(R.id.tv_start);
        tvAuxPrompt = findViewById(R.id.aux_prompt_tv);
        btStart = findViewById(R.id.btn_start);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            setTvAuxPrompt(group,checkedId);
            if (checkedId == R.id.rb_button1) {
                HomeDishWasher.getInstance().auxMode = DishWasherConstant.AUX_PAN_POWFULL;
            } else if (checkedId == R.id.rb_button2) {
                HomeDishWasher.getInstance().auxMode = DishWasherConstant.AUX_KILL_POWFULL;
            } else if (checkedId == R.id.rb_button3) {
                HomeDishWasher.getInstance().auxMode = DishWasherConstant.AUX_FLUSH;
            } else if (checkedId == R.id.rb_button4) {
                HomeDishWasher.getInstance().auxMode = DishWasherConstant.AUX_DOWN_WASH;
            } else
                HomeDishWasher.getInstance().auxMode = -1;
        });

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                    DishWasher dishWasher = (DishWasher) device;
                    setLock(dishWasher.StoveLock == 1);
                    boolean toWaringPage = toWaringPage(dishWasher.abnormalAlarmStatus);
                    if(!toWaringPage && dishWasher.workMode != 0){
                        switch (dishWasher.powerStatus){
                            case DishWasherState.WAIT://启动设置成功后，设备powerStatus在一段时间内，仍然在待机状态
                            case DishWasherState.WORKING:
                            case DishWasherState.PAUSE:
                                dealWasherWorkingState(dishWasher);
                        }
                    }
                }
            }
        });

        MqttDirective.getInstance().getStrLiveData().observe(this,s->{
            LogUtils.i("MqttDishWasher onDecodeMsg  guid : arrvice "+s+"");
            if(s == null || s.trim().length() == 0){
                return ;
            }
            LogUtils.i("MqttDishWasher onDecodeMsg  guid : arrvice "+s+"");
            String[] split = s.split(MqttDirective.STR_LIVE_DATA_FLAG);
            if(split == null || split.length != 2){
                return;
            }
            if (split[0].equals(HomeDishWasher.getInstance().guid)) {
                int code = Integer.parseInt(split[1]);
                if(code == MsgKeys.getDishWasherPower){
                    sendStartWorkOrAppointCommand();
                }
            }
        });
    }

    private void dealWasherWorkingState(DishWasher dishWasher){
        if(dishWasher.workMode == 0){
            return;
        }
        switch (dishWasher.AppointmentSwitchStatus){
            case DishWasherState.APPOINTMENT_OFF:
                if(dishWasher.powerStatus == DishWasherState.WAIT){//待机状态下，无工作模式
                    return;
                }
                if(dishWasher.remainingWorkingTime == 0){//无剩余工作时间
                    return;
                }
                Intent intent = new Intent();
                DishWasherModeBean newMode = modeBean.getNewMode();
                DishWasherModelUtil.initWorkingInfo(modeBean,dishWasher);
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                intent.setClass(this, WorkActivity.class);
                startActivity(intent);
                break;
            case DishWasherState.APPOINTMENT_ON:
                if(dishWasher.AppointmentRemainingTime == 0){
                    return;
                }
                Intent appointingIntent = new Intent();
                DishWasherModeBean needDish = modeBean.getNewMode();
                DishWasherModelUtil.initWorkingInfo(needDish,dishWasher);
                appointingIntent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, needDish);
                appointingIntent.setClass(this, AppointingActivity.class);
                startActivity(appointingIntent);
                HomeDishWasher.getInstance().workHours = needDish.time;
                HomeDishWasher.getInstance().orderWorkTime = dishWasher.AppointmentRemainingTime;
                break;
        }
    }


    private void setTvAuxPrompt(RadioGroup group, int checkedId){
        if(checkedId != -1){
            int resId = DishWasherAuxEnum.matchPromptRes(((RadioButton)group.findViewById(checkedId)).getText().toString());
            if(resId > 0){
                tvAuxPrompt.setText(resId);
            }else{
                tvAuxPrompt.setText("");
            }
            //设置显示时间
            DishWasherAuxBean auxBean = getAuxBean(DishWasherAuxEnum.matchValue(((RadioButton) group.findViewById(checkedId)).getText().toString()));
            tvTime.setText(getSpan(auxBean.time));

            tvTime.setTextColor(getResources().getColor(R.color.dishwasher_lock));
            tvMode.setTextColor(getResources().getColor(R.color.dishwasher_white70));
            tvTemp.setText(auxBean.temp+"");
            tvTemp.setTextColor(getResources().getColor(R.color.dishwasher_white70));
            tvTempUnit.setTextColor(getResources().getColor(R.color.dishwasher_white70));

            for(int i = 0; i< group.getChildCount();i++){
                CancelRadioButton child = (CancelRadioButton) group.getChildAt(i);
                child.setTextColor(child.getId() == checkedId ? getResources().getColor(R.color.dishwasher_white) : getResources().getColor(R.color.dishwasher_white70));
            }
        }else{
            tvAuxPrompt.setText("");
            tvTemp.setText(modeBean .temp+"");
            tvTime.setText(getSpan(modeBean.time));
            tvTime.setTextColor(getResources().getColor(R.color.dishwasher_white));
            tvMode.setTextColor(getResources().getColor(R.color.dishwasher_white));
            tvTemp.setTextColor(getResources().getColor(R.color.dishwasher_white));
            tvTempUnit.setTextColor(getResources().getColor(R.color.dishwasher_white));

            for(int i = 0; i< group.getChildCount();i++){
                CancelRadioButton child = (CancelRadioButton) group.getChildAt(i);
                child.setTextColor(getResources().getColor(R.color.dishwasher_white));
            }
        }
    }

    /**
     * 获取附加功能对应实体
     */
    private DishWasherAuxBean getAuxBean(int auxCode){
        if(modeBean == null || modeBean.auxList == null){
            return null;
        }
        for( DishWasherAuxBean auxBean : modeBean.auxList){
            if(auxBean.code == auxCode){
                return auxBean;
            }
        }
        return null;
    }




    @Override
    protected void initData() {

        if (null != getIntent())
            modeBean = (DishWasherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);
        if (null != modeBean) {
            setData(modeBean);
            switch (modeBean.code) {
                case DishWasherConstant.MODE_FLUSH:
                case DishWasherConstant.MODE_SELFCLEAN: {
                    //是否立即启动
                    radioGroup.setVisibility(View.GONE);
                    tvStartHint.setVisibility(View.VISIBLE);
                }
                break;
                case DishWasherConstant.MODE_SMART:
                case DishWasherConstant.MODE_QUICK: {
                    rButton1.setVisibility(View.INVISIBLE);
                    rButton4.setVisibility(View.INVISIBLE);
                }
                break;
                case DishWasherConstant.MODE_POWFULL:
                case DishWasherConstant.MODE_SAVING:
                case DishWasherConstant.MODE_DAILY:
                case DishWasherConstant.MODE_BABYCARE: {

                }
                break;
                case DishWasherConstant.MODE_BRIGHT: {
                    rButton1.setVisibility(View.INVISIBLE);
                    rButton4.setVisibility(View.INVISIBLE);
                    rButton2.setText(R.string.dishwasher_flush);
                    rButton3.setText(R.string.dishwasher_down_wash);
                }
                break;
            }
            if(modeBean.code == DishWasherEnum.AUTO_AERATION.FLUSH.getCode()){
                hideRight();
            }
        }
    }

    //模式参数设置
    private void setData(DishWasherModeBean modeBean) {
        if(modeBean.code == DishWasherEnum.AUTO_AERATION.FLUSH.getCode()){
            tvMode.setText(DishWasherEnum.AUTO_AERATION.FLUSH.getValue());//护婴净存，不换行显示
        }else{
            tvMode.setText(modeBean.name);
        }
        tvTime.setText(getSpan(modeBean.time));
        int temp = modeBean.temp;
        if (temp > 0) {
            tvTemp.setText(temp + "");
            tvTempUnit.setVisibility(View.VISIBLE);
        } else {
            tvTemp.setText("");
            tvTempUnit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //预约
            Intent intent = new Intent();
            DishWasherModeBean newMode = modeBean.getNewMode();
            DishWasherAuxBean auxBean = getAuxBean(getAuxCode());
            if(auxBean != null){
                newMode.time = auxBean.time;
                newMode.auxCode = auxBean.code;
            }
            intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
            intent.setClass(this, AppointmentActivity.class);
            //startActivity(intent);
            startActivityForResult(intent,DishWasherConstant.APPOINTMENT_CODE);
        } else if (id == R.id.btn_start) {
            if(ClickUtils.isFastClick()){
                //ToastUtils.showLong(this,"被重复点击了");
                return;//防止快速重复点击
            }
            startWork();
        } else if (id == R.id.ll_left) {
            //返回
            finish();
        }
    }




    private void startWork(){
        DishWasher curDevice = getCurDevice();
        if(!DishWasherCommandHelper.checkDishWasherState(this,curDevice)){
            getLastState();
            return;
        }
        DishWasherCommandHelper.sendPowerState(DishWasherState.WAIT);
//        if((curDevice.powerStatus == DishWasherState.OFF)){
//            sendSetPowerStateCommand();
//        }else {
//            sendStartWorkCommand();
//        }
    }


    private void sendStartWorkOrAppointCommand(){
        boolean isAppointing = false;
        String s = btStart.getText().toString();
        if(getResources().getString(R.string.dishwasher_start_appoint).equals(s)){
            isAppointing = true;
        }
        if(isAppointing){
            sendAppointingCommand();
        }else{
            DishWasherCommandHelper.sendStartWork(modeBean.code,(short) getAuxCode(),directive_offset + MsgKeys.setDishWasherWorkMode);
        }

    }


    private void sendStartWorkCommand(){
        DishWasherCommandHelper.sendStartWork(modeBean.code,(short) getAuxCode(),directive_offset + MsgKeys.setDishWasherWorkMode);
    }

    /**
     * 发送预约命令
     */
    private void sendAppointingCommand(){
        try {
            TextView viewById = findViewById(R.id.tv_right);
            long appointingTime = getAppointingTimeMin(viewById.getText().toString());
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
     *
     * @return 获取附加模式code
     */
    private int getAuxCode(){
        if(radioGroup.getVisibility() == View.VISIBLE){
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = radioGroup.findViewById(checkedRadioButtonId);
            if(radioButton == null){
                return DishWasherAuxEnum.AUX_NONE.getCode();
            }else{
                return DishWasherAuxEnum.matchValue(radioButton.getText().toString());
            }
        }
        return DishWasherAuxEnum.AUX_NONE.getCode();
    }

    /**
     * 获取时间展示SpannableString
     * @param seconds 工作时间（单位：秒）
     * @return
     */
    private SpannableString getSpan(int seconds){
        String time = TimeUtils.secToHourMinH(seconds);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DishWasherConstant.APPOINTMENT_CODE && resultCode == RESULT_OK){
            String timeValue = data.getStringExtra(DishWasherConstant.APPOINTMENT_RESULT);
            setRight(timeValue);
            btStart.setText(R.string.dishwasher_start_appoint);
        }
    }
}