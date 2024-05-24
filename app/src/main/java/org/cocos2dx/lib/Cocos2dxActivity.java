package org.cocos2dx.lib;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import org.cocos2dx.lib.Cocos2dxHandler;
import org.cocos2dx.lib.Cocos2dxHelper;

/* loaded from: classes2.dex */
public abstract class Cocos2dxActivity extends Activity implements Cocos2dxHelper.Cocos2dxHelperListener {
    private static final String TAG = Cocos2dxActivity.class.getSimpleName();
    private static Cocos2dxActivity sContext = null;
    private Cocos2dxGLSurfaceView mGLSurfaceView = null;
    private int[] mGLContextAttrs = null;
    private Cocos2dxHandler mHandler = null;
    private Cocos2dxVideoHelper mVideoHelper = null;
    private Cocos2dxWebViewHelper mWebViewHelper = null;
    private Cocos2dxEditBoxHelper mEditBoxHelper = null;
    private boolean hasFocus = false;
    private boolean showVirtualButton = false;
    private boolean gainAudioFocus = false;
    private boolean paused = true;
    protected ResizeLayout mFrameLayout = null;

    private static native int[] getGLContextAttrs();

    public Cocos2dxGLSurfaceView getGLSurfaceView() {
        return this.mGLSurfaceView;
    }

    public static Context getContext() {
        return sContext;
    }

