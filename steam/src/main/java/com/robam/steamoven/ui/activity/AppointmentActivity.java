package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.steamoven.R;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.adapter.RvStringAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

//工作预约
public class AppointmentActivity extends SteamBaseActivity {
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

    //SEGMENT_DATA_FLAG
    private MultiSegment multiSegment;

    /**
     * 预约时间
     * @return
     */
    private String orderTime;
    private TextView tvTime;

    private int directive_offset = 12000000;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_appointment;
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
            switch (s - directive_offset){
                case 0:
                    try {
                        toAppointingPage();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
    }


    private void toAppointingPage() throws ParseException {
        //int appointTime = (int) getAppointingTimeMin(tvTime.getText().toString()) * 60;
        //multiSegment.workRemaining = appointTime;
        HomeSteamOven.getInstance().orderTime = multiSegment.workRemaining;
        HomeSteamOven.getInstance().workMode = (short) multiSegment.code;
        HomeSteamOven.getInstance().workHours = multiSegment.duration;
        Intent intent = new Intent(this,AppointingActivity.class);
        intent.putExtra(Constant.SEGMENT_DATA_FLAG,multiSegment);
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
        multiSegment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        initHourAndMinScroll(hourData,minuteData);
        //setOrderDate();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            finish();
        } else if (id == R.id.btn_ok) { //确认预约
//            try {
//                toAppointingPage();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            try {
                sendAppointCommand();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.ll_left) {
            finish();
        }
    }

    private void sendAppointCommand() throws ParseException {
        multiSegment.workRemaining = (int) getAppointingTimeMin(tvTime.getText().toString()) * 60;

        int steamFlow = multiSegment.steam;
        int setTemp = multiSegment.defTemp;
        int mode = multiSegment.code;
        int setTime = multiSegment.duration;
        int orderTime = multiSegment.workRemaining;

        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        if (steamFlow == 0){
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 7);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }
        }else {
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 9);
            }
        }
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_0) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);

        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        if (orderTime==0){
            commonMap.put(SteamConstant.workCtrl, 1);
        }else {
            commonMap.put(SteamConstant.workCtrl, 3);
        }

        //预约时间
        commonMap.put(SteamConstant.setOrderMinutesKey, 5);
        commonMap.put(SteamConstant.setOrderMinutesLength, 1);
        if (orderTime<=255){
            commonMap.put(SteamConstant.setOrderMinutes01, orderTime);
        }else{
            if (orderTime<=(256*256)&&orderTime>255) {
                commonMap.put(SteamConstant.setOrderMinutesKey, 5);
                commonMap.put(SteamConstant.setOrderMinutesLength, 2);
                short time = (short) (orderTime & 0xff);
                commonMap.put(SteamConstant.setOrderMinutes01, time);
                short highTime = (short) ((orderTime >> 8) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes02, highTime);
            }else if (orderTime<=255*255*255&&orderTime>255*255){
                commonMap.put(SteamConstant.setOrderMinutesKey, 5);
                commonMap.put(SteamConstant.setOrderMinutesLength, 4);
                short time = (short) (orderTime & 0xff);
                commonMap.put(SteamConstant.setOrderMinutes01, time);
                short highTime = (short) ((orderTime >> 8) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes02, highTime);
                short time1 = (short) ((orderTime >> 16) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes03, time1);
            }
        }

        //commonMap.put(SteamConstant.setOrderMinutes, orderTime);

        //段数
        commonMap.put(SteamConstant.sectionNumberKey, 100) ;
        commonMap.put(SteamConstant.sectionNumberLength, 1) ;
        commonMap.put(SteamConstant.sectionNumber, 1) ;

        commonMap.put(SteamConstant.rotateSwitchKey, 9) ;
        commonMap.put(SteamConstant.rotateSwitchLength, 1) ;
        commonMap.put(SteamConstant.rotateSwitch, 0) ;
        //模式
        commonMap.put(SteamConstant.modeKey, 101) ;
        commonMap.put(SteamConstant.modeLength, 1) ;
        commonMap.put(SteamConstant.mode, mode) ;
        //温度上温度

        if (setTemp!=0) {
            commonMap.put(SteamConstant.setUpTempKey, 102);
            commonMap.put(SteamConstant.setUpTempLength, 1);
            commonMap.put(SteamConstant.setUpTemp, setTemp);
        }
        //时间
        setTime*=60;
        commonMap.put(SteamConstant.setTimeKey, 104);
        commonMap.put(SteamConstant.setTimeLength, 1);

        final short lowTime = setTime > 255 ? (short) (setTime & 0Xff):(short)setTime;
        if (setTime<=255){
            commonMap.put(SteamConstant.setTime0b, lowTime);
        }else{
            commonMap.put(SteamConstant.setTimeKey, 104);
            commonMap.put(SteamConstant.setTimeLength, 2);
            short time = (short)(setTime & 0xff);
            commonMap.put(SteamConstant.setTime0b, time);
            short highTime = (short) ((setTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.setTime1b, highTime);
        }

        if (steamFlow!=0) {
            //蒸汽量
            commonMap.put(SteamConstant.steamKey, 106);
            commonMap.put(SteamConstant.steamLength, 1);
            commonMap.put(SteamConstant.steam, steamFlow);
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,MsgKeys.setDeviceAttribute_Req+directive_offset);
    }

    /**
     * 设置下方提示的开始时间
     */
    private void setOrderDate() {
        String hour = mHourAdapter.getItem(mHourManager.getPickedPosition());
        String minute = mMinuteAdapter.getItem(mMinuteManager.getPickedPosition());
        orderTime = hour + ":" + minute;
        if (DateUtil.compareTime(DateUtil.getCurrentTime(DateUtil.PATTERN), orderTime, DateUtil.PATTERN) == 1) {
            tvTime.setText(String.format(getString(R.string.steam_work_order_hint2), orderTime ));
        } else {
            tvTime.setText(String.format(getString(R.string.steam_work_order_hint3), orderTime ));
        }
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
            minScrollP = (min / 10 +1)*10;
        }
        mHourView.scrollToPosition(hourScrollP);
        mMinuteView.scrollToPosition(minScrollP);

        String curOrderTime = hourData.get(hourScrollP)+":"+minuteData.get(minScrollP);
        if(hour >= 23 && min >= 50){
            tvTime.setText(String.format(getString(R.string.steam_work_order_hint2), curOrderTime ));
        }{
            tvTime.setText(String.format(getString(R.string.steam_work_order_hint3), curOrderTime ));
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
}