package com.robam.common.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.robam.common.R;

//全屏dialog
public class FullDialog extends Dialog {
    private Window mWindow = null;
    private View mView = null;
    private boolean isTouchToDismiss = true;
    private Context mContext = null;

    public FullDialog(Context context, View view) {
        super(context, R.style.common_fullDialog);
        mView = view;
        mWindow = getWindow();
        mContext = context;
    }


    public FullDialog(Context context, View view, boolean isDismiss) {
        super(context, R.style.common_fullDialog);
        mContext = context;
        mView = view;
        mWindow = getWindow();
        isTouchToDismiss = isDismiss;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(mView);
        //全屏
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.getDecorView().setPadding(0, 0, 0, 0);
        mWindow.setAttributes(layoutParams);
    }
}
