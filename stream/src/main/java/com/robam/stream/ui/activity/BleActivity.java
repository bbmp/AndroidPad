package com.robam.stream.ui.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.PermissionUtils;
import com.robam.stream.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            return;
        }

        PermissionUtils.requestPermission(this, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onSucceed() {
                onPermissionGranted();
            }

            @Override
            public void onFailed() {
                //权限未给
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void onPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
            new AlertDialog.Builder(this)
                    .setTitle("title")
                    .setMessage("gps")
                    .setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setPositiveButton("setting",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                                        @Override
                                        public void onActivityResult(ActivityResult result) {
                                            Intent data = result.getData();
                                            int code = result.getResultCode();

                                            if (checkGPSIsOpen()) {
                                                setScanRule();
                                                startScan();
                                            }

                                        }
                                    }).launch(intent);
                                }
                            })

                    .setCancelable(false)
                    .show();
        } else {
            setScanRule();
            startScan();
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void setScanRule() {

        UUID[] serviceUuids = null;

        String[] names = null;

        String mac = "";

        boolean isAutoConnect = false;

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtils.e("onScanStarted " + success);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                LogUtils.e("device=" + bleDevice.getName());
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }
}