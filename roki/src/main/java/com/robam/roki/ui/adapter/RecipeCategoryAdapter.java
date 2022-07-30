package com.robam.roki.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.ImageUtils;
import com.robam.roki.R;
import com.robam.roki.bean.Dc;
import com.robam.roki.bean.Recipe;
import com.robam.roki.constant.IDeviceType;
import com.robam.roki.utils.NumberUtil;
import com.robam.roki.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> implements LoadMoreModule {
    public static final int OTHER_VIEW = 2;
    private LayoutInflater mInflater;
    private RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.roki_img_default)
            .error(R.mipmap.roki_img_default)
            .override(350*2, 158*2)
            .format(DecodeFormat.PREFER_RGB_565);
    List<Recipe> mList = new ArrayList<>();


    public RecipeCategoryAdapter() {
        super(R.layout.roki_item_recommend_recipe);

    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Recipe recipe) {
        if (null != recipe) {
            if (!TextUtils.isEmpty(recipe.stampLogo)) {
                baseViewHolder.setVisible(R.id.logo, true);
                ImageView imageView = baseViewHolder.getView(R.id.logo);
                ImageUtils.loadImage(getContext(), recipe.stampLogo, imageView);
            } else {
                baseViewHolder.setVisible(R.id.logo, false);
            }
            //modify by wang 22/04/21
            if (!TextUtils.isEmpty(recipe.video))
                baseViewHolder.setVisible(R.id.iv_play, true);
            else
                baseViewHolder.setVisible(R.id.iv_play, false);


            if (!TextUtils.isEmpty(recipe.imgLarge)) {
                ImageView imgView = baseViewHolder.getView(R.id.iv_img);

                ImageUtils.loadImage(getContext(), RecipeUtils.getRecipeImgUrl(recipe), options, imgView);

            } else {
                baseViewHolder.setImageResource(R.id.iv_img, R.mipmap.roki_img_default);
            }
            baseViewHolder.setText(R.id.home_recipe_tv_recipename, recipe.name);
//                recipeCategoryViewHolder.collect.setText("收藏 " +  NumberUtil.converString(mList.get(position).collectCount) );
//                recipeCategoryViewHolder.collect2.setText("阅读 " + NumberUtil.converString(mList.get(position).viewCount) );
            baseViewHolder.setText(R.id.home_recipe_tv_collect2, "热度 " + NumberUtil.converString(recipe.viewCount));


//                Drawable drawable1 = UiUtils.getResources().getDrawable(R.drawable.hot, null);
//                drawable1.setBounds(0, 0, 35, 35);
//                recipeCategoryViewHolder.collect2.setCompoundDrawables(drawable1, null, null, null);


//                TextView tvCollection = (TextView) holder.getView(R.id.tv_collection_number);
            List<Dc> dcs = recipe.getDcs();
            if (dcs != null && dcs.size() != 0) {
//                    Drawable drawable = UiUtils.getResources().getDrawable(DeviceNameHelper.getIcon(dcs), null);
//                    drawable.setBounds(0, 0, 35, 35);

            } else {

            }
            List<Dc> js_dcs = recipe.getDcs();
            if (0 != js_dcs.size()) {

                if (js_dcs.size() == 1) {
                    baseViewHolder.setVisible(R.id.tv_device_name_one, true);
                    baseViewHolder.setVisible(R.id.tv_device_name_two, false);
                    switch (js_dcs.get(0).getDc()) {
                        case IDeviceType.RDKX:
                            baseViewHolder.setText(R.id.tv_device_name_one, "电烤箱");
                            break;
                        case IDeviceType.RZQL:
                            baseViewHolder.setText(R.id.tv_device_name_one,"电蒸箱");
                            break;
                        case IDeviceType.RWBL:
                            baseViewHolder.setText(R.id.tv_device_name_one,"微");
                            break;
                        case IDeviceType.RRQZ:
                            baseViewHolder.setText(R.id.tv_device_name_one,"灶具");
                            break;
                        case IDeviceType.RZKY:
                            baseViewHolder.setText(R.id.tv_device_name_one,"一体机");
                            break;
                        case IDeviceType.RIKA:
                            baseViewHolder.setText(R.id.tv_device_name_one,"RIKA");
                            break;
                        case IDeviceType.KZNZ:
                            baseViewHolder.setText(R.id.tv_device_name_one,"智能灶");
                            break;
                        default:
                            break;

                    }

                } else {
                    baseViewHolder.setVisible(R.id.tv_device_name_one, true);
                    baseViewHolder.setVisible(R.id.tv_device_name_two, true);
                    switch (js_dcs.get(0).getDc()) {
                        case IDeviceType.RDKX:
                            baseViewHolder.setText(R.id.tv_device_name_one,"电烤箱");
                            break;
                        case IDeviceType.RZQL:
                            baseViewHolder.setText(R.id.tv_device_name_one,"电蒸箱");
                            break;
                        case IDeviceType.RWBL:
                            baseViewHolder.setText(R.id.tv_device_name_one,"微");
                            break;
                        case IDeviceType.RRQZ:
                            baseViewHolder.setText(R.id.tv_device_name_one,"灶具");
                            break;
                        case IDeviceType.RZKY:
                            baseViewHolder.setText(R.id.tv_device_name_one,"一体机");
                            break;
                        case IDeviceType.RIKA:
                            baseViewHolder.setText(R.id.tv_device_name_one,"RIKA");
                            break;
                        case IDeviceType.KZNZ:
                            baseViewHolder.setText(R.id.tv_device_name_one,"智能灶");
                            break;
                        default:
                            break;
                    }
                    switch (js_dcs.get(1).getDc()) {
                        case IDeviceType.RDKX:
                            baseViewHolder.setText(R.id.tv_device_name_two,"电烤箱");
                            break;
                        case IDeviceType.RZQL:
                            baseViewHolder.setText(R.id.tv_device_name_two,"电蒸箱");
                            break;
                        case IDeviceType.RWBL:
                            baseViewHolder.setText(R.id.tv_device_name_two,"微");
                            break;
                        case IDeviceType.RRQZ:
                            baseViewHolder.setText(R.id.tv_device_name_two,"灶具");
                            break;
                        case IDeviceType.RZKY:
                            baseViewHolder.setText(R.id.tv_device_name_two,"一体机");
                            break;
                        case IDeviceType.RIKA:
                            baseViewHolder.setText(R.id.tv_device_name_two,"RIKA");
                            break;
                        case IDeviceType.KZNZ:
                            baseViewHolder.setText(R.id.tv_device_name_two,"智能灶");
                            break;
                        default:
                            break;
                    }

                }


            }

            ImageView ivCollection = baseViewHolder.getView(R.id.iv_collection);
            ivCollection.setSelected(recipe.collected);
        }
    }

}
