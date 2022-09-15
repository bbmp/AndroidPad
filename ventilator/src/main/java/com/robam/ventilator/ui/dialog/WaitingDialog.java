package com.robam.ventilator.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.ventilator.R;

public class WaitingDialog extends BaseDialog {
    private ImageView ivWaiting;
    private Animation imgAnimation;

    public WaitingDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.ventilator_dialog_layout_waiting, null);
        ivWaiting = rootView.findViewById(R.id.iv_waiting);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }

    @Override
    public void show() {
        super.show();
        imgAnimation = AnimationUtils.loadAnimation(mContext, R.anim.ventilator_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        imgAnimation.setInterpolator(lin);
        ivWaiting.startAnimation(imgAnimation);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (imgAnimation != null) {
            imgAnimation.cancel();
            ivWaiting.clearAnimation();
            imgAnimation = null;
        }
    }

}
