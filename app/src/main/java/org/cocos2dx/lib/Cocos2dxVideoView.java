package org.cocos2dx.lib;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import java.io.IOException;
import java.util.Map;

/* loaded from: classes2.dex */
public class Cocos2dxVideoView extends SurfaceView implements MediaController.MediaPlayerControl {
    private static final String AssetResourceRoot = "assets/";
    private static final int EVENT_COMPLETED = 3;
    private static final int EVENT_ERROR = 4;
    private static final int EVENT_PAUSED = 1;
    private static final int EVENT_PLAYING = 0;
    private static final int EVENT_STOPPED = 2;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    private String TAG;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    protected Cocos2dxActivity mCocos2dxActivity;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private int mCurrentBufferPercentage;
    private int mCurrentState;
    private int mDuration;
    private MediaPlayer.OnErrorListener mErrorListener;
    protected boolean mFullScreenEnabled;
    protected int mFullScreenHeight;
    protected int mFullScreenWidth;
    private boolean mIsAssetRouse;
    private boolean mKeepRatio;
    private boolean mLooping;
    private MediaPlayer mMediaPlayer;
    private boolean mNeedResume;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private OnVideoEventListener mOnVideoEventListener;
    MediaPlayer.OnPreparedListener mPreparedListener;
    SurfaceHolder.Callback mSHCallback;
    private int mSeekWhenPrepared;
    protected MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener;
    private SurfaceHolder mSurfaceHolder;
    private int mTargetState;
    private boolean mUserInputEnabled;
    private String mVideoFilePath;
    private int mVideoHeight;
    private Uri mVideoUri;
    private int mVideoWidth;
    protected int mViewHeight;
    protected int mViewLeft;
    private int mViewTag;
    protected int mViewTop;
    protected int mViewWidth;
    protected int mVisibleHeight;
    protected int mVisibleLeft;
    protected int mVisibleTop;
    protected int mVisibleWidth;

    /* loaded from: classes2.dex */
    public interface OnVideoEventListener {
        void onVideoEvent(int i, int i2);
    }

