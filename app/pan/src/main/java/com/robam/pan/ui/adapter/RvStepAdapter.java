package com.robam.pan.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.pan.R;
import com.robam.pan.bean.RecipeStep;

public class RvStepAdapter extends BaseQuickAdapter<RecipeStep, BaseViewHolder> {
    public RvStepAdapter() {
        super(R.layout.pan_item_recipe_step);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecipeStep recipeStep) {
        if (null != recipeStep) {
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.pan_recipe_step_des), recipeStep.getNo() + "", recipeStep.getDesc()));
            if (recipeStep.hideImage)
                baseViewHolder.getView(R.id.iv_step_img).setVisibility(View.GONE);
            else {
                ImageView imageView = baseViewHolder.getView(R.id.iv_step_img);
                ImageUtils.loadImage(getContext(), recipeStep.image, imageView);
            }
        }
    }
}
