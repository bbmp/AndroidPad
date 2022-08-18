package com.robam.stove.ui.pages;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.ui.adapter.RvModeAdapter;

import java.util.List;

public class ModeSelectPage extends StoveBasePage {
    /**
     * 模式 选择
     */
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    /**
     * 模式选择
     */
    private RvModeAdapter rvModeAdapter;
    //绑定的tab
    private TabLayout.Tab tab;

    //回调接口
    private IModeSelect iModeSelect;

    public ModeSelectPage(TabLayout.Tab tab, IModeSelect iModeSelect) {
        this.tab = tab;
        this.iModeSelect = iModeSelect;
    }

    public void setList(List<String> selectList) {
        rvModeAdapter.setList(selectList);

        pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());
        rvModeAdapter.setPickPosition(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());

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
        rvModeAdapter = new RvModeAdapter();
        rvSelect.setAdapter(rvModeAdapter);

        //默认模式
        if (null != getArguments()) {
            List<String> selectList = getArguments().getStringArrayList("mode");

            setList(selectList);
        }
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

                        rvModeAdapter.setPickPosition(position);
                        if (null != tab) {
                            //切换模式
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvModeAdapter.getItem(position));
                        }
                        if (null != iModeSelect) {
                            iModeSelect.updateTab("mode", rvModeAdapter.getItem(position));
                        }
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }
}
