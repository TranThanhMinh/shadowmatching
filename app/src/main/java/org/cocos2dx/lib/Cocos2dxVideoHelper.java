package org.cocos2dx.lib;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;
import org.cocos2dx.lib.Cocos2dxVideoView;

/* loaded from: classes2.dex */
public class Cocos2dxVideoHelper {
    static final int KeyEventBack = 1000;
    private static final int VideoTaskCreate = 0;
    private static final int VideoTaskFullScreen = 12;
    private static final int VideoTaskKeepRatio = 11;
    private static final int VideoTaskPause = 5;
    private static final int VideoTaskRemove = 1;
    private static final int VideoTaskRestart = 10;
    private static final int VideoTaskResume = 6;
    private static final int VideoTaskSeek = 8;
    private static final int VideoTaskSetLooping = 13;
    private static final int VideoTaskSetRect = 3;
    private static final int VideoTaskSetSource = 2;
    private static final int VideoTaskSetUserInputEnabled = 14;
    private static final int VideoTaskSetVisible = 9;
    private static final int VideoTaskStart = 4;
    private static final int VideoTaskStop = 7;
    static VideoHandler mVideoHandler = null;
    private static int videoTag = 0;
    private Cocos2dxActivity mActivity;
    private FrameLayout mLayout;
    private SparseArray<Cocos2dxVideoView> sVideoViews;
    Cocos2dxVideoView.OnVideoEventListener videoEventListener = new Cocos2dxVideoView.OnVideoEventListener() { // from class: org.cocos2dx.lib.Cocos2dxVideoHelper.1
        @Override // org.cocos2dx.lib.Cocos2dxVideoView.OnVideoEventListener
        public void onVideoEvent(int tag, int event) {
            Cocos2dxVideoHelper.this.mActivity.runOnGLThread(new VideoEventRunnable(tag, event));
        }
    };

