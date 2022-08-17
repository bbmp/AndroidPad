package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.stove.R;

public class CurveEditDialog extends BaseDialog {
    private LinearLayout llLeftCenter;
    private LinearLayout llCenter;

    public CurveEditDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.stove_activity_layout_curve_edit, null);
        llLeftCenter = rootView.findViewById(R.id.ll_left_center);
        llLeftCenter.setVisibility(View.VISIBLE);
        llCenter = rootView.findViewById(R.id.ll_center);
        llCenter.setVisibility(View.VISIBLE);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
}