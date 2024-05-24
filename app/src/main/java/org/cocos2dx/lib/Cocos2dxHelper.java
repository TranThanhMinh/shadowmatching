package org.cocos2dx.lib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.KeyCharacterMap;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.enhance.gameservice.IGameTuningService;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class Cocos2dxHelper {
    private static final int BOOST_TIME = 7;
    private static final String PREFS_NAME = "Cocos2dxPrefsFile";
    private static final int RUNNABLES_PER_FRAME = 5;
    private static boolean sAccelerometerEnabled;
    private static boolean sActivityVisible;
    private static AssetManager sAssetManager;
    private static Cocos2dxMusic sCocos2dMusic;
    private static Cocos2dxHelperListener sCocos2dxHelperListener;
    private static boolean sCompassEnabled;
    private static String sPackageName;
    private static final String TAG = Cocos2dxHelper.class.getSimpleName();
    private static Cocos2dxSound sCocos2dSound = null;
    private static Cocos2dxAccelerometer sCocos2dxAccelerometer = null;
    private static Activity sActivity = null;
    private static Set<PreferenceManager.OnActivityResultListener> onActivityResultListeners = new LinkedHashSet();
    private static Vibrator sVibrateService = null;
    private static IGameTuningService mGameServiceBinder = null;
    private static String sAssetsPath = "";
    private static ZipResourceFile sOBBFile = null;
    private static boolean sInited = false;
    private static ServiceConnection connection = new ServiceConnection() { // from class: org.cocos2dx.lib.Cocos2dxHelper.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            IGameTuningService unused = Cocos2dxHelper.mGameServiceBinder = IGameTuningService.Stub.asInterface(service);
            Cocos2dxHelper.fastLoading(7);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Cocos2dxHelper.sActivity.getApplicationContext().unbindService(Cocos2dxHelper.connection);
        }
    };

    /* loaded from: classes2.dex */
    public interface Cocos2dxHelperListener {
        void runOnGLThread(Runnable runnable);

        void showDialog(String str, String str2);
    }

    private static native void nativeSetAudioDeviceInfo(boolean z, int i, int i2);

    private static native void nativeSetContext(Context context, AssetManager assetManager);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetEditTextDialogResult(byte[] bArr);

    public static void runOnGLThread(Runnable r) {
        ((Cocos2dxActivity) sActivity).runOnGLThread(r);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void init(Activity activity) {
        sActivity = activity;
        sCocos2dxHelperListener = (Cocos2dxHelperListener) activity;
        if (!sInited) {
            PackageManager pm = activity.getPackageManager();
            boolean isSupportLowLatency = pm.hasSystemFeature("android.hardware.audio.low_latency");
            String str = TAG;
            Log.d(str, "isSupportLowLatency:" + isSupportLowLatency);
            int sampleRate = 44100;
            int bufferSizeInFrames = 192;
            if (Build.VERSION.SDK_INT >= 17) {
                AudioManager am = (AudioManager) activity.getSystemService("audio");
                Object[] parameters = {Cocos2dxReflectionHelper.getConstantValue(AudioManager.class, "PROPERTY_OUTPUT_SAMPLE_RATE")};
                String strSampleRate = (String) Cocos2dxReflectionHelper.invokeInstanceMethod(am, "getProperty", new Class[]{String.class}, parameters);
                Object[] parameters2 = {Cocos2dxReflectionHelper.getConstantValue(AudioManager.class, "PROPERTY_OUTPUT_FRAMES_PER_BUFFER")};
                String strBufferSizeInFrames = (String) Cocos2dxReflectionHelper.invokeInstanceMethod(am, "getProperty", new Class[]{String.class}, parameters2);
                try {
                    sampleRate = Integer.parseInt(strSampleRate);
                    bufferSizeInFrames = Integer.parseInt(strBufferSizeInFrames);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "parseInt failed", e);
                }
                Log.d(TAG, "sampleRate: " + sampleRate + ", framesPerBuffer: " + bufferSizeInFrames);
            } else {
                Log.d(str, "android version is lower than 17");
            }
            nativeSetAudioDeviceInfo(isSupportLowLatency, sampleRate, bufferSizeInFrames);
            ApplicationInfo applicationInfo = activity.getApplicationInfo();
            sPackageName = applicationInfo.packageName;
            sCocos2dMusic = new Cocos2dxMusic(activity);
            AssetManager assets = activity.getAssets();
            sAssetManager = assets;
            nativeSetContext(activity, assets);
            Cocos2dxBitmap.setContext(activity);
            sVibrateService = (Vibrator) activity.getSystemService("vibrator");
            sInited = true;
            Intent serviceIntent = new Intent(IGameTuningService.class.getName());
            serviceIntent.setPackage("com.enhance.gameservice");
            activity.getApplicationContext().bindService(serviceIntent, connection, 1);
        }
    }

    public static String getAssetsPath() {
        if (sAssetsPath.equals("")) {
            String pathToOBB = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/" + sPackageName;
            String[] fileNames = new File(pathToOBB).list(new FilenameFilter() { // from class: org.cocos2dx.lib.Cocos2dxHelper.1
                @Override // java.io.FilenameFilter
                public boolean accept(File dir, String name) {
                    return name.startsWith("main.") && name.endsWith(".obb");
                }
            });
            String fullPathToOBB = "";
            if (fileNames != null && fileNames.length > 0) {
                fullPathToOBB = pathToOBB + "/" + fileNames[0];
            }
            File obbFile = new File(fullPathToOBB);
            if (obbFile.exists()) {
                sAssetsPath = fullPathToOBB;
            } else {
                sAssetsPath = sActivity.getApplicationInfo().sourceDir;
            }
        }
        return sAssetsPath;
    }

    public static ZipResourceFile getObbFile() {
        if (sOBBFile == null) {
            int versionCode = 1;
            try {
                versionCode = Cocos2dxActivity.getContext().getPackageManager().getPackageInfo(getCocos2dxPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            try {
                sOBBFile = APKExpansionSupport.getAPKExpansionZipFile(Cocos2dxActivity.getContext(), versionCode, 0);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return sOBBFile;
    }

    public static Activity getActivity() {
        return sActivity;
    }

    public static void addOnActivityResultListener(PreferenceManager.OnActivityResultListener listener) {
        onActivityResultListeners.add(listener);
    }

    public static Set<PreferenceManager.OnActivityResultListener> getOnActivityResultListeners() {
        return onActivityResultListeners;
    }

    public static boolean isActivityVisible() {
        return sActivityVisible;
    }

    public static String getCocos2dxPackageName() {
        return sPackageName;
    }

    public static String getCocos2dxWritablePath() {
        return sActivity.getFilesDir().getAbsolutePath();
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static AssetManager getAssetManager() {
        return sAssetManager;
    }

    public static void enableAccelerometer() {
        sAccelerometerEnabled = true;
        getAccelerometer().enableAccel();
    }

    public static void enableCompass() {
        sCompassEnabled = true;
        getAccelerometer().enableCompass();
    }

    public static void setAccelerometerInterval(float interval) {
        getAccelerometer().setInterval(interval);
    }

    public static void disableAccelerometer() {
        sAccelerometerEnabled = false;
        getAccelerometer().disable();
    }

    public static void setKeepScreenOn(boolean value) {
        ((Cocos2dxActivity) sActivity).setKeepScreenOn(value);
    }

    public static void vibrate(float duration) {
        sVibrateService.vibrate((long) (1000.0f * duration));
    }

    public static String getVersion() {
        try {
            String version = Cocos2dxActivity.getContext().getPackageManager().getPackageInfo(Cocos2dxActivity.getContext().getPackageName(), 0).versionName;
            return version;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean openURL(String url) {
        try {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            sActivity.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static long[] getObbAssetFileDescriptor(String path) {
        AssetFileDescriptor descriptor;
        long[] array = new long[3];
        if (getObbFile() != null && (descriptor = getObbFile().getAssetFileDescriptor(path)) != null) {
            try {
                ParcelFileDescriptor parcel = descriptor.getParcelFileDescriptor();
                Method method = parcel.getClass().getMethod("getFd", new Class[0]);
                array[0] = ((Integer) method.invoke(parcel, new Object[0])).intValue();
                array[1] = descriptor.getStartOffset();
                array[2] = descriptor.getLength();
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.toString());
            } catch (NoSuchMethodException e2) {
                Log.e(TAG, "Accessing file descriptor directly from the OBB is only supported from Android 3.1 (API level 12) and above.");
            } catch (InvocationTargetException e3) {
                Log.e(TAG, e3.toString());
            }
        }
        return array;
    }

    public static void preloadBackgroundMusic(String pPath) {
        sCocos2dMusic.preloadBackgroundMusic(pPath);
    }

    public static void playBackgroundMusic(String pPath, boolean isLoop) {
        sCocos2dMusic.playBackgroundMusic(pPath, isLoop);
    }

    public static void resumeBackgroundMusic() {
        sCocos2dMusic.resumeBackgroundMusic();
    }

    public static void pauseBackgroundMusic() {
        sCocos2dMusic.pauseBackgroundMusic();
    }

    public static void stopBackgroundMusic() {
        sCocos2dMusic.stopBackgroundMusic();
    }

    public static void rewindBackgroundMusic() {
        sCocos2dMusic.rewindBackgroundMusic();
    }

    public static boolean willPlayBackgroundMusic() {
        return sCocos2dMusic.willPlayBackgroundMusic();
    }

    public static boolean isBackgroundMusicPlaying() {
        return sCocos2dMusic.isBackgroundMusicPlaying();
    }

    public static float getBackgroundMusicVolume() {
        return sCocos2dMusic.getBackgroundVolume();
    }

    public static void setBackgroundMusicVolume(float volume) {
        sCocos2dMusic.setBackgroundVolume(volume);
    }

    public static void preloadEffect(String path) {
        getSound().preloadEffect(path);
    }

    public static int playEffect(String path, boolean isLoop, float pitch, float pan, float gain) {
        return getSound().playEffect(path, isLoop, pitch, pan, gain);
    }

    public static void resumeEffect(int soundId) {
        getSound().resumeEffect(soundId);
    }

    public static void pauseEffect(int soundId) {
        getSound().pauseEffect(soundId);
    }

    public static void stopEffect(int soundId) {
        getSound().stopEffect(soundId);
    }

    public static float getEffectsVolume() {
        return getSound().getEffectsVolume();
    }

    public static void setEffectsVolume(float volume) {
        getSound().setEffectsVolume(volume);
    }

    public static void unloadEffect(String path) {
        getSound().unloadEffect(path);
    }

    public static void pauseAllEffects() {
        getSound().pauseAllEffects();
    }

    public static void resumeAllEffects() {
        getSound().resumeAllEffects();
    }

    public static void stopAllEffects() {
        getSound().stopAllEffects();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setAudioFocus(boolean isAudioFocus) {
        sCocos2dMusic.setAudioFocus(isAudioFocus);
        getSound().setAudioFocus(isAudioFocus);
    }

    public static void end() {
        sCocos2dMusic.end();
        getSound().end();
    }

    public static void onResume() {
        sActivityVisible = true;
        if (sAccelerometerEnabled) {
            getAccelerometer().enableAccel();
        }
        if (sCompassEnabled) {
            getAccelerometer().enableCompass();
        }
    }

    public static void onPause() {
        sActivityVisible = false;
        if (sAccelerometerEnabled) {
            getAccelerometer().disable();
        }
    }

    public static void onEnterBackground() {
        getSound().onEnterBackground();
        sCocos2dMusic.onEnterBackground();
    }

    public static void onEnterForeground() {
        getSound().onEnterForeground();
        sCocos2dMusic.onEnterForeground();
    }

    public static void terminateProcess() {
        Process.killProcess(Process.myPid());
    }

    private static void showDialog(String pTitle, String pMessage) {
        sCocos2dxHelperListener.showDialog(pTitle, pMessage);
    }

    public static void setEditTextDialogResult(String pResult) {
        try {
            final byte[] bytesUTF8 = pResult.getBytes("UTF8");
            sCocos2dxHelperListener.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxHelper.3
                @Override // java.lang.Runnable
                public void run() {
                    Cocos2dxHelper.nativeSetEditTextDialogResult(bytesUTF8);
                }
            });
        } catch (UnsupportedEncodingException e) {
        }
    }

    public static int getDPI() {
        Display d;
        if (sActivity != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = sActivity.getWindowManager();
            if (wm != null && (d = wm.getDefaultDisplay()) != null) {
                d.getMetrics(metrics);
                return (int) (metrics.density * 160.0f);
            }
            return -1;
        }
        return -1;
    }

    public static boolean getBoolForKey(String key, boolean defaultValue) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        try {
            return settings.getBoolean(key, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Boolean.parseBoolean(value.toString());
            }
            if (value instanceof Integer) {
                int intValue = ((Integer) value).intValue();
                return intValue != 0;
            }
            if (value instanceof Float) {
                float floatValue = ((Float) value).floatValue();
                return floatValue != 0.0f;
            }
            return defaultValue;
        }
    }

    public static int getIntegerForKey(String key, int defaultValue) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        try {
            return settings.getInt(key, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Integer.parseInt(value.toString());
            }
            if (value instanceof Float) {
                return ((Float) value).intValue();
            }
            if (value instanceof Boolean) {
                boolean booleanValue = ((Boolean) value).booleanValue();
                if (booleanValue) {
                    return 1;
                }
            }
            return defaultValue;
        }
    }

    public static float getFloatForKey(String key, float defaultValue) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        try {
            return settings.getFloat(key, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Float.parseFloat(value.toString());
            }
            if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            }
            if (value instanceof Boolean) {
                boolean booleanValue = ((Boolean) value).booleanValue();
                if (booleanValue) {
                    return 1.0f;
                }
            }
            return defaultValue;
        }
    }

    public static double getDoubleForKey(String key, double defaultValue) {
        return getFloatForKey(key, (float) defaultValue);
    }

    public static String getStringForKey(String key, String defaultValue) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        try {
            return settings.getString(key, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            return settings.getAll().get(key).toString();
        }
    }

    public static void setBoolForKey(String key, boolean value) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setIntegerForKey(String key, int value) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setFloatForKey(String key, float value) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void setDoubleForKey(String key, double value) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, (float) value);
        editor.apply();
    }

    public static void setStringForKey(String key, String value) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void deleteValueForKey(String key) {
        SharedPreferences settings = sActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
    }

    public static byte[] conversionEncoding(byte[] text, String fromCharset, String newCharset) {
        try {
            String str = new String(text, fromCharset);
            return str.getBytes(newCharset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int setResolutionPercent(int per) {
        try {
            IGameTuningService iGameTuningService = mGameServiceBinder;
            if (iGameTuningService == null) {
                return -1;
            }
            return iGameTuningService.setPreferredResolution(per);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int setFPS(int fps) {
        try {
            IGameTuningService iGameTuningService = mGameServiceBinder;
            if (iGameTuningService == null) {
                return -1;
            }
            return iGameTuningService.setFramePerSecond(fps);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int fastLoading(int sec) {
        try {
            IGameTuningService iGameTuningService = mGameServiceBinder;
            if (iGameTuningService == null) {
                return -1;
            }
            return iGameTuningService.boostUp(sec);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getTemperature() {
        try {
            IGameTuningService iGameTuningService = mGameServiceBinder;
            if (iGameTuningService == null) {
                return -1;
            }
            return iGameTuningService.getAbstractTemperature();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int setLowPowerMode(boolean enable) {
        try {
            IGameTuningService iGameTuningService = mGameServiceBinder;
            if (iGameTuningService == null) {
                return -1;
            }
            return iGameTuningService.setGamePowerSaving(enable);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isScreenRound() {
        if (Build.VERSION.SDK_INT >= 23 && sActivity.getResources().getConfiguration().isScreenRound()) {
            return true;
        }
        return false;
    }

    public static boolean isCutoutEnabled() {
        if (Build.VERSION.SDK_INT < 28) {
            return false;
        }
        WindowManager.LayoutParams lp = sActivity.getWindow().getAttributes();
        return lp.layoutInDisplayCutoutMode == 1;
    }

    public static int[] getSafeInsets() {
        List<Rect> rects;
        int[] safeInsets = {0, 0, 0, 0};
        if (Build.VERSION.SDK_INT >= 28) {
            Window cocosWindow = sActivity.getWindow();
            DisplayCutout displayCutout = cocosWindow.getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null && (rects = displayCutout.getBoundingRects()) != null && rects.size() != 0) {
                safeInsets[0] = displayCutout.getSafeInsetBottom();
                safeInsets[1] = displayCutout.getSafeInsetLeft();
                safeInsets[2] = displayCutout.getSafeInsetRight();
                safeInsets[3] = displayCutout.getSafeInsetTop();
            }
        }
        return safeInsets;
    }

    public static boolean hasSoftKeys() {
        if (Build.VERSION.SDK_INT >= 17) {
            Display display = sActivity.getWindowManager().getDefaultDisplay();
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            display.getRealMetrics(realDisplayMetrics);
            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;
            boolean r2 = true;
            if (realWidth - displayWidth <= 0 && realHeight - displayHeight <= 0) {
                r2 = false;
            }
            boolean hasSoftwareKeys = r2;
            return hasSoftwareKeys;
        }
        boolean hasMenuKey = ViewConfiguration.get(sActivity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(4);
        boolean hasSoftwareKeys2 = (hasMenuKey || hasBackKey) ? false : true;
        return hasSoftwareKeys2;
    }

    public static float[] getAccelValue() {
        return getAccelerometer().accelerometerValues;
    }

    public static float[] getCompassValue() {
        return getAccelerometer().compassFieldValues;
    }

    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    private static Cocos2dxAccelerometer getAccelerometer() {
        if (sCocos2dxAccelerometer == null) {
            sCocos2dxAccelerometer = new Cocos2dxAccelerometer(sActivity);
        }
        return sCocos2dxAccelerometer;
    }

    private static Cocos2dxSound getSound() {
        if (sCocos2dSound == null) {
            sCocos2dSound = new Cocos2dxSound(sActivity);
        }
        return sCocos2dSound;
    }
}
