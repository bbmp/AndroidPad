package com.robam.dishwasher.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.dishwasher.R;

public class WorkCompleteDialog extends BaseDialog {
    private TextView mContent;
    public WorkCompleteDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.dishwasher_dialog_layout_work_complete, null);
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
