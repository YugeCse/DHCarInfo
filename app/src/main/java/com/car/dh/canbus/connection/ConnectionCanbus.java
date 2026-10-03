package com.car.dh.canbus.connection;

import android.os.RemoteException;

import com.car.dh.canbus.data.DataCanbus;
import com.car.dh.canbus.data.FinalCanbus;
import com.car.dh.canbus.callback.ModuleCallbackCanbusProxy;
import com.car.dh.canbus.ipc.IRemoteToolkit;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionCanbus implements ConnectionObserver {
    private static final ConnectionCanbus INSTANCE = new ConnectionCanbus();

    public static ConnectionCanbus getInstance() {
        return INSTANCE;
    }

    private ConnectionCanbus() {
    }

    @Override // com.syu.module.ConnectionObserver
    public void onConnected(IRemoteToolkit toolkit) {
        try {
            DataCanbus.PROXY.setRemoteModule(toolkit.getRemoteModule(7));
            ModuleCallbackCanbusProxy callback = ModuleCallbackCanbusProxy.getInstance();
            DataCanbus.PROXY.register(callback, 1000, 1);
            DataCanbus.PROXY.register(callback, FinalCanbus.U_AIR_WINDOW_ENABLE, 1);
            DataCanbus.PROXY.register(callback, 1003, 1);
            DataCanbus.PROXY.register(callback, FinalCanbus.U_DOOR_WINDOW_ENABLE, 1);
            DataCanbus.PROXY.register(callback, FinalCanbus.U_CAR_BT_ON, 1);
            DataCanbus.PROXY.register(callback, FinalCanbus.U_SHOW_AIR_WINDOW, 1);
            System.out.println("已经连接到 ConnectionCanbus");
        } catch (RemoteException e) {
            System.out.println("连接 ConnectionCanbus 失败");
            e.printStackTrace();
        }
    }

    @Override // com.syu.module.ConnectionObserver
    public void onDisconnected() {
        System.out.println("已经取消连接 ConnectionCanbus");
        DataCanbus.PROXY.setRemoteModule(null);
        DataCanbus.DATA[1000] = 0;
        ModuleCallbackCanbusProxy.getInstance().setCallbackCanbus(null);
    }
}
