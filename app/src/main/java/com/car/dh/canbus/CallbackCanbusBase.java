package com.car.dh.canbus;

import com.car.dh.ipc.IModuleCallback;

/* JADX INFO: loaded from: classes.dex */
public abstract class CallbackCanbusBase extends IModuleCallback.Stub {
    public abstract void in();

    public abstract void out();
}
