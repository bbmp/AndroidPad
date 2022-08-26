package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.robam.common.ui.view.ExtImageSpan;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.VentilatorConstant;

import java.util.ArrayList;
import java.util.List;

public class MatchNetworkActivity extends VentilatorBaseActivity {
    private TextView tvHint;
    private String model = ""; //设备类型
    private TextView tvNext, tvOk;

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
        if ("9B328".equals(model)) {
            //灶具
            string = getResources().getString(R.string.ventilator_match_hint4);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ("KP100".equals(model)) {
            //锅
            string = getResources().getString(R.string.ventilator_match_hint3);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ("XG858".equals(model)) {
            //消毒柜
            string = getResources().getString(R.string.ventilator_match_hint1);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else if ("WB758".equals(model)) {
            //洗碗机
            string = getResources().getString(R.string.ventilator_match_hint6);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else {
            string = getResources().getString(R.string.ventilator_match_hint7);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        }


        tvHint.setText(spannableString);
    }

    //已授权
    private void onPermissionGranted() {
        setScanRule();
        startScan();
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
        cancelScan();
    }

    //取消扫描
    private void cancelScan() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //扫描规则
    private void setScanRule() {
        String[] names = new String[] {"ROBAM"};
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }
    //开始扫描
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtils.e("onScanStarted");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                LogUtils.e("onLeScan " + bleDevice.getName());
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtils.e("onScanFinished ");
                if (!isDestroyed()) {  //界面销毁不连接
                    if (null != scanResultList && scanResultList.size() > 0) {
                        connect(scanResultList.get(0));
                    } else {
                        //未扫描到
                        tvNext.setText(R.string.ventilator_rematch);
                        tvNext.setClickable(true);
                    }
                }
            }
        });
    }
    //连接设备
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtils.e("onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                LogUtils.e("onConnectFail" + exception.getDescription());
                tvNext.setText(R.string.ventilator_rematch);
                tvNext.setClickable(true);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onConnectSuccess" + bleDevice.getName());
                getBuletoothGatt(bleDevice);
                //跳设备首页
                finish();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onDisConnected");
                if (null != gatt)
                    gatt.close();
            }
        });
    }

    //获取gatt提供的服务
    private void getBuletoothGatt(BleDevice bleDevice) {
        //获取设备服务
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        List<BluetoothGattService> serviceList = new ArrayList<>();
        for (BluetoothGattService service : gatt.getServices()) {
            serviceList.add(service);
        }
        //
        for (BluetoothGattService service: serviceList) {
            for (BluetoothGattCharacteristic characteristic: service.getCharacteristics()) {
                int charaProp = characteristic.getProperties();
                LogUtils.e("charaProp " + charaProp);
                if (charaProp == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
                    indicate(bleDevice, characteristic);
                    return;
                }
            }
        }
    }
    //订阅通知
    private void notify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().notify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {

                        // 打开通知操作成功（UI线程）

                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        // 打开通知操作失败（UI线程）
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现（UI线程）
                    }
                });
    }
    //取消通知
    private void stopNotify(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().stopNotify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString());
    }
    //订阅可靠通知
    private void indicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().indicate(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleIndicateCallback() {

                    @Override
                    public void onIndicateSuccess() {
                        // 打开通知操作成功（UI线程）
                        LogUtils.e("onIndicateSuccess");
                    }

                    @Override
                    public void onIndicateFailure(final BleException exception) {
                        // 打开通知操作失败（UI线程）
                        LogUtils.e("onIndicateFailure " + exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现（UI线程）
                        LogUtils.e("onCharacteristicChanged" + HexUtil.formatHexString(characteristic.getValue(), true));
                    }
                });
    }
    //取消订阅
    private void stopIndicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BleManager.getInstance().stopIndicate(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString());
    }
}