package com.robam.stove.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.stove.R;
import com.robam.stove.bean.RecipeStep;

public class RvStepAdapter extends BaseQuickAdapter<RecipeStep, BaseViewHolder> {
    public RvStepAdapter() {
        super(R.layout.stove_item_recipe_step);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecipeStep recipeStep) {
        baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.stove_recipe_step_des), recipeStep.stepNo+"", recipeStep.stepDesc));
    }
}
