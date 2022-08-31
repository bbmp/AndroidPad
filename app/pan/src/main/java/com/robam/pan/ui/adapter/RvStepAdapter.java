package com.robam.pan.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.pan.R;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.RecipeStep;

//曲线步骤
public class RvStepAdapter extends BaseQuickAdapter<CurveStep, BaseViewHolder> {
    public RvStepAdapter() {
        super(R.layout.pan_item_recipe_step);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CurveStep curveStep) {
        if (null != curveStep) {
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.pan_recipe_step_des), curveStep.no + "", curveStep.description));
            ImageView imageView = baseViewHolder.getView(R.id.iv_step_img);
            ImageUtils.loadImage(getContext(), curveStep.imageUrl, imageView);

        }
    }
}
