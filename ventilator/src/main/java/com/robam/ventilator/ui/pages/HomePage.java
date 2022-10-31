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
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.common.device.subdevice.Pan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.module.IPublicSteamApi;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.module.IPublicCabinetApi;
import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ScreenUtils;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.protocol.ble.BleVentilator;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.ui.activity.AddDeviceActivity;
import com.robam.ventilator.ui.activity.MatchNetworkActivity;
import com.robam.ventilator.ui.activity.SimpleModeActivity;
import com.robam.ventilator.ui.adapter.RvMainFunctonAdapter;
import com.robam.ventilator.ui.adapter.RvProductsAdapter;
import com.robam.ventilator.ui.adapter.RvSettingAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class    HomePage extends VentilatorBasePage {
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

    private IDialog homeLock;

    public static HomePage newInstance() {
        return new HomePage();
    }

    private long refreshTime;

    private IPublicPanApi iPublicPanApi = ModulePubliclHelper.getModulePublic(IPublicPanApi.class, IPublicPanApi.PAN_PUBLIC);

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
                    //断开服务，重新请求订阅，避免设备添加后看不到
//                    MqttManager.getInstance().stop();
//                    getDeviceInfo(AccountInfo.getInstance().getUser().getValue());
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
                if (position == 0) { //锁屏清洗提示
                    lockClean();
                } else { //挡位选择
                    if (position == rvFunctionAdapter.getPickPosition()) {//已经选中了
                        position = -1;
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_CLOSE);
                    } else if (position == 1) {
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_WEAK);
                    } else if (position == 2) {
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                    } else if (position == 3) {
                        VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_FRY);
                    }
                }
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

                Intent intent = new Intent();

                intent.setClassName(getContext(), venFunBean.into);
                startActivity(intent);

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
                //close products menu
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                startActivity(new Intent(getContext(), AddDeviceActivity.class));
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
                //非在线状态
                if (device.status != Device.ONLINE)
                    return;
                //跳转设备首页
                Intent intent = new Intent();
                intent.putExtra(ComnConstant.EXTRA_GUID, device.guid);
                switch (device.dc) {
                    case IDeviceType.RRQZ:
                        AccountInfo.getInstance().topGuid = device.guid;
                        intent.putExtra(ComnConstant.EXTRA_GUID, device.guid); //传入启动设备
                        intent.setClassName(getContext(), IPublicStoveApi.STOVE_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RZNG:
                        AccountInfo.getInstance().topGuid = device.guid;
                        intent.putExtra(ComnConstant.EXTRA_GUID, device.guid); //传入启动设备
                        intent.setClassName(getContext(), IPublicPanApi.PAN_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RXWJ:
                        AccountInfo.getInstance().topGuid = device.guid;
                        intent.setClassName(getContext(), IPublicDishWasherApi.DISHWASHER_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RXDG:
                        AccountInfo.getInstance().topGuid = device.guid;
                        intent.setClassName(getContext(), IPublicCabinetApi.CABINET_HOME);
                        startActivity(intent);
                        break;
                    case IDeviceType.RZKY:
                        AccountInfo.getInstance().topGuid = device.guid;
                        intent.setClassName(getContext(), IPublicSteamApi.STEAM_HOME);
                        startActivity(intent);
                        break;
                }
            }
        });
        rvProductsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.btn_left_close) {
                    Device device = (Device) adapter.getItem(position);
                    if (device instanceof Stove) {
                        Stove stove = (Stove) device;
                        if (stove.leftLevel != 0) //关左灶
                            StoveAbstractControl.getInstance().setAttribute(device.guid, IPublicStoveApi.STOVE_LEFT, 0x00, StoveConstant.STOVE_CLOSE);
                    }
                } else if (view.getId() == R.id.btn_right_close) {
                    Device device = (Device) adapter.getItem(position);
                    if (device instanceof Stove) {
                        Stove stove = (Stove) device;
                        if (stove.rightLevel != 0) //关右灶
                            StoveAbstractControl.getInstance().setAttribute(device.guid, IPublicStoveApi.STOVE_RIGHT, 0x00, StoveConstant.STOVE_CLOSE);
                    }
                } else if (view.getId() == R.id.btn_detail) {
                    //查看详情
                    Device device = (Device) adapter.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra(VentilatorConstant.EXTRA_MODEL, device.dc);
                    intent.setClass(getContext(), MatchNetworkActivity.class);
                    startActivity(intent);
                } else if (view.getId() == R.id.btn_work) {
                    //工作控制
                    Device device = (Device) adapter.getItem(position);
                    if (device instanceof SteamOven) {
                        SteamOven steamOven = (SteamOven) device;
                        if (steamOven.workStatus == 4 || steamOven.workStatus == 2)   //工作中和预热中
                            SteamAbstractControl.getInstance().pauseWork(device.guid);
                        else if (steamOven.workStatus == 5 || steamOven.workStatus == 3)  //暂停中
                            SteamAbstractControl.getInstance().continueWork(device.guid);
                    } else if (device instanceof Pan) {
                        Pan pan = (Pan) device;
                        if (pan.workStatus == 3 && null != iPublicPanApi) //电量不足
                            iPublicPanApi.lowBatteryHint(getContext());
                    }
                }
                //close products menu
