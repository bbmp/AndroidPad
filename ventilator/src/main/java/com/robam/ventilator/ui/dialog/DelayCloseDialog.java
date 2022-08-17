package com.robam.ventilator.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.ventilator.R;

/**
 * 延时关机
 */
public class DelayCloseDialog extends BaseDialog {
    private TextView mCancelTv;
    private TextView mOkTv;
    protected TextView mContent;
    private MCountdownView tvCountdown;

    public DelayCloseDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.ventilator_dialog_layout_delay_close, null);
        tvCountdown = rootView.findViewById(R.id.tv_countdown);
        mCancelTv = rootView.findViewById(R.id.tv_cancel);
        mOkTv = rootView.findViewById(R.id.tv_ok);
        mContent = rootView.findViewById(R.id.tv_work_content);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }


    /**
     * 设置倒计时
     */
    //在外面调用
    public void setCountDownTime() {

        int totalTime = 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
            @Override
            public void onCountDown(int currentSecond) {
//                SteamOven.getInstance().orderLeftTime = currentSecond;



                tvCountdown.setText(currentSecond + "s");
                if (currentSecond <= 0)
                    ;

            }
        });
        tvCountdown.start();
    }
}
