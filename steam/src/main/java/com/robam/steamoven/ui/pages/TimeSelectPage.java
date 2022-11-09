package com.robam.steamoven.ui.pages;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.List;

public class TimeSelectPage extends SteamBasePage {
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private RvTimeAdapter rvTimeAdapter;

    private TabLayout.Tab tab;
    //当前模式
    private ModeBean curMode;

    //当前时间
    private String curTime;

//    private IModeSelect iModeSelect;

    public TimeSelectPage(TabLayout.Tab tab, ModeBean modeBean) {
        this.tab = tab;
        this.curMode = modeBean;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect = findViewById(R.id.rv_select);

        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);
    }

    @Override
    protected void initData() {
        rvTimeAdapter = new RvTimeAdapter(1);
        rvSelect.setAdapter(rvTimeAdapter);

        updateTimeTab(curMode);
//        if (null != iModeSelect)
//            iModeSelect.updateTab(Stove.getInstance().workMode);
    }

    public void updateTimeTab(ModeBean modeBean) {
        if (null == rvTimeAdapter)
            return;

        ArrayList<String> timeList = new ArrayList<>();
        for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
            timeList.add(i + "");
        }

        rvTimeAdapter.setList(timeList);
        int offset = modeBean.defTime - modeBean.minTime;
        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%timeList.size() + offset;
        pickerLayoutManager.scrollToPosition(position);
        rvTimeAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
//            tab.getCustomView().findViewById(R.id.tv_time).setVisibility(View.VISIBLE);
//            tab.getCustomView().findViewById(R.id.tv_temp).setVisibility(View.GONE);
            textView.setText(rvTimeAdapter.getItem(position));
        }
        //默认时间
        curTime = rvTimeAdapter.getItem(position);
    }
    //获取当前时间
    public String getCurTime() {
        return curTime;
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
                .setOnPickerListener((recyclerView, position) -> {
                    //指示器更新
                    rvTimeAdapter.setPickPosition(position);
                    curTime = rvTimeAdapter.getItem(position);
                    if (null != tab) {
                        TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                        textView.setText(rvTimeAdapter.getItem(position));
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }
}
