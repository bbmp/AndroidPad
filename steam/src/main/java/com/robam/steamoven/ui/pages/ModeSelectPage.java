package com.robam.steamoven.ui.pages;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvModeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectPage extends SteamBasePage {
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

    //初始模式
    private List<ModeBean> selectList;

    public ModeSelectPage(TabLayout.Tab tab, List<ModeBean> selectList, IModeSelect iModeSelect) {
        this.tab = tab;
        this.selectList = selectList;
        this.iModeSelect = iModeSelect;
    }

    public void setList(List<ModeBean> selectList) {
        rvModeAdapter.setList(selectList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size();
        pickerLayoutManager.scrollToPosition(initPos);
        rvModeAdapter.setPickPosition(initPos);

        if (null != iModeSelect)
            iModeSelect.updateTab(rvModeAdapter.getItem(initPos).code);
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
        rvModeAdapter = new RvModeAdapter();
        rvSelect.setAdapter(rvModeAdapter);

        //默认模式
        if (null != selectList)
            setList(selectList);
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
                            textView.setText(rvModeAdapter.getItem(position).name);
                        }
                        if (null != iModeSelect) {
                            iModeSelect.updateTab( rvModeAdapter.getItem(position).code);
                        }
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }

}
