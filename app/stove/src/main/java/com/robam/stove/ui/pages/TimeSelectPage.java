package com.robam.stove.ui.pages;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.ModeBean;
import com.robam.stove.bean.Stove;
import com.robam.stove.ui.adapter.RvTimeAdapter;

import java.util.List;

public class TimeSelectPage extends StoveBasePage {
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private RvTimeAdapter rvTimeAdapter, rvTempAdapter;

    private TabLayout.Tab tab;

    private int type;//时间or温度

    private IModeSelect iModeSelect;

    public TimeSelectPage(TabLayout.Tab tab, IModeSelect iModeSelect) {
        this.tab = tab;
        this.iModeSelect = iModeSelect;
    }

    //时间设置
    public void setTimeList(List<String> selectList, int offset) {
        if (null == rvTimeAdapter)
            return;
        rvSelect.setAdapter(rvTimeAdapter);

        rvTimeAdapter.setList(selectList);

        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%selectList.size() + offset;
        pickerLayoutManager.scrollToPosition(position);
        rvTimeAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
            tab.getCustomView().findViewById(R.id.tv_time).setVisibility(View.VISIBLE);
            tab.getCustomView().findViewById(R.id.tv_temp).setVisibility(View.GONE);
            textView.setText(rvTimeAdapter.getItem(position));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.stove_page_layout_mode_select;
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
//        rvTempAdapter = new RvTimeAdapter(0);


        if (null != iModeSelect)
            iModeSelect.updateTab(Stove.getInstance().workMode);
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
                        rvTimeAdapter.setPickPosition(position);
                        if (null != tab) {
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvTimeAdapter.getItem(position));
                        }
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }
}
