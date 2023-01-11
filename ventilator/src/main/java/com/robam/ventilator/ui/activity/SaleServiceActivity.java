package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.device.Plat;
import com.robam.common.http.DownloadListener;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.BuildConfig;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.AppTypeRes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class SaleServiceActivity extends VentilatorBaseActivity {
    private TextView tvSysV;
    private TextView tvModelV;
    private TextView tvNewVersion;
    private String versionUrl;
    private IDialog updateDialog, progressDialog;
    int curProgress = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_sale_service;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        tvSysV = findViewById(R.id.tv_sys_v);
        tvModelV = findViewById(R.id.tv_model_v);
        tvNewVersion = findViewById(R.id.tv_newVersion);
        tvNewVersion.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvNewVersion.getPaint().setAntiAlias(true);//抗锯齿
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_saleservice);
        setOnClickListener(R.id.ll_left, R.id.tv_newVersion);
    }

    @Override
    protected void initData() {
        tvSysV.setText(BuildConfig.VERSION_NAME);
        tvModelV.setText(BuildConfig.MODEL);
        CloudHelper.checkAppVersion(this, "RKPAD", Plat.getPlatform().getDt(), AppTypeRes.class, new RetrofitCallback<AppTypeRes>() {
            @Override
            public void onSuccess(AppTypeRes appTypeRes) {
                if (null != appTypeRes && null != appTypeRes.ver && null != appTypeRes.ver.url) {
                    LogUtils.e(appTypeRes.ver.url);
                    versionUrl = appTypeRes.ver.url;
                    String version = String.format(getString(R.string.ventilator_new_version), appTypeRes.ver.code + "");
                    tvNewVersion.setVisibility(View.VISIBLE);
                    tvNewVersion.setText(version);
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
                ProgressBar progressBar = progressDialog.getRootView().findViewById(R.id.sbr_progress);
                progressBar.setProgress(curProgress);
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
                        showProressDialog();
                        CloudHelper.downloadFile(getApplicationContext(), versionUrl, new DownloadListener() {
                            @Override
                            public void onProgress(int progress) {
                                LogUtils.e("thread=" + Thread.currentThread() + " process=" + progress);
                                curProgress = progress;
                                runOnUiThread(updateProgress);
                            }

                            @Override
                            public void onFinish(String path) {
                                closeProgressDialog();
                                //静默安装
                                if (installSilent(path)) {
                                    //安装成功，重启应用
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClassName(getApplicationContext(), "com.robam.androidpad.MainActivity");
                                    startActivity(intent);

                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(10);
                                }
                            }

                            @Override
                            public void onFail(String errorInfo) {
                                closeProgressDialog();
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
            progressDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_UPDATE_VERSION);
            progressDialog.setCancelable(false);
        }
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