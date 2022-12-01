package com.robam.ventilator.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.utils.QrUtils;
import com.robam.ventilator.R;

public class ShareDialog extends BaseDialog {
    private ImageView ivCode;

    public ShareDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.ventilator_dialog_layout_share, null);
        ivCode = rootView.findViewById(R.id.iv_qrcode);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }

        setListeners(new DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //点击任意地方关闭
                dismiss();
            }
        }, R.id.share_dialog);
    }

    @Override
    public void setContentText(CharSequence contentStr) {
        Bitmap imgBit = QrUtils.create2DCode(contentStr.toString(), (int)mContext.getResources().getDimension(com.robam.common.R.dimen.dp_194),
                (int)mContext.getResources().getDimension(com.robam.common.R.dimen.dp_194), Color.WHITE);

        ivCode.setImageBitmap(imgBit);

    }
}
