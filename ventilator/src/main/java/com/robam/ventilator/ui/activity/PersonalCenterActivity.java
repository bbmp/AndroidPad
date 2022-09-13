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
import com.robam.common.http.RetrofitCallback;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.PageIndicator;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.ui.pages.DeviceUserPage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
                        List<Fragment> fragments = new ArrayList<>();
                        for (Device device: getDeviceRes.devices) {
                            DeviceUserPage deviceUserPage = new DeviceUserPage(device, userInfo);
                            fragments.add(deviceUserPage);
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
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                }
            });
        } else {
            //停止取消所有订阅
            MqttManager.getInstance().stop();
            //logout
            AccountInfo.getInstance().deviceList.clear();
        }
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
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_login_exit_hint);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    AccountInfo.getInstance().getUser().setValue(null);
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
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
}