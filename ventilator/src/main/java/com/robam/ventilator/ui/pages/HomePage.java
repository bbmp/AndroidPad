package com.robam.ventilator.ui.pages;

import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.robam.cabinet.bean.Cabinet;
import com.robam.common.IDeviceType;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.RTopic;
import com.robam.common.utils.DeviceUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.pan.bean.Pan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.stove.device.HomeStove;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.module.IPublicCabinetApi;
import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ScreenUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;
import com.robam.ventilator.ui.activity.AddDeviceMainActivity;
import com.robam.ventilator.ui.activity.SimpleModeActivity;
import com.robam.ventilator.ui.adapter.RvMainFunctonAdapter;
import com.robam.ventilator.ui.adapter.RvProductsAdapter;
import com.robam.ventilator.ui.adapter.RvSettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends VentilatorBasePage {
    /**
     * 主功能
     */
    private RecyclerView recyclerView;
    /**
     *
     */
    private RvMainFunctonAdapter rvFunctionAdapter;
    /**
     * 系统设置
     */
    private RecyclerView rvLeft;
    /**
     * 产品中心
     */
    private RecyclerView rvRight;
    private List<VenFunBean> funList = new ArrayList<>();
    //系统设置menu
    private List<VenFunBean> settingList = new ArrayList<>();
    //系统设置
    private RvSettingAdapter settingAdapter;
    //产品中心
    private RvProductsAdapter rvProductsAdapter;
    private TextView tvPerformance, tvComfort;
    private Group group;
    private DrawerLayout drawerLayout;
    private LinearLayout llSetting, llProducts;

    public static HomePage newInstance() {
        return new HomePage();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_home;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.rv_fun);
        tvPerformance = findViewById(R.id.tv_performance);
        tvComfort = findViewById(R.id.tv_comfort);
        group = findViewById(R.id.ventilator_group);
        drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.setScrimColor(getResources().getColor(R.color.ventilator_drawer_bg));
        llSetting = findViewById(R.id.ll_drawer_left);
        llProducts = findViewById(R.id.ll_drawer_right);
        rvLeft = findViewById(R.id.rv_left);
        rvRight = findViewById(R.id.rv_right);

        tvPerformance.setSelected(true);

        rvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRight.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        //获取px
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_45)));
        showCenter();
        setOnClickListener(R.id.tv_performance, R.id.tv_comfort, R.id.ll_drawer_left, R.id.ll_drawer_right);

        //禁止手勢滑動
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //抽屉滑动式回调的方法
//                LogUtils.e("x" + drawerView.getWidth());
//                LogUtils.e("---onDrawerSlide---"+slideOffset);
                if (drawerView.getId() == R.id.drawer_left)
                    llSetting.setX(slideOffset * drawerView.getWidth());
                else {
//                    LogUtils.e("x" + llProducts.getX());
                    int width = ScreenUtils.getWidthPixels(getContext());
                    llProducts.setX(width - slideOffset * drawerView.getWidth() - getContext().getResources().getDimension(com.robam.common.R.dimen.dp_90));
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //抽屉打开时会回调的方法
                LogUtils.i("---onDrawerOpened---");
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    findViewById(R.id.iv_left_left).setVisibility(View.VISIBLE);
                    findViewById(R.id.iv_left_right).setVisibility(View.INVISIBLE);
                }
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    findViewById(R.id.iv_right_left).setVisibility(View.INVISIBLE);
                    findViewById(R.id.iv_right_right).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                LogUtils.i("---onDrawerClosed---");

                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
                findViewById(R.id.iv_left_left).setVisibility(View.INVISIBLE); //箭头切换
                findViewById(R.id.iv_left_right).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_right_left).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_right_right).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //抽屉的状态改变时会回调的方法
                LogUtils.i("---onDrawerStateChanged---");
            }
        });
    }

    @Override
    protected void initData() {
        //主功能
        funList.add(new VenFunBean(1, "fun1", "logo_roki", R.drawable.ventilator_oil_clean, "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", R.drawable.ventilator_gear_weak, "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", R.drawable.ventilator_gear_medium, "into"));
        funList.add(new VenFunBean(1, "fun4", "logo_roki", R.drawable.ventilator_gear_max, "into"));
        rvFunctionAdapter = new RvMainFunctonAdapter();
        recyclerView.setAdapter(rvFunctionAdapter);
        rvFunctionAdapter.setList(funList);
        //主功能
        rvFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0)
                    screenLock();
                else {
                   //挡位
                }
                rvFunctionAdapter.setPickPosition(position);
            }
        });
        //左边菜单
        settingAdapter = new RvSettingAdapter();
        rvLeft.setAdapter(settingAdapter);
        //设置功能
        settingList.add(new VenFunBean(1, "个人中心", "", -1, "com.robam.ventilator.ui.activity.PersonalCenterActivity"));
        settingList.add(new VenFunBean(2, "网络设置", "", -1, "com.robam.ventilator.ui.activity.WifiSettingActivity"));
        settingList.add(new VenFunBean(3, "时间设定", "", -1, "com.robam.ventilator.ui.activity.DateSettingActivity"));
        settingList.add(new VenFunBean(4, "屏幕亮度", "", -1, "com.robam.ventilator.ui.activity.ScreenBrightnessActivity"));
        settingList.add(new VenFunBean(5, "恢复出厂", "", -1, "com.robam.ventilator.ui.activity.ResetActivity"));
        settingList.add(new VenFunBean(6, "关于售后", "", -1, "com.robam.ventilator.ui.activity.SaleServiceActivity"));
        settingList.add(new VenFunBean(7, "关于产品", "", -1, "com.robam.ventilator.ui.activity.AboutActivity"));
        settingList.add(new VenFunBean(8, "智能设置", "", -1, "com.robam.ventilator.ui.activity.SmartSettingActivity"));
        settingAdapter.setList(settingList);

        settingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //close setting menu
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                VenFunBean venFunBean = (VenFunBean) adapter.getItem(position);
                if (venFunBean.funtionCode == 9) {
                    simpleMode(); //进入极简模式
                } else {
                    Intent intent = new Intent();

                    intent.setClassName(getContext(), venFunBean.into);
                    startActivity(intent);
                }
            }
        });
        //右边菜单
        rvProductsAdapter  = new RvProductsAdapter(this);
        rvRight.setAdapter(rvProductsAdapter);

        View head = LayoutInflater.from(getContext()).inflate(R.layout.ventilator_item_layout_image, null);
        ImageView ivHead = head.findViewById(R.id.iv_head);
        ivHead.setImageResource(R.drawable.ventilator_ic_bg);
        rvProductsAdapter.addHeaderView(head);
        View foot = LayoutInflater.from(getContext()).inflate(R.layout.ventilator_item_layout_button, null);
        //添加产品
        foot.findViewById(R.id.tv_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddDeviceMainActivity.class));
            }
        });
        rvProductsAdapter.addFooterView(foot);

        rvProductsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //close products menu
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                Device device = (Device) adapter.getItem(position);

                //跳转设备首页
                Intent intent = new Intent();
                switch (device.dc) {
                    case IDeviceType.RYYJ:
                        intent.setClassName(getContext(), IPublicStoveApi.STOVE_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RZNG:
                        intent.setClassName(getContext(), IPublicPanApi.PAN_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RXWJ:
                        intent.setClassName(getContext(), IPublicDishWasherApi.DISHWASHER_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RXDG:
                        intent.setClassName(getContext(), IPublicCabinetApi.CABINET_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RZKY:
                        intent.setClassName(getContext(), IPublicCabinetApi.CABINET_HOME);
                        break;
                }
            }
        });
        rvProductsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.btn_left_close) {
                    Device device = (Device) adapter.getItem(position);
                    device.status = 1;
                    device.workStatus = 1;
                    HomeStove.getInstance().leftWorkMode = 1;
                    //加head需要加1
                    rvProductsAdapter.notifyItemChanged(position + 1);
                }
            }
        });
        //监听用户登录状态
        AccountInfo.getInstance().getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                getDeviceInfo(userInfo);
            }
        });
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
                        List<Device> deviceList = getDeviceRes.devices;
                        AccountInfo.getInstance().deviceList.clear();
                        for (Device device: deviceList) {
                            if (IDeviceType.RYYJ.equals(device.dc)) //烟机
                                AccountInfo.getInstance().deviceList.add(new Ventilator(device));
                            else if (IDeviceType.RZKY.equals(device.dc)) //一体机
                                AccountInfo.getInstance().deviceList.add(new SteamOven(device));
                            else if (IDeviceType.RXWJ.equals(device.dc)) //洗碗机
                                AccountInfo.getInstance().deviceList.add(new DishWasher(device));
                            else if (IDeviceType.RXDG.equals(device.dc))  //消毒柜
                                AccountInfo.getInstance().deviceList.add(new Cabinet(device));
                            List<Device> subDevices = device.subDevices;
                            if (null != subDevices) {
                                for (Device subDevice : subDevices) {
                                    if (IDeviceType.RZNG.equals(subDevice.dc)) //锅
                                        AccountInfo.getInstance().deviceList.add(new Pan(subDevice));
                                }
                            }
                        }
                        //绑定设备
                        bindDevice();
                        //订阅设备主题
                        subscribeDevice();
                        if (null != rvProductsAdapter)
                            rvProductsAdapter.setList(AccountInfo.getInstance().deviceList);
                    }
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                }
            });
        } else {
            //logout
            AccountInfo.getInstance().deviceList.clear();

            if (null != rvProductsAdapter)
                rvProductsAdapter.setList(AccountInfo.getInstance().deviceList);
        }
    }
    //绑定设备
    private void bindDevice() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.guid.equals(VentilatorFactory.getPlatform().getDeviceOnlySign())) //已绑定
                return;
        }
        CloudHelper.bindDevice(this, AccountInfo.getInstance().getUser().getValue().id,
                VentilatorFactory.getPlatform().getDeviceOnlySign(), IDeviceType.RYYJ_ZN, true, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (null != baseResponse)
                            LogUtils.e("绑定成功" + VentilatorFactory.getPlatform().getDeviceOnlySign());
                    }

                    @Override
                    public void onFaild(String err) {

                    }
                });
        //子设备绑定
    }

    //循环订阅
    private void subscribeDevice() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            MqttManager.getInstance().subscribe(DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_performance) {
            if (!tvPerformance.isSelected()) {
                tvPerformance.setSelected(true);
                tvComfort.setSelected(false);
                group.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_comfort) {
            if (!tvComfort.isSelected()) {
                tvPerformance.setSelected(false);
                tvComfort.setSelected(true);
                group.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_drawer_left) {
            open(Gravity.LEFT);
        } else if (id == R.id.ll_drawer_right) {
            open(Gravity.RIGHT);
        }
    }

    public void open(int gravity) {
        //判断当前
        if (drawerLayout.isDrawerOpen(gravity)) {
            drawerLayout.closeDrawer(gravity);
        }
        else {
            if (gravity == Gravity.LEFT)
                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            if (gravity == Gravity.RIGHT)
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            drawerLayout.openDrawer(gravity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //锁屏
    private void screenLock() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_LOCK);
        iDialog.setCancelable(false);
        //长按解锁
        ImageView imageView = iDialog.getRootView().findViewById(R.id.iv_lock);
        ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iDialog.dismiss();
                if (null != rvFunctionAdapter)
                    rvFunctionAdapter.setPickPosition(-1);
                return true;
            }
        });
        iDialog.show();
    }

    //切换至极简模式
    private void simpleMode() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_simple_mode_hint);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    startActivity(new Intent(getContext(), SimpleModeActivity.class));
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
}