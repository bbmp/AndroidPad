package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;

public class RvSettingAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    public RvSettingAdapter() {
        super(R.layout.ventilator_item_menu_title);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFunBean) {
        baseViewHolder.setText(R.id.tv_desc, venFunBean.funtionName);
    }
}
