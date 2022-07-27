package com.robam.common.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.robam.common.R;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO});


    }


    /**
     * 检查是否拥有指定的所有权限
     */

    /**
     * 初始化软键盘
     */
    protected void initSoftKeyboard() {
        // 点击外部隐藏软键盘，提升用户体验
        getContentView().setOnClickListener(v -> {
            // 隐藏软键，避免内存泄漏
            hideKeyboard(getCurrentFocus());
        });
    }

    /**
     * 和 setContentView 对应的方法
     */
    public ViewGroup getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }
    /**
     * 批量申请权限
     *
     * @param permissionStr 要申请的权限
     * @return true: 允许  false: 拒绝
     */
    protected boolean getPermission(String[] permissionStr) {
        if (permissionStr == null || permissionStr.length == 0) {
            throw new NullPointerException("permissionStr is a not null values!");
        }

        boolean isAllGranted = checkPermissionAllGranted(permissionStr);

        if (isAllGranted) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();

            for (String permission : permissionStr) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(permission);
                }
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 12);
            } else {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                List<String> cancelPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        cancelPermissions.add(permissions[i]);
                    }
                }
                if (cancelPermissions.isEmpty()) {   //全部允许
                    getPermissionsResult(true, null);
                } else {                             //有不允许的
                    getPermissionsResult(false, cancelPermissions);
                }
        }

    }
    /**
     * 权限申请结果回调方法，如果想获取申请结果，请在子类中重写该方法即可
     *
     * @param isGranted         true: 全部允许  false: 不允许
     * @param cancelPermissions 如果全部允许则该参数为null, 如果没有全部允许则该参数会返回没有允许的权限
     */
    protected void getPermissionsResult(boolean isGranted, List<String> cancelPermissions) {

        if (!isGranted) {
            ToastUtils.showLong(this, "您拒绝了一些应用需要的权限，可能导致部分功能不能正常使用哦!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("requestCode::" + requestCode + " resultCode  " + resultCode);
    }

}
