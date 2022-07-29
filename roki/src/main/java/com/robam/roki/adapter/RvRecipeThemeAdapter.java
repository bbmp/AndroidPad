package com.robam.roki.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.HeadPage;
import com.robam.common.utils.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.bean.Dc;
import com.robam.roki.bean.Recipe;
import com.robam.roki.bean.RecipeTheme;
import com.robam.roki.bean.ThemeRecipeMultipleItem;
import com.robam.roki.http.CloudHelper;
import com.robam.roki.response.PersonalizedRecipeRes;
import com.robam.roki.utils.DeviceNameHelper;
import com.robam.roki.utils.NumberUtil;
import com.robam.roki.utils.RecipeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RvRecipeThemeAdapter extends BaseMultiItemQuickAdapter<ThemeRecipeMultipleItem, BaseViewHolder> implements LoadMoreModule {
    private HeadPage headPage;

    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_icon_recipe_default) //预加载图片
            .error(R.mipmap.roki_icon_recipe_default) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (167*2), (int) (160*2));
//            .transform(new MultiTransformation(new CenterCrop(), new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.TOP)));
//            .transform(new RoundedCornersTransformation(30, 0,RoundedCornersTransformation.CornerType.TOP)); //圆角

    public RvRecipeThemeAdapter(HeadPage fragment) {
        this.headPage = fragment;
        addItemType(ThemeRecipeMultipleItem.IMG_RECIPE_MSG_TEXT, R.layout.roki_layout_item_recipe);
        addItemType(ThemeRecipeMultipleItem.IMG_THEME_RECIPE_MSG_TEXT, R.layout.roki_layout_item_recipe_theme_list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ThemeRecipeMultipleItem multiItemEntity) {
        switch (multiItemEntity.getItemType()) {
            case ThemeRecipeMultipleItem.IMG_RECIPE_MSG_TEXT:


                final Recipe recipe = multiItemEntity.getRecipe();
                holder.setText(R.id.tv_recipe_name, recipe.name)
//                        .setText(R.id.tv_collection_number, "收藏" + NumberUtil.converString(recipe.collectCount))
//                        .setText(R.id.tv_collection_number, "")
                        .setText(R.id.tv_recipe_read_number, "热度 " + NumberUtil.converString(recipe.viewCount));


                ImageView imageView=(ImageView) holder.getView(R.id.iv_tag_recipe);

                ImageUtils.loadImage(getContext(), RecipeUtils.getRecipeImgUrl(recipe), maskOption, imageView);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RecipeDetailPage.show(recipe.id, recipe.sourceType);
//                    }
//                });

                List<Dc> dcs = recipe.getDcs();
                if (dcs != null && dcs.size() != 0) {
                    holder.setVisible(R.id.tv_device_name, true);
                    holder.setText(R.id.tv_device_name, DeviceNameHelper.getDeviceName2(dcs));

                } else {
                    holder.setVisible(R.id.tv_device_name, false);
//                    holder.setVisible(R.id.img_recipe_read_number,false);

                }
                if(!TextUtils.isEmpty(recipe.video))
                    holder.getView(R.id.iv_play).setVisibility(View.VISIBLE);
                else
                    holder.getView(R.id.iv_play).setVisibility(View.GONE);
                break;
            case ThemeRecipeMultipleItem.IMG_THEME_RECIPE_MSG_TEXT:
//                 ThemeRecipeList themeRecipeList = multiItemEntity.getThemeRecipeList();
                RecipeTheme recipeTheme = multiItemEntity.getRecipeTheme();
//                RecipeTheme recipeTheme = themeRecipeList.getRecipeTheme();
                holder.setText(R.id.tv_theme_title, recipeTheme.name)
                        .setText(R.id.tv_theme_subname, recipeTheme.subName);

                //初始化专题recyclerView
                RecyclerView recyclerView = (RecyclerView) holder.getView(R.id.rv_recipe_pic_list);

                //获取专题菜谱数据
                getBygetCookbookBythemeId("cn", 4, 0, recipeTheme, recyclerView);

//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SelectThemeDetailPage.show(recipeTheme, SelectThemeDetailPage.TYPE_THEME_RECIPE);
//                    }
//                });

                break;

            default:
                break;
        }
    }

    /**
     * 根据主题id查询下属所有菜单
     */
    private void getBygetCookbookBythemeId(String lang, long limit, final int start, final RecipeTheme recipeTheme, RecyclerView recyclerView) {

        CloudHelper.getCookBookBythemeId(headPage, lang, limit, start, recipeTheme.id.intValue(), PersonalizedRecipeRes.class, new RetrofitCallback<PersonalizedRecipeRes>() {
            @Override
            public void onSuccess(PersonalizedRecipeRes personalizedRecipeRes) {
                List<Recipe> recipes = personalizedRecipeRes.cookbooks;
                RvThemePicAdapter rvThemePicAdapter = new RvThemePicAdapter();
                recyclerView.setAdapter(rvThemePicAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int i) {
                        if (i < 3)
                            return 1;
                        else return 3;
                    }
                });
                recyclerView.setLayoutManager(gridLayoutManager);
                rvThemePicAdapter.setList(recipes);
                rvThemePicAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                        SelectThemeDetailPage.show(recipeTheme, SelectThemeDetailPage.TYPE_THEME_RECIPE);
                    }
                });
            }

            @Override
            public void onFaild(String err) {

            }

        });
    }

}
