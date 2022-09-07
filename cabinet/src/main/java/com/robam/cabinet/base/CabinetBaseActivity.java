package com.robam.cabinet.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.robam.cabinet.R;
import com.robam.cabinet.manager.CabinetActivityManager;
import com.robam.cabinet.ui.dialog.LockDialog;
import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ToastUtils;

public abstract class CabinetBaseActivity extends BaseActivity {
    private IDialog ilockDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CabinetActivityManager.getInstance().addActivity(this);
        setOnClickListener(R.id.ll_left, R.id.ll_right_center);
    }
//    public void showFloat() {
//        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
//    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CabinetActivityManager.getInstance().removeActivity(this);
    }

    //锁屏
    private void screenLock() {
        TextView tvRightCenter = findViewById(R.id.tv_right_center);
        tvRightCenter.setTextColor(getResources().getColor(R.color.cabinet_lock));
        ImageView ivRightCenter = findViewById(R.id.iv_right_center);
        ivRightCenter.setImageResource(R.drawable.cabinet_screen_lock);
        if (null == ilockDialog) {
            ilockDialog = new LockDialog(this);
            ilockDialog.setCancelable(false);
            ilockDialog.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort(getContext(), R.string.cabinet_unlock_hint);
                }
            });
            LinearLayout linearLayout = ilockDialog.getRootView().findViewById(R.id.ll_right_center);
            ClickUtils.setLongClick(new Handler(), linearLayout, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ilockDialog.dismiss();
                    //解锁
                    tvRightCenter.setTextColor(getResources().getColor(R.color.cabinet_white));
                    ivRightCenter.setImageResource(R.drawable.cabinet_screen_unlock);
                    return true;
                }
            });
        }

        ilockDialog.show();
    }
}
