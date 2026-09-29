package com.car.dh.canbus;


import com.car.dh.ipc.IRemoteToolkit;

/* JADX INFO: loaded from: classes.dex */
public interface ConnectionObserver {
    void onConnected(IRemoteToolkit iRemoteToolkit);

    void onDisconnected();
}
