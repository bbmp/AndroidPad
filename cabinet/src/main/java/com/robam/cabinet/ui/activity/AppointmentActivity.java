package com.robam.cabinet.ui.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.ui.adapter.RvStringAdapter;
import com.robam.cabinet.util.CabinetAppointmentUtil;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import java.util.ArrayList;
import java.util.Map;

//工作预约
public class AppointmentActivity extends CabinetBaseActivity {
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
     *
     * @return
     */
    private String orderTime;
    private TextView tvTime;

    private CabModeBean cabModeBean = null;

    public int directive_offset = 1300000;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_appointment;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
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

        setOnClickListener(R.id.btn_cancel, R.id.btn_ok);

        MqttDirective.getInstance().getDirective().observe(this, s -> {
            if(s == (MsgKeys.SetSteriPowerOnOff_Req + directive_offset)){
                //HomeCabinet.getInstance().orderTime = cabModeBean.steriReminderTime+"";
                //TODO(确定能否获取到当前是那个模式处于预约状态 - 找设备开发人员确定，若无法获取，则通知UI调整设计)
                HomeCabinet.getInstance().orderTime = CabinetAppointmentUtil.getAppointmentTime(tvTime.getText().toString())+"";
                HomeCabinet.getInstance().workHours = cabModeBean.defTime;
                Intent intent = new Intent(this,AppointingActivity.class);
                intent.putExtra(CabinetConstant.EXTRA_MODE_BEAN, cabModeBean);
                startActivity(intent);
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
        ArrayList<String> minuteData = new ArrayList<>(60);
        for (int i = 0; i <= 59; i++) {
            minuteData.add((i < 10 ? "0" : "") + i + "");
        }
        mHourAdapter.setList(hourData);
        mMinuteAdapter.setList(minuteData);
        mHourView.setAdapter(mHourAdapter);
        mMinuteView.setAdapter(mMinuteAdapter);
        //默认
        setOrderDate();

        cabModeBean = (CabModeBean) getIntent().getSerializableExtra(CabinetConstant.EXTRA_MODE_BEAN);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            finish();
        } else if (id == R.id.btn_ok) { //确认预约
            HomeCabinet.getInstance().orderTime = CabinetAppointmentUtil.getAppointmentTime(tvTime.getText().toString())+"";
            HomeCabinet.getInstance().workHours = cabModeBean.defTime;
            startWork();
//            startActivity(AppointingActivity.class);
//            finish();
        } else if (id == R.id.ll_left) {
            finish();
        }
    }

    /**
     * 设置下方提示的开始时间
     */
    private void setOrderDate() {
        String hour = mHourAdapter.getItem(mHourManager.getPickedPosition());
        String minute = mMinuteAdapter.getItem(mMinuteManager.getPickedPosition());
        orderTime = hour + ":" + minute;
        if (DateUtil.compareTime(DateUtil.getCurrentTime(DateUtil.PATTERN), orderTime, DateUtil.PATTERN) == 1) {
            tvTime.setText(String.format(getString(R.string.cabinet_work_order_hint2), orderTime));
        } else {
            tvTime.setText(String.format(getString(R.string.cabinet_work_order_hint3), orderTime));
        }
    }



    //TODO(不同方式有不同的参数)
    private void startWork() {
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.SteriStatus, cabModeBean.code);
        map.put(CabinetConstant.SteriTime, cabModeBean.defTime);
        map.put(CabinetConstant.ArgumentNumber,1);//附加参数 - 预约放在附加参数中

        //预约时间
        map.put(CabinetConstant.Key,2);//附加参数 - 预约放在附加参数中
        map.put(CabinetConstant.Length,2);//附加参数 - 预约放在附加参数中
        //TODO("CabinetAppointmentUtil.getAppointmentTime 需要修改")
        map.put(CabinetConstant.SteriReserveTime, CabinetAppointmentUtil.getAppointmentTime(orderTime));//附加参数 - 预约放在附加参数中
        CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
//        Msg msg = newReqMsg(MsgKeys.SetSteriPowerOnOff_Req);
//        msg.putOpt(MsgParams.TerminalType, terminalType);
//        msg.putOpt(MsgParams.UserId, getSrcUser());
//        msg.putOpt(MsgParams.SteriStatus, mode);
//        msg.putOpt(MsgParams.SteriTime, min);
//        msg.putOpt(MsgParams.ArgumentNumber, argumentNumber);
//        if (argumentNumber > 0) {
//            if (setSteriTem != 0) {
//                msg.putOpt(MsgParams.Key, (short) 1);
//                msg.putOpt(MsgParams.Length, (short) 1);
//                msg.putOpt(MsgParams.warmDishTempValue, setSteriTem);
//            }
//
//            if (orderTime != 0) {
//                msg.putOpt(MsgParams.Key, (short) 2);
//                msg.putOpt(MsgParams.Length, (short) 2);
//                msg.putOpt(MsgParams.SteriReserveTime, orderTime);
//            }
//        }

    }
}