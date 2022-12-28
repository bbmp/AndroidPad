package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.bean.WorkModeBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.CabinetEnum;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.constant.EventConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.ui.adapter.RvTimeAdapter;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModeSelectActivity extends CabinetBaseActivity {
    private RecyclerView rvMode;
    private RvTimeAdapter rvTimeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvMode, tvNum;
    CabModeBean cabModeBean = null;

    public int directive_offset = 30000;
    public static   final int  POWER_ON_OFFSET=  300;
    private TextView btStart;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_modeselect;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        rvMode = findViewById(R.id.rv_mode);
        tvMode = findViewById(R.id.tv_mode);
        tvNum = findViewById(R.id.tv_num);
        btStart = findViewById(R.id.btn_start);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(5)
//                .setAlpha(false)
                .setScale(0.44f)
                .setOnPickerListener((recyclerView, position) -> {
                    rvTimeAdapter.setPickPosition(position);
                    tvNum.setText(rvTimeAdapter.getItem(position));
                    //设置工作时长
                }).build();
        rvMode.setLayoutManager(pickerLayoutManager);
        setOnClickListener(R.id.ll_right, R.id.btn_start, R.id.iv_float);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(toWaringPage(cabinet.faultId)){
                        return;
                    }
                    if(toOffLinePage(cabinet)){
                        return;
                    }
                    switch (cabinet.workMode){
                        case CabinetConstant.FUN_DISINFECT:
                        case CabinetConstant.FUN_CLEAN:
                        case CabinetConstant.FUN_DRY:
                        case CabinetConstant.FUN_FLUSH:
                        case CabinetConstant.FUN_SMART:
                        case CabinetConstant.FUN_WARING:
                            toWorkingPage(cabinet);
                            break;
                    }
                }
            }
        });

       /* MqttDirective.getInstance().getDirective().observe(this, s->{
            if(s != EventConstant.WARING_CODE_NONE){
                showWaring(s);
            }
            switch (s - directive_offset){
                case POWER_ON_OFFSET:
                    CabinetCommonHelper.startAppointCommand(cabModeBean.code,
                            Integer.parseInt(rvTimeAdapter.getItem(pickerLayoutManager.getPickedPosition())),0,
                            directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
            }
        });*/

    }

    private void toWorkingPage(Cabinet cabinet) {
        if(cabinet.remainingModeWorkTime > 0){//工作
            WorkModeBean workModeBean = new WorkModeBean(cabinet.workMode,0, cabinet.remainingModeWorkTime);
            Intent intent = new Intent(this,WorkActivity.class);
            intent.putExtra(Constant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
            finish();
        }else if(cabinet.remainingAppointTime > 0){//预约 每次结束后，都有一段时间预约时间是1380，需与设备端一起排查问题
            WorkModeBean workModeBean = new WorkModeBean(cabinet.workMode, cabinet.remainingAppointTime,cabinet.modeWorkTime);
            Intent intent = new Intent(this,AppointingActivity.class);
            intent.putExtra(Constant.EXTRA_MODE_BEAN,workModeBean);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void initData() {
        if (null != getIntent()){
            cabModeBean = (CabModeBean) getIntent().getSerializableExtra(Constant.EXTRA_MODE_BEAN);
        }
        if (null != cabModeBean) {
            //当前模式
            HomeCabinet.getInstance().workMode = cabModeBean.code;

            List<String> lists = new ArrayList();

            tvMode.setText(cabModeBean.name);
            for (int i = cabModeBean.minTime; i <= cabModeBean.maxTime; i += cabModeBean.stepTime)
                lists.add(i + "");

            if(cabModeBean.code != CabinetEnum.SMART.getCode()){
                setRight(R.string.cabinet_appointment);
            }
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
            Intent intent = new Intent(this,AppointmentActivity.class);
            CabModeBean cabModeBean = this.cabModeBean.newCab();
            cabModeBean.defTime = Integer.parseInt(rvTimeAdapter.getItem(pickerLayoutManager.getPickedPosition()));
            intent.putExtra(Constant.EXTRA_MODE_BEAN, cabModeBean);
            startActivityForResult(intent,Constant.APPOINT_CODE);
        } else if (id == R.id.btn_start) {
            //开始工作
            //startWork();
            if(ClickUtils.isFastClick()){
                return;//防止快速重复点击
            }
           this.startAppointOrWork();
        } else if (view.getId() == R.id.iv_float) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left) {
            finish();
        }
    }


    private void startAppointOrWork(){
        if(!this.checkDoorState()){//新增检查门状态
            return;
        }
        String text = btStart.getText().toString();

        if(getResources().getText(R.string.cabinet_start_appoint).equals(text)){//预约
            try {
                TextView time = findViewById(R.id.tv_right);
                int appointingTimeMin = (int) getAppointingTimeMin(time.getText().toString());
                CabinetCommonHelper.startAppointCommand(cabModeBean.code,
                        cabModeBean.defTime,
                        appointingTimeMin,
                        directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
                //LogUtils.i("startAppointOrWork ...appointingTimeMin "+appointingTimeMin);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{//开始工作
            CabinetCommonHelper.startAppointCommand(cabModeBean.code,
                    Integer.parseInt(rvTimeAdapter.getItem(pickerLayoutManager.getPickedPosition())),0,
                    directive_offset + MsgKeys.SetSteriPowerOnOff_Req);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.APPOINT_CODE && resultCode == RESULT_OK) {
            //展示预约设计时间
            String result = data.getStringExtra(Constant.APPOINTMENT_RESULT);
            setRight(result);
            btStart.setText(R.string.cabinet_start_appoint);
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