package com.robam.cabinet.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.cabinet.R;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;

public class LockDialog extends BaseDialog {
    private ImageView ivLock;

    public LockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.cabinet_dialog_layout_screen_lock, null);
        rootView.findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
        TextView tvRightCenter = rootView.findViewById(R.id.tv_right_center);
        tvRightCenter.setText("");
        ImageView ivRightCenter = rootView.findViewById(R.id.iv_right_center);
        ivRightCenter.setImageDrawable(null);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }

    }

}
