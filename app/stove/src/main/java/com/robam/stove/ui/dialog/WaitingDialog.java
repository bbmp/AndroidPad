package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.stove.R;

public class WaitingDialog extends BaseDialog {
    public WaitingDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.stove_dialog_layout_waiting, null);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
}
