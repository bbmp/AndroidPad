package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.ui.adapter.RvIntegerAdapter;
import com.robam.common.ui.helper.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends CabinetBaseActivity {
    private RecyclerView rvMode;
    private RvIntegerAdapter rvIntegerAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvMode, tvNum;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_modeselect;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        setRight(R.string.cabinet_appointment);
        rvMode = findViewById(R.id.rv_mode);
        tvMode = findViewById(R.id.tv_mode);
        tvNum = findViewById(R.id.tv_num);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setAlpha(false)
                .setScale(0.44f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        rvIntegerAdapter.setPickPosition(position);
                        tvNum.setText(rvIntegerAdapter.getItem(position).toString());
                        //设置工作时长
                        Cabinet.getInstance().workHours = rvIntegerAdapter.getItem(position).intValue();
                    }
                }).build();
        rvMode.setLayoutManager(pickerLayoutManager);
        setOnClickListener(R.id.ll_right, R.id.btn_start);
    }

    @Override
    protected void initData() {
        CabFunBean cabFunBean = (CabFunBean) getIntent().getParcelableExtra("mode");
        //当前模式
        Cabinet.getInstance().workMode = (short) cabFunBean.funtionCode;

        List<Integer> lists = new ArrayList();
        if (null != cabFunBean) {
            tvMode.setText(cabFunBean.funtionName);
            switch (cabFunBean.funtionCode) {
                case 1:
                    for (int i = 40; i <= 60; i+=5)
                        lists.add(i);
                    break;
                case 2:
                    for (int i = 3; i<=15; i++)
                        lists.add(i);
                    break;
                case 3:
                    for (int i = 20; i<40; i++)
                        lists.add(i);
                    break;
                case 4:
                    for (int i = 20; i <= 30; i+=5)
                        lists.add(i);
                    break;
                case 5:
                    lists.add(45);
                    break;
            }
        }
        rvIntegerAdapter = new RvIntegerAdapter();
        rvMode.setAdapter(rvIntegerAdapter);
        rvIntegerAdapter.setList(lists);
        //初始位置
        int initPos = (Integer.MAX_VALUE/2) - (Integer.MAX_VALUE / 2) % lists.size();
        pickerLayoutManager.scrollToPosition(initPos);
        //默认
        tvNum.setText(lists.get(0) + "");
        //工作时长
        Cabinet.getInstance().workHours = lists.get(0);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //预约
            startActivity(new Intent(this, AppointMentActivity.class));
        } else if (id == R.id.btn_start) {
            //开始工作
            startActivity(WorkActivity.class);
            finish();
        }
    }
}