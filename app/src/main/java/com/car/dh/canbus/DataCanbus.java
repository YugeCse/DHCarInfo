package com.car.dh.canbus;

import com.car.dh.ipc.RemoteModuleProxy;

/* JADX INFO: loaded from: classes.dex */
public class DataCanbus {
    public static final boolean DEBUG_AIR = true;
    public static final boolean DEBUG_DOOR = true;
    public static int carId;
    public static int sCanbusId;
    public static final RemoteModuleProxy PROXY = new RemoteModuleProxy();
    public static final int[] DATA = new int[FinalCanbus.U_CNT_MAX];
    public static final UiNotifyEvent[] NOTIFY_EVENTS = new UiNotifyEvent[FinalCanbus.U_CNT_MAX];

    static {
        for (int i = 0; i < 1200; i++) {
            NOTIFY_EVENTS[i] = new UiNotifyEvent(i);
        }
        DATA[1001] = 1;
        DATA[1002] = 1;
    }
}
