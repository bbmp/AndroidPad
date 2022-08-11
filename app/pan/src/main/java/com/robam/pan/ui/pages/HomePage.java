package com.robam.pan.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.Constant.Constant;
import com.robam.pan.Constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBasePage;
import com.robam.pan.bean.PanFunBean;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends PanBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    //快炒
    private LinearLayout llQuick, llStir;
    private TextView tvQuick, tvStir;
    //浮标
    private ImageView ivFloat;
    @Override
    protected int getLayoutId() {
        return R.layout.pan_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();
        rvMain = findViewById(R.id.rv_main);
        llQuick = findViewById(R.id.ll_quick_fry);
        llStir = findViewById(R.id.ll_stir_fry);
        tvQuick = findViewById(R.id.tv_quick);
        tvStir = findViewById(R.id.tv_stir);
        ivFloat = findViewById(R.id.iv_float);
        rvMain.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFunctionAdapter = new RvMainFunctionAdapter();
        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                Intent intent = new Intent();
                PanFunBean panFunBean = (PanFunBean) adapter.getItem(position);
                intent.putExtra(Constant.FUNCTION_BEAN, panFunBean);
                if (panFunBean.into == null || panFunBean.into.length() == 0) {
                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
                    return;
                }
                intent.setClassName(getContext(), panFunBean.into);
                startActivity(intent);

            }

        });
        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(ivFloat, llQuick, llStir);
    }

    @Override
    protected void initData() {
        List<PanFunBean> functionList = new ArrayList<>();
        functionList.add(new PanFunBean(1, "云端菜谱", "", "recipe", "com.robam.pan.ui.activity.RecipeActivity"));
        functionList.add(new PanFunBean(2, "我的最爱", "", "favorite", "com.robam.pan.ui.activity.FavoriteActivity"));
        functionList.add(new PanFunBean(3, "烹饪曲线", "", "curve", "com.robam.pan.ui.activity.CurveActivity"));
        rvMainFunctionAdapter.setList(functionList);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_float) {
            getActivity().finish();
        } else if (id == R.id.ll_quick_fry) {
            if (!llQuick.isSelected()) {
                //关闭当前模式，持续快炒中
                llQuick.setSelected(true);
                tvQuick.setText(R.string.pan_quick_frying);
                llStir.setSelected(false);
                tvStir.setText(R.string.pan_stir_fry);
            }
        } else if (id == R.id.ll_stir_fry) {
            if (!llStir.isSelected()) {
                llStir.setSelected(true);
                tvStir.setText(R.string.pan_stir_frying);
                llQuick.setSelected(false);
                tvQuick.setText(R.string.pan_quick_fry);
            }
        }
    }
}
