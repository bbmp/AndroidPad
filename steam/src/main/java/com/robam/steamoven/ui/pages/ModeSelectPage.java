package com.robam.steamoven.ui.pages;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvModeAdapter;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSelectPage extends SteamBasePage {
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect2;
    /**
     * 指示器
     */
    private RecyclerView rvDot;
    /**
     * 指示器adapter
     */
    private RvDotAdapter rvDotAdapter;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private RvModeAdapter rvModeAdapter;
    /**
     * 选中的模式
     */
    private ModeBean modeBean;

    private TabLayout.Tab tab;
    //回调接口
    private IModeSelect iModeSelect;

    private List<ModeBean> selectList;

    public ModeSelectPage(TabLayout.Tab tab, List<ModeBean> selectlist, IModeSelect iModeSelect) {
        this.tab = tab;
        this.selectList = selectlist;
        this.iModeSelect = iModeSelect;
    }

    public void setList(List<ModeBean> selectList) {
        rvModeAdapter.setList(selectList);

        List<String> dotList = new ArrayList<>();
        for (ModeBean bean: selectList)
            dotList.add(bean.name);
        rvDotAdapter.setList(dotList);
        rvDotAdapter.setPickPosition(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());

        pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());
        rvModeAdapter.setPickPosition(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());

    }
    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect2 = findViewById(R.id.rv_select_2);
        rvDot = findViewById(R.id.rv_dot);
        rvDot.setVisibility(View.VISIBLE);

        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        rvDotAdapter = new RvDotAdapter();
        rvDot.setAdapter(rvDotAdapter);
    }

    @Override
    protected void initData() {
//获取当前功能下的模式
        rvModeAdapter = new RvModeAdapter();
        rvSelect2.setAdapter(rvModeAdapter);

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
                        //指示器更新
                        rvDotAdapter.setPickPosition(position);
                        rvModeAdapter.setPickPosition(position);
                        if (null != tab) {
                            //切换模式
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvModeAdapter.getItem(position).name);
                        }
                        if (null != iModeSelect) {
                            iModeSelect.updateTab(rvModeAdapter.getItem(position).code);
                        }
                    }
                })
                .build();
        rvSelect2.setLayoutManager(pickerLayoutManager);
    }

    public interface IModeSelect {
        void updateTab(int mode);
    }
}
