package com.robam.common.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.robam.common.R;
import com.robam.common.ui.action.ClickAction;

public abstract class BaseDialog implements IDialog{
    protected Context mContext;
    protected FullDialog mDialog;
    protected View rootView;

    protected abstract void initView();


    public BaseDialog(Context context) {
        mContext = context;
        initView();
    }

    @Override
    public void setCancelable(boolean b) {
        if (mDialog != null) mDialog.setCancelable(b);
    }

    @Override
    public boolean isShow() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }

    @Override
    public void show() {
        if (mDialog != null) {

            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    mDialog.show();
                }
            } else {
                mDialog.show();
            }
        }
    }

    @Override
    public void dismiss() {
        if (mDialog != null) mDialog.dismiss();
    }

    @Override
    public void setContentText(int contentStrId) {

    }

    @Override
    public void setContentText(CharSequence contentStr) {

    }

    @Override
    public void setCancelText(int res) {

    }

    @Override
    public void setOKText(int res) {

    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public void setListeners(DialogOnClickListener onClickListener, int... viewIds) {
        if (null != rootView) {
            for (int i = 0; i < viewIds.length; i++) {
                rootView.findViewById(viewIds[i]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onClickListener)
                            onClickListener.onClick(v);
                        if (null != mDialog)
                            mDialog.dismiss();
                    }
                });
            }
        }
    }
}
