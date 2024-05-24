package org.cocos2dx.lib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.SoundPool;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public class Cocos2dxSound {
    private static final int INVALID_SOUND_ID = -1;
    private static final int INVALID_STREAM_ID = -1;
    private static final int LOAD_TIME_OUT = 500;
    private static final int MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5;
    private static final int MAX_SIMULTANEOUS_STREAMS_I9100 = 3;
    private static final int SOUND_PRIORITY = 1;
    private static final int SOUND_QUALITY = 5;
    private static final float SOUND_RATE = 1.0f;
    private static final String TAG = "Cocos2dxSound";
    private final Context mContext;
    private float mLeftVolume;
    private float mRightVolume;
    private SoundPool mSoundPool;
    private boolean mIsAudioFocus = true;
    private final HashMap<String, ArrayList<Integer>> mPathStreamIDsMap = new HashMap<>();
    private final Object mLockPathStreamIDsMap = new Object();
    private final HashMap<String, Integer> mPathSoundIDMap = new HashMap<>();
    private ConcurrentHashMap<Integer, SoundInfoForLoadedCompleted> mPlayWhenLoadedEffects = new ConcurrentHashMap<>();

    public Cocos2dxSound(Context context) {
        this.mContext = context;
        initData();
    }

    private void initData() {
        if (Cocos2dxHelper.getDeviceModel().contains("GT-I9100")) {
            this.mSoundPool = new SoundPool(3, 3, 5);
        } else {
            this.mSoundPool = new SoundPool(5, 3, 5);
        }
        this.mSoundPool.setOnLoadCompleteListener(new OnLoadCompletedListener());
        this.mLeftVolume = 0.5f;
        this.mRightVolume = 0.5f;
    }

    public int preloadEffect(String path) {
        Integer soundID = this.mPathSoundIDMap.get(path);
        if (soundID == null) {
            soundID = Integer.valueOf(createSoundIDFromAsset(path));
            if (soundID.intValue() != -1) {
                this.mPathSoundIDMap.put(path, soundID);
            }
        }
        return soundID.intValue();
    }

    public void unloadEffect(String path) {
        synchronized (this.mLockPathStreamIDsMap) {
            ArrayList<Integer> streamIDs = this.mPathStreamIDsMap.get(path);
            if (streamIDs != null) {
                Iterator<Integer> it = streamIDs.iterator();
                while (it.hasNext()) {
                    Integer steamID = it.next();
                    this.mSoundPool.stop(steamID.intValue());
                }
            }
            this.mPathStreamIDsMap.remove(path);
        }
        Integer soundID = this.mPathSoundIDMap.get(path);
        if (soundID != null) {
            this.mSoundPool.unload(soundID.intValue());
            this.mPathSoundIDMap.remove(path);
        }
    }

    public int playEffect(String path, boolean loop, float pitch, float pan, float gain) {
        Integer soundID = this.mPathSoundIDMap.get(path);
        if (soundID != null) {
            int streamID = doPlayEffect(path, soundID.intValue(), loop, pitch, pan, gain);
            return streamID;
        }
        int streamID2 = preloadEffect(path);
        Integer soundID2 = Integer.valueOf(streamID2);
        if (soundID2.intValue() == -1) {
            return -1;
        }
        SoundInfoForLoadedCompleted info = new SoundInfoForLoadedCompleted(path, loop, pitch, pan, gain);
        this.mPlayWhenLoadedEffects.putIfAbsent(soundID2, info);
        synchronized (info) {
            try {
                info.wait(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int streamID3 = info.effectID;
        this.mPlayWhenLoadedEffects.remove(soundID2);
        return streamID3;
    }

    public void stopEffect(int steamID) {
        this.mSoundPool.stop(steamID);
        synchronized (this.mLockPathStreamIDsMap) {
            Iterator<String> it = this.mPathStreamIDsMap.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String pPath = it.next();
                if (this.mPathStreamIDsMap.get(pPath).contains(Integer.valueOf(steamID))) {
                    this.mPathStreamIDsMap.get(pPath).remove(this.mPathStreamIDsMap.get(pPath).indexOf(Integer.valueOf(steamID)));
                    break;
                }
            }
        }
    }

    public void pauseEffect(int steamID) {
        this.mSoundPool.pause(steamID);
    }

    public void resumeEffect(int steamID) {
        this.mSoundPool.resume(steamID);
    }

    public void pauseAllEffects() {
        synchronized (this.mLockPathStreamIDsMap) {
            if (!this.mPathStreamIDsMap.isEmpty()) {
                for (Map.Entry<String, ArrayList<Integer>> entry : this.mPathStreamIDsMap.entrySet()) {
                    Iterator<Integer> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        int steamID = it.next().intValue();
                        this.mSoundPool.pause(steamID);
                    }
                }
            }
        }
    }

    public void resumeAllEffects() {
        synchronized (this.mLockPathStreamIDsMap) {
            if (!this.mPathStreamIDsMap.isEmpty()) {
                for (Map.Entry<String, ArrayList<Integer>> entry : this.mPathStreamIDsMap.entrySet()) {
                    Iterator<Integer> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        int steamID = it.next().intValue();
                        this.mSoundPool.resume(steamID);
                    }
                }
            }
        }
    }

    public void stopAllEffects() {
        synchronized (this.mLockPathStreamIDsMap) {
            if (!this.mPathStreamIDsMap.isEmpty()) {
                for (Map.Entry<String, ArrayList<Integer>> entry : this.mPathStreamIDsMap.entrySet()) {
                    Iterator<Integer> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        int steamID = it.next().intValue();
                        this.mSoundPool.stop(steamID);
                    }
                }
            }
            this.mPathStreamIDsMap.clear();
        }
    }

    public float getEffectsVolume() {
        return (this.mLeftVolume + this.mRightVolume) / 2.0f;
    }

    public void setEffectsVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        if (volume > SOUND_RATE) {
            volume = SOUND_RATE;
        }
        this.mRightVolume = volume;
        this.mLeftVolume = volume;
        if (!this.mIsAudioFocus) {
            return;
        }
        setEffectsVolumeInternal(volume, volume);
    }

    private void setEffectsVolumeInternal(float left, float right) {
        synchronized (this.mLockPathStreamIDsMap) {
            if (!this.mPathStreamIDsMap.isEmpty()) {
                for (Map.Entry<String, ArrayList<Integer>> entry : this.mPathStreamIDsMap.entrySet()) {
                    Iterator<Integer> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        int steamID = it.next().intValue();
                        this.mSoundPool.setVolume(steamID, left, right);
                    }
                }
            }
        }
    }

    public void end() {
        this.mSoundPool.release();
        synchronized (this.mLockPathStreamIDsMap) {
            this.mPathStreamIDsMap.clear();
        }
        this.mPathSoundIDMap.clear();
        this.mPlayWhenLoadedEffects.clear();
        this.mLeftVolume = 0.5f;
        this.mRightVolume = 0.5f;
        initData();
    }

    private int createSoundIDFromAsset(String path) {
        int soundID;
        try {
            if (path.startsWith("/")) {
                soundID = this.mSoundPool.load(path, 0);
            } else if (Cocos2dxHelper.getObbFile() != null) {
                AssetFileDescriptor assetFileDescriptor = Cocos2dxHelper.getObbFile().getAssetFileDescriptor(path);
                soundID = this.mSoundPool.load(assetFileDescriptor, 0);
            } else {
                soundID = this.mSoundPool.load(this.mContext.getAssets().openFd(path), 0);
            }
        } catch (Exception e) {
            soundID = -1;
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
        if (soundID == 0) {
            return -1;
        }
        return soundID;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized int doPlayEffect(String path, int soundId, boolean loop, float pitch, float pan, float gain) {
        int streamID;
        float leftVolume = (SOUND_RATE - clamp(pan, 0.0f, SOUND_RATE)) * this.mLeftVolume * gain;
        float rightVolume = (SOUND_RATE - clamp(-pan, 0.0f, SOUND_RATE)) * this.mRightVolume * gain;
        float soundRate = clamp(pitch * SOUND_RATE, 0.5f, 2.0f);
        streamID = this.mSoundPool.play(soundId, clamp(leftVolume, 0.0f, SOUND_RATE), clamp(rightVolume, 0.0f, SOUND_RATE), 1, loop ? -1 : 0, soundRate);
        synchronized (this.mLockPathStreamIDsMap) {
            try {
                ArrayList<Integer> streamIDs = this.mPathStreamIDsMap.get(path);
                if (streamIDs == null) {
                    streamIDs = new ArrayList<>();
                    this.mPathStreamIDsMap.put(path, streamIDs);
                }
                streamIDs.add(Integer.valueOf(streamID));
            } finally {
           //     th = th;
                while (true) {
                    try {
                        break;
                    } catch (Throwable th) {
                   //     th = th;
                    }
                }
            }
        }
        return streamID;
    }

    public void onEnterBackground() {
        this.mSoundPool.autoPause();
    }

    public void onEnterForeground() {
        this.mSoundPool.autoResume();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioFocus(boolean isFocus) {
        this.mIsAudioFocus = isFocus;
        float leftVolume = isFocus ? this.mLeftVolume : 0.0f;
        float rightVolume = isFocus ? this.mRightVolume : 0.0f;
        setEffectsVolumeInternal(leftVolume, rightVolume);
    }

    /* loaded from: classes2.dex */
    private class SoundInfoForLoadedCompleted {
        int effectID = -1;
        float gain;
        boolean isLoop;
        float pan;
        String path;
        float pitch;

        SoundInfoForLoadedCompleted(String path, boolean isLoop, float pitch, float pan, float gain) {
            this.path = path;
            this.isLoop = isLoop;
            this.pitch = pitch;
            this.pan = pan;
            this.gain = gain;
        }
    }

    /* loaded from: classes2.dex */
    public class OnLoadCompletedListener implements SoundPool.OnLoadCompleteListener {
        public OnLoadCompletedListener() {
        }

        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            SoundInfoForLoadedCompleted info;
            if (status == 0 && (info = (SoundInfoForLoadedCompleted) Cocos2dxSound.this.mPlayWhenLoadedEffects.get(Integer.valueOf(sampleId))) != null) {
                info.effectID = Cocos2dxSound.this.doPlayEffect(info.path, sampleId, info.isLoop, info.pitch, info.pan, info.gain);
                synchronized (info) {
                    info.notifyAll();
                }
            }
        }
    }
}
