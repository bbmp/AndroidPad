package com.robam.common.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.robam.common.utils.LogUtils;

public class UIService {
    @SuppressLint("RestrictedApi")
    public static void postPage(View v, @IdRes int resid) {
        NavController controller = Navigation.findNavController(v);
        LogUtils.i("post before size = " + controller.getBackStack().size());
        controller.navigate(resid);
        LogUtils.i("post after size = " + controller.getBackStack().size());
    }

    @SuppressLint("RestrictedApi")
    public static void postPage(View v, @IdRes int resid, Bundle bundle) {
        NavController controller = Navigation.findNavController(v);
        LogUtils.i("post before size = " + controller.getBackStack().size());
        controller.navigate(resid, bundle);
        LogUtils.i("post after size = " + controller.getBackStack().size());
    }

    @SuppressLint("RestrictedApi")
    public static void popBack(View v) {
        NavController controller = Navigation.findNavController(v);
        LogUtils.i("pop before size = " + controller.getBackStack().size());
        controller.popBackStack();
        LogUtils.i("pop after size = " + controller.getBackStack().size());
    }

    public static void postPagePopUp(View v, @IdRes int resid, Bundle bundle) {
        NavController controller = Navigation.findNavController(v);
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(resid, false).build();

        controller.navigate(resid, bundle, navOptions);
    }
}
