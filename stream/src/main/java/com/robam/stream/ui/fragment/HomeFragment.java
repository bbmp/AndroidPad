package com.robam.stream.ui.fragment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.serialport.helper.SerialPortHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.robam.common.utils.PermissionUtils;
import com.robam.foodmaterialdetect.FoodMaterialDetectCallback;
import com.robam.foodmaterialdetect.FoodMaterialHelper;
import com.robam.stream.R;
import com.robam.stream.ui.activity.BleActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button detect;
    private Button btTake, btBle;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        detect = rootView.findViewById(R.id.bt_detect);
        btTake = rootView.findViewById(R.id.bt_take);
        btBle = rootView.findViewById(R.id.ble);

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodMaterialHelper.classify(getContext(), "/vendor/model/ji.jpg", new FoodMaterialDetectCallback() {
                    @Override
                    public void onResult(String result) {
                        Log.e("onResult", result);
                    }
                });
            }
        });
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.navigation_takephoto);
            }
        });
        btBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), BleActivity.class);
                startActivity(intent);
            }
        });
        return  rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭串口
        SerialPortHelper.getInstance().closeDevice();
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }
}