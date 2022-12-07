package com.robam.roki.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.Collection;
import java.util.List;


/**
 *    author : 210190
 *    robam
 *    time   : 2018/11/24
 *    desc   : Dialog 技术基类
 */
public abstract class BaseDialog implements IRokiDialog {
    protected Context mContext;
    protected CoreDialog mDialog;
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
    public void setTitle(int titleStrId) {

    }

    @Override
    public void setTitle(CharSequence titleStr) {

    }

    @Override
    public void setContentText(int contentStrId) {

    }

    @Override
    public void setContentText(CharSequence contentStr) {

    }

    @Override
    public void setContentList(List<?> list) {

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
                            onClickListener.onClick(v, -1);
                        if (null != mDialog)
                            mDialog.dismiss();
                    }
                });
            }
        }
    }
}
