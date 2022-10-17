package com.robam.steamoven.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.R;

public class SelectStoveDialog extends BaseDialog {
    private TextView mContent;
    private View viewLeft, viewRight;
    //左灶和状态
    private TextView tvLeftStove, tvLeftStatus;
    //右灶和状态
    private TextView tvRightStove, tvRightStatus;
    //左灶和右灶提示
    private TextView tvLeftClose, tvRightClose;

    public SelectStoveDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.steam_dialog_layout_select_stove, null);
        mContent = rootView.findViewById(R.id.tv_work_content);
        viewLeft = rootView.findViewById(R.id.view_left);
        tvLeftStove = rootView.findViewById(R.id.tv_left_stove);
        tvLeftStatus = rootView.findViewById(R.id.tv_left_status);
        tvLeftClose = rootView.findViewById(R.id.tv_left_close);
        viewRight = rootView.findViewById(R.id.view_right);
        tvRightStove = rootView.findViewById(R.id.tv_right_stove);
        tvRightStatus = rootView.findViewById(R.id.tv_right_status);
        tvRightClose = rootView.findViewById(R.id.tv_right_close);

        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }

    //检查炉头状态
    public void checkStoveStatus() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                Stove stove = (Stove) device;
//                if (stove.leftStatus != StoveConstant.STOVE_CLOSE && stove.leftLevel != 0) {  //工作中
//                    viewLeft.setEnabled(false);
//                    tvLeftStove.setEnabled(false);
//                    tvLeftStatus.setEnabled(false);
//                    tvLeftStatus.setText(R.string.st_stove_using);
//                    tvLeftClose.setVisibility(View.VISIBLE);
//                }
//                if (stove.rightStatus != StoveConstant.STOVE_CLOSE && stove.rightLevel != 0) {
//                    //工作中
//                    viewRight.setEnabled(false);
//                    tvRightStove.setEnabled(false);
//                    tvRightStatus.setEnabled(false);
//                    tvRightStatus.setText(R.string.stove_stove_using);
//                    tvRightClose.setVisibility(View.VISIBLE);
//                }
                break;
            }
        }
    }
}