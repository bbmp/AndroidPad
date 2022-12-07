package com.robam.steamoven.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.robam.common.bean.AccountInfo;
import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.device.HomeSteamOven;

//辅助工作中
public class WorkActivity extends SteamBaseActivity {
    /**
     * 倒计时
     */
    private TextView tvCountdown;

    private TextView tvMode;

    private TextView tvTemp;

    private ImageView ivStart;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_work;
    }

    @Override
    protected void initView() {
        showCenter();

        tvMode = findViewById(R.id.tv_mode);
        tvTemp = findViewById(R.id.tv_temp);
        tvCountdown = findViewById(R.id.tv_countdown);
        ivStart = findViewById(R.id.iv_start);
        setOnClickListener(R.id.iv_start);
    }

    @Override
    protected void initData() {
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (HomeSteamOven.getInstance().guid.equals(s)) { //当前设备变化
                    setWorkData();
                }
            }
        });
        setWorkData();
    }

    /**
     * 设置倒计时和温度
     */
    private void setWorkData() {
        //工作模式
        tvMode.setText(SteamModeEnum.match(HomeSteamOven.getInstance().workMode));
        //剩余总时长
        int workHours = HomeSteamOven.getInstance().totalRemainSeconds;
        String time = DateUtil.secForMatTime3(workHours);
        tvCountdown.setText(time);
        //温度
        tvTemp.setText(HomeSteamOven.getInstance().workTemp + "");
        //工作状态
        if (HomeSteamOven.getInstance().workState != 0)   //工作状态
            ivStart.setImageResource(R.drawable.steam_work_pause);
        else //暂停状态
            ivStart.setImageResource(R.drawable.steam_work_continue);
    }
}
