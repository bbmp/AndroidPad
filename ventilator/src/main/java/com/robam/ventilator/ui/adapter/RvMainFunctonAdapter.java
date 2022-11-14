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

public class RvMainFunctonAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    private Context mContext;
    private int pickPosition = -1;
    private Animation imgAnimation;

    public void setPickPosition(int pickPosition) {
        if (this.pickPosition == pickPosition)
            return;

        this.pickPosition = pickPosition;
        notifyDataSetChanged();
    }

    public int getPickPosition() {
        return pickPosition;
    }

    public RvMainFunctonAdapter(Context context) {
        super(R.layout.ventilator_item_layout_function);
        this.mContext = context;
        imgAnimation = AnimationUtils.loadAnimation(mContext, R.anim.ventilator_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        imgAnimation.setInterpolator(lin);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFun) {
        if (null != venFun) {
//            baseViewHolder.setText(R.id.tv_fun, venFun.getTitle());
            if (getItemPosition(venFun) == pickPosition) {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.6f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.6f);
                if (0 != pickPosition)
                    baseViewHolder.getView(R.id.iv_fun).startAnimation(imgAnimation);
            } else {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.0f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.0f);
                baseViewHolder.getView(R.id.iv_fun).clearAnimation();
                imgAnimation.cancel();
            }
            baseViewHolder.setImageResource(R.id.iv_fun, venFun.iconRes);
        }
    }
}
