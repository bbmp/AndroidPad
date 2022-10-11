package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.cabinet.bean.Cabinet;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.constant.ComnConstant;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.common.device.subdevice.Pan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.device.subdevice.Stove;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.Device;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.bean.Ventilator;
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        //获取px
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_38)));

        rvDeviceWork.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        rvDevideOnline.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    @Override
    protected void initData() {
        //主功能
        List<VenFunBean> funList = new ArrayList<>();
        funList.add(new VenFunBean(1, "fun1", "logo_roki", R.drawable.ventilator_gear_weak, "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", R.drawable.ventilator_gear_medium, "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", R.drawable.ventilator_gear_max, "into"));
        rvShortcutFunAdapter = new RvShortcutFunAdapter();
        recyclerView.setAdapter(rvShortcutFunAdapter);
        rvShortcutFunAdapter.setList(funList);
        rvShortcutFunAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                finish();
            }
        });
        //设备
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(new Ventilator("油烟机", IDeviceType.RYYJ, "5068s"));
        for (Device device: AccountInfo.getInstance().deviceList)
            deviceList.add(device);
//        deviceList.add(new Cabinet("消毒柜", IDeviceType.RXDG, "XG858"));

//
        List<Device> deviceList2 = new ArrayList<>();
//        deviceList2.add(new Pan("无人锅", IDeviceType.RZNG, "KP100"));
//        deviceList2.add(new Stove("灶具", IDeviceType.RRQZ, "9B328"));
//        deviceList2.add(new SteamOven("一体机", IDeviceType.RZKY, "CQ928"));
//        deviceList2.add(new DishWasher("洗碗机", IDeviceType.RXWJ, "WB758"));

        getDeviceInfo(AccountInfo.getInstance().getUser().getValue());

        rvShortcutWorkAdapter = new RvShortcutDeviceAdapter();
        rvDeviceWork.setAdapter(rvShortcutWorkAdapter);
        rvShortcutWorkAdapter.setList(deviceList);

        rvShortcutOnlineAdapter = new RvShortcutDeviceAdapter();
        rvDevideOnline.setAdapter(rvShortcutOnlineAdapter);
        rvShortcutOnlineAdapter.setList(deviceList2);

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
    }
    //快捷入口跳转
    private void shortCutForward(@NonNull BaseQuickAdapter<?, ?> adapter, int position) {
        Device device = (Device) adapter.getItem(position);
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
        startActivity(intent);
        finish();
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
        List<Device> deviceList = new ArrayList<>();
        if (null != userInfo) {
            CloudHelper.getDevices(this, userInfo.id, GetDeviceRes.class, new RetrofitCallback<GetDeviceRes>() {
                @Override
                public void onSuccess(GetDeviceRes getDeviceRes) {
                    if (null != getDeviceRes && null != getDeviceRes.devices) {
                        List<Device> deviceList = getDeviceRes.devices;
                        for (Device device: deviceList) {
                            if (IDeviceType.RYYJ.equals(device.dc))    //烟机
                                deviceList.add(new Ventilator(device));
                            else if (IDeviceType.RZKY.equals(device.dc)) //一体机
                                deviceList.add(new SteamOven(device));
                            else if (IDeviceType.RXWJ.equals(device.dc)) //洗碗机
                                deviceList.add(new DishWasher(device));
                            else if (IDeviceType.RXDG.equals(device.dc))  //消毒柜
                                deviceList.add(new Cabinet(device));
                            List<Device> subDevices = device.subDevices;  //子设备
                            if (null != subDevices) {
                                for (Device subDevice : subDevices) {
                                    if (IDeviceType.RZNG.equals(subDevice.dc)) //锅
                                        deviceList.add(new Pan(subDevice));
                                    else if (IDeviceType.RRQZ.equals(subDevice.dc))
                                        deviceList.add(new Stove(subDevice));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                }
            });
        } else {
            //未登录只展示烟机
            deviceList.add(new Ventilator("油烟机", IDeviceType.RYYJ, "5068s"));
        }
    }
}