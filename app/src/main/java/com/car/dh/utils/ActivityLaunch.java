package com.car.dh.utils;

import android.content.ComponentName;
import android.content.Intent;

import com.car.dh.app.DHApplication;

/* JADX INFO: loaded from: classes.dex */
public class ActivityLaunch {
    public static void startActivity(String packageName, String ActivityName) {
        ComponentName componetName = new ComponentName(packageName, ActivityName);
        try {
            Intent intent = new Intent();
            intent.setComponent(componetName);
            defIntentSetForStartActivity(intent);
            DHApplication.singleton().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void defIntentSetForStartActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /** 返回桌面 **/
    public static void backHomeDesktop(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DHApplication.singleton().startActivity(intent);
    }

    public static void openSelfApp(){
        String pkg = DHApplication.singleton().getPackageName();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg, pkg + ".MainActivityDefault2"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        DHApplication.singleton().startActivity(intent);
    }

}
