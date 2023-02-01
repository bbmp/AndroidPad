package com.robam.ventilator.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.utils.WindowsUtils;
import com.robam.ventilator.R;

public class UpdateDialog extends BaseDialog {
    private TextView tvUpdate;

    public UpdateDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.ventilator_dialog_layout_update, null);
        tvUpdate = rootView.findViewById(R.id.tv_updating);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
        //关闭浮窗
        WindowsUtils.hidePopupWindow();
    }

    @Override
    public void setContentText(int contentStrId) {
        tvUpdate.setText(contentStrId);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //显示浮窗
        WindowsUtils.showPopupWindow();
    }
}
