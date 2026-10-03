package com.car.dh.canbus.callback;

import com.car.dh.canbus.ipc.IModuleCallback;

/* JADX INFO: loaded from: classes.dex */
public abstract class CallbackCanbusBase extends IModuleCallback.Stub {
    public abstract void in();

    public abstract void out();
}
