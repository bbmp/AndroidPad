package com.robam.roki.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.common.ui.view.ExtWebView;
import com.robam.roki.R;
import com.robam.roki.utils.PageArgumentKey;

public class RWebActivity extends BaseActivity {
    private ExtWebView evKitchenKnowledgAct;
    private FrameLayout flVideo;
    private String url = "https://h5.myroki.com/#/kitchenKnowledge";
    private String KIRCHEN_SOURCE_ACT_URL = "https://h5.myroki.com/#/chuYuanActivity";//分享链接
    private String mActiveUrl;
    private String mShareImgUrl;
    private String mShareTitle;
    private String mShareText;
    private String mVideoUrl;
    private String mShareId;
    private WebChromeClient.CustomViewCallback mCallback;


    @Override
    protected int getLayoutId() {
        return R.layout.roki_activity_layout_rweb;
    }

    @Override
    protected void initView() {
        evKitchenKnowledgAct = findViewById(R.id.ev_kitchen_knowledg_act);
        flVideo = findViewById(R.id.fl_video);
    }

    @Override
    protected void initData() {
        url = getIntent().getStringExtra(PageArgumentKey.Url);
        setiingWebToJS();
//        boolean logon = Plat.accountService.isLogon();
//        Long userId = Plat.accountService.getCurrentUserId();

//        if (logon) {
//            evKitchenKnowledgAct.loadUrl(url + "?userId=" + userId);
//            evKitchenKnowledgAct.loadUrl(url + "?userId=" + userId  + "&isApp=true");
//        } else {
            evKitchenKnowledgAct.loadUrl(url  + "?isApp=true");
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && evKitchenKnowledgAct.canGoBack()) {
            if (evKitchenKnowledgAct != null) {
                evKitchenKnowledgAct.goBack();// 返回前一个页面
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setiingWebToJS() {

        WebSettings settings = evKitchenKnowledgAct.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        settings.setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);

        evKitchenKnowledgAct.addJavascriptInterface(new CallMethodFromAndroidLister(this), "RecipeDetail");
        evKitchenKnowledgAct.setWebChromeClient(new CustomWebViewChromeClient());
        evKitchenKnowledgAct.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                WebView.HitTestResult hit = view.getHitTestResult();
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (TextUtils.isEmpty(hit.getExtra()) || hit.getType() == 0) {
                    //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                    Log.e("重定向", "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                    Log.e("重定向", "GetURL: " + view.getUrl() + "\n" +"getOriginalUrl()"+ view.getOriginalUrl());
                    Log.d("重定向", "URL: " + url);
                }
                String directUrl = request.getUrl().toString();
                if (directUrl.startsWith("http://") || directUrl.startsWith("https://")) { //加载的url是http/https协议地址
                    view.loadUrl(directUrl);
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走

                } else { //加载的url是自定义协议地址
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            }
        });
    }

    class CallMethodFromAndroidLister {
        Context context;

        public CallMethodFromAndroidLister(Context context) {
            this.context = context;
        }

        /**
         * 返回方法
         */
        @JavascriptInterface
        public void goback(boolean b) {
            evKitchenKnowledgAct.post(new Runnable() {
                @Override
                public void run() {
                    if (evKitchenKnowledgAct.canGoBack()) {
                        evKitchenKnowledgAct.goBack();
                    } else {
                        finish();
                    }
                }
            });

        }

        /**
         * 分享文章方法
         */
        @JavascriptInterface
        public void shareActive(String id, String imgUrl, String title, String text) {
            mActiveUrl = KIRCHEN_SOURCE_ACT_URL + "/articleShare?id=" + id;
            mShareImgUrl = imgUrl;
            mShareTitle = title;
            mShareText = text;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int selfPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (selfPermission == 0) {
//                    KitchenSourceShareDialog.show(context, mActiveUrl, imgUrl, title, text);
//                } else {
//                    PermissionsUtils.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, PermissionsUtils.CODE_KITCHEN_SHARE_ACTIVE);
//                }
//            } else {
//                KitchenSourceShareDialog.show(context, mActiveUrl, imgUrl, title, text);
//            }
        }

        /**
         * 分享视频方法
         */
        @JavascriptInterface
        public void shareVideo(String id, String url, String imgUrl, String title, String text) {
            mVideoUrl = KIRCHEN_SOURCE_ACT_URL + "/videoShare?id=" + id + "&videoUrl=" + url;
            mShareId = id;
            mShareImgUrl = imgUrl;
            mShareTitle = title;
            mShareText = text;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int selfPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (selfPermission == 0) {
//                    KitchenSourceShareDialog.show(context, id, mVideoUrl, imgUrl, title, text);
//                } else {
//                    PermissionsUtils.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, PermissionsUtils.CODE_KITCHEN_SHARE_VIDEO);
//                }
//            } else {
//                KitchenSourceShareDialog.show(context, id, mVideoUrl, imgUrl, title, text);
//            }


        }

        /**
         * 跳转到菜谱详情页面
         *
         * @param id
         */
        @JavascriptInterface
        public void cookDetail(String id) {

            LogUtils.i("id:" + id);
            long recipeId = Long.parseLong(id);
//            RecipeDetailPage.show(recipeId, RecipeDetailPage.unKnown);
        }

        /**
         * 删除评论
         *
         * @param id
         */
        @JavascriptInterface
        public void deleteComment(final String id) {

//            final IRokiDialog deleteCommentDialog = RokiDialogFactory.createDialogByType(RWebActivity.this, DialogUtil.DIALOG_TYPE_10);
//            deleteCommentDialog.setTitleText(R.string.is_delete_title);
//            deleteCommentDialog.setContentText(R.string.is_delete_content);
//            deleteCommentDialog.setOkBtn(R.string.ok_btn, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    deleteCommentDialog.dismiss();
//                    CookbookManager.getInstance().deleteKitComment(Long.parseLong(id), new VoidCallback() {
//                        @Override
//                        public void onSuccess() {
//                            //删除后刷新页面
//                            mHandler.sendEmptyMessage(0);
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//
//                        }
//                    });
//                }
//            });
//            deleteCommentDialog.setCancelBtn(R.string.can_btn, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    deleteCommentDialog.dismiss();
//                }
//            });
//            deleteCommentDialog.show();


        }
        /**
         * 提示
         *
         * @param text
         */
        @JavascriptInterface
        public void BombBox(String text) {
            ToastUtils.showShort(RWebActivity.this, text);
        }

    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    class CustomWebViewChromeClient extends WebChromeClient {

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            fullScreen();
            evKitchenKnowledgAct.setVisibility(View.GONE);
            flVideo.setVisibility(View.VISIBLE);
            flVideo.addView(view);
            mCallback = callback;

//            StatusBarUtils.setTextDark(RWebActivity.this , false);
//            StatusBarUtils.setColor(RWebActivity.this , Color.BLACK);
            RWebActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            fullScreen();
            if (mCallback!=null){
                mCallback.onCustomViewHidden();
            }
            evKitchenKnowledgAct.setVisibility(View.VISIBLE);
            flVideo.removeAllViews();
            flVideo.setVisibility(View.GONE);
//            StatusBarUtils.setTextDark(RWebActivity.this , true);
//            StatusBarUtils.setColor(RWebActivity.this , Color.WHITE);
            super.onHideCustomView();

        }
    }
}