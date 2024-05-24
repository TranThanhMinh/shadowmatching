package com.oppo.oiface.engine;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IOIfaceService extends IInterface {
    int getMemoryUsage(int i) throws RemoteException;

    String getOifaceVersion() throws RemoteException;

    void onAppRegister() throws RemoteException;

    void registerEngineClient(IOIfaceNotifier iOIfaceNotifier) throws RemoteException;

    void updateGameEngineInfo(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IOIfaceService {
        private static final String DESCRIPTOR = "com.oppo.oiface.IOIfaceService";
        static final int TRANSACTION_REGISTER_ENGINE_CLIENT = 154;
        static final int TRANSACTION_getMemoryUsage = 109;
        static final int TRANSACTION_getOifaceversion = 105;
        static final int TRANSACTION_onAppRegister = 104;
        static final int TRANSACTION_updateGameEngineInfo = 155;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOIfaceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IOIfaceService)) {
                return (IOIfaceService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IOIfaceService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.oppo.oiface.engine.IOIfaceService
            public void registerEngineClient(IOIfaceNotifier callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_REGISTER_ENGINE_CLIENT, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.oppo.oiface.engine.IOIfaceService
            public void onAppRegister() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(104, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.oppo.oiface.engine.IOIfaceService
            public String getOifaceVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.oppo.oiface.engine.IOIfaceService
            public int getMemoryUsage(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(109, _data, _reply, 0);
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.oppo.oiface.engine.IOIfaceService
            public void updateGameEngineInfo(String json) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(json);
                    this.mRemote.transact(Stub.TRANSACTION_updateGameEngineInfo, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
