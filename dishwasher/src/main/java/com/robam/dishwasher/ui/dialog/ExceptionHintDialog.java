package com.robam.dishwasher.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.dishwasher.R;

public class ExceptionHintDialog extends BaseDialog {

    public ExceptionHintDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.dishwasher_dialog_layout_exception, null);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }

        setListeners(new DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //点击任意地方关闭
                dismiss();
            }
        }, R.id.full_dialog);
    }
}
