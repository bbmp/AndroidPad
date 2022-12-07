package com.robam.stove.ui.pages;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.constant.StoveConstant;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.ModeBean;
import com.robam.stove.ui.adapter.RvTempAdapter;
import com.robam.stove.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.List;

public class TempSelectPage extends StoveBasePage {
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    private RvTempAdapter rvTempAdapter;
    private TabLayout.Tab tab;

//    private IModeSelect iModeSelect;
    //当前模式
    private ModeBean curMode;
    //当前温度
    private String curTemp;

    public TempSelectPage(TabLayout.Tab tab, ModeBean modeBean) {
        this.tab = tab;
        this.curMode = modeBean;
    }

    public void setTempList(List<String> selectList, int offset) {
        if (null == rvTempAdapter)
            return;
        rvSelect.setAdapter(rvTempAdapter);

        rvTempAdapter.setList(selectList);

        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%selectList.size() + offset;
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
        return R.layout.stove_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect = findViewById(R.id.rv_select);

        //设置选择recycleView的layoutManage
        setLayoutManage(3, 0.44f);
    }

    @Override
    protected void initData() {
        rvTempAdapter = new RvTempAdapter();

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
        tempList.add("低温");
        tempList.add("中温");
        tempList.add("高温");

        rvTempAdapter.setList(tempList);
        int offset = modeBean.defTemp - modeBean.minTemp;

        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%tempList.size() + offset;
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
    //获取子模式
    public int getSubMode() {
        int position = 0;
        if (null != rvTempAdapter)
            position = rvTempAdapter.getPickPosition();
        if (position == 1) //中温
            return StoveConstant.SUBMODE_MID;
        if (position == 2) //高温
            return StoveConstant.SUBMODE_HIGH;
        return StoveConstant.SUBMODE_LOW;    //低温
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

                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }
}
