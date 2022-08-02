package com.robam.steamoven.base.action;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.robam.common.utils.ImageUtils;
import com.robam.steamoven.R;

/**
 *    author : 210190
 *    robam
 *    time   : 2019/12/08
 *    desc   : gif背景
 */
public interface BackgroundImgAction  {

    @Nullable
    ImageView getBgImg();

    /**
     * 设置背景图
     */
    default void setBgImg(Context context , int id) {
        if (getBgImg() != null) {

            ImageUtils.loadGif(context, id, R.drawable.steam_ic_bg_tran, getBgImg());
        }
    }
    default void setBgImgNoGif(Context context ,int id) {
        if (getBgImg() != null) {

            ImageUtils.loadImage(context, id, getBgImg());
        }
    }

    /**
     * 递归获取 ViewGroup 中的 img 对象
     */
    default ImageView obtainBgImg(ViewGroup group) {
        if (group == null) {
            return null;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof ImageView)) {
                return (ImageView) view;
            }
            if (view instanceof ViewGroup) {
                ImageView imageView = obtainBgImg((ViewGroup) view);
                if (imageView != null) {
                    return imageView;
                }
            }
        }
        return null;
    }
}