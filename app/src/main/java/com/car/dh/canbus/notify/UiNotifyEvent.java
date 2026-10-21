package com.car.dh.canbus.notify;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public final class UiNotifyEvent implements Runnable {
    public static final Handler HANDLER_UI = new Handler(Looper.getMainLooper());
    private final ArrayList<IUiNotify> mUiNotifies = new ArrayList<>();
    private int mUpdateCode;

    public UiNotifyEvent() { }

    public UiNotifyEvent(int updateCode) {
        this.mUpdateCode = updateCode;
    }

    public synchronized int getUpdateCode() { return mUpdateCode; }

    public synchronized void setUpdateCode(int updateCode) { this.mUpdateCode = updateCode; }

    public synchronized int size() { return mUiNotifies.size(); }

    public synchronized void addNotify(IUiNotify notify) {
        if (notify != null && !mUiNotifies.contains(notify)) mUiNotifies.add(notify);
    }

    public void addNotify(IUiNotify notify, int onNotify) {
        boolean added;
        int updateCode;
        synchronized (this) {
            added = notify != null && !mUiNotifies.contains(notify);
            if (added) mUiNotifies.add(notify);
            updateCode = mUpdateCode;
        }
        if (onNotify == 1 && notify != null) notify.onNotify(updateCode, null, null, null);
    }

    public synchronized void removeNotify(IUiNotify notify) {
        if (notify != null) mUiNotifies.remove(notify);
    }

    public synchronized void clearNotifies() { mUiNotifies.clear(); }

    public void onNotify() {
        if (hasListeners()) HANDLER_UI.post(this);
    }

    public void onNotify(int[] ints, float[] flts, String[] strs) {
        if (hasListeners()) HANDLER_UI.post(new NotifyData(ints, flts, strs));
    }

    private synchronized boolean hasListeners() { return !mUiNotifies.isEmpty(); }

    private synchronized List<IUiNotify> snapshot() { return new ArrayList<>(mUiNotifies); }

    private final class NotifyData implements Runnable {
        private final int[] ints;
        private final float[] flts;
        private final String[] strs;

        NotifyData(int[] ints, float[] flts, String[] strs) {
            this.ints = ints;
            this.flts = flts;
            this.strs = strs;
        }

        @Override public void run() {
            int code = getUpdateCode();
            for (IUiNotify notify : snapshot()) {
                try {
                    notify.onNotify(code, ints, flts, strs);
                } catch (RuntimeException exception) {
                    android.util.Log.e("UiNotifyEvent", "Listener failed for updateCode=" + code, exception);
                }
            }
        }
    }

    @Override public void run() {
        int code = getUpdateCode();
        for (IUiNotify notify : snapshot()) {
            try {
                notify.onNotify(code, null, null, null);
            } catch (RuntimeException exception) {
                android.util.Log.e("UiNotifyEvent", "Listener failed for updateCode=" + code, exception);
            }
        }
    }
}
