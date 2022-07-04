package com.robam.roki.dialog.type;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robam.roki.R;
import com.robam.roki.dialog.BaseDialog;
import com.robam.roki.dialog.CoreDialog;

public class DialogType_24 extends BaseDialog {

    private Button mCancelTv;
    private Button mOkTv;
    protected TextView mContxt;

    public DialogType_24(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.roki_dialog_layout_type_24, null);
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
    }

}