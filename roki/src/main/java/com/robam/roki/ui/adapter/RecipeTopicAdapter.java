package com.robam.roki.ui.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.bean.Recipe;
import com.robam.roki.utils.RecipeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecipeTopicAdapter extends BaseMultiItemQuickAdapter<Recipe, BaseViewHolder> {
    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_icon_recipe_default) //预加载图片
            .error(R.mipmap.roki_icon_recipe_default) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .override(167*2, 117*2)
            .diskCacheStrategy(DiskCacheStrategy.NONE); //缓存
//            .transform(new RoundedCornersTransformation(30, 0,RoundedCornersTransformation.CornerType.BOTTOM)); //圆角

    public RecipeTopicAdapter(@Nullable List<Recipe> data) {
        super(data);
        addItemType(Recipe.IMG, R.layout.roki_item_lastweek_topic);
        addItemType(Recipe.TEXT, R.layout.roki_item_more_footer);
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Recipe recipe) {
        switch (recipe.getItemType()) {
            case Recipe.IMG:
                ImageView ivItemTopic = baseViewHolder.getView(R.id.iv_item_topic_img);
                TextView recipeName = baseViewHolder.getView(R.id.tv_recipe_name);

                TextView ivWeekTopicRanking = baseViewHolder.getView(R.id.iv_week_topic_ranking);
                recipeName.setText(recipe.name);

                ImageUtils.loadImage(getContext(), RecipeUtils.getRecipeImgUrl(recipe), options, ivItemTopic);
                ivWeekTopicRanking.setText("TOP" + (getItemPosition(recipe) + 1));

                if (!TextUtils.isEmpty(recipe.video))
                    baseViewHolder.getView(R.id.iv_play).setVisibility(View.VISIBLE);
                else
                    baseViewHolder.getView(R.id.iv_play).setVisibility(View.GONE);
                break;
            case Recipe.TEXT:
                break;
            default:
                break;
        }
    }
}
