package com.robam.cabinet.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robam.cabinet.R;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;

//停止工作
public class WorkStopDialog extends BaseDialog {
    private TextView mCancelTv;
    private TextView mOkTv;
    protected TextView mContent;

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.cabinet_dialog_layout_work_stop, null);
        mCancelTv = rootView.findViewById(R.id.tv_cancel);
        mOkTv = rootView.findViewById(R.id.tv_ok);
        mContent = rootView.findViewById(R.id.tv_work_content);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }

    public WorkStopDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setContentText(int res) {
        mContent.setText(res);
    }

}
