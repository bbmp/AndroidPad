package com.robam.cabinet.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBasePage;
import com.robam.cabinet.bean.FunctionBean;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.manage.FunctionManager;
import com.robam.cabinet.ui.adapter.RvDotAdapter;
import com.robam.cabinet.ui.adapter.RvMainFunctionAdapter;
import com.robam.common.ui.HeadPage;
import com.robam.common.ui.UIService;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends CabinetBasePage {
    private RecyclerView rvMain, rvDot;
    private PickerLayoutManager pickerLayoutManager;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private RvDotAdapter rvDotAdapter;
    private ImageView imageView;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_page_layout_home;
    }

    @Override
    protected void initView() {
        rvMain = findViewById(R.id.rv_main);
        rvDot = findViewById(R.id.rv_dot);
        imageView = findViewById(R.id.iv_bg);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.33f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvDotAdapter.setPickPosition(position);
                        rvMainFunctionAdapter.setPickPosition(position);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFunctionAdapter = new RvMainFunctionAdapter();
        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                scollToPosition(position);
                Intent intent = new Intent();
                FunctionBean functionBean = (FunctionBean) adapter.getItem(position);
                intent.putExtra(Constant.FUNCTION_BEAN, functionBean);
                if (functionBean.into == null || functionBean.into.length() == 0) {
                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
                    return;
                }
                intent.setAction(functionBean.into);
                startActivity(intent);
            }

        });
        rvMain.setAdapter(rvMainFunctionAdapter);
        rvDotAdapter = new RvDotAdapter();
        rvDot.setAdapter(rvDotAdapter);

        showCenter();
        setOnClickListener(R.id.iv_float);
    }

    @Override
    protected void initData() {
        List<FunctionBean> functionBeans = FunctionManager.getFuntionList(getContext());
        rvMainFunctionAdapter.setList(functionBeans);
        List<String> dotList = new ArrayList<>();
        for (FunctionBean functionBean: functionBeans) {
            dotList.add(functionBean.funtionName);
        }

        rvDotAdapter.setList(dotList);
        rvDotAdapter.setPickPosition(Integer.MAX_VALUE / 2);
        rvMainFunctionAdapter.setPickPosition(Integer.MAX_VALUE / 2);
        pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
        setBackground(Integer.MAX_VALUE / 2);
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

    /**
     * 滚动并居中
     */
    private void scollToPosition(int index) {
        pickerLayoutManager.smoothScrollToPosition(rvMain, new RecyclerView.State(), index);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_float)
            getActivity().finish();
    }
}
