package com.robam.dishwasher.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.dishwasher.R;

public class RvStringAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public RvStringAdapter() {
        super(R.layout.dishwasher_picker_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_picker_name, s);
    }
}
