package com.robam.stove.ui.adapter;

import android.text.TextUtils;
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
import com.robam.stove.bean.StoveCurveDetail;

import java.util.List;

public class RvCurveAdapter extends BaseQuickAdapter<StoveCurveDetail, BaseViewHolder> {
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
       notifyItemRangeChanged(0, getItemCount(), "update select");
    }

    public RvCurveAdapter() {
        super(R.layout.stove_item_curve_recipe);
        addChildClickViewIds(R.id.iv_select);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, StoveCurveDetail stoveCurveDetail) {
        if (status == STATUS_ALL) {
            baseViewHolder.getView(R.id.iv_select).setSelected(true);
            baseViewHolder.setVisible(R.id.iv_select, true);
        }
        //删除状态
        else if (status == STATUS_DELETE) {
            baseViewHolder.getView(R.id.iv_select).setSelected(stoveCurveDetail.isSelected());
            baseViewHolder.setVisible(R.id.iv_select, true);
        } else {
            baseViewHolder.setVisible(R.id.iv_select, false);
        }

        ImageView imageView = baseViewHolder.getView(R.id.iv_recipe);
        if (stoveCurveDetail.curveCookbookId == 0 && stoveCurveDetail.needTime == 0)
            baseViewHolder.setVisible(R.id.iv_add, true);
        else
            baseViewHolder.setGone(R.id.iv_add, true);
        ImageUtils.loadImage(getContext(), stoveCurveDetail.imageCover, maskOption, imageView);
        baseViewHolder.setText(R.id.tv_recipe, stoveCurveDetail.name);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position, @NonNull List<Object> payloads) {
        if (null == payloads || payloads.isEmpty())
            super.onBindViewHolder(baseViewHolder, position, payloads);
        else {
            //局部更新
            StoveCurveDetail stoveCurveDetail = getData().get(position);
            if (status == STATUS_ALL) {
                baseViewHolder.getView(R.id.iv_select).setSelected(true);
                baseViewHolder.setVisible(R.id.iv_select, true);
            }
            //删除状态
            else if (status == STATUS_DELETE) {
                baseViewHolder.getView(R.id.iv_select).setSelected(stoveCurveDetail.isSelected());
                baseViewHolder.setVisible(R.id.iv_select, true);
            } else {
                baseViewHolder.setVisible(R.id.iv_select, false);
            }
        }
    }
}
