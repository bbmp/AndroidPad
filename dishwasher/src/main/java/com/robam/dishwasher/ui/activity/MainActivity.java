package com.robam.dishwasher.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.activity.BaseActivity;
import com.robam.dishwasher.R;

//远程入口，供烟机调用
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishwasher_activity_layout_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_dishwasher_activity_main);
        Navigation.setViewNavController(findViewById(R.id.nav_host_dishwasher_activity_main), navController);

    }
}
