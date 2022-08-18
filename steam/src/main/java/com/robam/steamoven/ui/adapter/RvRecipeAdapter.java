package com.robam.steamoven.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.model.SteamRecipe;

public class RvRecipeAdapter extends BaseQuickAdapter<SteamRecipe, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.steam_main_item_bg) //预加载图片
            .error(R.drawable.steam_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (182), (int) (182));

    public RvRecipeAdapter() {
        super(R.layout.steam_recipe_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SteamRecipe steamRecipe) {
        if (null != steamRecipe) {
            ImageView imageView = baseViewHolder.getView(R.id.iv_recipe_img);
            ImageUtils.loadImage(getContext(), steamRecipe.getImgUrl(), maskOption, imageView);
            baseViewHolder.setText(R.id.tv_recipe, steamRecipe.getName());
        }
    }
}
