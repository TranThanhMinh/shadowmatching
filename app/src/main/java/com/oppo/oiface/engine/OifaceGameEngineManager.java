package com.oppo.oiface.engine;

import android.os.IBinder;
import android.os.RemoteException;

//import android.util.Slog;
import com.oppo.oiface.engine.IOIfaceNotifier;
import com.oppo.oiface.engine.IOIfaceService;

import org.cocos2dx.cpp.ServiceManager;

import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class OifaceGameEngineManager {
    private static final String TAG = "OppoManager";
    private static final String oppoSdkVersion = "2.1";
    private WeakReference<CallBack> mCallbacks;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.oppo.oiface.engine.OifaceGameEngineManager.2
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            IOIfaceService unused = OifaceGameEngineManager.mService = null;
         //   Slog.d(OifaceGameEngineManager.TAG, "OIfaceService binderDied");
        }
    };
    private IBinder mRemote;
    private static IOIfaceService mService = null;
    private static OifaceGameEngineManager mOppoManager = null;

    private OifaceGameEngineManager() {
        connectOifaceService();
    }

    private boolean connectOifaceService() {
        IBinder checkService = ServiceManager.checkService("oiface");
        this.mRemote = checkService;
        IOIfaceService asInterface = IOIfaceService.Stub.asInterface(checkService);
        mService = asInterface;
        if (asInterface == null) {
            return false;
        }
        try {
            asInterface.registerEngineClient(new IOIfaceNotifier.Stub() { // from class: com.oppo.oiface.engine.OifaceGameEngineManager.1
                @Override // com.oppo.oiface.engine.IOIfaceNotifier
                public void onSystemNotify(String result) {
                    if (OifaceGameEngineManager.this.mCallbacks != null && OifaceGameEngineManager.this.mCallbacks.get() != null) {
                        ((CallBack) OifaceGameEngineManager.this.mCallbacks.get()).systemCallBack(result);
                    }
                }
            });
            this.mRemote.linkToDeath(this.mDeathRecipient, 0);
            return true;
        } catch (Exception e) {
            //  Slog.d(TAG, "IOIfaceService registerEngineClient error" + e);
            mService = null;
            return false;
        }
    }

    public static OifaceGameEngineManager getInstance() {
        if (mService == null) {
            synchronized (OifaceGameEngineManager.class) {
                if (mService == null) {
                    mOppoManager = new OifaceGameEngineManager();
                }
            }
        }
        return mOppoManager;
    }

    public String getOifaceVersion() {
        if (mService == null && !connectOifaceService()) {
            return null;
        }
        try {
            return mService.getOifaceVersion() + ":" + oppoSdkVersion;
        } catch (Exception e) {
            mService = null;
          //  Slog.d(TAG, "getOifaceVersion error:" + e);
            return null;
        }
    }

    public int getMemoryUsage(int pid) {
        if (mService == null && !connectOifaceService()) {
            return -1;
        }
        try {
            return mService.getMemoryUsage(pid);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void systemStatus(CallBack callBack) {
        if (mService == null) {
            return;
        }
        try {
            this.mCallbacks = new WeakReference<>(callBack);
            mService.onAppRegister();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean updateGameEngineInfo(String json) {
        if (mService == null && !connectOifaceService()) {
            return false;
        }
        try {
            mService.updateGameEngineInfo(json);
            return true;
        } catch (Exception e) {
            mService = null;
          //  Slog.d(TAG, "updateGameInfo error:" + e);
            return false;
        }
    }
}
