package com.robam.cabinet.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;

public class WorkCompleteDialog extends BaseDialog {
    protected TextView mContent;

    public WorkCompleteDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.cabinet_dialog_layout_work_complete, null);
        mContent = rootView.findViewById(R.id.tv_work_content);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }


    @Override
    public void setContentText(CharSequence contentStr) {
        mContent.setText(contentStr);
    }
}