//                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                    drawerLayout.closeDrawer(Gravity.RIGHT);
//                }
            }
        });
        //监听用户登录状态
        AccountInfo.getInstance().getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                getDeviceInfo(userInfo);
            }
        });
        //监听设备状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Plat.getPlatform().getDeviceOnlySign().equals(s)) { //烟机更新
                    if (null != rvFunctionAdapter && null != homeLock && homeLock.isShow()) //锁定
                        rvFunctionAdapter.setPickPosition(0);
                    else if (HomeVentilator.getInstance().gear == (byte) 0xA1 && null != rvFunctionAdapter)
                        rvFunctionAdapter.setPickPosition(1);
                    else if (HomeVentilator.getInstance().gear == (byte) 0xA2 && null != rvFunctionAdapter)
                        rvFunctionAdapter.setPickPosition(2);
                    else if (HomeVentilator.getInstance().gear == (byte) 0xA6 && null != rvFunctionAdapter)
                        rvFunctionAdapter.setPickPosition(3);
                    else if (null != rvFunctionAdapter)
                        rvFunctionAdapter.setPickPosition(-1);

                    if (HomeVentilator.getInstance().param7 == 0x00) { //性能模式
                        tvPerformance.setSelected(true);
                        tvComfort.setSelected(false);
                        group.setVisibility(View.GONE);
                    } else {  //舒适模式
                        tvPerformance.setSelected(false);
                        tvComfort.setSelected(true);
                        group.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s)) {

//                        rvProductsAdapter.setList(AccountInfo.getInstance().deviceList);
//                        return;
                        break;
                    }
                }
                //找不到设备
                if (System.currentTimeMillis() - refreshTime < 2000 && refreshTime != 0) //防止频繁刷
                    return;
                LogUtils.e("onChanged " + s);
                refreshTime = System.currentTimeMillis();
                rvProductsAdapter.setList(AccountInfo.getInstance().deviceList);
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

                    clearDevice();

                    if (null != getDeviceRes && null != getDeviceRes.devices) {
                        List<Device> deviceList = getDeviceRes.devices;
                        for (Device device: deviceList) {
                            if (IDeviceType.RZKY.equals(device.dc)) //一体机
                                AccountInfo.getInstance().deviceList.add(new SteamOven(device));
                            else if (IDeviceType.RXWJ.equals(device.dc)) //洗碗机
                                AccountInfo.getInstance().deviceList.add(new DishWasher(device));
                            else if (IDeviceType.RXDG.equals(device.dc))  //消毒柜
                                AccountInfo.getInstance().deviceList.add(new Cabinet(device));
                            else if (IDeviceType.RYYJ.equals(device.dc) && device.guid.equals(Plat.getPlatform().getDeviceOnlySign())) {
                                //当前烟机子设备
                                List<Device> subDevices = device.subDevices;
                                Set<String> sets = new HashSet<>();
                                if (null != subDevices) {

                                    for (Device subDevice : subDevices) {
                                        sets.add(new Gson().toJson(subDevice));

                                    }
                                }
                                MMKVUtils.setSubDevice(sets);  //设备更新，子设备覆盖
                            }
                        }
                        //绑定设备
                        bindDevice(deviceList);

                    }
                    //获取子设备
                    getSubDevices(readSubDevices());
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                    //获取子设备
                    getSubDevices(readSubDevices());
                }
            });
        } else {
            //logout
            clearDevice();
            //获取子设备
            getSubDevices(readSubDevices());
        }
    }
    //本地读取子设备
    private List<Device> readSubDevices() {
        Set<String> deviceSets = MMKVUtils.getSubDevice();
        List<Device> subDevices = new ArrayList<>();
        if (null != deviceSets) {
            for (String json: deviceSets) {
                Device subDevice = new Gson().fromJson(json, Device.class);
                subDevices.add(subDevice);
            }
        }
        return subDevices;
    }
    //获取子设备
    private void getSubDevices(List<Device> subDevices) {

        if (null != subDevices) {  //添加新增的设备
            for (Device subDevice: subDevices) {

                if (AccountInfo.getInstance().isExist(AccountInfo.getInstance().deviceList, subDevice))
                    continue;
                else {
                    if (IDeviceType.RZNG.equals(subDevice.dc)) {//锅
                        AccountInfo.getInstance().deviceList.add(new Pan(subDevice));


                    } else if (IDeviceType.RRQZ.equals(subDevice.dc)) { //灶具
                        AccountInfo.getInstance().deviceList.add(new Stove(subDevice));

                    }
                }
            }
        }
        //删除减少的设备
        Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (!AccountInfo.getInstance().isExist(subDevices, device)) { //不存在
                if (device instanceof Pan) {
                    //断开蓝牙
                    BlueToothManager.disConnect(((Pan) device).bleDevice);
                    iterator.remove();
                } else if (device instanceof Stove) {
                    //断开蓝牙
                    BlueToothManager.disConnect(((Stove) device).bleDevice);
                    iterator.remove();
                }
            }
        }
        List<String> names = new ArrayList();

        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && null == ((Pan) device).bleDevice)
                names.add(BlueToothManager.pan);
            else if (device instanceof Stove && null == ((Stove) device).bleDevice)
                names.add(BlueToothManager.stove);
        }
        if (names.size() > 0) {
            BlueToothManager.setScanRule(names.toArray(new String[names.size()]));
            BleVentilator.startScan("", null);
        }
        //订阅设备主题
        subscribeDevice();

        if (null != rvProductsAdapter)
            rvProductsAdapter.setList(AccountInfo.getInstance().deviceList);
    }

    //绑定设备 返回列表中无主设备
    private void bindDevice(List<Device> deviceList) {
        for (Device device: deviceList) {
            if (device.guid.equals(Plat.getPlatform().getDeviceOnlySign())) //已绑定
                return;
        }
        //绑定主设备
        CloudHelper.bindDevice(this, AccountInfo.getInstance().getUser().getValue().id,
                Plat.getPlatform().getDeviceOnlySign(), IDeviceType.RYYJ_ZN, true, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (null != baseResponse)
                            LogUtils.e("绑定成功" + Plat.getPlatform().getDeviceOnlySign());
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
            MqttManager.getInstance().subscribe(device.dc, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_performance) {
            if (!tvPerformance.isSelected()) {

                VentilatorAbstractControl.getInstance().setSmart(VentilatorConstant.FAN_SMART_CLOSE); //智感恒吸关闭
            }
        } else if (id == R.id.tv_comfort) {
            if (!tvComfort.isSelected()) {

                VentilatorAbstractControl.getInstance().setSmart(VentilatorConstant.FAN_SMART_OPEN);  //打开智感恒吸
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
        if (null != homeLock && homeLock.isShow())
            homeLock.dismiss();
    }
    //锁屏清洗提示
    private void lockClean() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_clean_oil_hint);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    screenLock();

                    //打开油网清洗
                    VentilatorAbstractControl.getInstance().openOilClean();
                    //开灯
                    Plat.getPlatform().openWaterLamp();
//                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_CLOSE);
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
    //锁屏
    private void screenLock() {
        if (null == homeLock) {
            homeLock = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_LOCK);
            homeLock.setCancelable(false);
            //长按解锁
            ImageView imageView = homeLock.getRootView().findViewById(R.id.iv_lock);
            ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    homeLock.dismiss();
//                    if (null != rvFunctionAdapter)
//                        rvFunctionAdapter.setPickPosition(-1);
                    //关闭油网清洗
                    VentilatorAbstractControl.getInstance().closeOilClean();
                    //关灯
                    Plat.getPlatform().closeWaterLamp();
                    return true;
                }
            });
        }
        homeLock.show();
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
    //删除设备。保留子设备
    private void clearDevice() {
        Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device instanceof Pan || device instanceof Stove)
                continue;
            iterator.remove();
        }
    }
}