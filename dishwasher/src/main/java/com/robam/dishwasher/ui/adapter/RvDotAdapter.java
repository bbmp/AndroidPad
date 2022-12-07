package com.robam.dishwasher.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.dishwasher.R;

public class RvDotAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int pickPosition;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvDotAdapter() {
        super(R.layout.dishwasher_item_dot);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        if (pickPosition == getItemPosition(s)) {
            baseViewHolder.setImageResource(R.id.iv_dot, R.drawable.dishwasher_indicator_selected);
        } else
            baseViewHolder.setImageResource(R.id.iv_dot, R.drawable.dishwasher_indicator_unselected);
    }
}
