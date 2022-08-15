package com.robam.steamoven.ui.pages;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvStringAdapter;

import java.util.ArrayList;
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

    private RvStringAdapter rvStringAdapter;
    /**
     * 选中的模式
     */
    private ModeBean modeBean;
    private List<String> selectList = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_mode_select;
    }

    @Override
    protected void initView() {
        rvSelect2 = findViewById(R.id.rv_select_2);
        rvDot = findViewById(R.id.rv_dot);

        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.66f);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));


        rvStringAdapter = new RvStringAdapter();
        rvSelect2.setAdapter(rvStringAdapter);
    }

    @Override
    protected void initData() {
//获取当前功能下的模式
//        funBean = (FuntionBean) getIntent().getParcelableExtra(Constant.FUNTION_BEAN);
//        if (funBean != null && funBean.mode != null) {
//            //辅助模式取消预约入口
//            if (funBean.mode.equals("auxmode")) {
//
//            }
//            modes = FuntionModeManage.getMode(this, funBean.mode);
            rvStringAdapter = new RvStringAdapter();
            rvSelect2.setAdapter(rvStringAdapter);
//            if (mode)
            selectList.add("鲜嫩蒸");
        selectList.add("营养蒸");
        selectList.add("高温蒸");
        selectList.add("澎湃蒸");
            rvStringAdapter.setList(selectList);


            rvDotAdapter = new RvDotAdapter();
            rvDot.setAdapter(rvDotAdapter);

            rvDotAdapter.setList(selectList);
            rvDotAdapter.setPickPosition(Integer.MAX_VALUE / 2);

            pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
            rvStringAdapter.setPickPosition(Integer.MAX_VALUE / 2);
//            modeBean = rvModeAdapter.getItem(rvModeAdapter.getIndex());
//            setFootData(modeBean);
//            initParameter(modeBean);
//        }
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
                        rvStringAdapter.setPickPosition(position);

                    }
                })
                .build();
        rvSelect2.setLayoutManager(pickerLayoutManager);
    }
}
