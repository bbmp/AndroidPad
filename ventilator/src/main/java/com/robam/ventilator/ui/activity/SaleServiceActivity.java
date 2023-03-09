package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.device.Plat;
import com.robam.common.http.DownloadListener;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.BuildConfig;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.AppTypeRes;
import com.robam.ventilator.ui.dialog.UpdateDialog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaleServiceActivity extends VentilatorBaseActivity {
    private TextView tvSys;
    private TextView tvSysV;
    private TextView tvModelV;
    private TextView tvNewVersion;
    private String versionUrl;
    private IDialog updateDialog;
    private UpdateDialog progressDialog;
    int curProgress = 0;
    String FIRMWARE = "firmware";
    String APK = "apk";
    private String updateMode = FIRMWARE;
    private int COUNT = 5;
    private long DURATION = 3* 1000;
    private long[] mHits = new long[COUNT];

    private long[] mBuildTime = new long[COUNT];

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_sale_service;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        tvSys = findViewById(R.id.tv_sys);
        tvSysV = findViewById(R.id.tv_sys_v);
        tvModelV = findViewById(R.id.tv_model_v);
        tvNewVersion = findViewById(R.id.tv_newVersion);
        tvNewVersion.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvNewVersion.getPaint().setAntiAlias(true);//抗锯齿
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_product);
        setOnClickListener(R.id.ll_left, R.id.tv_newVersion);
    }

    @Override
    protected void initData() {
//        try {
//            String app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
//            tvSysV.setText(app_ver);
//        } catch (Exception e) {}
        tvSysV.setText(Plat.getPlatform().getFirmwareVersion());

        tvModelV.setText(BuildConfig.MODEL);
        tvSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mBuildTime, 1, mBuildTime, 0, mBuildTime.length - 1);
                mBuildTime[mBuildTime.length - 1] = System.currentTimeMillis();
                if (mBuildTime[0] >= (System.currentTimeMillis() - DURATION)) {
                    long[] hits = new long[COUNT];
                    System.arraycopy(hits, 0, mBuildTime, 0, mBuildTime.length);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(Build.TIME);
                    ToastUtils.showLong(getApplicationContext(), simpleDateFormat.format(date));
                }
            }
        });
        tvSysV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = System.currentTimeMillis();
                if (mHits[0] >= (System.currentTimeMillis() - DURATION)) {
                    long[] hits = new long[COUNT];
                    System.arraycopy(hits, 0, mHits, 0, mHits.length);
                    goHome();
                }
            }
        });

        checkFirmware();
    }
    //进入工程测试
    private void goHome() {
        ComponentName componetName = new ComponentName(
//这个是另外一个应用程序的包名
                "com.ayst.factorytest",
//这个参数是要启动的Activity
                "com.ayst.factorytest.MainActivity");

        Intent intent = new Intent();
        intent.setComponent(componetName);
        startActivity(intent);
    }

    //检查固件
    private void checkFirmware() {
        CloudHelper.checkAppVersion(this, "RKPAD", Plat.getPlatform().getDt(), FIRMWARE, AppTypeRes.class, new RetrofitCallback<AppTypeRes>(){

            @Override
            public void onSuccess(AppTypeRes appTypeRes) {
                if (null != appTypeRes && null != appTypeRes.ver && null != appTypeRes.ver.url) {
                    String curVer = Plat.getPlatform().getFirmwareVersion();
                    try {
                        curVer = curVer.replace(".", "");
                        if (Integer.parseInt(curVer) != appTypeRes.ver.code) { //版本不一致
                            LogUtils.e(appTypeRes.ver.url);
                            updateMode = FIRMWARE; //更新固件
                            versionUrl = appTypeRes.ver.url;
                            String version = String.format(getString(R.string.ventilator_new_version), appTypeRes.ver.code + "");
                            tvNewVersion.setVisibility(View.VISIBLE);
                            tvNewVersion.setText(version);
                            return;
                        }
                    } catch (Exception e) {}
                }

                checkApk();
            }

            @Override
            public void onFaild(String err) {
                checkApk();
            }
        });
    }
    //检查apk
    private void checkApk() {
        CloudHelper.checkAppVersion(this, "RKPAD", Plat.getPlatform().getDt(), APK, AppTypeRes.class, new RetrofitCallback<AppTypeRes>() {
            @Override
            public void onSuccess(AppTypeRes appTypeRes) {
                if (null != appTypeRes && null != appTypeRes.ver && null != appTypeRes.ver.url) {
                    String curVer = DeviceUtils.getVersionName(getApplicationContext());
                    try {
                        curVer = curVer.replace(".", "");
                        if (Integer.parseInt(curVer) != appTypeRes.ver.code) { //版本不一致
                            LogUtils.e(appTypeRes.ver.url);
                            updateMode = APK; //更新应用
                            versionUrl = appTypeRes.ver.url;
                            String version = String.format(getString(R.string.ventilator_new_version), appTypeRes.ver.code + "");
                            tvNewVersion.setVisibility(View.VISIBLE);
                            tvNewVersion.setText(version);
                        }
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
        else if (id == R.id.tv_newVersion) {
            //下载
            updateVersion();
        }
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (null != progressDialog) {
                progressDialog.setProgress(curProgress);
            }
        }
    };
    private boolean installSilent(String path) {
        boolean result = false;
        BufferedReader es = null;
        DataOutputStream os = null;

        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            String command = "pm install -r " + path + "\n";
            os.write(command.getBytes(Charset.forName("utf-8")));
            os.flush();
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();
            es = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = es.readLine()) != null) {
                builder.append(line);
            }

        /* Installation is considered a Failure if the result contains
            the Failure character, or a success if it is not.
             */
            if (!builder.toString().contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {

        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (es != null) {
                    es.close();
                }
            } catch (IOException e) {

            }
        }

        return result;
    }

    private void updateVersion() {
        if (null == updateDialog) {
            updateDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
            updateDialog.setCancelable(false);
            updateDialog.setContentText(R.string.ventilator_update_version_hint);
            updateDialog.setOKText(R.string.ventilator_update);
            updateDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        String fileName = "update.zip";
                        if (FIRMWARE.equals(updateMode))
                            fileName = "update.zip";
                        else
                            fileName = "update.apk";
                        showProressDialog();
                        CloudHelper.downloadFile(SaleServiceActivity.this, getApplicationContext(), versionUrl, fileName, new DownloadListener() {
                            @Override
                            public void onProgress(int progress) {
                                LogUtils.e("thread=" + Thread.currentThread().getName() + " process=" + progress);
                                HomeVentilator.getInstance().updateOperationTime(); //防止5分钟关机
                                curProgress = progress;
                                runOnUiThread(updateProgress);
                            }

                            @Override
                            public void onFinish(String path) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeProgressDialog();
                                    }
                                });

                                HomeVentilator.getInstance().updateOperationTime();
                                if (FIRMWARE.equals(updateMode)) {
                                    if (Plat.getPlatform().verifyPackage(path)) {
                                        Plat.getPlatform().intallPackage(path);
                                    }
                                } else {
                                    //静默安装
                                    ComponentName componetName = new ComponentName(
                                            //这个是另外一个应用程序的包名
                                            "com.robam.update",
                                            //这个参数是要启动的Activity
                                            "com.robam.update.UpdateService");

                                    Intent intent = new Intent();
                                    intent.setComponent(componetName);
                                    startService(intent);
//                                    if (installSilent(path)) {
                                        //安装成功，重启应用
//                                        Intent intent = new Intent();
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        intent.setClassName(getApplicationContext(), "com.robam.androidpad.MainActivity");
//                                        startActivity(intent);
//
//                                        android.os.Process.killProcess(android.os.Process.myPid());
//                                        System.exit(10);
//                                    }
                                }
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                HomeVentilator.getInstance().updateOperationTime();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeProgressDialog();
                                        ToastUtils.showShort(getApplicationContext(), "下载失败");
                                    }
                                });
                            }
                        });

                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }

        updateDialog.show();
    }

    private void showProressDialog() {
        if (null == progressDialog) {
            progressDialog = new UpdateDialog(getContext());
            progressDialog.setCancelable(false);
        }
        //初始进度
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != updateDialog && updateDialog.isShow())
            updateDialog.dismiss();
        closeProgressDialog();
    }

    private void closeProgressDialog() {
        if (null != progressDialog && progressDialog.isShow())
            progressDialog.dismiss();
    }
}