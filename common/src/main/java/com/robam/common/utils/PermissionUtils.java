package com.robam.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.functions.Consumer;


public class PermissionUtils {

    public static void requestPermission(FragmentActivity activity, OnPermissionListener listener, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                .subscribe(grated-> {
                    if (grated) {
                        //全部已经授权
                        if (null != listener)
                            listener.onSucceed();

                    } else {
                        //起码有一个没有授权
                        if (null != listener)
                            listener.onFailed();
                    }
                });
    }

    public static boolean isGranted(FragmentActivity activity, String permission) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions.isGranted(permission);
    }

    public static void requestPermission(Fragment fragment, OnPermissionListener listener, String... permissions) {

        RxPermissions rxPermissions = new RxPermissions(fragment);
        rxPermissions.request(permissions)
                    .subscribe(grated-> {
                        if (grated) {
                            //全部已经授权
                            if (null != listener)
                                listener.onSucceed();

                            } else {
                                //起码有一个没有授权
                                if (null != listener)
                                    listener.onFailed();
                            }
                    });
    }

    public interface OnPermissionListener {
        void onSucceed();

        void onFailed();
    }
}
