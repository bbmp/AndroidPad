package com.robam.common.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

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

    public static String getDataDir() {
        File file = null;
        file = Environment.getDataDirectory();
        return file.getPath();
    }

    public static String getTotalSize(Context context, File path) {
        // 创建StatFs对象
        StatFs stat = new StatFs(path.getPath());
        // 获取每个存储快的大小
        long blockSize = stat.getBlockSize();
        // 获取所有的存储块
        long blockCount = stat.getBlockCount();
        // 获取内部存储的总大小
        long totalSize = blockCount * blockSize;
        // 将long类型转为字符串
        String totalStr = Formatter.formatFileSize(context, totalSize);
        return totalStr;
    }
    public static String getAvailSize(Context context, File path) {
        // 创建StatFs对象
        StatFs stat = new StatFs(path.getPath());
        // 获取每个存储快的大小
        long blockSize = stat.getBlockSize();
        // 获取可用的存储块
        long availableBlocks = stat.getAvailableBlocks();
        // 获取内部存储的可用大小
        long availSize = availableBlocks * blockSize;
        // 将long类型转为字符串
        String availStr = Formatter.formatFileSize(context, availSize);
        return availStr;
    }

}
