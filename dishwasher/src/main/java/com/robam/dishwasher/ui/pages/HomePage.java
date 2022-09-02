package com.robam.dishwasher.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBasePage;
import com.robam.dishwasher.bean.DishWaherFunBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends DishWasherBasePage {
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private ImageView imageView;
    private TextView tvFunhint;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_page_layout_home;
    }

    @Override
    protected void initView() {
        showFloat(); //快捷图标
        rvMain = findViewById(R.id.rv_main);
        imageView = findViewById(R.id.iv_bg);
        tvFunhint = findViewById(R.id.tv_fun_hint);

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
                        tvFunhint.setText(rvMainFunctionAdapter.getItem(position).desc);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainFunctionAdapter = new RvMainFunctionAdapter();
        rvMain.setAdapter(rvMainFunctionAdapter);
        showCenter();
        setOnClickListener(R.id.iv_float);
    }

    @Override
    protected void initData() {
        DishWasher.getInstance().init(getContext());
        List<DishWaherFunBean> functionList = DishWasher.getInstance().getDishWaherFunBeans();

        rvMainFunctionAdapter.setList(functionList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
        rvMainFunctionAdapter.setPickPosition(initPos);
        pickerLayoutManager.scrollToPosition(initPos);
        setBackground(initPos);
        tvFunhint.setText(rvMainFunctionAdapter.getItem(initPos).desc);

        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                DishWaherFunBean dishWaherFunBean = (DishWaherFunBean) adapter.getItem(position);

                DishWasher.getInstance().workMode = dishWaherFunBean.funtionCode;
                Intent intent = new Intent();
                intent.putExtra("mode", dishWaherFunBean);
                intent.setClassName(getContext(), dishWaherFunBean.into);
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
        if (view.getId() == R.id.iv_float)
            getActivity().finish();
    }
}
