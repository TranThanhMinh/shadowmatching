package org.cocos2dx.lib;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class Cocos2dxAudioFocusManager {
    private static final int AUDIOFOCUS_GAIN = 0;
    private static final int AUDIOFOCUS_LOST = 1;
    private static final int AUDIOFOCUS_LOST_TRANSIENT = 2;
    private static final int AUDIOFOCUS_LOST_TRANSIENT_CAN_DUCK = 3;
    private static final String TAG = "AudioFocusManager";
    private static AudioManager.OnAudioFocusChangeListener sAfChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.1
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int focusChange) {
            Log.d(Cocos2dxAudioFocusManager.TAG, "onAudioFocusChange: " + focusChange + ", thread: " + Thread.currentThread().getName());
            if (focusChange == -1) {
                Log.d(Cocos2dxAudioFocusManager.TAG, "Pause music by AUDIOFOCUS_LOSS");
                Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Cocos2dxAudioFocusManager.nativeOnAudioFocusChange(1);
                        Cocos2dxHelper.setAudioFocus(false);
                    }
                });
                return;
            }
            if (focusChange == -2) {
                Log.d(Cocos2dxAudioFocusManager.TAG, "Pause music by AUDIOFOCUS_LOSS_TRANSILENT");
                Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        Cocos2dxAudioFocusManager.nativeOnAudioFocusChange(2);
                        Cocos2dxHelper.setAudioFocus(false);
                    }
                });
            } else if (focusChange == -3) {
                Log.d(Cocos2dxAudioFocusManager.TAG, "Lower the volume, keep playing by AUDIOFOCUS_LOSS_TRANSILENT_CAN_DUCK");
                Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        Cocos2dxAudioFocusManager.nativeOnAudioFocusChange(3);
                        Cocos2dxHelper.setAudioFocus(false);
                    }
                });
            } else if (focusChange == 1) {
                Log.d(Cocos2dxAudioFocusManager.TAG, "Resume music by AUDIOFOCUS_GAIN");
                Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.1.4
                    @Override // java.lang.Runnable
                    public void run() {
                        Cocos2dxAudioFocusManager.nativeOnAudioFocusChange(0);
                        Cocos2dxHelper.setAudioFocus(true);
                    }
                });
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeOnAudioFocusChange(int i);

    Cocos2dxAudioFocusManager() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean registerAudioFocusListener(Context context) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        int result = am.requestAudioFocus(sAfChangeListener, 3, 1);
        if (result == 1) {
            Log.d(TAG, "requestAudioFocus succeed");
            return true;
        }
        Log.e(TAG, "requestAudioFocus failed!");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unregisterAudioFocusListener(Context context) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        int result = am.abandonAudioFocus(sAfChangeListener);
        if (result == 1) {
            Log.d(TAG, "abandonAudioFocus succeed!");
        } else {
            Log.e(TAG, "abandonAudioFocus failed!");
        }
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxAudioFocusManager.2
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxHelper.setAudioFocus(true);
                Cocos2dxAudioFocusManager.nativeOnAudioFocusChange(0);
            }
        });
    }
}
