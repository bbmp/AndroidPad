package com.robam.steamoven.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.CurveStep;

//曲线菜谱步骤
public class RvStep3Adapter extends BaseQuickAdapter<CurveStep, BaseViewHolder> {
    public RvStep3Adapter() {
        super(R.layout.steam_item_recipe_step);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CurveStep curveStep) {
        if (null != curveStep) {
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.steam_recipe_step_des), curveStep.no + "", curveStep.description));
            //图片隐藏
            baseViewHolder.getView(R.id.iv_step_img).setVisibility(View.GONE);
        }
    }
}