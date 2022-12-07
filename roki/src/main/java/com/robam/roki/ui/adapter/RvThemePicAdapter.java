package com.robam.roki.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.bean.Recipe;
import com.robam.roki.utils.RecipeUtils;

public class RvThemePicAdapter  extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_icon_recipe_default) //预加载图片
            .error(R.mipmap.roki_icon_recipe_default) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.NONE); //缓存

    public RvThemePicAdapter() {
        super(R.layout.roki_layout_item_recipe_theme);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Recipe recipe) {
        if (recipe != null) {
            ImageView iv_theme_item_img = (ImageView) holder.getView(R.id.iv_theme_recipe_pic);
            ImageView iv_theme_item_img2 = holder.getView(R.id.iv_theme_recipe_pic2);
            if (getItemPosition(recipe) < 3) {
                iv_theme_item_img.setVisibility(View.VISIBLE);
                iv_theme_item_img2.setVisibility(View.GONE);

                ImageUtils.loadImage(getContext(), RecipeUtils.getRecipeImgUrl(recipe), maskOption, iv_theme_item_img);
            } else if (getItemPosition(recipe) == 3) {
                iv_theme_item_img.setVisibility(View.GONE);
                iv_theme_item_img2.setVisibility(View.VISIBLE);

                ImageUtils.loadImage(getContext(), RecipeUtils.getRecipeImgUrl(recipe), maskOption, iv_theme_item_img2);
            }
        }
    }
}
