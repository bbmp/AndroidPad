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
import android.widget.ImageView;
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
import com.robam.common.IDeviceType;
import com.robam.common.ble.BleDeviceInfo;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.ui.view.ExtImageSpan;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.VentilatorConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchNetworkActivity extends VentilatorBaseActivity {
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
            Drawable drawable = getResources().getDrawable(R.drawable.ventilator_r);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (IDeviceType.RZNG.equals(model)) {
            //锅
            ivDevice.setImageResource(R.drawable.ventilator_pan);
            string = getResources().getString(R.string.ventilator_match_hint3);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.ventilator_r);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (IDeviceType.RXDG.equals(model)) {
            //消毒柜
            ivDevice.setImageResource(R.drawable.ventilator_cabinet);
            string = getResources().getString(R.string.ventilator_match_hint1);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.ventilator_r);
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
        String[] names = new String[] {"ROKI"};
        BlueToothManager.setScanRule(names);
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

    //开始扫描
    private void startScan() {
        BlueToothManager.startScan(new BleScanCallback() {
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
                        if (scanResultList.get(0).getName().contains("ROKI"))
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
        BlueToothManager.connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtils.e("onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                LogUtils.e("onConnectFail " + exception.getDescription());
                tvNext.setText(R.string.ventilator_rematch);
                tvNext.setClickable(true);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onConnectSuccess " + bleDevice.getName());
                //连接成功
                BlueToothManager.addDeviceToMap(bleDevice);
                getBuletoothGatt(bleDevice);
                //跳设备首页
                finish();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.e("onDisConnected");
                //掉线
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
            UUID uuid = service.getUuid();
            if (uuid.toString().contains("fff0")) { //service uuid
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    uuid = characteristic.getUuid();
                    if (uuid.toString().contains("fff1")) {   //读写
                        LogUtils.e("uuid " + uuid);
                    } else if (uuid.toString().contains("fff4")) {  //notify
                        int charaProp = characteristic.getProperties();
                        LogUtils.e("uuid " + uuid);
                        if (charaProp == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                            notify(bleDevice, characteristic);
                        }
                    }
                }
                break;
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
                        LogUtils.e("onNotifySuccess");
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        // 打开通知操作失败（UI线程）
                        LogUtils.e("onNotifyFailure " + exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现（UI线程）
                        LogUtils.e("onCharacteristicChanged " + HexUtil.formatHexString(characteristic.getValue(), true));
                        BlueToothManager.bleParser(bleDevice, characteristic.getValue());
                    }
                });
    }

    //订阅可靠通知
    private void indicate(BleDevice bleDevice, BluetoothGattCharacteristic characteristic) {
        BlueToothManager.indicate(
                bleDevice,
                characteristic,
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

}