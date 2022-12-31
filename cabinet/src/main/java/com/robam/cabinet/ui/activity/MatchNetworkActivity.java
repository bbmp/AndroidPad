package com.robam.cabinet.ui.activity;

import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;

public class MatchNetworkActivity extends CabinetBaseActivity{
    private TextView tvHint;
    private String model = ""; //设备类型
    private TextView tvNext, tvOk;
    private ImageView ivDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_match_network;
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
                if (device.guid.equals(s) && device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) { //当前锅
                    Cabinet cabinet = (Cabinet) device;
                    setLock(cabinet.isChildLock == 1);
                    if(!CabinetCommonHelper.isSafe()){
                        return;
                    }
                    if(toWaringPage(cabinet.faultId)){
                        return;
                    }
                    if(!isOffLine(cabinet)){
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
        ivDevice.setImageResource(R.drawable.cabinet_cabinet);
        String string = getResources().getString(R.string.cabinet_match_hint8);
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