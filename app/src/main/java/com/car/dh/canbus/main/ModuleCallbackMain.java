package com.car.dh.canbus.main;

import android.os.RemoteException;
import android.util.Log;

import com.car.dh.canbus.ipc.IModuleCallback;

public class ModuleCallbackMain extends IModuleCallback.Stub {
    private static final String TAG = "ModuleCallbackMain";
    private static final ModuleCallbackMain INSTANCE = new ModuleCallbackMain();

    public static ModuleCallbackMain getInstance() {
        return INSTANCE;
    }

    private ModuleCallbackMain() {
    }

    private boolean intsOk(int[] ints, int min) {
        return ints != null && ints.length >= min;
    }

    @Override
    public void update(int updateCode, int[] ints, float[] flts, String[] strs) throws RemoteException {
        if (updateCode < 0 || updateCode >= DataMain.DATA.length) {
            return;
        }
        if (updateCode == 174) {
            // The generic handler only needs ints[0]. The optional diagnostic fields need 3 values.
            if (!intsOk(ints, 1)) return;
            if (intsOk(ints, 3) && ints[1] == -2) {
                Log.v(TAG, "174 modevalue == " + ints[2]);
            }
            HandlerMain.update(updateCode, ints, flts, strs);
            return;
        }
        if (intsOk(ints, 1)) {
            HandlerMain.update(updateCode, ints[0]);
        }
    }
}
