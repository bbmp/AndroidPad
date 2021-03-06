package com.robam.common.utils;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtils {

    public static void laodImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static  void loadImage(View view, String url, ImageView imageView) {
        Glide.with(view).load(url).into(imageView);
    }

    public static void loadImage(Context context, String url, int resErr, int resHolder, int overWidth, int overHeight, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .error(resErr)
                .placeholder(resHolder)
                .override(overWidth, overHeight)
                .into(imageView);
    }
}
