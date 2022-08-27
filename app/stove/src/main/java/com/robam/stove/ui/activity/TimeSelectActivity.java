package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveModeEnum;
import com.robam.stove.factory.StoveDialogFactory;
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
        setOnClickListener(R.id.btn_start);
    }

    @Override
    protected void initData() {
        List<String> lists = new ArrayList();

        for (int i = StoveModeEnum.MODE_TIMING.minTime; i <= StoveModeEnum.MODE_TIMING.maxTime; i++)
            lists.add(i + "");
        rvTimeAdapter = new RvTimeAdapter(1);
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_start) {
            //炉头选择
            selectStove();
        }
    }
    //炉头选择
    private void selectStove() {
        //炉头选择提示
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.view_left || id == R.id.view_right)
                    openFire();
            }
        }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        iDialog.show();
    }

    //点火提示
    private void openFire() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
        iDialog.setCancelable(false);
        iDialog.show();
    }
}