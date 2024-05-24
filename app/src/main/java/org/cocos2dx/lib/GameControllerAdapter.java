package org.cocos2dx.lib;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class GameControllerAdapter {
    private static ArrayList<Runnable> sRunnableFrameStartList = null;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeControllerAxisEvent(String str, int i, int i2, float f, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeControllerButtonEvent(String str, int i, int i2, boolean z, float f, boolean z2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeControllerConnected(String str, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeControllerDisconnected(String str, int i);

    public static void addRunnableToFrameStartList(Runnable runnable) {
        if (sRunnableFrameStartList == null) {
            sRunnableFrameStartList = new ArrayList<>();
        }
        sRunnableFrameStartList.add(runnable);
    }

    public static void removeRunnableFromFrameStartList(Runnable runnable) {
        ArrayList<Runnable> arrayList = sRunnableFrameStartList;
        if (arrayList != null) {
            arrayList.remove(runnable);
        }
    }

    public static void onDrawFrameStart() {
        ArrayList<Runnable> arrayList = sRunnableFrameStartList;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                sRunnableFrameStartList.get(i).run();
            }
        }
    }

    public static void onConnected(final String vendorName, final int controller) {
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.GameControllerAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                GameControllerAdapter.nativeControllerConnected(vendorName, controller);
            }
        });
    }

    public static void onDisconnected(final String vendorName, final int controller) {
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.GameControllerAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                GameControllerAdapter.nativeControllerDisconnected(vendorName, controller);
            }
        });
    }

    public static void onButtonEvent(final String vendorName, final int controller, final int button, final boolean isPressed, final float value, final boolean isAnalog) {
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.GameControllerAdapter.3
            @Override // java.lang.Runnable
            public void run() {
                GameControllerAdapter.nativeControllerButtonEvent(vendorName, controller, button, isPressed, value, isAnalog);
            }
        });
    }

    public static void onAxisEvent(final String vendorName, final int controller, final int axisID, final float value, final boolean isAnalog) {
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.GameControllerAdapter.4
            @Override // java.lang.Runnable
            public void run() {
                GameControllerAdapter.nativeControllerAxisEvent(vendorName, controller, axisID, value, isAnalog);
            }
        });
    }
}
