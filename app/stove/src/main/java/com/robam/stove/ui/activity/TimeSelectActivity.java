package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveModeEnum;
import com.robam.stove.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.List;

public class TimeSelectActivity extends StoveBaseActivity {
    private RecyclerView rvTime;
    private RvTimeAdapter rvTimeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvNum;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_time_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        tvNum = findViewById(R.id.tv_num);
        rvTime = findViewById(R.id.rv_time);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(5)
//                .setAlpha(false)
                .setScale(0.44f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        rvTimeAdapter.setPickPosition(position);
                        tvNum.setText(rvTimeAdapter.getItem(position));
                        //设置工作时长
                        Stove.getInstance().workHours = Integer.parseInt(rvTimeAdapter.getItem(position));
                    }
                }).build();
        rvTime.setLayoutManager(pickerLayoutManager);
    }

    @Override
    protected void initData() {
        List<String> lists = new ArrayList();

        for (int i = StoveModeEnum.MODE_TIMING.minTime; i <= StoveModeEnum.MODE_TIMING.maxTime; i++)
            lists.add(i + "");
        rvTimeAdapter = new RvTimeAdapter();
        rvTime.setAdapter(rvTimeAdapter);
        rvTimeAdapter.setList(lists);
        //初始位置
        int initPos = (Integer.MAX_VALUE/2) - (Integer.MAX_VALUE / 2) % lists.size();
        pickerLayoutManager.scrollToPosition(initPos);
        //默认第一个
        tvNum.setText(lists.get(0));
        //工作时长
        Stove.getInstance().workHours = Integer.parseInt(lists.get(0));
    }
}