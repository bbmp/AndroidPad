package com.robam.steamoven.ui.activity;

import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;

public class MatchNetworkActivity extends SteamBaseActivity {
    private TextView tvHint;
    private String model = ""; //设备类型
    private TextView tvNext, tvOk;
    private ImageView ivDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_match_network;
    }

    @Override
    protected void initView() {
        //showLeft();
        showCenter();
        model = IDeviceType.RXDG;
        tvHint = findViewById(R.id.tv_match_hint);
        tvNext = findViewById(R.id.tv_next);
        tvOk = findViewById(R.id.tv_ok);
        ivDevice = findViewById(R.id.iv_device);
        setOnClickListener(R.id.tv_next, R.id.tv_ok);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(steamOven.status != Device.OFFLINE){
                        goHome();
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        SpannableString spannableString = null;
        //消毒柜
        ivDevice.setImageResource(R.drawable.steam_steam);
        String string = getResources().getString(R.string.steam_match_hint8);
        spannableString = new SpannableString(string);
        tvNext.setVisibility(View.GONE);
        //tvOk.setVisibility(View.VISIBLE);
        tvHint.setText(spannableString);
    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId() == R.id.ll_left){
            goHome();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}