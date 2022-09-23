package com.robam.steamoven.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.model.RecipeStep;

//云端菜谱
public class RvStepAdapter extends BaseQuickAdapter<RecipeStep, BaseViewHolder> {
    public RvStepAdapter() {
        super(R.layout.steam_item_recipe_step);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecipeStep recipeStep) {
        baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.steam_recipe_step_des), recipeStep.step_no +"", recipeStep.step_des));
        ImageView imageView = baseViewHolder.getView(R.id.iv_step_img);
        ImageUtils.loadImage(getContext(), recipeStep.step_img, imageView);
    }
}
