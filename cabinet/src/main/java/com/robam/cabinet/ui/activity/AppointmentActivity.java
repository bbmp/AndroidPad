package com.robam.cabinet.ui.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.EventConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.ui.adapter.RvStringAdapter;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public static  final int POWER_ON_OFFSET=  400;

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

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
//                    if(toWaringPage(cabinet.alarmStatus)){
//                        return;
//                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                        case CabinetConstant.FUN_WARING:
                            if(cabinet.remainingAppointTime != 0){
                                toAppointPage(cabinet);
                            }
                            break;
                    }

                }
            }
        });

        MqttDirective.getInstance().getDirective().observe(this, s->{
            if(s != EventConstant.WARING_CODE_NONE){
                showWaring(s);
            }
            switch (s - directive_offset){
                case POWER_ON_OFFSET:
                    try {
                        CabinetCommonHelper.startAppointCommand(cabModeBean.code,
                                cabModeBean.defTime,
                                (int)getAppointingTimeMin(tvTime.getText().toString()),
                                directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
                        //startWork();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
    }

    private void toAppointPage(Cabinet cabinet){
        Intent intent = new Intent(this,AppointingActivity.class);
        WorkModeBean workModeBean = new WorkModeBean(this.cabModeBean);
        workModeBean.orderSurplusTime = cabinet.remainingAppointTime;
        intent.putExtra(Constant.EXTRA_MODE_BEAN, workModeBean);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initData() {
        // 生产小时
        ArrayList<String> hourData = new ArrayList<>(24);
        for (int i = 0; i <= 23; i++) {
            hourData.add((i < 10 ? "0" : "") + i + "");
        }

        cabModeBean = (CabModeBean) getIntent().getSerializableExtra(Constant.EXTRA_MODE_BEAN);

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
        //默认
        initHourAndMinScroll(hourData,minuteData);
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
        if(hour >= 23 && min >= 50){
            tvTime.setText(String.format(getString(R.string.cabinet_work_order_hint2), curOrderTime ));
        }{
            tvTime.setText(String.format(getString(R.string.cabinet_work_order_hint3), curOrderTime ));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            finish();
        } else if (id == R.id.btn_ok) { //确认预约
            CabinetCommonHelper.startPowerOn(directive_offset+POWER_ON_OFFSET);

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



    @Deprecated
    private void startWork() throws ParseException {
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.CABINET_STATUS, cabModeBean.code);
        map.put(CabinetConstant.CABINET_TIME, cabModeBean.defTime);
        map.put(CabinetConstant.ArgumentNumber,1);//附加参数 - 预约放在附加参数中
        //预约时间
        map.put(CabinetConstant.Key,2);//附加参数 - 预约放在附加参数中
        map.put(CabinetConstant.Length,2);//附加参数 - 预约放在附加参数中
        map.put(CabinetConstant.CABINET_APPOINT_TIME, getAppointingTimeMin(tvTime.getText().toString()));//附加参数 - 预约放在附加参数中
        CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
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
}