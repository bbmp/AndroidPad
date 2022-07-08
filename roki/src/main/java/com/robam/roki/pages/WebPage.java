package com.robam.roki.pages;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.robam.common.ui.HeadPage;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.common.view.ExtWebView;
import com.robam.roki.R;
import com.robam.roki.utils.PageArgumentKey;

public class WebPage extends HeadPage {
    private ExtWebView webView;
    private ImageView ivBack;
    private String url;
    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_web;
    }

    @Override
    protected void initView() {
        setStateBarFixer();
        webView = (ExtWebView) findViewById(R.id.webView);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        url = bundle.getString(PageArgumentKey.Url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setCallback(new ExtWebView.Callback() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {

                LogUtils.i("title:" + title);

            }

            @Override
            public void onReceivedError(WebView webView, int errorCode,
                                        String description, String failingUrl) {
                ToastUtils.showShort(getContext(), description);
            }
        });

        webView.loadUrl(url);
    }

}
