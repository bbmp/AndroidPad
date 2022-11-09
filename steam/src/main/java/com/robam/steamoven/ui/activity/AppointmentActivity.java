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
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.adapter.RvStringAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
            try {
                multiSegment.workRemaining = (int) getAppointingTimeMin(tvTime.getText().toString()) * 60;
                if(SteamModeEnum.EXP.getMode() == multiSegment.code){
                    SteamCommandHelper.sendCommandForExp(multiSegment, multiSegment.workRemaining,MsgKeys.setDeviceAttribute_Req+directive_offset);
                }else{
                    SteamCommandHelper.sendAppointCommand(multiSegment,multiSegment.workRemaining,MsgKeys.setDeviceAttribute_Req+directive_offset);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
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