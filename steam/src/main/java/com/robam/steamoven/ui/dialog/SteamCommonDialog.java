package com.robam.steamoven.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.steamoven.R;

public class SteamCommonDialog extends BaseDialog {
    private TextView mCancelTv;
    private TextView mOkTv;
    private TextView mContent;

    public SteamCommonDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.steam_dialog_layout_common, null);
        mCancelTv = rootView.findViewById(R.id.tv_cancel);
        mOkTv = rootView.findViewById(R.id.tv_ok);
        mContent = rootView.findViewById(R.id.tv_work_content);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
    @Override
    public void setContentText(int res) {
        mContent.setText(res);
    }

    @Override
    public void setCancelText(int res) {
        mCancelTv.setText(res);
    }

    @Override
    public void setOKText(int res) {
        mOkTv.setText(res);
    }
}
