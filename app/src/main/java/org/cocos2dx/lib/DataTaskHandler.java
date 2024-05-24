package org.cocos2dx.lib;

import android.util.Log;
import com.loopj.android.http.BinaryHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

/* compiled from: Cocos2dxDownloader.java */
/* loaded from: classes2.dex */
class DataTaskHandler extends BinaryHttpResponseHandler {
    private Cocos2dxDownloader _downloader;
    int _id;
    private long _lastBytesWritten;

    void LogD(String msg) {
        Log.d("Cocos2dxDownloader", msg);
    }

    public DataTaskHandler(Cocos2dxDownloader downloader, int id) {
        super(new String[]{".*"});
        this._downloader = downloader;
        this._id = id;
        this._lastBytesWritten = 0L;
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onProgress(long bytesWritten, long totalSize) {
        long dlBytes = bytesWritten - this._lastBytesWritten;
        this._downloader.onProgress(this._id, dlBytes, bytesWritten, totalSize);
        this._lastBytesWritten = bytesWritten;
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onStart() {
        this._downloader.onStart(this._id);
    }

    @Override // com.loopj.android.http.BinaryHttpResponseHandler, com.loopj.android.http.AsyncHttpResponseHandler
    public void onFailure(int i, Header[] headers, byte[] errorResponse, Throwable throwable) {
        LogD("onFailure(i:" + i + " headers:" + headers + " throwable:" + throwable);
        String errStr = "";
        if (throwable != null) {
            errStr = throwable.toString();
        }
        this._downloader.onFinish(this._id, i, errStr, null);
    }

    @Override // com.loopj.android.http.BinaryHttpResponseHandler, com.loopj.android.http.AsyncHttpResponseHandler
    public void onSuccess(int i, Header[] headers, byte[] binaryData) {
        LogD("onSuccess(i:" + i + " headers:" + headers);
        this._downloader.onFinish(this._id, 0, null, binaryData);
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onFinish() {
        this._downloader.runNextTaskIfExists();
    }
}
