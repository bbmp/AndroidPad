package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

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

    private void updateVersion() {
        if (null == updateDialog) {
            updateDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
            updateDialog.setCancelable(false);
            updateDialog.setContentText(R.string.ventilator_update_version_hint);
            updateDialog.setOKText(R.string.ventilator_update);
            updateDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    showProressDialog();
                    if (v.getId() == R.id.tv_ok) {
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