    public Cocos2dxVideoView(Cocos2dxActivity activity, int tag) {
        super(activity);
        this.TAG = "Cocos2dxVideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        this.mCocos2dxActivity = null;
        this.mViewLeft = 0;
        this.mViewTop = 0;
        this.mViewWidth = 0;
        this.mViewHeight = 0;
        this.mVisibleLeft = 0;
        this.mVisibleTop = 0;
        this.mVisibleWidth = 0;
        this.mVisibleHeight = 0;
        this.mFullScreenEnabled = false;
        this.mFullScreenWidth = 0;
        this.mFullScreenHeight = 0;
        this.mViewTag = 0;
        this.mNeedResume = false;
        this.mIsAssetRouse = false;
        this.mLooping = false;
        this.mUserInputEnabled = true;
        this.mVideoFilePath = null;
        this.mKeepRatio = false;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                Cocos2dxVideoView.this.mVideoWidth = mp.getVideoWidth();
                Cocos2dxVideoView.this.mVideoHeight = mp.getVideoHeight();
                if (Cocos2dxVideoView.this.mVideoWidth != 0 && Cocos2dxVideoView.this.mVideoHeight != 0) {
                    Cocos2dxVideoView.this.getHolder().setFixedSize(Cocos2dxVideoView.this.mVideoWidth, Cocos2dxVideoView.this.mVideoHeight);
                }
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                Cocos2dxVideoView.this.mCurrentState = 2;
                if (Cocos2dxVideoView.this.mOnPreparedListener != null) {
                    Cocos2dxVideoView.this.mOnPreparedListener.onPrepared(Cocos2dxVideoView.this.mMediaPlayer);
                }
                Cocos2dxVideoView.this.mVideoWidth = mp.getVideoWidth();
                Cocos2dxVideoView.this.mVideoHeight = mp.getVideoHeight();
                int seekToPosition = Cocos2dxVideoView.this.mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    Cocos2dxVideoView.this.seekTo(seekToPosition);
                }
                if (Cocos2dxVideoView.this.mVideoWidth != 0 && Cocos2dxVideoView.this.mVideoHeight != 0) {
                    Cocos2dxVideoView.this.fixSize();
                }
                if (Cocos2dxVideoView.this.mTargetState == 3) {
                    Cocos2dxVideoView.this.start();
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                Cocos2dxVideoView.this.mCurrentState = 5;
                Cocos2dxVideoView.this.mTargetState = 5;
                if (!Cocos2dxVideoView.this.mLooping) {
                    Cocos2dxVideoView.this.release(true);
                }
                if (Cocos2dxVideoView.this.mOnVideoEventListener != null) {
                    Cocos2dxVideoView.this.mOnVideoEventListener.onVideoEvent(Cocos2dxVideoView.this.mViewTag, 3);
                }
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.4
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                int messageId;
                Log.d(Cocos2dxVideoView.this.TAG, "Error: " + framework_err + "," + impl_err);
                Cocos2dxVideoView.this.mCurrentState = -1;
                Cocos2dxVideoView.this.mTargetState = -1;
                if (Cocos2dxVideoView.this.mOnVideoEventListener != null) {
                    Cocos2dxVideoView.this.mOnVideoEventListener.onVideoEvent(Cocos2dxVideoView.this.mViewTag, 4);
                }
                if ((Cocos2dxVideoView.this.mOnErrorListener == null || !Cocos2dxVideoView.this.mOnErrorListener.onError(Cocos2dxVideoView.this.mMediaPlayer, framework_err, impl_err)) && Cocos2dxVideoView.this.getWindowToken() != null) {
                    Resources r = Cocos2dxVideoView.this.mCocos2dxActivity.getResources();
                    if (framework_err == 200) {
                        messageId = r.getIdentifier("VideoView_error_text_invalid_progressive_playback", "string", "android");
                    } else {
                        messageId = r.getIdentifier("VideoView_error_text_unknown", "string", "android");
                    }
                    int titleId = r.getIdentifier("VideoView_error_title", "string", "android");
                    int buttonStringId = r.getIdentifier("VideoView_error_button", "string", "android");
                    new AlertDialog.Builder(Cocos2dxVideoView.this.mCocos2dxActivity).setTitle(r.getString(titleId)).setMessage(messageId).setPositiveButton(r.getString(buttonStringId), new DialogInterface.OnClickListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.4.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (Cocos2dxVideoView.this.mOnVideoEventListener != null) {
                                Cocos2dxVideoView.this.mOnVideoEventListener.onVideoEvent(Cocos2dxVideoView.this.mViewTag, 3);
                            }
                        }
                    }).setCancelable(false).show();
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.5
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Cocos2dxVideoView.this.mCurrentBufferPercentage = percent;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: org.cocos2dx.lib.Cocos2dxVideoView.6
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                boolean isValidState = Cocos2dxVideoView.this.mTargetState == 3;
                boolean hasValidSize = Cocos2dxVideoView.this.mVideoWidth == w && Cocos2dxVideoView.this.mVideoHeight == h;
                if (Cocos2dxVideoView.this.mMediaPlayer != null && isValidState && hasValidSize) {
                    if (Cocos2dxVideoView.this.mSeekWhenPrepared != 0) {
                        Cocos2dxVideoView cocos2dxVideoView = Cocos2dxVideoView.this;
                        cocos2dxVideoView.seekTo(cocos2dxVideoView.mSeekWhenPrepared);
                    }
                    Cocos2dxVideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                Cocos2dxVideoView.this.mSurfaceHolder = holder;
                Cocos2dxVideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cocos2dxVideoView.this.mSurfaceHolder = null;
                Cocos2dxVideoView.this.release(true);
            }
        };
        this.mViewTag = tag;
        this.mCocos2dxActivity = activity;
        initVideoView();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mVideoWidth == 0 || this.mVideoHeight == 0) {
            setMeasuredDimension(this.mViewWidth, this.mViewHeight);
            Log.i(this.TAG, "" + this.mViewWidth + ":" + this.mViewHeight);
            return;
        }
        setMeasuredDimension(this.mVisibleWidth, this.mVisibleHeight);
        Log.i(this.TAG, "" + this.mVisibleWidth + ":" + this.mVisibleHeight);
    }

    public void setVideoRect(int left, int top, int maxWidth, int maxHeight) {
        this.mViewLeft = left;
        this.mViewTop = top;
        this.mViewWidth = maxWidth;
        this.mViewHeight = maxHeight;
        fixSize(left, top, maxWidth, maxHeight);
    }

    public void setFullScreenEnabled(boolean enabled, int width, int height) {
        if (this.mFullScreenEnabled != enabled) {
            this.mFullScreenEnabled = enabled;
            if (width != 0 && height != 0) {
                this.mFullScreenWidth = width;
                this.mFullScreenHeight = height;
            }
            fixSize();
        }
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                int result = Math.min(desiredSize, specSize);
                return result;
            case 0:
                return desiredSize;
            case 1073741824:
                return specSize;
            default:
                return desiredSize;
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    public void setVisibility(int visibility) {
        if (visibility == 4) {
            boolean isPlaying = isPlaying();
            this.mNeedResume = isPlaying;
            if (isPlaying) {
                this.mSeekWhenPrepared = getCurrentPosition();
            }
        } else if (this.mNeedResume) {
            start();
            this.mNeedResume = false;
        }
        super.setVisibility(visibility);
    }

    private void initVideoView() {
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        getHolder().addCallback(this.mSHCallback);
        getHolder().setType(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mUserInputEnabled && (event.getAction() & 255) == 1) {
            if (isPlaying()) {
                pause();
            } else if (this.mCurrentState == 4) {
                resume();
            }
        }
        return true;
    }

    public void setVideoFileName(String path) {
        if (path.startsWith(AssetResourceRoot)) {
            path = path.substring(AssetResourceRoot.length());
        }
        if (path.startsWith("/")) {
            this.mIsAssetRouse = false;
            setVideoURI(Uri.parse(path), null);
        } else {
            this.mVideoFilePath = path;
            this.mIsAssetRouse = true;
            setVideoURI(Uri.parse(path), null);
        }
    }

    public void setVideoURL(String url) {
        this.mIsAssetRouse = false;
        setVideoURI(Uri.parse(url), null);
    }

    private void setVideoURI(Uri uri, Map<String, String> headers) {
        this.mVideoUri = uri;
        this.mSeekWhenPrepared = 0;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setLooping(boolean looping) {
        this.mLooping = looping;
    }

    public void setUserInputEnabled(boolean enableInput) {
        this.mUserInputEnabled = enableInput;
    }

    public void stopPlayback() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            this.mTargetState = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openVideo() {
        if (this.mSurfaceHolder == null) {
            return;
        }
        if (this.mIsAssetRouse) {
            if (this.mVideoFilePath == null) {
                return;
            }
        } else if (this.mVideoUri == null) {
            return;
        }
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        this.mCocos2dxActivity.sendBroadcast(i);
        release(false);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mMediaPlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(this.mPreparedListener);
            this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
            this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
            this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
            this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
            this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
            this.mMediaPlayer.setAudioStreamType(3);
            this.mMediaPlayer.setScreenOnWhilePlaying(true);
            this.mMediaPlayer.setLooping(this.mLooping);
            this.mDuration = -1;
            this.mCurrentBufferPercentage = 0;
            if (this.mIsAssetRouse) {
                AssetFileDescriptor afd = this.mCocos2dxActivity.getAssets().openFd(this.mVideoFilePath);
                if (afd == null && Cocos2dxHelper.getObbFile() != null) {
                    afd = Cocos2dxHelper.getObbFile().getAssetFileDescriptor(this.mVideoFilePath);
                }
                this.mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } else {
                this.mMediaPlayer.setDataSource(this.mCocos2dxActivity, this.mVideoUri);
            }
            this.mMediaPlayer.prepareAsync();
            this.mCurrentState = 1;
        } catch (IOException ex) {
            Log.w(this.TAG, "Unable to open content: " + this.mVideoUri, ex);
            this.mCurrentState = -1;
            this.mTargetState = -1;
            this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
        } catch (IllegalArgumentException ex2) {
            Log.w(this.TAG, "Unable to open content: " + this.mVideoUri, ex2);
            this.mCurrentState = -1;
            this.mTargetState = -1;
            this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
        }
    }

    public void setKeepRatio(boolean enabled) {
        this.mKeepRatio = enabled;
        fixSize();
    }

    public void fixSize() {
        if (this.mFullScreenEnabled) {
            fixSize(0, 0, this.mFullScreenWidth, this.mFullScreenHeight);
        } else {
            fixSize(this.mViewLeft, this.mViewTop, this.mViewWidth, this.mViewHeight);
        }
    }

    public void fixSize(int left, int top, int width, int height) {
        int i;
        int i2 = this.mVideoWidth;
        if (i2 == 0 || (i = this.mVideoHeight) == 0) {
            this.mVisibleLeft = left;
            this.mVisibleTop = top;
            this.mVisibleWidth = width;
            this.mVisibleHeight = height;
        } else if (width != 0 && height != 0) {
            if (this.mKeepRatio) {
                if (i2 * height > width * i) {
                    this.mVisibleWidth = width;
                    this.mVisibleHeight = (i * width) / i2;
                } else if (i2 * height < width * i) {
                    this.mVisibleWidth = (i2 * height) / i;
                    this.mVisibleHeight = height;
                }
                this.mVisibleLeft = ((width - this.mVisibleWidth) / 2) + left;
                this.mVisibleTop = ((height - this.mVisibleHeight) / 2) + top;
            } else {
                this.mVisibleLeft = left;
                this.mVisibleTop = top;
                this.mVisibleWidth = width;
                this.mVisibleHeight = height;
            }
        } else {
            this.mVisibleLeft = left;
            this.mVisibleTop = top;
            this.mVisibleWidth = i2;
            this.mVisibleHeight = i;
        }
        getHolder().setFixedSize(this.mVisibleWidth, this.mVisibleHeight);
        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(-2, -2);
        lParams.leftMargin = this.mVisibleLeft;
        lParams.topMargin = this.mVisibleTop;
        lParams.gravity = 51;
        setLayoutParams(lParams);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnVideoEventListener l) {
        this.mOnVideoEventListener = l;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release(boolean cleartargetstate) {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (cleartargetstate) {
                this.mTargetState = 0;
            }
        }
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
            OnVideoEventListener onVideoEventListener = this.mOnVideoEventListener;
            if (onVideoEventListener != null) {
                onVideoEventListener.onVideoEvent(this.mViewTag, 0);
            }
        }
        this.mTargetState = 3;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
            OnVideoEventListener onVideoEventListener = this.mOnVideoEventListener;
            if (onVideoEventListener != null) {
                onVideoEventListener.onVideoEvent(this.mViewTag, 1);
            }
        }
        this.mTargetState = 4;
    }

    public void stop() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            stopPlayback();
            OnVideoEventListener onVideoEventListener = this.mOnVideoEventListener;
            if (onVideoEventListener != null) {
                onVideoEventListener.onVideoEvent(this.mViewTag, 2);
            }
        }
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        if (isInPlaybackState() && this.mCurrentState == 4) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
            OnVideoEventListener onVideoEventListener = this.mOnVideoEventListener;
            if (onVideoEventListener != null) {
                onVideoEventListener.onVideoEvent(this.mViewTag, 0);
            }
        }
    }

    public void restart() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(0);
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
            this.mTargetState = 3;
        }
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getDuration() {
        if (isInPlaybackState()) {
            int i = this.mDuration;
            if (i > 0) {
                return i;
            }
            int duration = this.mMediaPlayer.getDuration();
            this.mDuration = duration;
            return duration;
        }
        this.mDuration = -1;
        return -1;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(msec);
            this.mSeekWhenPrepared = 0;
        } else {
            this.mSeekWhenPrepared = msec;
        }
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    public boolean isInPlaybackState() {
        int i;
        return (this.mMediaPlayer == null || (i = this.mCurrentState) == -1 || i == 0 || i == 1) ? false : true;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canPause() {
        return true;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekBackward() {
        return true;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekForward() {
        return true;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getAudioSessionId() {
        return this.mMediaPlayer.getAudioSessionId();
    }
}
