package com.robam.pan.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.Constant.Constant;
import com.robam.pan.R;
import com.robam.pan.base.PanBasePage;
import com.robam.pan.bean.PanFunBean;
import com.robam.pan.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends PanBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.pan_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();
        rvMain = findViewById(R.id.rv_main);
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
                intent.setAction(panFunBean.into);
                startActivity(intent);
            }

        });
        rvMain.setAdapter(rvMainFunctionAdapter);
    }

    @Override
    protected void initData() {
        List<PanFunBean> functionList = new ArrayList<>();
        functionList.add(new PanFunBean(1, "云端菜谱", "", "recipe", "com.robam.pan.ui.activity.RecipeActivity"));
        functionList.add(new PanFunBean(2, "我的最爱", "", "favorite", "com.robam.pan.ui.activity.FavoriteActivity"));
        functionList.add(new PanFunBean(3, "烹饪曲线", "", "curve", "com.robam.pan.ui.activity.CurveActivity"));
        rvMainFunctionAdapter.setList(functionList);
    }
}
