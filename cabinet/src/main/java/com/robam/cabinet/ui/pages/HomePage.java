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
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.ui.adapter.RvDotAdapter;
import com.robam.cabinet.ui.adapter.RvMainFunctionAdapter;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends CabinetBasePage {
    private RecyclerView rvMain, rvDot;
    private PickerLayoutManager pickerLayoutManager;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private RvDotAdapter rvDotAdapter;
    private ImageView imageView;
    //主功能
    private List<CabFunBean> functionList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_page_layout_home;
    }

    @Override
    protected void initView() {
        showFloat();

        rvMain = findViewById(R.id.rv_main);
        rvDot = findViewById(R.id.rv_dot);
        imageView = findViewById(R.id.iv_bg);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
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

        rvMain.setAdapter(rvMainFunctionAdapter);
        rvDotAdapter = new RvDotAdapter();
        rvDot.setAdapter(rvDotAdapter);

        showCenter();
        setOnClickListener(R.id.iv_float);
    }

    @Override
    protected void initData() {
//        List<CabFunBean> cabFunBeans = FunctionManager.getFuntionList(getContext());
        functionList.add(new CabFunBean(1, "消毒", "", "disinfect", "com.robam.cabinet.ui.activity.ModeSelectActivity"));
        functionList.add(new CabFunBean(2, "快洁", "", "clean", "com.robam.cabinet.ui.activity.ModeSelectActivity"));
        functionList.add(new CabFunBean(3, "烘干", "", "dry", "com.robam.cabinet.ui.activity.ModeSelectActivity"));
        functionList.add(new CabFunBean(4, "净存", "", "flush", "com.robam.cabinet.ui.activity.ModeSelectActivity"));
        functionList.add(new CabFunBean(5, "智能", "", "smart", "com.robam.cabinet.ui.activity.ModeSelectActivity"));
        rvMainFunctionAdapter.setList(functionList);
        List<String> dotList = new ArrayList<>();
        for (CabFunBean cabFunBean : functionList) {
            dotList.add(cabFunBean.funtionName);
        }

        rvDotAdapter.setList(dotList);
        //初始位置第一个
        rvDotAdapter.setPickPosition(0);
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
        rvMainFunctionAdapter.setPickPosition(initPos);
        pickerLayoutManager.scrollToPosition(initPos);
        setBackground(initPos);

        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                CabFunBean cabFunBean = (CabFunBean) adapter.getItem(position);

                Intent intent = new Intent();
                intent.putExtra("mode", cabFunBean);
                intent.setClassName(getContext(), cabFunBean.into);
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

    /**
     * 滚动并居中
     */
    private void scollToPosition(int index) {
        pickerLayoutManager.smoothScrollToPosition(rvMain, new RecyclerView.State(), index);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_float) {
//            getActivity().finish();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.HomeActivity");
            startActivity(intent);
        }
    }
}
