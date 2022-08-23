package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.Device;
import com.robam.ventilator.ui.adapter.RvAddDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

//添加设备入口
public class AddDeviceMainActivity extends VentilatorBaseActivity {
    private RecyclerView rvDevice;
    private RvAddDeviceAdapter rvAddDeviceAdapter;

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
                    //判断设备类型

                    //开始配网流程
                    startActivity(MatchNetworkActivity.class);
                }
            }
        });
    }

    @Override
    protected void initData() {
        List<Device> deviceList2 = new ArrayList<>();
        deviceList2.add(new Device("明火自动翻炒锅", "KP100"));
        deviceList2.add(new Device("燃气灶", "9B328"));
        deviceList2.add(new Device("洗碗机", "WB758"));
        deviceList2.add(new Device("消毒柜", "XG858"));
        deviceList2.add(new Device("蒸烤一体机", "CQ928"));
        rvAddDeviceAdapter.setList(deviceList2);
    }
}