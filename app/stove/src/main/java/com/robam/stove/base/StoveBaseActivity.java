package com.robam.stove.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.Plat;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.manager.StoveActivityManager;
import com.robam.stove.ui.dialog.LockDialog;

public abstract class StoveBaseActivity extends BaseActivity {
    private IDialog iDialogAffirm, ilockDialog;
    private TextView tvRightCenter;
    private ImageView ivRightCenter;
    private IDialog panDialog, stoveDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoveActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left, R.id.ll_right_center);
        tvRightCenter = findViewById(R.id.tv_right_center);
        ivRightCenter = findViewById(R.id.iv_right_center);

        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Plat.getPlatform().getDeviceOnlySign().equals(s)) //烟机
                    return;
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (null != device.guid && device.guid.equals(s) && device.guid.equals(HomeStove.getInstance().guid) && IDeviceType.RRQZ.equals(device.dc)) {
                        Stove stove = (Stove) device;
                        if (stove.lockStatus == StoveConstant.LOCK) {
                            screenLock();
                        } else {
                            if (null != ilockDialog)
                                ilockDialog.dismiss();
                            //解锁
                            tvRightCenter.setTextColor(getResources().getColor(R.color.stove_white));
                            ivRightCenter.setImageResource(R.drawable.stove_screen_unlock);
                        }
                        break;
                    }
                }
            }
        });
    }
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }

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

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        findViewById(R.id.ll_right).setVisibility(View.INVISIBLE);
    }

    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right_center) {//童锁处理
            if (HomeStove.getInstance().isStoveOffline()) {
                ToastUtils.showShort(getApplicationContext(), R.string.stove_offline);
                return;
            }
            affirmLock();
        }
    }

    //锁屏确认
    private void affirmLock() {
        if (null == iDialogAffirm) {
            iDialogAffirm = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogAffirm.setCancelable(false);
            iDialogAffirm.setContentText(R.string.stove_affirm_lock_hint);
            iDialogAffirm.setOKText(R.string.stove_ok);
            iDialogAffirm.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        if (HomeStove.getInstance().isStoveOffline()) {
                            ToastUtils.showShort(getApplicationContext(), R.string.stove_offline);
                            return;
                        }

                        StoveAbstractControl.getInstance().setLock(HomeStove.getInstance().guid, StoveConstant.LOCK);
                    }
                    iDialogAffirm = null;
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogAffirm.show();
    }
    //锁屏
    private void screenLock() {
        tvRightCenter.setTextColor(getResources().getColor(R.color.stove_step));
        ivRightCenter.setImageResource(R.drawable.stove_screen_lock);
        if (null == ilockDialog) {
            ilockDialog = new LockDialog(this);
            ilockDialog.setCancelable(false);
            ilockDialog.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort(getApplicationContext(), R.string.stove_unlock_hint);
                }
            });
            LinearLayout linearLayout = ilockDialog.getRootView().findViewById(R.id.ll_right_center);
            ClickUtils.setLongClick(new Handler(), linearLayout, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    StoveAbstractControl.getInstance().setLock(HomeStove.getInstance().guid, StoveConstant.UNLOCK);
                    return true;
                }
            });
        }

        ilockDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StoveActivityManager.getInstance().removeActivity(this);
        if (null != iDialogAffirm && iDialogAffirm.isShow())
            iDialogAffirm.dismiss();
        if (null != ilockDialog && ilockDialog.isShow())
            ilockDialog.dismiss();
        if (null != panDialog && panDialog.isShow())
            panDialog.dismiss();
        if (null != stoveDialog && stoveDialog.isShow())
            stoveDialog.dismiss();
    }

    //是否锁屏状态
    public boolean isLock() {
        if (null != ilockDialog && ilockDialog.isShow())
            return true;
        return false;
    }
    //检查锅是否工作中
    protected boolean isPanWorking() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device instanceof Pan) {
                Pan pan = (Pan) device;
                if (pan.mode != 0) {//工作中
                    ToastUtils.showShort(getApplicationContext(), R.string.stove_pan_working);
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
            panDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            panDialog.setCancelable(false);
            panDialog.setContentText(R.string.stove_need_match_pan);
            panDialog.setOKText(R.string.stove_go_match);
            panDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                        if (null != iPublicVentilatorApi)
                            iPublicVentilatorApi.startMatchNetwork(StoveBaseActivity.this, IDeviceType.RZNG);
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
            stoveDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            stoveDialog.setCancelable(false);
            stoveDialog.setContentText(R.string.stove_need_match_stove);
            stoveDialog.setOKText(R.string.stove_go_match);
            stoveDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                        if (null != iPublicVentilatorApi)
                            iPublicVentilatorApi.startMatchNetwork(StoveBaseActivity.this, IDeviceType.RRQZ);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stoveDialog.show();
        return true;
    }
}
