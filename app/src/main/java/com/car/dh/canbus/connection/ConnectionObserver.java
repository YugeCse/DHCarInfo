package com.car.dh.canbus.connection;


import com.car.dh.canbus.ipc.IRemoteToolkit;

/* JADX INFO: loaded from: classes.dex */
public interface ConnectionObserver {
    void onConnected(IRemoteToolkit iRemoteToolkit);

    void onDisconnected();
}
