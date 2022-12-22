package com.robam.stove.ui.activity;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.device.HomeStove;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.ModeBean;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvTimeAdapter;
import com.robam.stove.ui.dialog.SelectStoveDialog;

import java.util.ArrayList;
import java.util.List;

public class TimeSelectActivity extends StoveBaseActivity {
    private RecyclerView rvTime;
    private RvTimeAdapter rvTimeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvNum;
    //工作时长
    private String workHours;

    private SelectStoveDialog selectStoveDialog;

    private IDialog openDialog;

    private int stoveId;

    private int timeTime; //定时时间

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
                        workHours = rvTimeAdapter.getItem(position);
                    }
                }).build();
        rvTime.setLayoutManager(pickerLayoutManager);
        setOnClickListener(R.id.btn_start);
        //监听开火状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (null != device.guid && device.guid.equals(s) && device.guid.equals(HomeStove.getInstance().guid) && IDeviceType.RRQZ.equals(device.dc)) { //当前灶
                        Stove stove = (Stove) device;
                        //开火提示状态
                        if (null != openDialog && openDialog.isShow()) {
                            if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_WORKING) { //左灶已点火
                                openDialog.dismiss();
                                StoveAbstractControl.getInstance().setTiming(stove.guid, (byte) IPublicStoveApi.STOVE_LEFT, (short) timeTime); //定时时间
                                startActivity(MainActivity.class); //跳转到首页
                            } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_WORKING) { //右灶已点火
                                openDialog.dismiss();
                                StoveAbstractControl.getInstance().setTiming(stove.guid, (byte) IPublicStoveApi.STOVE_RIGHT, (short) timeTime); //定时时间
                                startActivity(MainActivity.class);
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        List<String> lists = new ArrayList();

        //定时功能一种模式
        List<ModeBean> modeBeans = null;
        if (null != getIntent())
            modeBeans = (ArrayList<ModeBean>) getIntent().getSerializableExtra(StoveConstant.EXTRA_MODE_LIST);
        if (null != modeBeans && modeBeans.size() > 0) {
            ModeBean modeBean = modeBeans.get(0);//取第一个模式
            for (int i = modeBean.minTime; i <= modeBean.maxTime; i++)
                lists.add(i + "");
            int offset = modeBean.defTime - modeBean.minTime;
            int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%lists.size() + offset;
            rvTimeAdapter = new RvTimeAdapter();
            rvTime.setAdapter(rvTimeAdapter);
            rvTimeAdapter.setList(lists);
            //初始位置
            pickerLayoutManager.scrollToPosition(position);
            //默认第一个
            tvNum.setText(rvTimeAdapter.getItem(position));
            //工作时长
            workHours = rvTimeAdapter.getItem(position);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_start) {
            //炉头选择
            selectStove();
        } else if (id == R.id.ll_left) { //返回
            finish();
        }
    }
    //炉头选择
    private void selectStove() {
        //检查灶是否连接
        if (HomeStove.getInstance().isStoveOffline()) {
            ToastUtils.showShort(this, R.string.stove_offline);
            return;
        }
        //炉头选择提示
        if (null == selectStoveDialog) {
            selectStoveDialog = new SelectStoveDialog(this);
            selectStoveDialog.setCancelable(false);
            selectStoveDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.view_left)
                        openFire(IPublicStoveApi.STOVE_LEFT);  //左灶
                    else if (id == R.id.view_right)
                        openFire(IPublicStoveApi.STOVE_RIGHT);   //右灶
                }
            }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        }
        //检查炉头状态
        selectStoveDialog.checkStoveStatus();
        selectStoveDialog.show();
    }

    //点火提示
    private void openFire(int stove) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (null != device.guid && IDeviceType.RRQZ.equals(device.dc) && device.guid.equals(HomeStove.getInstance().guid)) {
                Stove stove1 = (Stove) device;
                if (null == openDialog) {
                    openDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
                    openDialog.setCancelable(false);
                }
                if (stove == IPublicStoveApi.STOVE_LEFT) {
                    openDialog.setContentText(R.string.stove_open_left_hint);
                    //进入工作状态
                    //选择左灶
                    timeTime = Integer.parseInt(workHours) * 60;
                    stoveId = IPublicStoveApi.STOVE_LEFT;
                } else {
                    openDialog.setContentText(R.string.stove_open_right_hint);
                    //选择右灶
                    timeTime = Integer.parseInt(workHours) * 60;
                    stoveId = IPublicStoveApi.STOVE_RIGHT;
                }
                openDialog.show();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != selectStoveDialog && selectStoveDialog.isShow())
            selectStoveDialog.dismiss();
        if (null != openDialog && openDialog.isShow())
            openDialog.dismiss();
    }
}