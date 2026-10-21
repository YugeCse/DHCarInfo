package com.car.dh.canbus.connection;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.car.dh.canbus.ipc.IRemoteToolkit;

import java.util.ArrayList;
import java.util.Random;

/** Maintains one application-scoped binding to the car toolkit service. */
public class MsToolkitConnection implements ServiceConnection {
    private static final String TAG = "MsToolkitConnection";
    @SuppressLint("StaticFieldLeak")
    private static final MsToolkitConnection INSTANCE = new MsToolkitConnection();

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<ConnectionObserver> mConnectionObservers = new ArrayList<>();
    private final Runnable mRunnableConnect = this::tryBindService;
    private final Random mRandom = new Random();

    private Context mContext;
    private IRemoteToolkit mRemoteToolkit;
    private boolean mConnecting;
    private boolean mBound;

    public static MsToolkitConnection getInstance() { return INSTANCE; }

    private MsToolkitConnection() { }

    public synchronized IRemoteToolkit getRemoteToolkit() { return mRemoteToolkit; }

    public void connect(Context context) { connect(context, 0L); }

    private synchronized void connect(Context context, long delayMillis) {
        if (context == null) return;
        if (mContext == null) mContext = context.getApplicationContext();
        if (mRemoteToolkit != null || mBound || mConnecting) return;
        mConnecting = true;
        mHandler.removeCallbacks(mRunnableConnect);
        mHandler.postDelayed(mRunnableConnect, Math.max(0L, delayMillis));
    }

    private void tryBindService() {
        final Context context;
        synchronized (this) {
            if (mRemoteToolkit != null || mBound) {
                mConnecting = false;
                return;
            }
            context = mContext;
            if (context == null) {
                mConnecting = false;
                return;
            }
            mConnecting = true;
        }

        boolean bound = false;
        try {
            Intent intent = new Intent("com.syu.ms.toolkit");
            intent.setComponent(new ComponentName("com.syu.ms", "app.ToolkitService"));
            bound = context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        } catch (RuntimeException exception) {
            Log.w(TAG, "Unable to bind toolkit service", exception);
        }

        synchronized (this) {
            mConnecting = false;
            mBound = bound;
        }
        if (!bound) scheduleRetry();
    }

    private void scheduleRetry() {
        synchronized (this) {
            if (mRemoteToolkit != null || mBound || mConnecting || mContext == null) return;
            mConnecting = true;
            mHandler.removeCallbacks(mRunnableConnect);
            mHandler.postDelayed(mRunnableConnect, 1000L + mRandom.nextInt(3000));
        }
    }

    public synchronized void addObserver(ConnectionObserver observer) {
        if (observer == null || mConnectionObservers.contains(observer)) return;
        mConnectionObservers.add(observer);
        if (mRemoteToolkit != null) mHandler.post(new OnServiceConnected(observer));
    }

    public synchronized void removeObserver(ConnectionObserver observer) {
        if (observer == null) return;
        boolean removed = mConnectionObservers.remove(observer);
        if (removed && mRemoteToolkit != null) mHandler.post(new OnServiceDisconnected(observer));
    }

    public synchronized void clearObservers() {
        if (mRemoteToolkit != null) {
            for (ConnectionObserver observer : new ArrayList<>(mConnectionObservers)) {
                mHandler.post(new OnServiceDisconnected(observer));
            }
        }
        mConnectionObservers.clear();
    }

    @Override public void onServiceConnected(ComponentName name, IBinder service) {
        synchronized (this) {
            mRemoteToolkit = IRemoteToolkit.Stub.asInterface(service);
            mBound = true;
            mConnecting = false;
            mHandler.removeCallbacks(mRunnableConnect);
            for (ConnectionObserver observer : new ArrayList<>(mConnectionObservers)) {
                mHandler.post(new OnServiceConnected(observer));
            }
        }
    }

    @Override public void onServiceDisconnected(ComponentName name) {
        synchronized (this) {
            mRemoteToolkit = null;
            // The binding is still active. Android will normally reconnect this binding;
            // do not call bindService repeatedly while it is pending reconnection.
            for (ConnectionObserver observer : new ArrayList<>(mConnectionObservers)) {
                mHandler.post(new OnServiceDisconnected(observer));
            }
        }
    }

    @Override public void onBindingDied(ComponentName name) {
        handleDeadBinding();
    }

    @Override public void onNullBinding(ComponentName name) {
        handleDeadBinding();
    }

    private void handleDeadBinding() {
        Context context;
        synchronized (this) {
            mRemoteToolkit = null;
            context = mContext;
            mBound = false;
            mConnecting = false;
            for (ConnectionObserver observer : new ArrayList<>(mConnectionObservers)) {
                mHandler.post(new OnServiceDisconnected(observer));
            }
        }
        if (context != null) {
            try { context.unbindService(this); } catch (IllegalArgumentException ignored) { }
        }
        scheduleRetry();
    }

    private final class OnServiceConnected implements Runnable {
        private final ConnectionObserver observer;
        OnServiceConnected(ConnectionObserver observer) { this.observer = observer; }
        @Override public void run() {
            IRemoteToolkit toolkit;
            synchronized (MsToolkitConnection.this) { toolkit = mRemoteToolkit; }
            if (toolkit != null) observer.onConnected(toolkit);
        }
    }

    private static final class OnServiceDisconnected implements Runnable {
        private final ConnectionObserver observer;
        OnServiceDisconnected(ConnectionObserver observer) { this.observer = observer; }
        @Override public void run() { observer.onDisconnected(); }
    }
}
