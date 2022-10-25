package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.ui.adapter.RvTimeAdapter;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModeSelectActivity extends CabinetBaseActivity {
    private RecyclerView rvMode;
    private RvTimeAdapter rvTimeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvMode, tvNum;
    CabModeBean cabModeBean = null;

    public int directive_offset = 30000;
    private boolean lock = false;
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

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                }
            }
        });

        MqttDirective.getInstance().getDirective().observe(this, s -> {
            if(s == (MsgKeys.SetSteriPowerOnOff_Req + directive_offset)){
                runOnUiThread(() -> {
                    HomeCabinet.getInstance().workMode = cabModeBean.code;
                    HomeCabinet.getInstance().workHours = cabModeBean.defTime;
                    startActivity(WorkActivity.class);
                    finish();
                });
            }
        });

    }

    @Override
    protected void initData() {
        //CabModeBean cabModeBean = null;
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
            //HomeCabinet.getInstance().workMode = cabModeBean.code;
            //cabModeBean.defTime = Integer.parseInt(rvTimeAdapter.getItem(pickerLayoutManager.getPickedPosition()));
            Intent intent = new Intent(this,AppointmentActivity.class);
            intent.putExtra(CabinetConstant.EXTRA_MODE_BEAN, cabModeBean);
            startActivity(intent);
        } else if (id == R.id.btn_start) {
            //开始工作

            startWork();
        } else if (view.getId() == R.id.iv_float) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left) {
            finish();
        }
    }

    private void startWork(){
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.SteriStatus, cabModeBean.code);
        //TODO(目前855运行时间有限定，非限定值，不会相应, 暂时显示设置限定值，使其能够工作)
        //map.put(CabinetConstant.SteriTime, Integer.parseInt(rvTimeAdapter.getItem(pickerLayoutManager.getPickedPosition())));
        map.put(CabinetConstant.SteriTime, cabModeBean.defTime);
        CabinetCommonHelper.sendCommonMsgForLiveData(map,directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
    }
}