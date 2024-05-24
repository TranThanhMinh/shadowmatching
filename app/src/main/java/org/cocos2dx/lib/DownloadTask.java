package org.cocos2dx.lib;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

/* compiled from: Cocos2dxDownloader.java */
/* loaded from: classes2.dex */
class DownloadTask {
    long bytesReceived;
    byte[] data;
    RequestHandle handle = null;
    AsyncHttpResponseHandler handler = null;
    long totalBytesExpected;
    long totalBytesReceived;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DownloadTask() {
        resetStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetStatus() {
        this.bytesReceived = 0L;
        this.totalBytesReceived = 0L;
        this.totalBytesExpected = 0L;
        this.data = null;
    }
}
