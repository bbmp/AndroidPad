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
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.pan.bean.Pan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.stove.bean.Stove;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.Device;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.bean.Ventilator;
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
        deviceList.add(new Cabinet("消毒柜", IDeviceType.SERIES_CABINET));
        deviceList.add(new Ventilator("油烟机", IDeviceType.SERIES_VENTILATOR));

        List<Device> deviceList2 = new ArrayList<>();
        deviceList2.add(new Pan("无人锅", IDeviceType.SERIES_PAN));
        deviceList2.add(new Stove("灶具", IDeviceType.SERIES_STOVE));
        deviceList2.add(new SteamOven("一体机", IDeviceType.SERIES_STEAM));
        deviceList2.add(new DishWasher("洗碗机", IDeviceType.SERIES_DISHWASHER));

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
        if (IDeviceType.SERIES_DISHWASHER.equals(device.getDisplayType()))
            intent.setClassName(ShortcutActivity.this, "com.robam.dishwasher.ui.activity.MainActivity");
        else if (IDeviceType.SERIES_STEAM.equals(device.getDisplayType()))
            intent.setClassName(ShortcutActivity.this, "com.robam.steamoven.ui.activity.MainActivity");
        else if (IDeviceType.SERIES_STOVE.equals(device.getDisplayType()))
            intent.setClassName(ShortcutActivity.this, "com.robam.stove.ui.activity.MainActivity");
        else if (IDeviceType.SERIES_PAN.equals(device.getDisplayType()))
            intent.setClassName(ShortcutActivity.this, "com.robam.pan.ui.activity.MainActivity");
        else if (IDeviceType.SERIES_CABINET.equals(device.getDisplayType()))
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
}