package com.robam.steam.ui.pages;

import android.content.Intent;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.serialport.helper.SerialPortHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.clj.fastble.BleManager;
import com.robam.common.ui.HeadPage;
import com.robam.foodmaterialdetect.FoodMaterialDetectCallback;
import com.robam.foodmaterialdetect.FoodMaterialHelper;
import com.robam.steam.R;
import com.robam.steam.ui.activity.BleActivity;

public class HomePage extends HeadPage {

    private Button detect;
    private Button btTake, btBle;

    public static HomePage newInstance() {
        return new HomePage();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_home;
    }

    @Override
    protected void initView() {
        detect = findViewById(R.id.bt_detect);
        btTake = findViewById(R.id.bt_take);
        btBle = findViewById(R.id.ble);

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
    }

    @Override
    protected void initData() {

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