    public void setKeepScreenOn(final boolean value) {
        runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxActivity.1
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxActivity.this.mGLSurfaceView.setKeepScreenOn(value);
            }
        });
    }

    public void setEnableVirtualButton(boolean value) {
        this.showVirtualButton = value;
    }

    public void setEnableAudioFocusGain(boolean value) {
        if (this.gainAudioFocus != value) {
            if (!this.paused) {
                if (value) {
                    Cocos2dxAudioFocusManager.registerAudioFocusListener(this);
                } else {
                    Cocos2dxAudioFocusManager.unregisterAudioFocusListener(this);
                }
            }
            this.gainAudioFocus = value;
        }
    }

    protected void onLoadNativeLibraries() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 128);
            Bundle bundle = ai.metaData;
            String libName = bundle.getString("android.app.lib_name");
            System.loadLibrary(libName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            Log.w(TAG, "[Workaround] Ignore the activity started from icon!");
            return;
        }
        hideVirtualButton();
        onLoadNativeLibraries();
        sContext = this;
        this.mHandler = new Cocos2dxHandler(this);
        Cocos2dxHelper.init(this);
        this.mGLContextAttrs = getGLContextAttrs();
        init();
        if (this.mVideoHelper == null) {
            this.mVideoHelper = new Cocos2dxVideoHelper(this, this.mFrameLayout);
        }
        if (this.mWebViewHelper == null) {
            this.mWebViewHelper = new Cocos2dxWebViewHelper(this.mFrameLayout);
        }
        if (this.mEditBoxHelper == null) {
            this.mEditBoxHelper = new Cocos2dxEditBoxHelper(this.mFrameLayout);
        }
        Window window = getWindow();
        window.setSoftInputMode(32);
        setVolumeControlStream(3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        Log.d(TAG, "onResume()");
        this.paused = false;
        super.onResume();
        if (this.gainAudioFocus) {
            Cocos2dxAudioFocusManager.registerAudioFocusListener(this);
        }
        hideVirtualButton();
        resumeIfHasFocus();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged() hasFocus=" + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        this.hasFocus = hasFocus;
        resumeIfHasFocus();
    }

    private void resumeIfHasFocus() {
        boolean readyToPlay = (isDeviceLocked() || isDeviceAsleep()) ? false : true;
        if (this.hasFocus && readyToPlay) {
            hideVirtualButton();
            Cocos2dxHelper.onResume();
            this.mGLSurfaceView.onResume();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        Log.d(TAG, "onPause()");
        this.paused = true;
        super.onPause();
        if (this.gainAudioFocus) {
            Cocos2dxAudioFocusManager.unregisterAudioFocusListener(this);
        }
        Cocos2dxHelper.onPause();
        this.mGLSurfaceView.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.gainAudioFocus) {
            Cocos2dxAudioFocusManager.unregisterAudioFocusListener(this);
        }
        super.onDestroy();
    }

    @Override // org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener
    public void showDialog(String pTitle, String pMessage) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = new Cocos2dxHandler.DialogMessage(pTitle, pMessage);
        this.mHandler.sendMessage(msg);
    }

    @Override // org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener
    public void runOnGLThread(Runnable pRunnable) {
        this.mGLSurfaceView.queueEvent(pRunnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (PreferenceManager.OnActivityResultListener listener : Cocos2dxHelper.getOnActivityResultListeners()) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void init() {
        ViewGroup.LayoutParams framelayout_params = new ViewGroup.LayoutParams(-1, -1);
        ResizeLayout resizeLayout = new ResizeLayout(this);
        this.mFrameLayout = resizeLayout;
        resizeLayout.setLayoutParams(framelayout_params);
        ViewGroup.LayoutParams edittext_layout_params = new ViewGroup.LayoutParams(-1, -2);
        Cocos2dxEditBox edittext = new Cocos2dxEditBox(this);
        edittext.setLayoutParams(edittext_layout_params);
        this.mFrameLayout.addView(edittext);
        Cocos2dxGLSurfaceView onCreateView = onCreateView();
        this.mGLSurfaceView = onCreateView;
        onCreateView.setPreserveEGLContextOnPause(true);
        this.mFrameLayout.addView(this.mGLSurfaceView);
        this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
        this.mGLSurfaceView.setCocos2dxEditText(edittext);
        setContentView(this.mFrameLayout);
    }

    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        if (this.mGLContextAttrs[3] > 0) {
            glSurfaceView.getHolder().setFormat(-3);
        }
        Cocos2dxEGLConfigChooser chooser = new Cocos2dxEGLConfigChooser(this.mGLContextAttrs);
        glSurfaceView.setEGLConfigChooser(chooser);
        return glSurfaceView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideVirtualButton() {
        if (!this.showVirtualButton && Build.VERSION.SDK_INT >= 19) {
            try {
                int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION")).intValue();
                int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN")).intValue();
                int SYSTEM_UI_FLAG_HIDE_NAVIGATION = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_HIDE_NAVIGATION")).intValue();
                int SYSTEM_UI_FLAG_FULLSCREEN = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_FULLSCREEN")).intValue();
                int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_IMMERSIVE_STICKY")).intValue();
                int SYSTEM_UI_FLAG_LAYOUT_STABLE = ((Integer) Cocos2dxReflectionHelper.getConstantValue(View.class, "SYSTEM_UI_FLAG_LAYOUT_STABLE")).intValue();
                Object[] parameters = {Integer.valueOf(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY)};
                Cocos2dxReflectionHelper.invokeInstanceMethod(getWindow().getDecorView(), "setSystemUiVisibility", new Class[]{Integer.TYPE}, parameters);
            } catch (NullPointerException e) {
                Log.e(TAG, "hideVirtualButton", e);
            }
        }
    }

    private static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        String str = TAG;
        Log.d(str, "model=" + model);
        String product = Build.PRODUCT;
        Log.d(str, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        Log.d(str, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    private static boolean isDeviceLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService("keyguard");
        boolean locked = keyguardManager.inKeyguardRestrictedInputMode();
        return locked;
    }

    private static boolean isDeviceAsleep() {
        PowerManager powerManager = (PowerManager) getContext().getSystemService("power");
        if (powerManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 20) {
            return !powerManager.isInteractive();
        }
        return !powerManager.isScreenOn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Cocos2dxEGLConfigChooser implements GLSurfaceView.EGLConfigChooser {
        private final int EGL_OPENGL_ES2_BIT = 4;
        private final int EGL_OPENGL_ES3_BIT = 64;
        private int[] mConfigAttributes;

        public Cocos2dxEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize, int multisamplingCount) {
            this.mConfigAttributes = new int[]{redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, multisamplingCount};
        }

        public Cocos2dxEGLConfigChooser(int[] attributes) {
            this.mConfigAttributes = attributes;
        }

        @Override // android.opengl.GLSurfaceView.EGLConfigChooser
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[][] EGLAttributes = new int[4][];
            int[] iArr = new int[19];
            iArr[0] = 12324;
            int[] iArr2 = this.mConfigAttributes;
            iArr[1] = iArr2[0];
            iArr[2] = 12323;
            iArr[3] = iArr2[1];
            iArr[4] = 12322;
            iArr[5] = iArr2[2];
            iArr[6] = 12321;
            iArr[7] = iArr2[3];
            iArr[8] = 12325;
            iArr[9] = iArr2[4];
            iArr[10] = 12326;
            iArr[11] = iArr2[5];
            iArr[12] = 12338;
            iArr[13] = iArr2[6] > 0 ? 1 : 0;
            iArr[14] = 12337;
            iArr[15] = iArr2[6];
            iArr[16] = 12352;
            iArr[17] = 4;
            iArr[18] = 12344;
            EGLAttributes[0] = iArr;
            int[] iArr3 = new int[19];
            iArr3[0] = 12324;
            iArr3[1] = iArr2[0];
            iArr3[2] = 12323;
            iArr3[3] = iArr2[1];
            iArr3[4] = 12322;
            iArr3[5] = iArr2[2];
            iArr3[6] = 12321;
            iArr3[7] = iArr2[3];
            iArr3[8] = 12325;
            iArr3[9] = iArr2[4] >= 24 ? 16 : iArr2[4];
            iArr3[10] = 12326;
            iArr3[11] = iArr2[5];
            iArr3[12] = 12338;
            iArr3[13] = iArr2[6] > 0 ? 1 : 0;
            iArr3[14] = 12337;
            iArr3[15] = iArr2[6];
            iArr3[16] = 12352;
            iArr3[17] = 4;
            iArr3[18] = 12344;
            EGLAttributes[1] = iArr3;
            int[] iArr4 = new int[19];
            iArr4[0] = 12324;
            iArr4[1] = iArr2[0];
            iArr4[2] = 12323;
            iArr4[3] = iArr2[1];
            iArr4[4] = 12322;
            iArr4[5] = iArr2[2];
            iArr4[6] = 12321;
            iArr4[7] = iArr2[3];
            iArr4[8] = 12325;
            iArr4[9] = iArr2[4] >= 24 ? 16 : iArr2[4];
            iArr4[10] = 12326;
            iArr4[11] = iArr2[5];
            iArr4[12] = 12338;
            iArr4[13] = 0;
            iArr4[14] = 12337;
            iArr4[15] = 0;
            iArr4[16] = 12352;
            iArr4[17] = 4;
            iArr4[18] = 12344;
            EGLAttributes[2] = iArr4;
            EGLAttributes[3] = new int[]{12352, 4, 12344};
            for (int[] eglAtribute : EGLAttributes) {
                EGLConfig result = doChooseConfig(egl, display, eglAtribute);
                if (result != null) {
                    return result;
                }
            }
            Log.e("device_policy", "Can not select an EGLConfig for rendering.");
            return null;
        }

        private EGLConfig doChooseConfig(EGL10 egl, EGLDisplay display, int[] attributes) {
            EGLConfig[] configs = new EGLConfig[1];
            int[] matchedConfigNum = new int[1];
            boolean result = egl.eglChooseConfig(display, attributes, configs, 1, matchedConfigNum);
            if (result && matchedConfigNum[0] > 0) {
                return configs[0];
            }
            return null;
        }
    }
}
