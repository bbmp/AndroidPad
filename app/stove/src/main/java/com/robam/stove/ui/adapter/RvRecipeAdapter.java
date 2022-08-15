package com.robam.stove.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.bean.StoveRecipe;

public class RvRecipeAdapter extends BaseQuickAdapter<StoveRecipe, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.stove_main_item_bg) //预加载图片
            .error(R.drawable.stove_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (182), (int) (182));

    public RvRecipeAdapter() {
        super(R.layout.stove_recipe_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, StoveRecipe stoveRecipe) {
        if (null != stoveRecipe) {
            ImageView imageView = baseViewHolder.getView(R.id.iv_recipe_img);
            ImageUtils.loadImage(getContext(), stoveRecipe.getImgUrl(), maskOption, imageView);
            baseViewHolder.setText(R.id.tv_recipe, stoveRecipe.getName());
        }
    }
}
