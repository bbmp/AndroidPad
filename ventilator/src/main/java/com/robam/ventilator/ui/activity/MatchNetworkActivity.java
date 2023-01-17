package com.robam.ventilator.ui.activity;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.common.IDeviceType;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.ui.view.ExtImageSpan;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.protocol.ble.BleVentilator;

public class MatchNetworkActivity extends VentilatorBaseActivity implements BleVentilator.BleCallBack{
    private TextView tvHint;
    private String model = ""; //设备类型
    private TextView tvNext, tvOk;
    private ImageView ivDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_match_network;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        if (null != getIntent())
            model = getIntent().getStringExtra(VentilatorConstant.EXTRA_MODEL);
        tvHint = findViewById(R.id.tv_match_hint);
        tvNext = findViewById(R.id.tv_next);
        tvOk = findViewById(R.id.tv_ok);
        ivDevice = findViewById(R.id.iv_device);

        setOnClickListener(R.id.tv_next, R.id.tv_ok);
    }

    private void checkPermissions() {
        //请求权限
        PermissionUtils.requestPermission(this, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onSucceed() {
                onPermissionGranted();
            }

            @Override
            public void onFailed() {
                //权限未给
                LogUtils.e("requestPermission onFailed");
                tvNext.setText(R.string.ventilator_rematch);
                tvNext.setClickable(true);
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    protected void initData() {
        SpannableString spannableString = null;
        String string = null;
        if (IDeviceType.RRQZ.equals(model)) {
            //灶具
            ivDevice.setImageResource(R.drawable.ventilator_stove);
            string = getResources().getString(R.string.ventilator_match_hint4);
            spannableString = new SpannableString(string);
            Drawable drawable1 = getResources().getDrawable(R.drawable.ventilator_things);
            Drawable drawable2 = getResources().getDrawable(R.drawable.ventilator_r);
            drawable1.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            drawable2.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("上\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable1), pos + 2, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("号\"");
            if (pos >= 0) {
                spannableString.setSpan(new ExtImageSpan(drawable2), pos + 2, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (IDeviceType.RZNG.equals(model)) {
            //锅
            ivDevice.setImageResource(R.drawable.ventilator_pan);
            string = getResources().getString(R.string.ventilator_match_hint3);
            spannableString = new SpannableString(string);
            Drawable drawable1 = getResources().getDrawable(R.drawable.ventilator_things);
            Drawable drawable2 = getResources().getDrawable(R.drawable.ventilator_r);
            drawable1.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            drawable2.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("上\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable1), pos + 2, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("号\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable2), pos + 2, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (IDeviceType.RXDG.equals(model)) {
            //消毒柜
            ivDevice.setImageResource(R.drawable.ventilator_cabinet);
//            string = getResources().getString(R.string.ventilator_match_hint8);
//            spannableString = new SpannableString(string);
//            tvNext.setVisibility(View.GONE);
//            tvOk.setVisibility(View.VISIBLE);
            string = getResources().getString(R.string.ventilator_match_hint1);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.ventilator_r);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
//            int pos = string.indexOf("[");
//            if (pos >= 0)
//                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            int pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else if (IDeviceType.RXWJ.equals(model)) {
            //洗碗机
            ivDevice.setImageResource(R.drawable.ventilator_dishwasher);
            string = getResources().getString(R.string.ventilator_match_hint6);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else {
            ivDevice.setImageResource(R.drawable.ventilator_steam);
            string = getResources().getString(R.string.ventilator_match_hint7);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        }


        tvHint.setText(spannableString);
    }

    //已授权
    private void onPermissionGranted() {
        if (model.equals(IDeviceType.RRQZ)) {
            String[] names = new String[]{BlueToothManager.stove};
            BlueToothManager.setScanRule(names);
        } else if (model.equals(IDeviceType.RZNG)) {
            String[] names = new String[]{BlueToothManager.pan};
            BlueToothManager.setScanRule(names);
        }

        BleVentilator.startScan(model, this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_ok)
            finish();
        else if (id == R.id.tv_next) {
            //下一步
            tvNext.setText(R.string.ventilator_match_ing);
            //不可点击
            tvNext.setClickable(false);
            //开启蓝牙
            checkPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //开始扫描
    private void startScan() {
        BlueToothManager.cancelScan();

        BleVentilator.startScan(model, this);
    }


    @Override
    public void onScanFinished() {
        //未扫描到
        if (!isDestroyed()) {
            tvNext.setText(R.string.ventilator_rematch);
            tvNext.setClickable(true);
        }
    }

    @Override
    public void onConnectFail() {
        if (!isDestroyed()) {
            tvNext.setText(R.string.ventilator_rematch);
            tvNext.setClickable(true);
        }
    }

    @Override
    public void onConnectSuccess() {
        //跳设备首页
        if (!isDestroyed()) {
            ToastUtils.showShort(getApplicationContext(), R.string.ventilator_add_success);
            finish();
        }
    }
}