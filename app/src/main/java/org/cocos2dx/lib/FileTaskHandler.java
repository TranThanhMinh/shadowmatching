package org.cocos2dx.lib;

import android.util.Log;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import java.io.File;

/* compiled from: Cocos2dxDownloader.java */
/* loaded from: classes2.dex */
class FileTaskHandler extends FileAsyncHttpResponseHandler {
    private Cocos2dxDownloader _downloader;
    File _finalFile;
    int _id;
    private long _initFileLen;
    private long _lastBytesWritten;

    void LogD(String msg) {
        Log.d("Cocos2dxDownloader", msg);
    }

    public FileTaskHandler(Cocos2dxDownloader downloader, int id, File temp, File finalFile) {
        super(temp, true);
        this._finalFile = finalFile;
        this._downloader = downloader;
        this._id = id;
        this._initFileLen = getTargetFile().length();
        this._lastBytesWritten = 0L;
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onProgress(long bytesWritten, long totalSize) {
        long dlBytes = bytesWritten - this._lastBytesWritten;
        long j = this._initFileLen;
        long dlNow = bytesWritten + j;
        long dlTotal = totalSize + j;
        this._downloader.onProgress(this._id, dlBytes, dlNow, dlTotal);
        this._lastBytesWritten = bytesWritten;
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onStart() {
        this._downloader.onStart(this._id);
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onFinish() {
        this._downloader.runNextTaskIfExists();
    }

    @Override // com.loopj.android.http.FileAsyncHttpResponseHandler
    public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
        LogD("onFailure(i:" + i + " headers:" + headers + " throwable:" + throwable + " file:" + file);
        String errStr = "";
        if (throwable != null) {
            errStr = throwable.toString();
        }
        this._downloader.onFinish(this._id, i, errStr, null);
    }

    @Override // com.loopj.android.http.FileAsyncHttpResponseHandler
    public void onSuccess(int i, Header[] headers, File file) {
        LogD("onSuccess(i:" + i + " headers:" + headers + " file:" + file);
        String errStr = null;
        if (this._finalFile.exists()) {
            if (this._finalFile.isDirectory()) {
                errStr = "Dest file is directory:" + this._finalFile.getAbsolutePath();
            } else if (!this._finalFile.delete()) {
                errStr = "Can't remove old file:" + this._finalFile.getAbsolutePath();
            }
            this._downloader.onFinish(this._id, 0, errStr, null);
        }
        File tempFile = getTargetFile();
        tempFile.renameTo(this._finalFile);
        this._downloader.onFinish(this._id, 0, errStr, null);
    }
}
