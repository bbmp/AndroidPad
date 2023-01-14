package com.robam.steamoven.ui.pages;

import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.robam.common.ui.IModeSelect;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.LogUtils;
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
    private RecyclerView rvSelect,rvDot;


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
    private int selectIndex = -1;
    private RvDotAdapter rvDotAdapter;

    public ModeSelectPage(TabLayout.Tab tab, List<ModeBean> selectList, IModeSelect iModeSelect) {
        this.tab = tab;
        this.selectList = selectList;
        this.iModeSelect = iModeSelect;
    }

    public ModeSelectPage(TabLayout.Tab tab, List<ModeBean> selectList, IModeSelect iModeSelect,int selectIndex) {
        this.tab = tab;
        this.selectList = selectList;
        this.iModeSelect = iModeSelect;
        this.selectIndex = selectIndex;
    }

    public void setList(List<ModeBean> selectList) {
        rvModeAdapter.setList(selectList);

        //初始位置
        int initPos = selectIndex >= 0 ? selectIndex : 0;
        if(needLoop){
             initPos = selectIndex != -1 ? selectIndex : (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size());
        }
        List<String> dotList = new ArrayList<>();
        for (ModeBean modeBean: selectList) {
            dotList.add(modeBean.name+modeBean.code);
        }
        rvDotAdapter.setList(dotList);
        rvDotAdapter.setPickPosition(initPos);

        pickerLayoutManager.scrollToPosition(initPos);
        rvModeAdapter.setPickPosition(initPos);
        LogUtils.i("setLayoutManage setList "+initPos);
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
        rvDot = findViewById(R.id.rv_dot);
        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);

    }

    @Override
    protected void initData() {
        rvModeAdapter = new RvModeAdapter();
        rvDotAdapter = new RvDotAdapter();
        rvSelect.setAdapter(rvModeAdapter);
        rvDot.setAdapter(rvDotAdapter);

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
                .setOnPickerListener((recyclerView, position) -> {
                    //rvModeAdapter.setPickPosition(position);
                    if (null != tab) {
                        //切换模式
                        TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
                        textView.setText(rvModeAdapter.getItem(position).name);
                    }
                    if (null != iModeSelect) {
                        iModeSelect.updateTab( rvModeAdapter.getItem(position).code);
                    }
                    rvDotAdapter.setPickPosition(position);
                    //LogUtils.i("setLayoutManage setOnPickerListener "+position);
                }).setOnSlideListener((recyclerView, position) -> {
                    rvDotAdapter.setPickPosition(position);
                    //LogUtils.i("setLayoutManage setOnSlideListener "+position);
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);

        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));


    }

}
