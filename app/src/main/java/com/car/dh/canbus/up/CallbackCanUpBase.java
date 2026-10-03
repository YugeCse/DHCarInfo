package com.car.dh.canbus.up;

import com.car.dh.canbus.ipc.IModuleCallback;

/* JADX INFO: loaded from: classes.dex */
public abstract class CallbackCanUpBase extends IModuleCallback.Stub {
    public abstract void in();

    public abstract void out();
}
