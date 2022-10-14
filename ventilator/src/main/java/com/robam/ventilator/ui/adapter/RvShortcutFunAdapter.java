package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.device.HomeVentilator;

public class RvShortcutFunAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    private int pickPosition = -1;

    public void setPickPosition(int pickPosition) {
        if (this.pickPosition == pickPosition)
            this.pickPosition = -1;
        else
            this.pickPosition = pickPosition;
        notifyDataSetChanged();
    }

    public int getPickPosition() {
        return pickPosition;
    }

    public RvShortcutFunAdapter() {
        super(R.layout.ventilator_item_layout_shortcut_fun);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFunBean) {
        if (null != venFunBean) {
            if (getItemPosition(venFunBean) == pickPosition)
                baseViewHolder.setBackgroundResource(R.id.tv_fun_name, R.drawable.ventilator_main_item_bg);
            else
                baseViewHolder.setBackgroundResource(R.id.tv_fun_name, R.drawable.ventilator_shortcut_item_bg);
            baseViewHolder.setImageResource(R.id.iv_fun, venFunBean.iconRes);
        }
    }
}
