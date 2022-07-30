package com.robam.roki.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.robam.common.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.common.view.ExtWebView;
import com.robam.roki.R;
import com.robam.roki.utils.PageArgumentKey;

//普通h5
public class WebActivity extends BaseActivity {
    private ExtWebView webView;
    private ImageView ivBack;
    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_web;
    }

    @Override
    protected void initView() {
        webView = (ExtWebView) findViewById(R.id.webView);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        url = getIntent().getStringExtra(PageArgumentKey.Url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setCallback(new ExtWebView.Callback() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {

                LogUtils.i("title:" + title);

            }

            @Override
            public void onReceivedError(WebView webView, int errorCode,
                                        String description, String failingUrl) {
                ToastUtils.showShort(WebActivity.this, description);
            }
        });

    }

    @Override
    protected void initData() {

        webView.loadUrl(url);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
