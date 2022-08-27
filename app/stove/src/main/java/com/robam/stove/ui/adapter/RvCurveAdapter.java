package com.robam.stove.ui.adapter;

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
import com.robam.stove.R;
import com.robam.stove.bean.StoveRecipe;

public class RvCurveAdapter extends BaseQuickAdapter<StoveRecipe, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.stove_recipe_img_bg) //预加载图片
            .error(R.drawable.stove_recipe_img_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (286), (int) (226));
    public static int STATUS_BACK = 0;  //返回
    public static int STATUS_DELETE = 1; //删除
    public static int STATUS_ALL = 2; //全选

   private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public RvCurveAdapter() {
        super(R.layout.stove_item_curve_recipe);
        addChildClickViewIds(R.id.iv_select);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, StoveRecipe stoveRecipe) {
        if (status == STATUS_ALL) {
            baseViewHolder.getView(R.id.iv_select).setSelected(true);
            baseViewHolder.setVisible(R.id.iv_select, true);
        }
        //删除状态
        else if (status == STATUS_DELETE) {
            baseViewHolder.getView(R.id.iv_select).setSelected(stoveRecipe.isSelected());
            baseViewHolder.setVisible(R.id.iv_select, true);
        } else {
            baseViewHolder.setVisible(R.id.iv_select, false);
        }

        ImageView imageView = baseViewHolder.getView(R.id.iv_recipe);
        ImageUtils.loadImage(getContext(), stoveRecipe.getImgUrl(), maskOption, imageView);
        baseViewHolder.setText(R.id.tv_recipe, stoveRecipe.getName());
    }
}
