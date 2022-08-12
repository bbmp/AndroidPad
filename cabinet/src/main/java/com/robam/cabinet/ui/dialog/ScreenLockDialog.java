package com.robam.cabinet.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.robam.cabinet.R;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;

public class ScreenLockDialog extends BaseDialog {
    private ImageView ivLock;

    public ScreenLockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.cabinet_dialog_layout_screen_lock, null);
        ivLock = rootView.findViewById(R.id.iv_screen_lock);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }

    }

}
