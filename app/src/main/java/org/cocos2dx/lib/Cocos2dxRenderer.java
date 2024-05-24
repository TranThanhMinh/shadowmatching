package org.cocos2dx.lib;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes2.dex */
public class Cocos2dxRenderer implements GLSurfaceView.Renderer {
    private static final long NANOSECONDSPERMICROSECOND = 1000000;
    private static final long NANOSECONDSPERSECOND = 1000000000;
    private static long sAnimationInterval = 16666668;
    private long mLastTickInNanoSeconds;
    private boolean mNativeInitCompleted = false;
    private int mScreenHeight;
    private int mScreenWidth;

    private static native void nativeDeleteBackward();

    private static native String nativeGetContentText();

    private static native void nativeInit(int i, int i2);

    private static native void nativeInsertText(String str);

    private static native boolean nativeKeyEvent(int i, boolean z);

    private static native void nativeOnPause();

    private static native void nativeOnResume();

    private static native void nativeOnSurfaceChanged(int i, int i2);

    private static native void nativeRender();

    private static native void nativeTouchesBegin(int i, float f, float f2);

    private static native void nativeTouchesCancel(int[] iArr, float[] fArr, float[] fArr2);

    private static native void nativeTouchesEnd(int i, float f, float f2);

    private static native void nativeTouchesMove(int[] iArr, float[] fArr, float[] fArr2);

    public static void setAnimationInterval(float interval) {
        sAnimationInterval = (long) (1.0E9f * interval);
    }

    public void setScreenWidthAndHeight(int surfaceWidth, int surfaceHeight) {
        this.mScreenWidth = surfaceWidth;
        this.mScreenHeight = surfaceHeight;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 GL10, EGLConfig EGLConfig) {
        nativeInit(this.mScreenWidth, this.mScreenHeight);
        this.mLastTickInNanoSeconds = System.nanoTime();
        this.mNativeInitCompleted = true;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 GL10, int width, int height) {
        nativeOnSurfaceChanged(width, height);
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl) {
        if (((float) sAnimationInterval) <= 1.6666668E7f) {
            nativeRender();
            return;
        }
        long now = System.nanoTime();
        long interval = now - this.mLastTickInNanoSeconds;
        long j = sAnimationInterval;
        if (interval < j) {
            try {
                Thread.sleep((j - interval) / NANOSECONDSPERMICROSECOND);
            } catch (Exception e) {
            }
        }
        this.mLastTickInNanoSeconds = System.nanoTime();
        nativeRender();
    }

    public void handleActionDown(int id, float x, float y) {
        nativeTouchesBegin(id, x, y);
    }

    public void handleActionUp(int id, float x, float y) {
        nativeTouchesEnd(id, x, y);
    }

    public void handleActionCancel(int[] ids, float[] xs, float[] ys) {
        nativeTouchesCancel(ids, xs, ys);
    }

    public void handleActionMove(int[] ids, float[] xs, float[] ys) {
        nativeTouchesMove(ids, xs, ys);
    }

    public void handleKeyDown(int keyCode) {
        nativeKeyEvent(keyCode, true);
    }

    public void handleKeyUp(int keyCode) {
        nativeKeyEvent(keyCode, false);
    }

    public void handleOnPause() {
        if (!this.mNativeInitCompleted) {
            return;
        }
        Cocos2dxHelper.onEnterBackground();
        nativeOnPause();
    }

    public void handleOnResume() {
        Cocos2dxHelper.onEnterForeground();
        nativeOnResume();
    }

    public void handleInsertText(String text) {
        nativeInsertText(text);
    }

    public void handleDeleteBackward() {
        nativeDeleteBackward();
    }

    public String getContentText() {
        return nativeGetContentText();
    }
}
