package com.robam.common.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

//下载回调
public interface DownloadListener {
    void onProgress(int progress);//下载进度

    void onFinish(String path);//下载完成

    void onFail(String errorInfo);//下载失败
}
