package com.car.dh.ui.screen.air;

import com.car.dh.canbus.AirHelper;
import com.car.dh.canbus.CallbackCanbusBase;
import com.car.dh.canbus.DataCanbus;
import com.car.dh.canbus.HandlerCanbus;
import com.car.dh.canbus.ModuleCallbackCanbusProxy;
import com.car.dh.ipc.IModuleCallback;

/* JADX INFO: loaded from: classes.dex */
public class CallbackCanbus extends CallbackCanbusBase {
    public static final int U_CNT_MAX = 98;

    @Override // com.syu.module.canbus.CallbackCanbusBase
    public void in() {
        IModuleCallback callback = ModuleCallbackCanbusProxy.getInstance();
        for (int i = 0; i < 98; i++) {
            DataCanbus.PROXY.register(callback, i, 1);
        }
        // AirHelper.getInstance().buildUi(new AIR_0438_DJ_YuanJingX1(TheApp.getInstance()));
        for (int i2 = 10; i2 < 97; i2++) {
            DataCanbus.NOTIFY_EVENTS[i2]
                    .addNotify(AirHelper.SHOW_AND_REFRESH);
        }
    }

    @Override // com.syu.module.canbus.CallbackCanbusBase
    public void out() {
        for (int i = 10; i < 97; i++) {
            DataCanbus.NOTIFY_EVENTS[i]
                    .removeNotify(AirHelper.SHOW_AND_REFRESH);
        }
        AirHelper.getInstance().destroyUi();
    }

    @Override // com.syu.ipc.IModuleCallback
    public void update(int updateCode, int[] ints, float[] flts, String[] strs) {
        if (updateCode >= 0 && updateCode < 98) {
            HandlerCanbus.update(updateCode, ints);
        }
    }
}
