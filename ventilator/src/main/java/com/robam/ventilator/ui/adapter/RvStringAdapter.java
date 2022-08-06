package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.constant.VentilatorEnum;

public class RvStringAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public RvStringAdapter() {
        super(R.layout.ventilator_item_menu_title);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_desc, VentilatorEnum.match(s));
    }
}
