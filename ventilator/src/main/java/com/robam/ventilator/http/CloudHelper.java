package com.robam.ventilator.http;

import android.content.Context;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.DownloadListener;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StorageUtils;
import com.robam.ventilator.constant.HostServer;
import com.robam.ventilator.request.AppTypeReq;
import com.robam.ventilator.request.BindDeviceReq;
import com.robam.ventilator.request.GetDeviceParamsReq;
import com.robam.ventilator.request.GetDeviceUserReq;
import com.robam.ventilator.request.GetUserReq;
import com.robam.ventilator.request.GetVerifyCodeReq;
import com.robam.ventilator.request.LoginQrcodeReq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);
    private static IDownloadService downloadService = RetrofitClient.getInstance().createDownApi(IDownloadService.class, HostServer.apiHost);

    //获取验证码
    public static <T extends BaseResponse> void getVerifyCode(ILife iLife, String phone, Class<T> entity,
                                                              final RetrofitCallback<T> callback) {
        String json = new GetVerifyCodeReq(phone).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getVerifyCode(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取token
    public static <T extends BaseResponse> void getToken(ILife iLife, String loginType
            ,String sjhm
            ,String smsCode
            ,String password
            ,String client_id
            ,String client_secret
            ,String appType
            , Class<T> entity, final RetrofitCallback<T> callback) {

        Call<ResponseBody> call = svr.getToken(loginType ,sjhm ,smsCode ,password, client_id ,client_secret ,appType);
        enqueue(iLife, entity, call, callback);

    }

    /**
     * 获取用户信息
     * @param authorization
     * @param callback
     */
    public static <T extends BaseResponse> void getUserInfo(ILife iLife, String authorization,
                                                      Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.getOauth(authorization);
        enqueue(iLife, entity, call, callback);

    }
    //绑定设备
    public static <T extends BaseResponse> void bindDevice(ILife iLife, long userId, String guid, String name,
                                                           boolean isOwner, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new BindDeviceReq(userId, guid, name, isOwner).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.bindDevice(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取设备
    public static <T extends BaseResponse> void getDevices(ILife iLife, long userId, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetUserReq(userId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getDevices(requestBody);
        enqueue(iLife, entity, call, callback);
    }
//    //获取二维码
//    public static <T extends BaseResponse> void getCode(ILife iLife, Class<T> entity, final RetrofitCallback<T> callback) {
//        Call<ResponseBody> call = svr.getCode();
//        enqueue(iLife, entity, call, callback);
//    }

    //获取设备绑定的用户
    public static <T extends BaseResponse> void getDeviceUsers(ILife iLife, long userid, String guid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetDeviceUserReq(userid, guid).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getDeviceUsers(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    //
    public static <T extends BaseResponse> void getLoginStatus(ILife iLife, String key, Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.getLoginStatus(key);
        enqueue(iLife, entity, call, callback);
    }
    //二维码登录
    public static <T extends BaseResponse> void loginQrcode(ILife iLife, String account, String pwd, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new LoginQrcodeReq(account, pwd).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.login(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //删除绑定的用户
    public static <T extends BaseResponse> void unbindDevice(ILife iLife, long userid, String guid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetDeviceUserReq(userid, guid).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.unbindDevice(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取设备参数
    public static <T extends BaseResponse> void getDeviceParams(ILife iLife, long userid, String deviceType, String category,
                                                                  Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetDeviceParamsReq(userid, deviceType, category).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getDeviceParams(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //检查版本
    public static <T extends BaseResponse> void checkAppVersion(ILife iLife, String appType, String deviceType, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new AppTypeReq(appType, deviceType).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.checkAppVersion(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //下载文件
    public static void downloadFile(Context context, String url, final DownloadListener listener) {

        Call<ResponseBody> responseBodyCall = downloadService.downloadFile(url);
        responseBodyCall.enqueue(new retrofit2.Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                saveFile(response.body(), StorageUtils.getCachDir(context) , "temp.apk", listener);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFail("net failed");
            }
        });

    }

    private static void saveFile(ResponseBody responseBody, String destFileDir, String destFileName, DownloadListener downloadListener) {
        InputStream is = null;
        byte[] buf = new byte[8192];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = responseBody.byteStream();
            final long total = responseBody.contentLength();
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            if (file.exists())
                file.delete();
            fos = new FileOutputStream(file);
            int progress = 0;
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                if ((int) (sum * 100 / total) != progress) {
                    progress = (int) (sum * 100 / total);
                    downloadListener.onProgress(progress);
                }
            }
            fos.flush();

            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath());

        } catch (IOException e) {
            downloadListener.onFail("IOException");
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //统一处理回调
    private static <T extends BaseResponse> void enqueue(ILife iLife, final Class<T> entity, Call<ResponseBody> call, final RetrofitCallback<T> callback) {
        WeakReference<ILife> iLifeWeakReference = new WeakReference<>(iLife);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //生命周期判断
                if (null == iLifeWeakReference.get() || iLifeWeakReference.get().isDestroyed()) {
                    LogUtils.e("page isDestroyed");
                    return;
                }
                try {
                    String body = response.body().string();
                    Gson gson = new Gson();
                    T object = gson.fromJson(body, entity);
                    BaseResponse rcReponse = object;
                    if (null != rcReponse && rcReponse.rc == 0) { //统一判断rc =0为正常
                        if (null != callback) {
                            callback.onSuccess(object);
                            return;
                        }
                    }
                    if (null != callback && null != rcReponse)
                        callback.onFaild(rcReponse.msg);
                } catch (Exception e) {
                    if (null != callback)
                        callback.onFaild("exception:" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if (null == iLifeWeakReference.get() || iLifeWeakReference.get().isDestroyed()) {
                    return;
                }
                if (null != callback)
                    callback.onFaild(throwable.toString());
            }
        });
    }

}
