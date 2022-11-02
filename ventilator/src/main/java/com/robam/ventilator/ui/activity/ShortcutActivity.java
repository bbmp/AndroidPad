package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.module.IPublicCabinetApi;
import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.module.IPublicSteamApi;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.common.device.subdevice.Pan;
import com.robam.pan.manager.PanActivityManager;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.manager.StoveActivityManager;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.Device;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.ui.adapter.RvShortcutFunAdapter;
import com.robam.ventilator.ui.adapter.RvShortcutDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

//快捷入口
public class ShortcutActivity extends VentilatorBaseActivity {
    /**
     * 主功能
     */
    private TextView tvWork, tvOnline;

    private ImageView ivWork, ivOnline;

    private RecyclerView recyclerView;

    private RvShortcutFunAdapter rvShortcutFunAdapter;

    private RecyclerView rvDeviceWork, rvDevideOnline;

    private RvShortcutDeviceAdapter rvShortcutWorkAdapter, rvShortcutOnlineAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_shortcut;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.rv_fun);
        rvDeviceWork = findViewById(R.id.rv_device_work);
        rvDevideOnline = findViewById(R.id.rv_device_online);
        tvWork = findViewById(R.id.tv_work);
        tvOnline = findViewById(R.id.tv_online);
        ivWork = findViewById(R.id.iv_work);
        ivOnline = findViewById(R.id.iv_online);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        //获取px
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_38)));

        rvDeviceWork.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        rvDevideOnline.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvShortcutFunAdapter = new RvShortcutFunAdapter();
        recyclerView.setAdapter(rvShortcutFunAdapter);
        //烟机状态
        if (HomeVentilator.getInstance().gear == (byte) 0xA1)
            rvShortcutFunAdapter.setPickPosition(0);
        else if (HomeVentilator.getInstance().gear == (byte) 0xA3)
            rvShortcutFunAdapter.setPickPosition(1);
        else if (HomeVentilator.getInstance().gear == (byte) 0xA6)
            rvShortcutFunAdapter.setPickPosition(2);
        else
            rvShortcutFunAdapter.setPickPosition(-1);
    }

    @Override
    protected void initData() {
        //主功能
        List<VenFunBean> funList = new ArrayList<>();
        funList.add(new VenFunBean(1, "fun1", "logo_roki", R.drawable.ventilator_gear_weak, "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", R.drawable.ventilator_gear_medium, "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", R.drawable.ventilator_gear_max, "into"));

        rvShortcutFunAdapter.setList(funList);
        rvShortcutFunAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == rvShortcutFunAdapter.getPickPosition()) {//已经选中了
                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_CLOSE);
                } else if (position == 0) {
                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_WEAK);
                } else if (position == 1) {
                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_MID);
                } else if (position == 2) {
                    VentilatorAbstractControl.getInstance().setFanGear(VentilatorConstant.FAN_GEAR_FRY);
                }
                finish();
            }
        });
        //设备
        rvShortcutWorkAdapter = new RvShortcutDeviceAdapter();
        rvDeviceWork.setAdapter(rvShortcutWorkAdapter);

        rvShortcutOnlineAdapter = new RvShortcutDeviceAdapter();
        rvDevideOnline.setAdapter(rvShortcutOnlineAdapter);

        setOnClickListener(R.id.activity_short);
        //工作设备
        rvShortcutWorkAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                shortCutForward(adapter, position);
            }
        });
        //在线设备
        rvShortcutOnlineAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                shortCutForward(adapter, position);
            }
        });
        getDeviceInfo(AccountInfo.getInstance().getUser().getValue());
    }
    //快捷入口跳转
    private void shortCutForward(@NonNull BaseQuickAdapter<?, ?> adapter, int position) {
        Device device = (Device) adapter.getItem(position);
        if (device.guid.equals(AccountInfo.getInstance().topGuid)) { //当前设备跳转当前设备
            finish();
            return;
        }
        //设备间切换
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ComnConstant.EXTRA_GUID, device.guid); //传入启动设备
        if (IDeviceType.RXWJ.equals(device.dc))
            intent.setClassName(ShortcutActivity.this, "com.robam.dishwasher.ui.activity.MainActivity");
        else if (IDeviceType.RZKY.equals(device.dc))
            intent.setClassName(ShortcutActivity.this, "com.robam.steamoven.ui.activity.MainActivity");
        else if (IDeviceType.RRQZ.equals(device.dc))
            intent.setClassName(ShortcutActivity.this, "com.robam.stove.ui.activity.MainActivity");
        else if (IDeviceType.RZNG.equals(device.dc))
            intent.setClassName(ShortcutActivity.this, "com.robam.pan.ui.activity.MainActivity");
        else if (IDeviceType.RXDG.equals(device.dc))
            intent.setClassName(ShortcutActivity.this, "com.robam.cabinet.ui.activity.MainActivity");
        else
            intent.setClassName(ShortcutActivity.this, "com.robam.ventilator.ui.activity.HomeActivity");
        //清空所有任务栈，除烟机
        StoveActivityManager.getInstance().finishAllActivity();
        PanActivityManager.getInstance().finishAllActivity();
        CabinetActivityManager.getInstance().finishAllActivity();

        startActivity(intent);
        AccountInfo.getInstance().topGuid = device.guid; //切换后的设备

