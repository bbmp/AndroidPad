package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.ui.view.SwitchButton;
import com.robam.common.utils.NetworkUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.ui.adapter.RvStringAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class DateSettingActivity extends VentilatorBaseActivity {

    private SwitchButton switchButton;
    private Group group;    //手动设置
    private LinearLayout llBorder; //自动设置
    private Button btnSave;

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
     * 重新layoutManage
     */
    private PickerLayoutManager mHourManager;
    private PickerLayoutManager mMinuteManager;

    private int hourInt;
    private int minuteInt;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_date_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_setting_time);

        switchButton = findViewById(R.id.sb_auto_set);
        group = findViewById(R.id.ventilator_group5);
        llBorder = findViewById(R.id.ll_border);
        btnSave = findViewById(R.id.btn_save);
        mHourView = findViewById(R.id.rv_time_hour);
        mMinuteView = findViewById(R.id.rv_time_minute);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton button, boolean checked) {
                group.setVisibility(checked? View.GONE: View.VISIBLE);
                llBorder.setVisibility(checked ? View.VISIBLE:View.GONE);
                if (checked) {
                    Settings.Global.putString(
                            getContentResolver(),
                            Settings.Global.AUTO_TIME,"1");
                } else {
                    Settings.Global.putString(
                            getContentResolver(),
                            Settings.Global.AUTO_TIME,"0");
                }
            }
        });

        //已联网
        if (NetworkUtils.isConnect(this)) {
            switchButton.setChecked(true);
            group.setVisibility(View.GONE);
            Settings.Global.putString(
                    getContentResolver(),
                    Settings.Global.AUTO_TIME,"1");
        } else {
            switchButton.setChecked(false);
            llBorder.setVisibility(View.GONE);
            Settings.Global.putString(
                    getContentResolver(),
                    Settings.Global.AUTO_TIME,"0");
        }
        mHourAdapter = new RvStringAdapter();
        mMinuteAdapter = new RvStringAdapter();

        mHourManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setMaxItem(3)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        hourInt = position;
                    }
                }).build();
        mMinuteManager = new PickerLayoutManager.Builder(this)
                .setScale(0.5f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        minuteInt = position;
                    }
                }).build();
        mHourView.setLayoutManager(mHourManager);
        mMinuteView.setLayoutManager(mMinuteManager);
        mHourView.setAdapter(mHourAdapter);
        mMinuteView.setAdapter(mMinuteAdapter);
        setOnClickListener(R.id.btn_save);
    }

    @Override
    protected void initData() {
        // 生产小时
        ArrayList<String> hourData = new ArrayList<>(24);
        for (int i = 0; i <= 23; i++) {
            hourData.add((i < 10 ? "0" : "") + i + "");
        }
        mHourAdapter.setList(hourData);

        // 生产分钟
        ArrayList<String> minuteData = new ArrayList<>(60);
        for (int i = 0; i <= 59; i++) {
            minuteData.add((i < 10 ? "0" : "") + i + "");
        }
        mMinuteAdapter.setList(minuteData);

        Calendar calendar = Calendar.getInstance();
        hourInt = calendar.get(Calendar.HOUR_OF_DAY);
        setHour(hourInt);
        minuteInt = calendar.get(Calendar.MINUTE);
        setMinute(minuteInt);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_save)
            setSysTime(Integer.parseInt(mHourAdapter.getItem(hourInt)) , Integer.parseInt(mMinuteAdapter.getItem(minuteInt)));
    }
    public void setHour(int hour) {
        int index = hour;
        if (index < 0 || hour == 24) {
            index = 0;
        } else if (index > mHourAdapter.getItemCount() - 1) {
            index = mHourAdapter.getItemCount() - 1;
        }
        mHourView.scrollToPosition(index);
    }


    public void setMinute(int minute) {
        int index = minute;
        if (index < 0) {
            index = 0;
        } else if (index > mMinuteAdapter.getItemCount() - 1) {
            index = mMinuteAdapter.getItemCount() - 1;
        }
        mMinuteView.scrollToPosition(index);
    }

    public void setSysTime(int hour,int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        try {
            if (when / 1000 < Integer.MAX_VALUE) {
                ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }
}