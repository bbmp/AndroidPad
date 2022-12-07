package com.robam.ventilator.ui.adapter;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;

public class RvShortcutFunAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    private int pickPosition = -1;
    private Animation imgAnimation;

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

    public RvShortcutFunAdapter(Context context) {
        super(R.layout.ventilator_item_layout_shortcut_fun);
        imgAnimation = AnimationUtils.loadAnimation(context, R.anim.ventilator_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        imgAnimation.setInterpolator(lin);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFunBean) {
        if (null != venFunBean) {
            if (getItemPosition(venFunBean) == pickPosition) {
                baseViewHolder.setBackgroundResource(R.id.tv_fun_name, R.drawable.ventilator_shortcut_item_selected);
//                baseViewHolder.getView(R.id.iv_fun).startAnimation(imgAnimation);
            } else {
                baseViewHolder.setBackgroundResource(R.id.tv_fun_name, R.drawable.ventilator_shortcut_item_unselected);
//                baseViewHolder.getView(R.id.iv_fun).clearAnimation();

            }
            baseViewHolder.setImageResource(R.id.iv_fun, venFunBean.iconRes);
        }
    }
}
