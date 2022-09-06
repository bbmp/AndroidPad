package com.robam.pan.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.utils.WindowsUtils;
import com.robam.pan.R;

public class CurveEidtDialog extends BaseDialog {
    private LinearLayout llLeftCenter;
    private LinearLayout llCenter;
    public CurveEidtDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.pan_activity_layout_curve_edit, null);
        llCenter = rootView.findViewById(R.id.ll_center);
        llCenter.setVisibility(View.VISIBLE);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
        //关闭浮窗
        WindowsUtils.hidePopupWindow();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //显示浮窗
        WindowsUtils.showPopupWindow();
    }
}
