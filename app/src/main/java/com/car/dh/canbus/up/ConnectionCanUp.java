package com.car.dh.canbus.up;

import android.os.RemoteException;

import com.car.dh.canbus.connection.ConnectionObserver;
import com.car.dh.canbus.callback.ModuleCallbackCanbusProxy;
import com.car.dh.canbus.ipc.IRemoteToolkit;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionCanUp implements ConnectionObserver {
    private static final ConnectionCanUp INSTANCE = new ConnectionCanUp();

    public static ConnectionCanUp getInstance() {
        return INSTANCE;
    }

    private ConnectionCanUp() {
    }

    @Override // com.syu.module.ConnectionObserver
    public void onConnected(IRemoteToolkit toolkit) {
        try {
            DataCanUp.PROXY.setRemoteModule(toolkit.getRemoteModule(14));
            ModuleCallbackCanUpProxy callback = ModuleCallbackCanUpProxy.getInstance();
            DataCanUp.PROXY.register(callback, 100, 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.syu.module.ConnectionObserver
    public void onDisconnected() {
        DataCanUp.PROXY.setRemoteModule(null);
        DataCanUp.DATA[100] = 0;
        ModuleCallbackCanbusProxy.getInstance().setCallbackCanbus(null);
    }
}
