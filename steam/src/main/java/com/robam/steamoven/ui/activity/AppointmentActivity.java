package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.steamoven.R;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.DateUtil;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.ui.adapter.RvStringAdapter;

import java.util.ArrayList;

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
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setOrderDate();
                    }
                }).build();
        mMinuteManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setOrderDate();
                    }
                })
                .build();
        mHourView.setLayoutManager(mHourManager);
        mMinuteView.setLayoutManager(mMinuteManager);

        setOnClickListener(R.id.btn_cancel, R.id.btn_ok);
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
        setOrderDate();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            finish();
        } else if (id == R.id.btn_ok) { //确认预约
            HomeSteamOven.getInstance().orderTime = orderTime;
            HomeSteamOven.getInstance().workMode = (short) multiSegment.code;
            HomeSteamOven.getInstance().workHours = Integer.parseInt(multiSegment.duration);
            Intent intent = new Intent(this,AppointingActivity.class);
            intent.putExtra(Constant.SEGMENT_DATA_FLAG,multiSegment);
            startActivity(intent);
            finish();
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
}