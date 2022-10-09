package com.robam.stove.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.utils.TimeUtils;
import com.robam.stove.bean.Stove;
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
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && device.guid.equals(HomeStove.getInstance().guid)) {
                Stove stove = (Stove) device;
                //左灶工作中
                if (stove.leftStatus == StoveConstant.STOVE_CLOSE || stove.leftLevel == 0) {
                    closeLeftStove();
                } else {
                    llLeftStove.setVisibility(View.VISIBLE);
                    if (stove.leftWorkMode == StoveConstant.MODE_FRY)
                        tvLeftStove.setText("左灶 " + stove.leftWorkTemp + "℃");
                    else
                        tvLeftStove.setText("左灶 " + stove.leftLevel + "档 " + TimeUtils.secToMin(stove.leftTimeHours));
                }
                //右灶工作中
                if (stove.rightStatus == StoveConstant.STOVE_CLOSE || stove.rightLevel == 0) {
                    closeRightStove();
                } else {
                    llRightStove.setVisibility(View.VISIBLE);
                    if (stove.rightWorkMode == StoveConstant.MODE_FRY)
                        tvRightStove.setText("右灶 " + stove.rightWorkTemp + "℃");
                    else
                        tvRightStove.setText("右灶 " + stove.rightLevel + "档 " + TimeUtils.secToMin(stove.rightTimeHours));
                }
                break;
            }
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
