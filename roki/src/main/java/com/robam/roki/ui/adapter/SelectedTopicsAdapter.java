package com.robam.roki.ui.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.bean.TopicMultipleItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SelectedTopicsAdapter extends BaseMultiItemQuickAdapter<TopicMultipleItem, BaseViewHolder> {
    private List<TopicMultipleItem> topicMultipleItemList;
    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_img_default) //预加载图片
            .error(R.mipmap.roki_img_default) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .override((int) (190*2), (int) (115*2))
            .diskCacheStrategy(DiskCacheStrategy.NONE); //缓存
    //            .transform(new MultiTransformation(new CenterCrop(), new RoundedCornersTransformation(30, 0))); //圆角
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_icon_recipe_default) //预加载图片
            .error(R.mipmap.roki_icon_recipe_default) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .override(167*2, 100*2)
            .diskCacheStrategy(DiskCacheStrategy.NONE); //缓存
//            .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCornersTransformation(30, 0,RoundedCornersTransformation.CornerType.TOP)));  //圆角


    public SelectedTopicsAdapter() {
        super();
        addItemType(TopicMultipleItem.IMG, R.layout.roki_item_selected_topic);
        addItemType(TopicMultipleItem.TEXT, R.layout.roki_item_more_footer);
        addItemType(TopicMultipleItem.IMG_THEME_RECIPE_COLLECT, R.layout.roki_item_recipe_theme_collect);
    }

    public SelectedTopicsAdapter(@Nullable List<TopicMultipleItem> data) {
        super(data);
        this.topicMultipleItemList = data;
        addItemType(TopicMultipleItem.IMG, R.layout.roki_item_selected_topic);
        addItemType(TopicMultipleItem.TEXT, R.layout.roki_item_more_footer);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, TopicMultipleItem topicMultipleItem) {
        switch (topicMultipleItem.getItemType()) {
            case TopicMultipleItem.IMG: {
                ImageView itemTopticsImg = baseViewHolder.getView(R.id.iv_item_topic_img);

                ImageUtils.loadImage(getContext(), topicMultipleItem.getContent(), options, itemTopticsImg);
            }
            break;
            case TopicMultipleItem.TEXT:


                break;
            case TopicMultipleItem.IMG_THEME_RECIPE_COLLECT: {
                ImageView itemTopticsImg = baseViewHolder.getView(R.id.iv_item_topic_img);

                ImageUtils.loadImage(getContext(), topicMultipleItem.getContent(), maskOption, itemTopticsImg);
                baseViewHolder.setText(R.id.tv_recipe_name, topicMultipleItem.getTitle());
            }
            break;
            default:
                break;
        }

    }
}
