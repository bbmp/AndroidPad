package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;

public class RvMainFunctonAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {

    private int pickPosition = -1;

    public void setPickPosition(int pickPosition) {
        if (this.pickPosition == pickPosition)
            this.pickPosition = -1;
        else
            this.pickPosition = pickPosition;
        notifyDataSetChanged();
    }

    public RvMainFunctonAdapter() {
        super(R.layout.ventilator_item_layout_function);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFun) {
        if (null != venFun) {
//            baseViewHolder.setText(R.id.tv_fun, venFun.getTitle());
            if (getItemPosition(venFun) == pickPosition) {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.6f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.6f);
            } else {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.0f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.0f);
            }
            baseViewHolder.setImageResource(R.id.iv_fun, venFun.iconRes);
        }
    }
}
