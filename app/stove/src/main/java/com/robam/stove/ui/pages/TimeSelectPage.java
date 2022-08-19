package com.robam.stove.ui.pages;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
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

    private RvTimeAdapter rvTimeAdapter;

    private TabLayout.Tab tab;

    private int offset;//偏移

    private String mode;

    private IModeSelect iModeSelect;

    public TimeSelectPage(TabLayout.Tab tab, IModeSelect iModeSelect) {
        this.tab = tab;
        this.iModeSelect = iModeSelect;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setList(List<String> selectList, int offset) {
        if (null == rvTimeAdapter)
            return;

        rvTimeAdapter.setList(selectList);
        this.offset = offset;

        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%selectList.size() + offset;
        pickerLayoutManager.scrollToPosition(position);
        rvTimeAdapter.setPickPosition(position);
        if (null != tab) {
            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
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
        rvTimeAdapter = new RvTimeAdapter();
        rvSelect.setAdapter(rvTimeAdapter);

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
