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
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvModeAdapter;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;

import java.util.ArrayList;
import java.util.List;

public class TimeSelectPage extends SteamBasePage {
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect2;
    /**
     * 指示器
     */
    private RecyclerView rvDot;

    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private RvTimeAdapter rvTimeAdapter;

    private TabLayout.Tab tab;

    private int type;//时间or温度

    private ModeBean modeBean; //当前模式

    public TimeSelectPage(TabLayout.Tab tab, int type, ModeBean bean) {
        this.tab = tab;
        this.type = type;
        this.modeBean = bean;
    }


    public void setList(List<String> selectList, int offset) {
        rvTimeAdapter.setList(selectList);

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
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect2 = findViewById(R.id.rv_select_2);
        rvDot = findViewById(R.id.rv_dot);

        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);

    }

    @Override
    protected void initData() {

        rvTimeAdapter = new RvTimeAdapter(type);
        rvSelect2.setAdapter(rvTimeAdapter);

        initList(modeBean);
    }

    private void initList(ModeBean modeBean) {
        if (null != modeBean) {
            ArrayList<String> list = new ArrayList<>();
            if (type == 0) {
                for (int i = modeBean.minTemp; i <= modeBean.maxTemp; i++) {
                    list.add(i + "");
                }
                setList(list, modeBean.defTemp - modeBean.minTemp);
            } else {
                for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
                    list.add(i + "");
                }
                setList(list, modeBean.defTime - modeBean.minTime);
            }

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
                        //指示器更新
                        rvTimeAdapter.setPickPosition(position);
                        if (null != tab) {
                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                            textView.setText(rvTimeAdapter.getItem(position));
                        }
                    }
                })
                .build();
        rvSelect2.setLayoutManager(pickerLayoutManager);
    }
}
