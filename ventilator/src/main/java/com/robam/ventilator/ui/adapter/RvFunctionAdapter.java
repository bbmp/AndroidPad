package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFun;

public class RvFunctionAdapter extends BaseQuickAdapter<VenFun, BaseViewHolder> {

    public RvFunctionAdapter() {
        super(R.layout.ventilator_item_layout_function);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFun venFun) {
        if (null != venFun) {
            baseViewHolder.setText(R.id.tv_fun, venFun.getTitle());
            baseViewHolder.setImageResource(R.id.iv_fun, R.mipmap.logo_roki);
        }
    }
}
