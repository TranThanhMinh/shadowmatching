package org.cocos2dx.lib;

import android.util.Log;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHeaders;

/* compiled from: Cocos2dxDownloader.java */
/* loaded from: classes2.dex */
class HeadTaskHandler extends AsyncHttpResponseHandler {
    private Cocos2dxDownloader _downloader;
    String _host;
    int _id;
    String _path;
    String _url;

    void LogD(String msg) {
        Log.d("Cocos2dxDownloader", msg);
    }

    public HeadTaskHandler(Cocos2dxDownloader downloader, int id, String host, String url, String path) {
        this._downloader = downloader;
        this._id = id;
        this._host = host;
        this._url = url;
        this._path = path;
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        boolean acceptRanges = false;
        int i = 0;
        while (true) {
            if (i >= headers.length) {
                break;
            }
            Header elem = headers[i];
            if (!elem.getName().equals(HttpHeaders.ACCEPT_RANGES)) {
                i++;
            } else {
                acceptRanges = Boolean.valueOf(elem.getValue().equals("bytes"));
                break;
            }
        }
        Cocos2dxDownloader.setResumingSupport(this._host, acceptRanges);
        Cocos2dxDownloader.createTask(this._downloader, this._id, this._url, this._path);
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onFinish() {
        this._downloader.runNextTaskIfExists();
    }

    @Override // com.loopj.android.http.AsyncHttpResponseHandler
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
        LogD("onFailure(code:" + statusCode + " headers:" + headers + " throwable:" + throwable + " id:" + this._id);
        String errStr = "";
        if (throwable != null) {
            errStr = throwable.toString();
        }
        this._downloader.onFinish(this._id, statusCode, errStr, null);
    }
}
