package com.robam.androidpad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.WindowManager;

import com.robam.common.skin.SkinDeviceUtils;
import com.robam.common.skin.SkinStatusBarUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //沉浸式
        SkinStatusBarUtils.translucent(this);
        //字体
        SkinStatusBarUtils.setStatusBarLightMode(this);
        setContentView(R.layout.activity_main);
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Navigation.setViewNavController(findViewById(R.id.nav_host_fragment), navController);
    }
}