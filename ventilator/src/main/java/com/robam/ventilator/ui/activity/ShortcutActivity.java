package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.Device;
import com.robam.ventilator.bean.VenFunBean;
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
            }
        });
        //设备
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(new Device("油烟机", "5068s"));
        deviceList.add(new Device("油烟机", "5068s"));

        List<Device> deviceList2 = new ArrayList<>();
        deviceList2.add(new Device("油烟机", "5068s"));
        deviceList2.add(new Device("洗碗机", "5068s"));
        deviceList2.add(new Device("洗碗机", "5068s"));

        rvShortcutWorkAdapter = new RvShortcutDeviceAdapter();
        rvDeviceWork.setAdapter(rvShortcutWorkAdapter);
        rvShortcutWorkAdapter.setList(deviceList);

        rvShortcutOnlineAdapter = new RvShortcutDeviceAdapter();
        rvDevideOnline.setAdapter(rvShortcutOnlineAdapter);
        rvShortcutOnlineAdapter.setList(deviceList2);

        setOnClickListener(R.id.activity_short);
        rvShortcutOnlineAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Activity activity = CabinetActivityManager.getInstance().currentActivity();
                if (null != activity) {
                    startActivity(activity.getClass());
                }
            }
        });
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