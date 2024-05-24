package org.cocos2dx.cpp;

import androidx.lifecycle.GeneratedAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MethodCallsLogger;

/* loaded from: classes.dex */
public class MyApplication_LifecycleAdapter implements GeneratedAdapter {
    final MyApplication mReceiver;

    MyApplication_LifecycleAdapter(MyApplication myApplication) {
        this.mReceiver = myApplication;
    }

    @Override // androidx.lifecycle.GeneratedAdapter
    public void callMethods(LifecycleOwner lifecycleOwner, Lifecycle.Event event, boolean z, MethodCallsLogger methodCallsLogger) {
        boolean z2 = methodCallsLogger != null;
        if (!z && event == Lifecycle.Event.ON_START) {
            if (!z2 || methodCallsLogger.approveCall("onMoveToForeground", 1)) {
                this.mReceiver.onMoveToForeground();
            }
        }
    }
}
