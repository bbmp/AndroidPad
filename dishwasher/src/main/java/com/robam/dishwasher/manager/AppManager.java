package com.robam.dishwasher.manager;

import android.app.Activity;
import android.service.voice.VoiceInteractionSession;

import java.util.Stack;

public class AppManager {
    private Stack<Activity> activityStack;
    private static AppManager instance = new AppManager();

    private AppManager() {
    }

    public static AppManager getInstance() {
        return instance;
    }

    public int getActivityStackSize() {
        if (null == activityStack)
            return 0;
        return activityStack.size();
    }

    /**
     * 启动activity，入栈
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (null == activityStack)
            activityStack = new Stack<>();
        if (null != activity)
            activityStack.add(activity);
    }
    public void removeActivity(Activity activity) {
        if (null != activity && null != activityStack) {
            activityStack.remove(activity);
        }
    }

    /**
     * 当前activity
     * @return
     */
    public Activity currentActivity() {
        if (null != activityStack)
            return activityStack.lastElement();
        return null;
    }

    /**
     * 关闭当前activity
     */
    public void finishActivity() {

        finishActivity(currentActivity());
    }

    /**
     * 关闭指定activity
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 关闭指定activity
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity: activityStack) {
            if (activity.getClass().equals(cls))
                finishActivity(activity);
        }
    }

    /**
     * 关闭所有activity
     */
    public void finishAllActivity() {
        if (null != activityStack) {
            for (Activity activity: activityStack) {
                if (null != activity)
                    activity.finish();
            }
            activityStack.clear();
        }
    }
}
