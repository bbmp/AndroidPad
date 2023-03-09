package com.robam.pan.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.R;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.manager.PanActivityManager;
import com.robam.pan.ui.activity.CurveActivity;

public abstract class PanBaseActivity extends BaseActivity {
    private IDialog stoveDialog, panDialog, batteryDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PanActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left, R.id.ll_right_center);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showLeftCenter() {
        findViewById(R.id.ll_left_center).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        ImageView ivWifi = findViewById(R.id.iv_center);
        //监听网络连接状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ivWifi.setVisibility(View.VISIBLE);
                else
                    ivWifi.setVisibility(View.GONE);
            }
        });
    }

    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
        ImageView ivBattery = findViewById(R.id.iv_right_center);
        TextView tvBattery = findViewById(R.id.tv_right_center);
        tvBattery.setText(R.string.pan_battery);
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                Pan pan = (Pan) device;
                //电量
                if (pan.battery < 20)
                    ivBattery.setImageResource(R.drawable.pan_battery_20);
                else if (pan.battery < 40)
                    ivBattery.setImageResource(R.drawable.pan_battery_40);
                else if (pan.battery < 60)
                    ivBattery.setImageResource(R.drawable.pan_battery_60);
                else if (pan.battery < 80)
                    ivBattery.setImageResource(R.drawable.pan_battery_80);
                else
                    ivBattery.setImageResource(R.drawable.pan_battery_100);
                break;
            }
        }
    }

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        findViewById(R.id.ll_right).setVisibility(View.INVISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
        else if (id == R.id.ll_right_center) {
            batteryHint();
        }
    }
    //电量提示
    protected void batteryHint() {
        if (null == batteryDialog) {
            batteryDialog = PanDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_ELECTRIC_QUANTITY);
            batteryDialog.setCancelable(false);
            batteryDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, R.id.tv_ok);
        }
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                Pan pan = (Pan) device;
                if (pan.battery >= 20)
                    batteryDialog.setContentText("翻炒锅电量" + pan.battery + "%");
                break;
            }
        }
        batteryDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PanActivityManager.getInstance().removeActivity(this);
        if (null != panDialog && panDialog.isShow())
            panDialog.dismiss();
        if (null != stoveDialog && stoveDialog.isShow())
            stoveDialog.dismiss();
        if (null != batteryDialog && batteryDialog.isShow())
            batteryDialog.dismiss();
    }
    //检查锅是否工作中
    protected boolean isPanWorking() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device instanceof Pan) {
                Pan pan = (Pan) device;
                if (pan.mode != 0) {//工作中
                    ToastUtils.showShort(getApplicationContext(), R.string.pan_pan_working);
                    return true;
                }
            }
        }
        return false;
    }

    //检查锅是否离线
    protected boolean isPanOffline() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device.status == Device.ONLINE) {

                return false;
            }
        }
        if (null == panDialog) {
            panDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
            panDialog.setCancelable(false);
            panDialog.setContentText(R.string.pan_need_match_pan);
            panDialog.setOKText(R.string.pan_go_match);
            panDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                        if (null != iPublicVentilatorApi)
                            iPublicVentilatorApi.startMatchNetwork(PanBaseActivity.this, IDeviceType.RZNG);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        panDialog.show();
        return true;
    }

    //检查灶具是否离线
    protected boolean isStoveOffline() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RRQZ) && device.status == Device.ONLINE) {

                return false;
            }
        }
        if (null == stoveDialog) {
            stoveDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
            stoveDialog.setCancelable(false);
            stoveDialog.setContentText(R.string.pan_need_match_stove);
            stoveDialog.setOKText(R.string.pan_go_match);
            stoveDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                        if (null != iPublicVentilatorApi)
                            iPublicVentilatorApi.startMatchNetwork(PanBaseActivity.this, IDeviceType.RRQZ);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stoveDialog.show();
        return true;
    }
}
