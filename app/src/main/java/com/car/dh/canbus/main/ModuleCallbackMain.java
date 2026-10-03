package com.car.dh.canbus.main;

import android.os.RemoteException;
import android.util.Log;

import com.car.dh.canbus.ipc.IModuleCallback;

/* JADX INFO: loaded from: classes.dex */
public class ModuleCallbackMain extends IModuleCallback.Stub {
    private static final ModuleCallbackMain INSTANCE = new ModuleCallbackMain();

    public static ModuleCallbackMain getInstance() {
        return INSTANCE;
    }

    private ModuleCallbackMain() {
    }

    public boolean intsOk(int[] ints, int min) {
        return ints != null && ints.length >= min;
    }

    @Override // com.syu.ipc.IModuleCallback
    public void update(int updateCode, int[] ints, float[] flts, String[] strs) throws RemoteException {
        if (updateCode < 200) {
            if (updateCode == 174) {
                if (intsOk(ints, 1)) {
                     if (ints[1] == -2) {
                         // Callback_0453_LZ_BBA_All.modevalue = ints[2];
                         // Callback_0453_LZ_LandRover_KeepCD.modevalue = ints[2];
                         Log.v("zed", "174  Callback_0453_LZ_BBA_All.modevalue  == " + ints[2]);
                     }
                    HandlerMain.update(updateCode, ints, flts, strs);
                    Log.v("zed", "174  rev==alll 11111111111 ints[0]== " + ints[0]);
                }
            } else {
                if (intsOk(ints, 1)) {
                    HandlerMain.update(updateCode, ints[0]);
                }
            }
        }
    }
}
