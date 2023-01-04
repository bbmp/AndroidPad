package com.robam.steamoven.ui.pages;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SteamSelectPage extends SteamBasePage {
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    private RvTimeAdapter rvSteamAdapter;
    private TabLayout.Tab tab;
    //当前模式
    private ModeBean curMode;
    //当前蒸汽
    private String curSteam;
    private int defValue = -1;
    private ArrayList<String> steamList = new ArrayList<String>() {
        {
            add("小");
            add("中");
            add("大");
        }
    };





    public void setSteamValue(String[] values){
        steamList.clear();
        steamList.addAll(Arrays.asList(values));
        if(rvSteamAdapter != null){
            rvSteamAdapter.setList(steamList);
            rvSteamAdapter.notifyDataSetChanged();
        }
    }

    public SteamSelectPage(TabLayout.Tab tab, ModeBean curMode) {
        this.tab = tab;
        this.curMode = curMode;
    }

    public SteamSelectPage(TabLayout.Tab tab, ModeBean curMode,int defValue) {
        this.tab = tab;
        this.curMode = curMode;
        this.defValue = defValue;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect = findViewById(R.id.rv_select);

        //设置选择recycleView的layoutManage
        setLayoutManage(3, 0.44f);
    }

    @Override
    protected void initData() {
        rvSteamAdapter = new RvTimeAdapter(2);
        rvSelect.setAdapter(rvSteamAdapter);
        rvSteamAdapter.setList(steamList);

        updateSteamTab(curMode);
    }

    public void updateSteamTab(ModeBean modeBean) {
        if (null == rvSteamAdapter)
            return;

        int offset = modeBean.defSteam - modeBean.minSteam;
        if(defValue != -1){
            offset = defValue - modeBean.minSteam;
        }
        int position = offset;
        if(needLoop){
             position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%steamList.size() + offset;
        }
        pickerLayoutManager.scrollToPosition(position);
        rvSteamAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
//            tab.getCustomView().findViewById(R.id.tv_time).setVisibility(View.VISIBLE);
//            tab.getCustomView().findViewById(R.id.tv_temp).setVisibility(View.GONE);
            textView.setText(rvSteamAdapter.getItem(position));
        }
        //默认steam
        curSteam = rvSteamAdapter.getItem(position);
    }

    /**
     * 设置layout
     *
     * @param maxItem
     * @param scale
     */
    private void setLayoutManage(int maxItem, float scale) {
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(maxItem)
                .setScale(scale)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        //指示器更新
                        rvSteamAdapter.setPickPosition(position);
                        curSteam = rvSteamAdapter.getItem(position);
                        if (null != tab) {
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvSteamAdapter.getItem(position));
                        }

                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }
}
