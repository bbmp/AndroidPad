package com.robam.stove.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.Stove;
import com.robam.stove.bean.StoveFunBean;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.constant.StoveEnum;
import com.robam.stove.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends StoveBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private PickerLayoutManager pickerLayoutManager;
    //浮标
    private ImageView ivFloat;
    private LinearLayout llLeftStove, llRightStove;
    private TextView tvLeftStove, tvRightStove;
    //背景
    private ImageView imageView;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();
        rvMain = findViewById(R.id.rv_main);
        ivFloat = findViewById(R.id.iv_float);
        imageView = findViewById(R.id.iv_bg);
        llLeftStove = findViewById(R.id.ll_left_stove);
        llRightStove = findViewById(R.id.ll_right_stove);
        tvLeftStove = findViewById(R.id.tv_left_stove);
        tvRightStove = findViewById(R.id.tv_right_stove);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvMainFunctionAdapter.setPickPosition(position);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainFunctionAdapter = new RvMainFunctionAdapter();

        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(R.id.iv_float, R.id.ll_left_stove, R.id.ll_right_stove);
    }

    @Override
    protected void initData() {
        List<StoveFunBean> functionList = new ArrayList<>();
        functionList.add(new StoveFunBean(StoveConstant.FUN_SMART, StoveEnum.match(StoveConstant.FUN_SMART), "", "smart", "com.robam.stove.ui.activity.ModeSelectActivity"));
        functionList.add(new StoveFunBean(StoveConstant.FUN_CURVE, StoveEnum.match(StoveConstant.FUN_CURVE), "", "curve", "com.robam.stove.ui.activity.CurveActivity"));
        functionList.add(new StoveFunBean(StoveConstant.FUN_RECIPE, StoveEnum.match(StoveConstant.FUN_RECIPE), "", "recipe", "com.robam.stove.ui.activity.RecipeActivity"));
        functionList.add(new StoveFunBean(StoveConstant.FUN_TIMING, StoveEnum.match(StoveConstant.FUN_TIMING), "", "timing", "com.robam.stove.ui.activity.TimeSelectActivity"));
        rvMainFunctionAdapter.setList(functionList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
        rvMainFunctionAdapter.setPickPosition(initPos);
        pickerLayoutManager.scrollToPosition(initPos);
        setBackground(initPos);

        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                StoveFunBean stoveFunBean = (StoveFunBean) adapter.getItem(position);

                Stove.getInstance().workMode = stoveFunBean.funtionCode;
                Intent intent = new Intent();
                intent.putExtra("mode", stoveFunBean);
                intent.setClassName(getContext(), stoveFunBean.into);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
        int resId = getResources().getIdentifier(rvMainFunctionAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        ImageUtils.loadGif(getContext(), resId, imageView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_float) {
            //快捷入口 隐式启动 降低耦合
            Intent intent = new Intent();
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left_stove) {
            if (!llLeftStove.isSelected()) {
                //左灶
                llLeftStove.setSelected(true);
                llRightStove.setSelected(false);
            }
        } else if (id == R.id.ll_right_stove) {
            if (!llRightStove.isSelected()) {
                llRightStove.setSelected(true);
                llLeftStove.setSelected(false);
            }
        }
    }
}
