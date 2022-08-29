package com.robam.pan.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.pan.R;
import com.robam.pan.bean.PanRecipe;

//云端菜谱
public class RvRecipeAdapter extends BaseQuickAdapter<PanRecipe, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.pan_main_item_bg) //预加载图片
            .error(R.drawable.pan_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (182), (int) (182));

    public RvRecipeAdapter() {
        super(R.layout.pan_item_recipe);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, PanRecipe panRecipe) {
        if (null != panRecipe) {

            ImageView imageView = baseViewHolder.getView(R.id.iv_recipe_img);
            ImageUtils.loadImage(getContext(), panRecipe.getImgUrl(), maskOption, imageView);
            baseViewHolder.setText(R.id.tv_recipe, panRecipe.getName());
        }
    }
}
