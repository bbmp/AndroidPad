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
import com.robam.steamoven.bean.SteamCurveDetail;

import java.util.List;

public class RvCurveAdapter extends BaseQuickAdapter<SteamCurveDetail, BaseViewHolder> {
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.steam_recipe_img_bg) //预加载图片
            .error(R.drawable.steam_recipe_img_bg) //加载失败图片
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
        super(R.layout.steam_item_curve_recipe);
        addChildClickViewIds(R.id.iv_select);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SteamCurveDetail steamCurveDetail) {
        if (status == STATUS_ALL) {
            baseViewHolder.getView(R.id.iv_select).setSelected(true);
            baseViewHolder.setVisible(R.id.iv_select, true);
        }
        //删除状态
        else if (status == STATUS_DELETE) {
            baseViewHolder.getView(R.id.iv_select).setSelected(steamCurveDetail.isSelected());
            baseViewHolder.setVisible(R.id.iv_select, true);
        } else {
            baseViewHolder.setVisible(R.id.iv_select, false);
        }

        ImageView imageView = baseViewHolder.getView(R.id.iv_recipe);
        ImageUtils.loadImage(getContext(), steamCurveDetail.imageCover, maskOption, imageView);
        baseViewHolder.setText(R.id.tv_recipe, steamCurveDetail.name);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position, @NonNull List<Object> payloads) {
        if (null == payloads || payloads.isEmpty())
            super.onBindViewHolder(baseViewHolder, position, payloads);
        else {
            //局部更新
            SteamCurveDetail steamCurveDetail = getData().get(position);
            if (status == STATUS_ALL) {
                baseViewHolder.getView(R.id.iv_select).setSelected(true);
                baseViewHolder.setVisible(R.id.iv_select, true);
            }
            //删除状态
            else if (status == STATUS_DELETE) {
                baseViewHolder.getView(R.id.iv_select).setSelected(steamCurveDetail.isSelected());
                baseViewHolder.setVisible(R.id.iv_select, true);
            } else {
                baseViewHolder.setVisible(R.id.iv_select, false);
            }
        }
    }
}
