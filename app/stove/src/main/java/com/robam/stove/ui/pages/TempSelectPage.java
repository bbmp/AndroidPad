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

public class TempSelectPage extends StoveBasePage {
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    private RvTimeAdapter rvTempAdapter;
    private TabLayout.Tab tab;

    private IModeSelect iModeSelect;

    public TempSelectPage(TabLayout.Tab tab, IModeSelect iModeSelect) {
        this.tab = tab;
        this.iModeSelect = iModeSelect;
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
        rvTempAdapter = new RvTimeAdapter(1);


//        if (null != iModeSelect)
//            iModeSelect.updateTab(Stove.getInstance().workMode);
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
