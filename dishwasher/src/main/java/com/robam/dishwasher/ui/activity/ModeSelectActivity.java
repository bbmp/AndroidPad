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
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWaherModeBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;

import org.eclipse.paho.client.mqttv3.util.Strings;

import java.util.HashMap;
import java.util.Map;

public class ModeSelectActivity extends DishWasherBaseActivity {
    private RadioGroup radioGroup;
    //模式
    private TextView tvMode;
    private TextView tvTime, tvTemp, tvTempUnit;
    private RadioButton rButton1, rButton2, rButton3, rButton4;
    private TextView tvStartHint;
    //当前模式
    private DishWaherModeBean modeBean = null;


    private short lowerWash = 1;//下层洗
    private short autoVentilation; //自动换气
    private short autoVentilationTime; //自动换气时间
    private short appointment; //预约
    private short appointmentTime; //预约时间

    //todo(一些模式暂无 - 需寻求正在ROKI　APP相关人员支持)
    private short enhancedDry;//加强干燥 - 暂无
    private short panSWash; //锅具强洗  - 暂无
    private short intensifyDegerming; //加强除菌   -暂无
    private short cxjc;  //长效净存   - 暂无



    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
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
        setRight(R.string.dishwasher_appointment);
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });
    }

    @Override
    protected void initData() {

        if (null != getIntent())
            modeBean = (DishWaherModeBean) getIntent().getSerializableExtra(DishWasherConstant.EXTRA_MODEBEAN);
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
        }
    }

    //模式参数设置
    private void setData(DishWaherModeBean modeBean) {
        tvMode.setText(modeBean.name);
        String time = TimeUtils.secToHourMinH(modeBean.time);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTime.setText(spannableString);
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
            if (null != modeBean)
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, modeBean);
            intent.setClass(this, AppointmentActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_start) {
            //开始工作
//            Intent intent = new Intent();
//            if (null != modeBean)
//                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, modeBean);
//            intent.setClass(this, WorkActivity.class);
//            startActivity(intent);
//            finish();
            startWork();
        } else if (id == R.id.ll_left) {
            //返回
            finish();
        }
    }

    private void startWork(){
        Map map = new HashMap();
        map.put(DishWasherConstant.UserId,getSrcUser());
        map.put(DishWasherConstant.DishWasherWorkMode, HomeDishWasher.getInstance().workMode);

        map.put(DishWasherConstant.LowerLayerWasher, lowerWash);

        map.put(DishWasherConstant.AutoVentilation, 0);
        map.put(DishWasherConstant.EnhancedDrySwitch, 0);
        map.put(DishWasherConstant.AppointmentSwitch, 0);
        map.put(DishWasherConstant.AppointmentTime, 0);
        /*msg.putOpt(MsgParams.UserId, getSrcUser());
        msg.putOpt(MsgParams.DishWasherWorkMode, workMode);
        msg.putOpt(MsgParams.LowerLayerWasher, bottomWasherSwitch);
        msg.putOpt(MsgParams.AutoVentilation, autoVentilation);
        msg.putOpt(MsgParams.EnhancedDrySwitch, enhancedDrySwitch);
        msg.putOpt(MsgParams.AppointmentSwitch, appointmentSwitch);
        msg.putOpt(MsgParams.AppointmentTime, appointmentTime);*/
        //HomeDishWasher.getInstance().auxMode   当前选中的附加程序(默认是0 未选择任何附加程序)

        DishWasherAbstractControl.getInstance().sendCommonMsg(map,HomeDishWasher.getInstance().guid, MsgKeys.setDishWasherWorkMode);
    }

    final public String getSrcUser() {
        long id =AccountInfo.getInstance().getUser().getValue().id;
        String userId = String.valueOf(id);
        if(userId.length() < 10){
            for(int i = 10 - userId.length();i<10;i++){
                userId += "0";
            }
        }
        return userId;
    }

}