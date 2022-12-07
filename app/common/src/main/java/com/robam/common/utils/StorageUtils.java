package com.robam.common.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class StorageUtils {
    /**
     * 获取App缓存目录
     *
     */
    public static String getCachDir(Context cx) {

        File file = null;
        if (StorageUtils.existSDCard()) {
            file = cx.getExternalCacheDir();
        }

        if (file == null) {
            file = cx.getCacheDir();
        }
        return file.getPath();
    }

    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
