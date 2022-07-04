package com.robam.roki.ui;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class UIService {
    public static void postPage(View v, @IdRes int resid) {
        NavController controller = Navigation.findNavController(v);

        controller.navigate(resid);
    }
}