//        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;  //屏蔽返回键
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.activity_short)
            finish();
    }

    private void getDeviceInfo(UserInfo userInfo) {
        List<Device> workList = new ArrayList<>();
        List<Device> onlineList = new ArrayList<>();
        Device ventilator = new Ventilator("油烟机", IDeviceType.RYYJ, "5068s");
        ventilator.guid = Plat.getPlatform().getDeviceOnlySign();
        if (HomeVentilator.getInstance().gear == (byte) 0xA0)
            onlineList.add(ventilator);
        else
            workList.add(ventilator);
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (IDeviceType.RZKY.equals(device.dc) && device.status == Device.ONLINE) {//一体机
                SteamOven steamOven = (SteamOven) device;
                if (steamOven.workStatus != 0)
                    workList.add(steamOven);
                else
                    onlineList.add(steamOven);
            } else if (IDeviceType.RXWJ.equals(device.dc) && device.status == Device.ONLINE) { //洗碗机
                DishWasher dishWasher = (DishWasher) device;
                if (dishWasher.workStatus != 0)
                    workList.add(dishWasher);
                else
                    onlineList.add(dishWasher);
            } else if (IDeviceType.RXDG.equals(device.dc) && device.status == Device.ONLINE) { //消毒柜
                Cabinet cabinet = (Cabinet) device;
                if (cabinet.workStatus != 0)
                    workList.add(cabinet);
                else
                    onlineList.add(cabinet);
            } else if (IDeviceType.RRQZ.equals(device.dc) && device.status == Device.ONLINE) {
                Stove stove = (Stove) device;
                if (stove.leftLevel == 0 && stove.rightLevel == 0)
                    onlineList.add(stove);
                else
                    workList.add(stove);
            } else if (IDeviceType.RZNG.equals(device.dc) && device.status == Device.ONLINE) {
                Pan pan = (Pan) device;
                onlineList.add(pan);
            }
        }
        if (workList.size() == 0) {
            tvWork.setVisibility(View.GONE);
            ivWork.setVisibility(View.GONE);
        } else {
            tvWork.setVisibility(View.VISIBLE);
            ivWork.setVisibility(View.VISIBLE);
        }
        if (null != rvShortcutWorkAdapter)
            rvShortcutWorkAdapter.setList(workList);
        if (onlineList.size() == 0) {
            tvOnline.setVisibility(View.GONE);
            ivOnline.setVisibility(View.GONE);
        } else {
            tvOnline.setVisibility(View.VISIBLE);
            ivOnline.setVisibility(View.VISIBLE);
        }
        //在线设备
        if (null != rvShortcutOnlineAdapter)
            rvShortcutOnlineAdapter.setList(onlineList);
    }
}