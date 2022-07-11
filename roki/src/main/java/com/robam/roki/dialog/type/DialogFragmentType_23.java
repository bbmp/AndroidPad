package com.robam.roki.dialog.type;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.robam.common.utils.ScreenUtils;
import com.robam.roki.R;
import com.robam.roki.cloud.IRokiRestService;
import com.robam.roki.ui.UIService;
import com.robam.roki.utils.PageArgumentKey;

public class DialogFragmentType_23 extends DialogFragment {
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roki_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView == null){
            //获取布局
            mRootView = getLayoutInflater().inflate(R.layout.roki_dialog_layout_type_23,container,false);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String contentStr = getString(R.string.roki_privacy_policy_content);
            ssb.append(contentStr);
            final int start = contentStr.indexOf("《");//第一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String url = String.format("%s", IRokiRestService.UserNotice);
                    Bundle bd = new Bundle();
                    bd.putString(PageArgumentKey.Url, url);
                    bd.putString(PageArgumentKey.WebTitle, "ROKI用户协议");
                    NavController navController = NavHostFragment.findNavController(DialogFragmentType_23.this);
                    navController.navigate(R.id.action_webpage, bd);
//                    UIService.postPage(widget, R.id.action_webpage, bd);
//                UIService.getInstance().postPage(PageKey.WebClientNew, bd);
//                dismiss();
//                WebActivity.start(mContext ,url);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 去掉下划线
                    ds.setColor(Color.parseColor("#ff61acff"));
                    ds.setUnderlineText(false);
                }

            }, start, start + 10, 0);

            final int end = contentStr.lastIndexOf("《");//最后一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String url = String.format("%s", IRokiRestService.RegisterAgreement);
                    Bundle bd = new Bundle();
                    bd.putString(PageArgumentKey.Url, url);
                    bd.putString(PageArgumentKey.WebTitle, "ROKI用户协议");
//                WebActivity.start(mContext ,url);
                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 去掉下划线
                    ds.setColor(Color.parseColor("#ff61acff"));
                    ds.setUnderlineText(false);
                }

            }, end, end + 6, 0);
            TextView tvContent = mRootView.findViewById(R.id.common_dialog_content_text);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            tvContent.setText(ssb);
        }

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                autoAdjustSize();
            }
        });
    }

    /**
     * 自动调整高度
     */
    private void autoAdjustSize() {
        // 高度不超过屏幕高度的80%
        int maxHeight = (int) (ScreenUtils.getHeightPixels(getContext()) * 0.8);
        int height = mRootView.getHeight();

        if (height > maxHeight) {
            height = maxHeight;

        }
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.roki_transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        //设置窗口的宽度为 MATCH_PARENT,效果是和屏幕宽度一样大
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = height;
        window.setAttributes(wlp);
    }
}
