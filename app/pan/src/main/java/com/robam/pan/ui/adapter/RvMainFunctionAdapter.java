package com.robam.pan.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.pan.R;
import com.robam.pan.bean.PanFunBean;

public class RvMainFunctionAdapter extends BaseQuickAdapter<PanFunBean, BaseViewHolder> {

    public RvMainFunctionAdapter() {
        super(R.layout.pan_item_main_function);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, PanFunBean panFunBean) {
        baseViewHolder.setText(R.id.tv_funtion_name, panFunBean.funtionName);
    }
}
