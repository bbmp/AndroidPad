package com.robam.roki.dialog.type;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robam.roki.R;
import com.robam.roki.activity.ui.WebActivity;
import com.robam.roki.cloud.IRokiRestService;
import com.robam.roki.dialog.BaseDialog;
import com.robam.roki.dialog.CoreDialog;
import com.robam.roki.ui.UIService;
import com.robam.roki.utils.PageArgumentKey;

public class DialogType_23 extends BaseDialog {
    private Button mCancelTv;
    private Button mOkTv;
    protected TextView mContxt;
    private TextView mTitleTv;

    public DialogType_23(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.roki_dialog_layout_type_23, null);
        mTitleTv = rootView.findViewById(R.id.common_dialog_title_text);
        mCancelTv = rootView.findViewById(R.id.common_dialog_cancel_btn);
        mOkTv = rootView.findViewById(R.id.common_dialog_ok_btn);
        mContxt = rootView.findViewById(R.id.common_dialog_content_text);
        if (mDialog == null) {
            mDialog = new CoreDialog(mContext, R.style.roki_dialog, rootView, true);
            mDialog.setPosition(Gravity.CENTER, 0, 0);
        }
    }




    @Override
    public void setContentText(int contentStrId) {
        super.setContentText(contentStrId);
        mContxt.setText(contentStrId);
    }


    @Override
    public void setContentText(CharSequence contentStr) {
        super.setContentText(contentStr);
        mContxt.setText(contentStr);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(contentStr);
        final int start = contentStr.toString().indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = String.format("%s", IRokiRestService.UserNotice);
//                Bundle bd = new Bundle();
//                bd.putString(PageArgumentKey.Url, url);
//                bd.putString(PageArgumentKey.WebTitle, "ROKI用户协议");
//                UIService.postPage(widget, R.id.action_webpage, bd);
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra(PageArgumentKey.Url, url);
                intent.putExtra(PageArgumentKey.WebTitle, "ROKI用户协议");
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 去掉下划线
                ds.setColor(Color.parseColor("#ff61acff"));
                ds.setUnderlineText(false);
            }

        }, start, start + 10, 0);

        final int end = contentStr.toString().lastIndexOf("《");//最后一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = String.format("%s", IRokiRestService.RegisterAgreement);
//                Bundle bd = new Bundle();
//                bd.putString(PageArgumentKey.Url, url);
//                bd.putString(PageArgumentKey.WebTitle, "ROKI用户协议");
//                WebActivity.start(mContext ,url);
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra(PageArgumentKey.Url, url);
                intent.putExtra(PageArgumentKey.WebTitle, "ROKI用户协议");
                mContext.startActivity(intent);
            }

            @Override

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 去掉下划线
                ds.setColor(Color.parseColor("#ff61acff"));
                ds.setUnderlineText(false);
            }

        }, end, end + 6, 0);
        mContxt.setMovementMethod(LinkMovementMethod.getInstance());
        mContxt.setText(ssb);
        avoidHintColor(mContxt);
    }

    private void avoidHintColor(View view) {
        if (view instanceof TextView)
            ((TextView) view).setHighlightColor(Color.TRANSPARENT);
    }
}
