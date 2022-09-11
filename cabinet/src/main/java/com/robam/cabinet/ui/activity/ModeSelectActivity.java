package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.cabinet.ui.adapter.RvTimeAdapter;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ClickUtils;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends CabinetBaseActivity {
    private RecyclerView rvMode;
    private RvTimeAdapter rvTimeAdapter;
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
        showRightCenter();
        setRight(R.string.cabinet_appointment);

        rvMode = findViewById(R.id.rv_mode);
        tvMode = findViewById(R.id.tv_mode);
        tvNum = findViewById(R.id.tv_num);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(5)
//                .setAlpha(false)
                .setScale(0.44f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        rvTimeAdapter.setPickPosition(position);
                        tvNum.setText(rvTimeAdapter.getItem(position).toString());
                        //设置工作时长
                        HomeCabinet.getInstance().workHours = Integer.parseInt(rvTimeAdapter.getItem(position));
                    }
                }).build();
        rvMode.setLayoutManager(pickerLayoutManager);
        setOnClickListener(R.id.ll_right, R.id.ll_right_center, R.id.btn_start, R.id.iv_float);
    }

    @Override
    protected void initData() {
        CabModeBean cabModeBean = null;
        if (null != getIntent())
            cabModeBean = (CabModeBean) getIntent().getSerializableExtra(CabinetConstant.EXTRA_MODE_BEAN);
        if (null != cabModeBean) {
            //当前模式
            HomeCabinet.getInstance().workMode = cabModeBean.code;

            List<String> lists = new ArrayList();

            tvMode.setText(cabModeBean.name);
            for (int i = cabModeBean.minTime; i <= cabModeBean.maxTime; i += cabModeBean.stepTime)
                lists.add(i + "");

            rvTimeAdapter = new RvTimeAdapter();
            rvMode.setAdapter(rvTimeAdapter);
            rvTimeAdapter.setList(lists);
            //初始位置
            int initPos = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % lists.size();
            pickerLayoutManager.scrollToPosition(initPos);
            //默认
            tvNum.setText(lists.get(0) + "");
            //工作时长
            HomeCabinet.getInstance().workHours = Integer.parseInt(lists.get(0));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //预约
            startActivity(new Intent(this, AppointmentActivity.class));
        } else if (id == R.id.btn_start) {
            //开始工作
            startActivity(WorkActivity.class);
            finish();
        } else if (view.getId() == R.id.iv_float) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left) {
            finish();
        }
    }
}