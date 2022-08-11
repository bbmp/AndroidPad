package com.robam.pan.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.pan.R;

public class SelectStoveDialog extends BaseDialog {
    protected TextView mContent;

    public SelectStoveDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.pan_dialog_layout_select_stove, null);
        mContent = rootView.findViewById(R.id.tv_work_content);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }
}
