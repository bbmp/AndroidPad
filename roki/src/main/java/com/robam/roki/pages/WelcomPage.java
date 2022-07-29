package com.robam.roki.pages;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import com.robam.common.ui.HeadPage;
import com.robam.common.ui.UIService;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.NetworkUtils;
import com.robam.common.utils.PreferenceUtils;
import com.robam.common.view.CountdownView2;
import com.robam.roki.R;
import com.robam.roki.ui.activity.HomeActivity;
import com.robam.roki.dialog.DialogUtils;
import com.robam.roki.dialog.IRokiDialog;
import com.robam.roki.factory.RokiDialogFactory;
import com.robam.roki.utils.PageArgumentKey;

public class WelcomPage extends HeadPage {
    private CountdownView2 cv_ring;
    private IRokiDialog privacyDialog;//隐私协议对话框
    private IRokiDialog exitDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_welcome;
    }

    @Override
    protected void initView() {
        //全屏
        setStateBarFixer();
        privacyDialog = RokiDialogFactory.createDialogByType(getContext(), DialogUtils.DIALOG_TYPE_23);
        exitDialog = RokiDialogFactory.createDialogByType(getContext(), DialogUtils.DIALOG_TYPE_24);

        cv_ring=findViewById(R.id.cv_ring);
        privacyPolicyDialog();
    }

    private void privacyPolicyDialog() {
        boolean isFirstUse = PreferenceUtils.getBool(getContext(), PageArgumentKey.IsFirstUse, true);
        LogUtils.i("isFirstUse:" + isFirstUse);
        if (isFirstUse) {
            String privacyContent = getContext().getString(R.string.roki_privacy_policy_content);
            privacyDialog.setCancelable(false);
            privacyDialog.setContentText(privacyContent);
            privacyDialog.setListeners(new IRokiDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v, int position) {
                    if (v.getId() == R.id.common_dialog_ok_btn) {
                        PreferenceUtils.setBool(getContext(), PageArgumentKey.IsFirstUse, false);
                        privacyDialog.dismiss();
                        if (NetworkUtils.isConnect(getContext())){
//                            WizardActivity.start(activity);
                            UIService.postPage(mRootView, R.id.action_wizardpage);
                        }else {
                            HomeActivity.start(getActivity());
                        }
                    } else if (v.getId() == R.id.common_dialog_cancel_btn) {
                        privacyDialog.dismiss();
                        showExitDialog();
                    }
                }
            }, R.id.common_dialog_ok_btn, R.id.common_dialog_cancel_btn);
            privacyDialog.show();

//            DialogFragmentType_23 dialogFragmentType_23 = new DialogFragmentType_23();
//            dialogFragmentType_23.showNow(getChildFragmentManager(), "type_23");

            return;
        }
        initAdvertData();

    }

    @Override
    protected void initData() {

    }

    private void showExitDialog() {
        exitDialog.setCancelable(false);
        exitDialog.setListeners(new IRokiDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (v.getId() == R.id.common_dialog_ok_btn) {
                    PreferenceUtils.setBool(getContext(), PageArgumentKey.IsFirstUse, false);
                    exitDialog.dismiss();
                    if (NetworkUtils.isConnect(getContext())){
                        UIService.postPage(mRootView, R.id.action_wizardpage);
                    }else {
                        HomeActivity.start(getActivity());
                    }
                } else if (v.getId() == R.id.common_dialog_cancel_btn) {
                    exitDialog.dismiss();
                    getActivity().finish();
                }
            }
        }, R.id.common_dialog_ok_btn, R.id.common_dialog_cancel_btn);
        exitDialog.show();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            getLaunchImage();
        }
    };

    private void initAdvertData() {
        cv_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNext();
            }
        });

        if (NetworkUtils.isConnect(getContext())){

            Message msg=new Message();
            handler.sendMessageDelayed(msg,1000);

        }
        else{
//            MainActivity.start(activity);
        }


    }

    private void getLaunchImage(){
//        StoreService.getInstance().getAppAdvertImg(new Callback<Reponses.AppAdvertImgResponses>() {
//            @Override
//            public void onSuccess(Reponses.AppAdvertImgResponses appAdvertImgResponses) {
//                List<Advert> images = appAdvertImgResponses.images;
//                for (Advert advert : images) {
//                    String imgUrl = advert.imgUrl;
//                    title = advert.title;
//                    mContent = advert.content;
//                    mType = advert.type;
//                    linkAction = advert.linkAction;
//                    secondTitle = advert.secondTitle;
//                    forwardImageUrl = advert.forwardImageUrl;
//                    if (imgUrl != null&&getActivity()!=null){
//                        GlideApp.with(cx)
//                                .load(imgUrl)
//                                .into(mImgAdvert);
////                        ImageUtils.displayImage(imgUrl, mImgAdvert);
//                    }
//                }
//                if (cv_ring!=null) {
//                    cv_ring.setVisibility(View.VISIBLE);
//                    cv_ring.start(new CountdownView2.StopLinstener() {
//                        @Override
//                        public void stop() {
//                            startNext();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
////                initAdvertData();
//                if (cv_ring != null){
//                    cv_ring.setVisibility(View.VISIBLE);
//                    cv_ring.start(new CountdownView2.StopLinstener() {
//                        @Override
//                        public void stop() {
//                            startNext();
//                        }
//                    });
//                }
//            }
//        });
        if (cv_ring != null){
            cv_ring.setVisibility(View.VISIBLE);
            cv_ring.start(new CountdownView2.StopLinstener() {
                @Override
                public void stop() {
                    startNext();
                }
            });
        }
    }

    private void startNext() {
        boolean isFirstUse = PreferenceUtils.getBool(getContext(), PageArgumentKey.IsFirstUse, true);
        LogUtils.i( "isFirstUse:" + isFirstUse);
        if (isFirstUse) {
            if (NetworkUtils.isConnect(getContext())) {
                UIService.postPage(mRootView, R.id.action_wizardpage);
            }else{
//                MainActivity.start(activity);
            }
        }else {
            HomeActivity.start(getActivity());
        }

    }

    @Override
    protected void setStateBarFixer() {

    }
}
