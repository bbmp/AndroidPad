package com.robam.roki.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

public class UIService {
    public static void postPage(View v, @IdRes int resid) {
        NavController controller = Navigation.findNavController(v);

        controller.navigate(resid);
    }

    public static void postPage(View v, @IdRes int resid, Bundle bundle) {
        NavController controller = Navigation.findNavController(v);

        controller.navigate(resid, bundle);
    }

    public static void popBack(View v) {
        NavController controller = Navigation.findNavController(v);

        controller.popBackStack();
    }

    public static void postPagePopUp(View v, @IdRes int resid, Bundle bundle) {
        NavController controller = Navigation.findNavController(v);
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(resid, false).build();

        controller.navigate(resid, bundle, navOptions);
    }
}
