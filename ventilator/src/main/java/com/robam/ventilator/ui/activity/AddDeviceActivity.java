package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.robam.cabinet.bean.Cabinet;
import com.robam.common.IDeviceType;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.common.device.subdevice.Pan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.common.device.subdevice.Stove;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.ui.adapter.RvAddDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

//添加设备入口
public class AddDeviceActivity extends VentilatorBaseActivity {
    private RecyclerView rvDevice;
    private RvAddDeviceAdapter rvAddDeviceAdapter;
    private List<Device> deviceList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_add_device;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        rvDevice = findViewById(R.id.rv_device);
        rvDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvAddDeviceAdapter = new RvAddDeviceAdapter();
        rvDevice.setAdapter(rvAddDeviceAdapter);

        rvAddDeviceAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.btn_add) {
                    //添加设备
                    Device device = (Device) adapter.getItem(position);
                    //判断设备类型 非锅和灶判断是否登录
                    if (!"9B328".equals(device.getDisplayType()) &&
                            !"KP100".equals(device.getDisplayType())) {
                        if (null == AccountInfo.getInstance().getUser().getValue()) {
                            startActivity(LoginPhoneActivity.class);
                            return;
                        }
                    }
                    Intent intent = new Intent();
                    intent.putExtra(VentilatorConstant.EXTRA_MODEL, device.dc);
                    intent.setClass(AddDeviceActivity.this, MatchNetworkActivity.class);
                    //开始配网流程
                    startActivity(intent);
                }
            }
        });
        //监听锅和灶
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s)) {
                        if (device instanceof Pan) {
                            List<Device> deviceList2 = new ArrayList<>();
                            for (Device device1: deviceList) {
                                if (device1.dc.equals(IDeviceType.RZNG)) //删除锅
                                    continue;
                                deviceList2.add(device1);
                            }
                            rvAddDeviceAdapter.setList(deviceList2);

                        } else if (device instanceof Stove) {
                            List<Device> deviceList2 = new ArrayList<>();
                            for (Device device1: deviceList) {
                                if (device1.dc.equals(IDeviceType.RRQZ)) //删除灶
                                    continue;
                                deviceList2.add(device1);
                            }
                            rvAddDeviceAdapter.setList(deviceList2);
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        boolean hasPan = false;
        boolean hasStove = false;
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan)
                hasPan = true;
            else if (device instanceof Stove)
                hasStove = true;
        }
        if (!hasPan)
            deviceList.add(new Pan("明火自动翻炒锅", IDeviceType.RZNG, "KP100"));
        if (!hasStove)
            deviceList.add(new Stove("燃气灶", IDeviceType.RRQZ, "9B328"));
        deviceList.add(new DishWasher("洗碗机", IDeviceType.RXWJ, "WB758"));
        deviceList.add(new Cabinet("消毒柜", IDeviceType.RXDG, "XG858"));
        deviceList.add(new SteamOven("蒸烤一体机", IDeviceType.RZKY, "CQ928"));
        rvAddDeviceAdapter.setList(deviceList);
    }
}