package com.robam.common.utils;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.BaseRequestOptions;

public class ImageUtils {

    public static void loadImage(Context context, int res, ImageView imageView) {
        Glide.with(context).load(res).into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadImage(View view, String url, ImageView imageView) {
        Glide.with(view).load(url).into(imageView);
    }

    public static void loadGif(Context context, int res, ImageView imageView) {
        Glide.with(context).asGif()
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade(600))
                .into(imageView);
    }

    public static void loadGif(Context context, int res, int placeholder, ImageView imageView) {
        Glide.with(context).asGif()
                .load(res)
                .placeholder(placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(600))
                .into(imageView);
    }

    public static void loadImage(Context context, String url, BaseRequestOptions<?> options, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
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