    public static native void nativeExecuteVideoCallback(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cocos2dxVideoHelper(Cocos2dxActivity activity, FrameLayout layout) {
        this.mLayout = null;
        this.mActivity = null;
        this.sVideoViews = null;
        this.mActivity = activity;
        this.mLayout = layout;
        mVideoHandler = new VideoHandler(this);
        this.sVideoViews = new SparseArray<>();
    }

    /* loaded from: classes2.dex */
    static class VideoHandler extends Handler {
        WeakReference<Cocos2dxVideoHelper> mReference;

        VideoHandler(Cocos2dxVideoHelper helper) {
            this.mReference = new WeakReference<>(helper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Cocos2dxVideoHelper helper = this.mReference.get();
                    helper._createVideoView(msg.arg1);
                    break;
                case 1:
                    Cocos2dxVideoHelper helper2 = this.mReference.get();
                    helper2._removeVideoView(msg.arg1);
                    break;
                case 2:
                    Cocos2dxVideoHelper helper3 = this.mReference.get();
                    helper3._setVideoURL(msg.arg1, msg.arg2, (String) msg.obj);
                    break;
                case 3:
                    Cocos2dxVideoHelper helper4 = this.mReference.get();
                    Rect rect = (Rect) msg.obj;
                    helper4._setVideoRect(msg.arg1, rect.left, rect.top, rect.right, rect.bottom);
                    break;
                case 4:
                    Cocos2dxVideoHelper helper5 = this.mReference.get();
                    helper5._startVideo(msg.arg1);
                    break;
                case 5:
                    Cocos2dxVideoHelper helper6 = this.mReference.get();
                    helper6._pauseVideo(msg.arg1);
                    break;
                case 6:
                    Cocos2dxVideoHelper helper7 = this.mReference.get();
                    helper7._resumeVideo(msg.arg1);
                    break;
                case 7:
                    Cocos2dxVideoHelper helper8 = this.mReference.get();
                    helper8._stopVideo(msg.arg1);
                    break;
                case 8:
                    Cocos2dxVideoHelper helper9 = this.mReference.get();
                    helper9._seekVideoTo(msg.arg1, msg.arg2);
                    break;
                case 9:
                    Cocos2dxVideoHelper helper10 = this.mReference.get();
                    if (msg.arg2 == 1) {
                        helper10._setVideoVisible(msg.arg1, true);
                        break;
                    } else {
                        helper10._setVideoVisible(msg.arg1, false);
                        break;
                    }
                case 10:
                    Cocos2dxVideoHelper helper11 = this.mReference.get();
                    helper11._restartVideo(msg.arg1);
                    break;
                case 11:
                    Cocos2dxVideoHelper helper12 = this.mReference.get();
                    if (msg.arg2 == 1) {
                        helper12._setVideoKeepRatio(msg.arg1, true);
                        break;
                    } else {
                        helper12._setVideoKeepRatio(msg.arg1, false);
                        break;
                    }
                case 12:
                    Cocos2dxVideoHelper helper13 = this.mReference.get();
                    Rect rect2 = (Rect) msg.obj;
                    if (msg.arg2 == 1) {
                        helper13._setFullScreenEnabled(msg.arg1, true, rect2.right, rect2.bottom);
                        break;
                    } else {
                        helper13._setFullScreenEnabled(msg.arg1, false, rect2.right, rect2.bottom);
                        break;
                    }
                case 13:
                    Cocos2dxVideoHelper helper14 = this.mReference.get();
                    helper14._setLooping(msg.arg1, msg.arg2 != 0);
                    break;
                case 14:
                    Cocos2dxVideoHelper helper15 = this.mReference.get();
                    helper15._setUserInputEnabled(msg.arg1, msg.arg2 != 0);
                    break;
                case 1000:
                    Cocos2dxVideoHelper helper16 = this.mReference.get();
                    helper16.onBackKeyEvent();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class VideoEventRunnable implements Runnable {
        private int mVideoEvent;
        private int mVideoTag;

        public VideoEventRunnable(int tag, int event) {
            this.mVideoTag = tag;
            this.mVideoEvent = event;
        }

        @Override // java.lang.Runnable
        public void run() {
            Cocos2dxVideoHelper.nativeExecuteVideoCallback(this.mVideoTag, this.mVideoEvent);
        }
    }

    public static int createVideoWidget() {
        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = videoTag;
        mVideoHandler.sendMessage(msg);
        int i = videoTag;
        videoTag = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _createVideoView(int index) {
        Cocos2dxVideoView videoView = new Cocos2dxVideoView(this.mActivity, index);
        this.sVideoViews.put(index, videoView);
        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(-2, -2);
        this.mLayout.addView(videoView, lParams);
        videoView.setZOrderOnTop(true);
        videoView.setOnCompletionListener(this.videoEventListener);
    }

    public static void removeVideoWidget(int index) {
        Message msg = new Message();
        msg.what = 1;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _removeVideoView(int index) {
        Cocos2dxVideoView view = this.sVideoViews.get(index);
        if (view != null) {
            view.stopPlayback();
            this.sVideoViews.remove(index);
            this.mLayout.removeView(view);
        }
    }

    public static void setVideoUrl(int index, int videoSource, String videoUrl) {
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = index;
        msg.arg2 = videoSource;
        msg.obj = videoUrl;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setVideoURL(int index, int videoSource, String videoUrl) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            switch (videoSource) {
                case 0:
                    videoView.setVideoFileName(videoUrl);
                    return;
                case 1:
                    videoView.setVideoURL(videoUrl);
                    return;
                default:
                    return;
            }
        }
    }

    public static void setLooping(int i, boolean z) {
        Message message = new Message();
        message.what = 13;
        message.arg1 = i;
        message.arg2 = z ? 1 : 0;
        mVideoHandler.sendMessage(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setLooping(int index, boolean looping) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.setLooping(looping);
        }
    }

    public static void setUserInputEnabled(int i, boolean z) {
        Message message = new Message();
        message.what = 14;
        message.arg1 = i;
        message.arg2 = z ? 1 : 0;
        mVideoHandler.sendMessage(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setUserInputEnabled(int index, boolean enableInput) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.setUserInputEnabled(enableInput);
        }
    }

    public static void setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        Message msg = new Message();
        msg.what = 3;
        msg.arg1 = index;
        msg.obj = new Rect(left, top, maxWidth, maxHeight);
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.setVideoRect(left, top, maxWidth, maxHeight);
        }
    }

    public static void setFullScreenEnabled(int index, boolean enabled, int width, int height) {
        Message msg = new Message();
        msg.what = 12;
        msg.arg1 = index;
        if (enabled) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        msg.obj = new Rect(0, 0, width, height);
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setFullScreenEnabled(int index, boolean enabled, int width, int height) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.setFullScreenEnabled(enabled, width, height);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBackKeyEvent() {
        int viewCount = this.sVideoViews.size();
        for (int i = 0; i < viewCount; i++) {
            int key = this.sVideoViews.keyAt(i);
            Cocos2dxVideoView videoView = this.sVideoViews.get(key);
            if (videoView != null) {
                videoView.setFullScreenEnabled(false, 0, 0);
                this.mActivity.runOnGLThread(new VideoEventRunnable(key, 1000));
            }
        }
    }

    public static void startVideo(int index) {
        Message msg = new Message();
        msg.what = 4;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _startVideo(int index) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.start();
        }
    }

    public static void pauseVideo(int index) {
        Message msg = new Message();
        msg.what = 5;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _pauseVideo(int index) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.pause();
        }
    }

    public static void resumeVideo(int index) {
        Message msg = new Message();
        msg.what = 6;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _resumeVideo(int index) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.resume();
        }
    }

    public static void stopVideo(int index) {
        Message msg = new Message();
        msg.what = 7;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _stopVideo(int index) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.stop();
        }
    }

    public static void restartVideo(int index) {
        Message msg = new Message();
        msg.what = 10;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _restartVideo(int index) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.restart();
        }
    }

    public static void seekVideoTo(int index, int msec) {
        Message msg = new Message();
        msg.what = 8;
        msg.arg1 = index;
        msg.arg2 = msec;
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _seekVideoTo(int index, int msec) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.seekTo(msec);
        }
    }

    public static void setVideoVisible(int index, boolean visible) {
        Message msg = new Message();
        msg.what = 9;
        msg.arg1 = index;
        if (visible) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setVideoVisible(int index, boolean visible) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            if (visible) {
                videoView.fixSize();
                videoView.setVisibility(0);
            } else {
                videoView.setVisibility(4);
            }
        }
    }

    public static void setVideoKeepRatioEnabled(int index, boolean enable) {
        Message msg = new Message();
        msg.what = 11;
        msg.arg1 = index;
        if (enable) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        mVideoHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setVideoKeepRatio(int index, boolean enable) {
        Cocos2dxVideoView videoView = this.sVideoViews.get(index);
        if (videoView != null) {
            videoView.setKeepRatio(enable);
        }
    }
}
