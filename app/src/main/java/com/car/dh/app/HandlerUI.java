package com.car.dh.app;

import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
public class HandlerUI extends Handler {
    private static final HandlerUI INSTANCE = new HandlerUI();

    public static HandlerUI getInstance() {
        return INSTANCE;
    }

    private HandlerUI() {
        super(Looper.getMainLooper());
    }
}
