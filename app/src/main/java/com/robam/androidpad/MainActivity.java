package com.robam.androidpad;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}