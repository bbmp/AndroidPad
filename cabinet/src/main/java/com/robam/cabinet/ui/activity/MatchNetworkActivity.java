package com.robam.cabinet.ui.activity;

import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.common.IDeviceType;

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
        showLeft();
        showCenter();
        model = IDeviceType.RXDG;
        tvHint = findViewById(R.id.tv_match_hint);
        tvNext = findViewById(R.id.tv_next);
        tvOk = findViewById(R.id.tv_ok);
        ivDevice = findViewById(R.id.iv_device);

        setOnClickListener(R.id.tv_next, R.id.tv_ok);
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