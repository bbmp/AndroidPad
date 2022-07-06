package com.robam.androidpad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.robam.common.skin.SkinDeviceUtils;
import com.robam.common.skin.SkinStatusBarUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式
        SkinStatusBarUtils.translucent(this);
        setContentView(R.layout.activity_main);
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Navigation.setViewNavController(findViewById(R.id.nav_host_fragment), navController);
    }
}