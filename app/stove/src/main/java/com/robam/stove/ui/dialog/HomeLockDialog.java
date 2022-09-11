package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.stove.device.HomeStove;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.stove.R;
import com.robam.stove.constant.StoveConstant;

public class HomeLockDialog extends BaseDialog {
    private LinearLayout llLeftStove, llRightStove;
    private TextView tvLeftStove, tvRightStove;

    public HomeLockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.stove_dialog_layout_home_lock, null);
        llLeftStove = rootView.findViewById(R.id.ll_left_stove);
        llRightStove = rootView.findViewById(R.id.ll_right_stove);
        tvLeftStove = rootView.findViewById(R.id.tv_left_stove);
        tvRightStove = rootView.findViewById(R.id.tv_right_stove);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }

    //检查炉头状态
    public void checkStoveStatus() {
        //左灶工作中
        if (HomeStove.getInstance().leftWorkMode != 0) {
            llLeftStove.setVisibility(View.VISIBLE);
            if (HomeStove.getInstance().leftWorkMode == StoveConstant.MODE_FRY)
                tvLeftStove.setText("左灶 " + HomeStove.getInstance().leftWorkTemp + "℃");
            else
                tvLeftStove.setText("左灶 " + HomeStove.getInstance().leftWorkHours + "min");
        }
        //右灶工作中
        if (HomeStove.getInstance().rightWorkMode != 0) {
            llRightStove.setVisibility(View.VISIBLE);
            if (HomeStove.getInstance().rightWorkMode == StoveConstant.MODE_FRY)
                tvRightStove.setText("右灶 " + HomeStove.getInstance().rightWorkTemp + "℃");
            else
                tvRightStove.setText("右灶 " + HomeStove.getInstance().rightWorkHours + "min");
        }
    }

    //左灶停止工作
    public void closeLeftStove() {
        llLeftStove.setVisibility(View.INVISIBLE);
    }

    //右灶停止工作
    public void closeRightStove() {
        llRightStove.setVisibility(View.INVISIBLE);
    }
}
