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
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.action.ClickAction;

public abstract class BaseDialog implements IDialog{
    protected Context mContext;
    protected FullDialog mDialog;
    protected View rootView;
    private IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);

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
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
            //有触摸，更新操作时间
            if (null != iPublicVentilatorApi)
                iPublicVentilatorApi.updateOperationTime();
        }
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

    @Override
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

                        dismiss();
                    }
                });
            }
        }
    }
}
