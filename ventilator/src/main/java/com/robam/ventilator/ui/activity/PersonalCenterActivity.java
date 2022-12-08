package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.BaseResponse;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.LiveDataBus;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.PageIndicator;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.ui.pages.DeviceUserPage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PersonalCenterActivity extends VentilatorBaseActivity {
    private TextView tvLogin;

    private LinearLayout llDot;
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();
    private ViewPager vpDevice;
    private Group group1, group2;
    //头像
    private ImageView ivHead;
    //昵称和手机
    private TextView tvName, tvPhone;
    private IDialog exitDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_personal_center;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        vpDevice = findViewById(R.id.vp_device);
        llDot = findViewById(R.id.ll_dot);
        group1 = findViewById(R.id.ventilator_group3); //登录
        group2 = findViewById(R.id.ventilator_group4); //未登录
        ivHead = findViewById(R.id.iv_head);
        tvName = findViewById(R.id.tv_nickname);
        tvPhone = findViewById(R.id.tv_phone);
        setOnClickListener(R.id.tv_login, R.id.btn_exit_login);

    }

    @Override
    protected void initData() {
        //监听用户信息
        AccountInfo.getInstance().getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                setUserInfo(userInfo);
            }
        });
        //删除设备监听
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Plat.getPlatform().getDeviceOnlySign().equals(s)) //当前烟机
                    return;
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s))
                        return;
                }
                //找不到设备
                getDeviceInfo(AccountInfo.getInstance().getUser().getValue());
            }
        });
        //烟机用户更新
        LiveDataBus.get().with(VentilatorConstant.VENTILATOR_USER, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Plat.getPlatform().getDeviceOnlySign().equals(s))
                    getDeviceInfo(AccountInfo.getInstance().getUser().getValue());
            }
        });
    }

    private void setUserInfo(UserInfo userInfo) {
        if (null == userInfo) { //未登录
            group1.setVisibility(View.GONE);
            group2.setVisibility(View.VISIBLE);
            vpDevice.setVisibility(View.GONE);
            llDot.setVisibility(View.GONE);
            tvName.setText("");
            tvPhone.setText("");
            ivHead.setImageDrawable(null);
        } else {
            ImageUtils.loadImage(this, userInfo.figureUrl, ivHead);
            tvName.setText(userInfo.nickname);
            tvPhone.setText(userInfo.phone);
            group1.setVisibility(View.VISIBLE);
            group2.setVisibility(View.GONE);

        }
        //获取绑定的设备
        getDeviceInfo(userInfo);
    }

    /**
     * 获取设备信息
     * @param userInfo
     */
    private void getDeviceInfo(UserInfo userInfo) {
        if (null != userInfo) {
            CloudHelper.getDevices(this, userInfo.id, GetDeviceRes.class, new RetrofitCallback<GetDeviceRes>() {
                @Override
                public void onSuccess(GetDeviceRes getDeviceRes) {
                    if (null != getDeviceRes && null != getDeviceRes.devices) {
                        for (Device device: getDeviceRes.devices) {
                            if (device.dc.equals(IDeviceType.RYYJ) && (Plat.getPlatform().getDeviceOnlySign()).equals(device.guid)) {//已经绑定当前烟机
                                setDeviceUserData(getDeviceRes.devices, userInfo);
                                return;
                            }
                        }
                    }
                    //还没有绑定
                    bindDevice(userInfo);
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                    ToastUtils.showShort(getApplicationContext(), R.string.ventilator_net_err);
                }
            });
        } else {
            //取消所有订阅 除子设备
            unSubscribeDevices();
            //logout
//            AccountInfo.getInstance().deviceList.clear();
        }
    }

    private void setDeviceUserData(List<Device> devices, UserInfo userInfo) {
        List<Fragment> fragments = new ArrayList<>();
        for (Device device: devices) {
            if (device.dc.equals(IDeviceType.RXWJ) ||
                    (device.dc.equals(IDeviceType.RYYJ) && (Plat.getPlatform().getDeviceOnlySign()).equals(device.guid)) ||   //当前烟机
                    device.dc.equals(IDeviceType.RXDG) ||
                    device.dc.equals(IDeviceType.RZKY)) { //过滤套系外设备
                DeviceUserPage deviceUserPage = new DeviceUserPage(device, userInfo);
                fragments.add(deviceUserPage);
                List<Device> subDevices = device.subDevices;
                if ((Plat.getPlatform().getDeviceOnlySign()).equals(device.guid) && null != subDevices) { //当前烟机子设备
                    for (Device subDevice : subDevices) {
                        if (IDeviceType.RZNG.equals(subDevice.dc)) {//锅
                            DeviceUserPage panUserPage = new DeviceUserPage(subDevice, userInfo);
                            fragments.add(panUserPage);
                        }
                        else if (IDeviceType.RRQZ.equals(subDevice.dc)) {//灶具
                            DeviceUserPage stoveUserPage = new DeviceUserPage(subDevice, userInfo);
                            fragments.add(stoveUserPage);
                        }
                    }
                }
            }
        }

        //添加设置适配器
        vpDevice.setAdapter(new DeviceUserPagerAdapter(getSupportFragmentManager(), fragments));
        vpDevice.setOffscreenPageLimit(fragments.size());
        //设置指示器
        PageIndicator pageIndicator = new PageIndicator(PersonalCenterActivity.this, llDot, fragments.size());
        vpDevice.addOnPageChangeListener(pageIndicator);
        //获取到设备显示
        vpDevice.setVisibility(View.VISIBLE);
        llDot.setVisibility(View.VISIBLE);
    }

    //绑定设备 返回列表中无主设备
    private void bindDevice(UserInfo userInfo) {
        //绑定主设备
        CloudHelper.bindDevice(this, userInfo.id,
                Plat.getPlatform().getDeviceOnlySign(), IDeviceType.RYYJ_ZN, true, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (null != baseResponse) {
                            LogUtils.e("绑定成功" + Plat.getPlatform().getDeviceOnlySign());
                            //重新获取
                            CloudHelper.getDevices(PersonalCenterActivity.this, userInfo.id, GetDeviceRes.class, new RetrofitCallback<GetDeviceRes>() {
                                @Override
                                public void onSuccess(GetDeviceRes getDeviceRes) {
                                    if (null != getDeviceRes && null != getDeviceRes.devices) {

                                        setDeviceUserData(getDeviceRes.devices, userInfo);

                                    }
                                }

                                @Override
                                public void onFaild(String err) {
                                    LogUtils.e("getDevices" + err);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("绑定失败" + Plat.getPlatform().getDeviceOnlySign());
                    }
                });
    }

    private void unSubscribeDevices() {
        String deleteGuid = null;
        Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device instanceof Pan || device instanceof Stove)
                continue;
            iterator.remove();
            deleteGuid = device.guid;  //删除的设备
            MqttManager.getInstance().unSubscribe(device.dc, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)); //取消订阅
        }
        if (null != deleteGuid)
            AccountInfo.getInstance().getGuid().setValue(deleteGuid);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login) {  //默认手机登录
            startActivity(LoginPhoneActivity.class);
        } else if (id == R.id.btn_exit_login) { //退出登录
            exitLogin();
        }
    }

    //获取绑定的设备
    //退出登录提示
    private void exitLogin() {
        if (null == exitDialog) {
            exitDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
            exitDialog.setCancelable(false);
            exitDialog.setContentText(R.string.ventilator_login_exit_hint);
            exitDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        AccountInfo.getInstance().getUser().setValue(null);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        exitDialog.show();
    }

    class DeviceUserPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public DeviceUserPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != exitDialog && exitDialog.isShow())
            exitDialog.dismiss();
    }
}