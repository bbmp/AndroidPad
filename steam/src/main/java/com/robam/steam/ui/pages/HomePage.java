package com.robam.steam.ui.pages;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.serialport.helper.SerialPortHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.clj.fastble.BleManager;
import com.robam.common.ui.HeadPage;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ToastUtils;
import com.robam.steam.R;
import com.robam.steam.bean.FuntionBean;
import com.robam.steam.constant.Constant;
import com.robam.steam.manager.FuntionModeManage;
import com.robam.steam.ui.adapter.RvMainFuntionAdapter;

import java.lang.reflect.Field;
import java.util.List;

public class HomePage extends HeadPage {

    private RecyclerView rvMain;
    private RvMainFuntionAdapter rvMainFuntionAdapter;
    private RelativeLayout llMain;
    private PickerLayoutManager pickerLayoutManager;

    public static HomePage newInstance() {
        return new HomePage();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_home;
    }

    @Override
    protected void initView() {
        rvMain = findViewById(R.id.rv_main);
        llMain = findViewById(R.id.ll_main);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.3f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
//                        rvMainFuntionAdapter.setIndex(position);
                        setBackground(position);
                    }
                })
                .build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainFuntionAdapter = new RvMainFuntionAdapter();
        rvMainFuntionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                scollToPosition(position);
                Intent intent = new Intent();
//                intent.putExtra(Constant.FUNTION_BEAN, rvMainFuntionAdapter.getItem(position));
//                FuntionBean item = rvMainFuntionAdapter.getItem(position);
//                if (item.into == null || item.into.length() == 0) {
//                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
//                    return;
//                }
//                intent.setAction(item.into);
//                startActivity(intent);
            }

        });
        rvMain.setAdapter(rvMainFuntionAdapter);
        //设置滑动速度
        setMaxFlingVelocity(rvMain, 3000);
//        hideItem2();
    }

    @Override
    protected void initData() {
        List<FuntionBean> funtionBeans = FuntionModeManage.getFuntionList(getContext());
        rvMainFuntionAdapter.setList(funtionBeans);
        pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
        setBackground(Integer.MAX_VALUE / 2);
    }

    @Override
    public void onClick(View view) {
//        keyTone();
    }

    /**
     * 滚动并居中
     */
    private void scollToPosition(int index) {
        pickerLayoutManager.smoothScrollToPosition(rvMain, new RecyclerView.State(), index);
    }

    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
//        int resId = getResources().getIdentifier(rvMainFuntionAdapter.getItem(index).backgroundImg, "drawable", this.getPackageName());
//        setBgImg(this, resId);
//        llMain.setBackgroundResource(resId);
    }

    /**
     * 设置滑动速度 通过反射设置
     *
     * @param recycleview
     * @param velocity
     */
    private void setMaxFlingVelocity(RecyclerView recycleview, int velocity) {
        try {
            Field field = recycleview.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recycleview, velocity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }
}