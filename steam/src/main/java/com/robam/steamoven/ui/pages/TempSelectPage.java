package com.robam.steamoven.ui.pages;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.List;

public class TempSelectPage extends SteamBasePage {
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    private RvTimeAdapter rvTempAdapter;
    private TabLayout.Tab tab;

    private IModeSelect iModeSelect;
    //当前模式
    private ModeBean curMode;
    //当前温度
    private String curTemp;
    int defaultValue;


    public TempSelectPage(TabLayout.Tab tab, ModeBean modeBean) {
        this.tab = tab;
        this.curMode = modeBean;
    }
    public TempSelectPage(TabLayout.Tab tab, ModeBean modeBean,int defaultValue) {
        this.tab = tab;
        this.curMode = modeBean;
        this.defaultValue = defaultValue;
    }



    public void setTempList(List<String> selectList, int offset) {
        if (null == rvTempAdapter)
            return;

        rvTempAdapter.setList(selectList);
        int position = offset;
        if(needLoop){
             position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%selectList.size() + offset;
        }
        pickerLayoutManager.scrollToPosition(position);
        rvTempAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
            textView.setText(rvTempAdapter.getItem(position));
        }
        //默认温度
        curTemp = rvTempAdapter.getItem(position);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect = findViewById(R.id.rv_select);
        rvSelect.setAdapter(rvTempAdapter);
        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);
    }

    @Override
    protected void initData() {
        rvTempAdapter = new RvTimeAdapter(0);

        updateTempTab(curMode);
//        if (null != iModeSelect)
//            iModeSelect.updateTab(Stove.getInstance().workMode);
    }

    public void updateTempTab(ModeBean modeBean) {
        if (null == rvTempAdapter)
            return;
        rvSelect.setAdapter(rvTempAdapter);
        //煎炸温度
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = modeBean.minTemp; i <= modeBean.maxTemp; i++) {
            tempList.add(i + "");
        }
        rvTempAdapter.setList(tempList);

        int offset = modeBean.defTemp - modeBean.minTemp;
        if(defaultValue > 0){
            offset = defaultValue - modeBean.minSteam;
        }
        int position = offset;
        if(needLoop){
            position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%tempList.size() + offset;
        }
        pickerLayoutManager.scrollToPosition(position);
        rvTempAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
            textView.setText(rvTempAdapter.getItem(position));
        }
        //默认温度
        curTemp = rvTempAdapter.getItem(position);
    }
    //获取当前温度
    public String getCurTemp() {
        return curTemp;
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
                        rvTempAdapter.setPickPosition(position);
                        curTemp = rvTempAdapter.getItem(position);
                        if (null != tab) {
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvTempAdapter.getItem(position));
                        }
                        if(iModeSelect != null){
                            iModeSelect.updateTab(Integer.parseInt(rvTempAdapter.getItem(position)));
                        }
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }

    public void setModeSelect(IModeSelect iModeSelect) {
        this.iModeSelect = iModeSelect;
    }
}
