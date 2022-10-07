package com.robam.stove.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.dialog.LockDialog;

public abstract class StoveBaseActivity extends BaseActivity {
    private IDialog iDialogAffirm, ilockDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListener(R.id.ll_left, R.id.ll_right_center);
    }
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showLeftCenter() {
        findViewById(R.id.ll_left_center).setVisibility(View.VISIBLE);
    }
    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        ImageView ivWifi = findViewById(R.id.iv_center);
        //监听网络连接状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ivWifi.setVisibility(View.VISIBLE);
                else
                    ivWifi.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        findViewById(R.id.ll_right).setVisibility(View.INVISIBLE);
    }

    public void showRightCenter() {
        findViewById(R.id.ll_right_center).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right_center) {//童锁处理
            affirmLock();
        }
    }

    //锁屏确认
    private void affirmLock() {
        if (null == iDialogAffirm) {
            iDialogAffirm = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogAffirm.setCancelable(false);
            iDialogAffirm.setContentText(R.string.stove_affirm_lock_hint);
            iDialogAffirm.setOKText(R.string.stove_ok);
            iDialogAffirm.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        screenLock();
                        StoveAbstractControl.getInstance().setLock(StoveConstant.LOCK);
                    }
                    iDialogAffirm = null;
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogAffirm.show();
    }
    //锁屏
    private void screenLock() {
        TextView tvRightCenter = findViewById(R.id.tv_right_center);
        tvRightCenter.setTextColor(getResources().getColor(R.color.stove_step));
        ImageView ivRightCenter = findViewById(R.id.iv_right_center);
        ivRightCenter.setImageResource(R.drawable.stove_screen_lock);
        if (null == ilockDialog) {
            ilockDialog = new LockDialog(this);
            ilockDialog.setCancelable(false);
            ilockDialog.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort(getContext(), R.string.stove_unlock_hint);
                }
            });
            LinearLayout linearLayout = ilockDialog.getRootView().findViewById(R.id.ll_right_center);
            ClickUtils.setLongClick(new Handler(), linearLayout, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ilockDialog.dismiss();
                    //解锁
                    tvRightCenter.setTextColor(getResources().getColor(R.color.stove_white));
                    ivRightCenter.setImageResource(R.drawable.stove_screen_unlock);
                    StoveAbstractControl.getInstance().setLock(StoveConstant.UNLOCK);
                    return true;
                }
            });
        }

        ilockDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != iDialogAffirm && iDialogAffirm.isShow())
            iDialogAffirm.dismiss();
        if (null != ilockDialog && ilockDialog.isShow())
            ilockDialog.dismiss();
    }

    //是否锁屏状态
    public boolean isLock() {
        if (null != ilockDialog && ilockDialog.isShow())
            return true;
        return false;
    }
}
