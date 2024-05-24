package org.cocos2dx.lib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.FileInputStream;

/* loaded from: classes2.dex */
public class Cocos2dxMusic {
    private static final String TAG = Cocos2dxMusic.class.getSimpleName();
    private MediaPlayer mBackgroundMediaPlayer;
    private final Context mContext;
    private String mCurrentPath;
    private float mLeftVolume;
    private boolean mPaused;
    private float mRightVolume;
    private boolean mIsLoop = false;
    private boolean mManualPaused = false;
    private boolean mIsAudioFocus = true;

    public Cocos2dxMusic(Context context) {
        this.mContext = context;
        initData();
    }

    public void preloadBackgroundMusic(String path) {
        String str = this.mCurrentPath;
        if (str == null || !str.equals(path)) {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            this.mBackgroundMediaPlayer = createMediaPlayer(path);
            this.mCurrentPath = path;
        }
    }

    public void playBackgroundMusic(String path, boolean isLoop) {
        String str = this.mCurrentPath;
        if (str == null) {
            this.mBackgroundMediaPlayer = createMediaPlayer(path);
            this.mCurrentPath = path;
        } else if (!str.equals(path)) {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            this.mBackgroundMediaPlayer = createMediaPlayer(path);
            this.mCurrentPath = path;
        }
        MediaPlayer mediaPlayer2 = this.mBackgroundMediaPlayer;
        if (mediaPlayer2 == null) {
            Log.e(TAG, "playBackgroundMusic: background media player is null");
            return;
        }
        try {
            if (this.mPaused) {
                mediaPlayer2.seekTo(0);
                this.mBackgroundMediaPlayer.start();
            } else if (mediaPlayer2.isPlaying()) {
                this.mBackgroundMediaPlayer.seekTo(0);
            } else {
                this.mBackgroundMediaPlayer.start();
            }
            this.mBackgroundMediaPlayer.setLooping(isLoop);
            this.mPaused = false;
            this.mIsLoop = isLoop;
        } catch (Exception e) {
            Log.e(TAG, "playBackgroundMusic: error state");
        }
    }

    public void stopBackgroundMusic() {
        MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.mBackgroundMediaPlayer = createMediaPlayer(this.mCurrentPath);
            this.mPaused = false;
        }
    }

    public void pauseBackgroundMusic() {
        try {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                this.mBackgroundMediaPlayer.pause();
                this.mPaused = true;
                this.mManualPaused = true;
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "pauseBackgroundMusic, IllegalStateException was triggered!");
        }
    }

    public void resumeBackgroundMusic() {
        try {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer != null && this.mPaused) {
                mediaPlayer.start();
                this.mPaused = false;
                this.mManualPaused = false;
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "resumeBackgroundMusic, IllegalStateException was triggered!");
        }
    }

    public void rewindBackgroundMusic() {
        if (this.mBackgroundMediaPlayer != null) {
            playBackgroundMusic(this.mCurrentPath, this.mIsLoop);
        }
    }

    public boolean willPlayBackgroundMusic() {
        AudioManager manager = (AudioManager) this.mContext.getSystemService("audio");
        return !manager.isMusicActive();
    }

    public boolean isBackgroundMusicPlaying() {
        try {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer == null) {
                return false;
            }
            boolean ret = mediaPlayer.isPlaying();
            return ret;
        } catch (IllegalStateException e) {
            Log.e(TAG, "isBackgroundMusicPlaying, IllegalStateException was triggered!");
            return false;
        }
    }

    public void end() {
        MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        initData();
    }

    public float getBackgroundVolume() {
        if (this.mBackgroundMediaPlayer != null) {
            return (this.mLeftVolume + this.mRightVolume) / 2.0f;
        }
        return 0.0f;
    }

    public void setBackgroundVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        this.mRightVolume = volume;
        this.mLeftVolume = volume;
        MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
        if (mediaPlayer != null && this.mIsAudioFocus) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void onEnterBackground() {
        try {
            MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                this.mBackgroundMediaPlayer.pause();
                this.mPaused = true;
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "onEnterBackground, IllegalStateException was triggered!");
        }
    }

    public void onEnterForeground() {
        MediaPlayer mediaPlayer;
        try {
            if (!this.mManualPaused && (mediaPlayer = this.mBackgroundMediaPlayer) != null && this.mPaused) {
                mediaPlayer.start();
                this.mPaused = false;
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "onEnterForeground, IllegalStateException was triggered!");
        }
    }

    private void initData() {
        this.mLeftVolume = 0.5f;
        this.mRightVolume = 0.5f;
        this.mBackgroundMediaPlayer = null;
        this.mPaused = false;
        this.mCurrentPath = null;
    }

    private MediaPlayer createMediaPlayer(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            if (path.startsWith("/")) {
                FileInputStream fis = new FileInputStream(path);
                mediaPlayer.setDataSource(fis.getFD());
                fis.close();
            } else if (Cocos2dxHelper.getObbFile() != null) {
                AssetFileDescriptor assetFileDescriptor = Cocos2dxHelper.getObbFile().getAssetFileDescriptor(path);
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            } else {
                AssetFileDescriptor assetFileDescriptor2 = this.mContext.getAssets().openFd(path);
                mediaPlayer.setDataSource(assetFileDescriptor2.getFileDescriptor(), assetFileDescriptor2.getStartOffset(), assetFileDescriptor2.getLength());
            }
            mediaPlayer.prepare();
            mediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
            return mediaPlayer;
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioFocus(boolean isFocus) {
        this.mIsAudioFocus = isFocus;
        MediaPlayer mediaPlayer = this.mBackgroundMediaPlayer;
        if (mediaPlayer != null) {
            float lVolume = isFocus ? this.mLeftVolume : 0.0f;
            float rVolume = isFocus ? this.mRightVolume : 0.0f;
            mediaPlayer.setVolume(lVolume, rVolume);
        }
    }
}
