package com.robam.steamoven.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.FuntionBean;

public class RvDotAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int pickPosition;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvDotAdapter() {
        super(R.layout.steam_item_dot);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        if (pickPosition == getItemPosition(s)) {
            baseViewHolder.setImageResource(R.id.iv_dot, R.drawable.steam_indicator_selected);
        } else
            baseViewHolder.setImageResource(R.id.iv_dot, R.drawable.steam_indicator_unselected);
    }
}
