package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;

public class RvShortcutFunAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    public RvShortcutFunAdapter() {
        super(R.layout.ventilator_item_layout_shortcut_fun);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFunBean) {
        int id = getContext().getResources().getIdentifier(venFunBean.backgroundImg, "drawable", getContext().getPackageName());
        baseViewHolder.setImageResource(R.id.iv_fun, id);
    }
}
