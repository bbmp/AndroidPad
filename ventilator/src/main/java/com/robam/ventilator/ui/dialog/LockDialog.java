package com.robam.ventilator.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.ventilator.R;

public class LockDialog extends BaseDialog {
    public LockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.ventilator_dialog_layout_lock, null);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
}