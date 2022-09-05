package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.stove.R;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveConstant;

public class LockDialog extends BaseDialog {
    private LinearLayout llLeftStove, llRightStove;
    private TextView tvLeftStove, tvRightStove;

    public LockDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.stove_dialog_layout_lock, null);
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
        if (Stove.getInstance().leftWorkMode != 0) {
            llLeftStove.setVisibility(View.VISIBLE);
            if (Stove.getInstance().leftWorkMode == StoveConstant.MODE_FRY)
                tvLeftStove.setText("左灶 " + Stove.getInstance().leftWorkTemp + "℃");
            else
                tvLeftStove.setText("左灶 " + Stove.getInstance().leftWorkHours + "min");
        }
        //右灶工作中
        if (Stove.getInstance().rightWorkMode != 0) {
            llRightStove.setVisibility(View.VISIBLE);
            if (Stove.getInstance().rightWorkMode == StoveConstant.MODE_FRY)
                tvRightStove.setText("右灶 " + Stove.getInstance().rightWorkTemp + "℃");
            else
                tvRightStove.setText("右灶 " + Stove.getInstance().rightWorkHours + "min");
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
