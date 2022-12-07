package com.robam.common.manager;

import android.app.Activity;


import java.lang.ref.WeakReference;

public class AppActivityManager {
    private static AppActivityManager aInstance = new AppActivityManager();

    private WeakReference<Activity> sCurrentActivity;

    private AppActivityManager() {

    }

    public static AppActivityManager getInstance() {
        return aInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivity != null)
            currentActivity = sCurrentActivity.get();

        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        this.sCurrentActivity = new WeakReference<>(activity);
    }
}
