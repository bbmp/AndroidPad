package com.robam.steamoven.ui.pages;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.serialport.helper.SerialPortHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.clj.fastble.BleManager;
import com.robam.common.manager.FunctionManager;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.manager.DataInitManage;
import com.robam.steamoven.manager.FuntionModeManage;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvMainFuntionAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends SteamBasePage {

    private RecyclerView rvMain, rvDot;
    private RvMainFuntionAdapter rvMainFuntionAdapter;
    private RvDotAdapter rvDotAdapter;
    private RelativeLayout llMain;
    private PickerLayoutManager pickerLayoutManager;
    private ImageView imageView;

    public static HomePage newInstance() {
        return new HomePage();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();

        rvMain = findViewById(R.id.rv_main);
        rvDot = findViewById(R.id.rv_dot);
        imageView = findViewById(R.id.iv_bg);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
//                        rvMainFuntionAdapter.setIndex(position);
                        setBackground(position);
                        rvDotAdapter.setPickPosition(position);
                        rvMainFuntionAdapter.setPickPosition(position);
                    }
                })
                .build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFuntionAdapter = new RvMainFuntionAdapter();
        rvMainFuntionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                scollToPosition(position);
                Intent intent = new Intent();
                FuntionBean funtionBean = (FuntionBean) adapter.getItem(position);
                intent.putExtra(SteamConstant.EXTRA_MODE_LIST, funtionBean.mode);
                if (funtionBean.into == null || funtionBean.into.length() == 0) {
                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
                    return;
                }
                intent.setClassName(getContext(), funtionBean.into);
                HomeSteamOven.getInstance().funCode = (short) funtionBean.funtionCode;
                startActivity(intent);
            }

        });
        rvMain.setAdapter(rvMainFuntionAdapter);
        rvDotAdapter = new RvDotAdapter();
        rvDot.setAdapter(rvDotAdapter);
        //设置滑动速度
//        setMaxFlingVelocity(rvMain, 3000);
    }

    @Override
    protected void initData() {
//        List<FuntionBean> funtionBeans = FuntionModeManage.getFuntionList(getContext());
        List<FuntionBean> funtionBeans = FunctionManager.getFuntionList(getContext(), FuntionBean.class, R.raw.steam);
        rvMainFuntionAdapter.setList(funtionBeans);
        List<String> dotList = new ArrayList<>();
        for (FuntionBean funtionBean: funtionBeans) {
            dotList.add(funtionBean.funtionName);
        }

        rvDotAdapter.setList(dotList);
        rvDotAdapter.setPickPosition(Integer.MAX_VALUE / 2);
        pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
        setBackground(Integer.MAX_VALUE / 2);
        if (!MMKVUtils.isInitData()) {
            DataInitManage.savaRecipe(getContext());
        }
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
        int resId = getResources().getIdentifier(rvMainFuntionAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        ImageUtils.loadGif(getContext(), resId, imageView);
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
    }
}