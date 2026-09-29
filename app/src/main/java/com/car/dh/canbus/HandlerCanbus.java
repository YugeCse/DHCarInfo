package com.car.dh.canbus;

import com.car.dh.app.DHApplication;
import com.car.dh.ui.screen.air.Callback_0000_null;
import com.car.dh.utils.ActivityLaunch;
import com.car.dh.ui.screen.air.CallbackCanbus;

/* JADX INFO: loaded from: classes.dex */
public class HandlerCanbus {
    private static final IUiNotify NTF_CANBUS_ID = new IUiNotify() { // from class: com.syu.module.canbus.HandlerCanbus.1
        @Override // com.syu.module.IUiNotify
        public void onNotify(int updateCode, int[] ints, float[] flts, String[] strs) {
            try {
                CallbackCanbusBase callbackCanbus =
                        HandlerCanbus.getCallbackCanbusById(DataCanbus.DATA[updateCode]);
                ModuleCallbackCanbusProxy.getInstance().setCallbackCanbus(callbackCanbus);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static void update(int updateCode, int[] ints) {
        if (ints != null && ints.length != 0 && DataCanbus.DATA[updateCode] != ints[0]) {
            DataCanbus.DATA[updateCode] = ints[0];
            DataCanbus.NOTIFY_EVENTS[updateCode].onNotify();
        }
    }

    public static void update(int updateCode, int value) {
        if (DataCanbus.DATA[updateCode] != value) {
            DataCanbus.DATA[updateCode] = value;
            DataCanbus.NOTIFY_EVENTS[updateCode].onNotify();
        }
    }

    public static void update(int updateCode, int[] ints, float[] flts, String[] strs) {
        if ((ints != null && ints.length != 0) || (strs != null && strs.length != 0)) {
            if (ints != null && DataCanbus.DATA[updateCode] != ints[0]) {
                DataCanbus.DATA[updateCode] = ints[0];
            }
            DataCanbus.NOTIFY_EVENTS[updateCode].onNotify(ints, flts, strs);
        }
    }

    public static void canbusId(int updateCode, int value) {
        if (DataCanbus.DATA[updateCode] != value) {
            DataCanbus.DATA[updateCode] = value;
            DataCanbus.NOTIFY_EVENTS[updateCode].onNotify();
        }
    }

    public static void updateCarBt(int value) {
        if (value == 1) {
            if (!DHApplication.isBtFront) {
                ActivityLaunch.startActivity(
                        "com.syu.canbus",
                        "com.syu.canbus.CarBtActi");
            }
        } else if (value == 0 && DHApplication.isBtFront) {
            ActivityLaunch.backHomeDesktop();
        }
    }

    static {
        DataCanbus.NOTIFY_EVENTS[1000].addNotify(NTF_CANBUS_ID, 1);
    }

    public static CallbackCanbusBase getCallbackCanbusById(int id) {
        DataCanbus.sCanbusId = id;
        int canbusId = id & 65535;
        int carId = (id >> 16) & 65535;
        DataCanbus.carId = carId;
        if (canbusId == 438 && carId == 5) {
            return new CallbackCanbus();
        }
        return new Callback_0000_null();
    }
}
