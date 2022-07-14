package com.robam.roki.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.robam.common.activity.BaseActivity;
import com.robam.roki.R;

public class HomeActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.roki_activity_layout_home);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_roki_activity_home);
        Navigation.setViewNavController(findViewById(R.id.nav_host_roki_activity_home), navController);
    }

}