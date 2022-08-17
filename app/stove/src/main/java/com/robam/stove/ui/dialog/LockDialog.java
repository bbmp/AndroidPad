package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.stove.R;

public class LockDialog extends BaseDialog {
    public LockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.stove_dialog_layout_lock, null);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
}
