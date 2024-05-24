package org.cocos2dx.lib;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.net.ssl.SSLException;

/* loaded from: classes2.dex */
public class Cocos2dxDownloader {
    private static HashMap<String, Boolean> _resumingSupport = new HashMap<>();
    private int _countOfMaxProcessingTasks;
    private int _id;
    private String _tempFileNameSufix;
    private AsyncHttpClient _httpClient = new AsyncHttpClient();
    private HashMap _taskMap = new HashMap();
    private Queue<Runnable> _taskQueue = new LinkedList();
    private int _runningTaskCount = 0;

    native void nativeOnFinish(int i, int i2, int i3, String str, byte[] bArr);

    native void nativeOnProgress(int i, int i2, long j, long j2, long j3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onProgress(final int id, final long downloadBytes, final long downloadNow, final long downloadTotal) {
        DownloadTask task = (DownloadTask) this._taskMap.get(Integer.valueOf(id));
        if (task != null) {
            task.bytesReceived = downloadBytes;
            task.totalBytesReceived = downloadNow;
            task.totalBytesExpected = downloadTotal;
        }
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxDownloader.1
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxDownloader cocos2dxDownloader = Cocos2dxDownloader.this;
                cocos2dxDownloader.nativeOnProgress(cocos2dxDownloader._id, id, downloadBytes, downloadNow, downloadTotal);
            }
        });
    }

    public void onStart(int id) {
        DownloadTask task = (DownloadTask) this._taskMap.get(Integer.valueOf(id));
        if (task != null) {
            task.resetStatus();
        }
    }

    public void onFinish(final int id, final int errCode, final String errStr, final byte[] data) {
        DownloadTask task = (DownloadTask) this._taskMap.get(Integer.valueOf(id));
        if (task == null) {
            return;
        }
        this._taskMap.remove(Integer.valueOf(id));
        Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxDownloader.2
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxDownloader cocos2dxDownloader = Cocos2dxDownloader.this;
                cocos2dxDownloader.nativeOnFinish(cocos2dxDownloader._id, id, errCode, errStr, data);
            }
        });
    }

    public static void setResumingSupport(String host, Boolean support) {
        _resumingSupport.put(host, support);
    }

    public static Cocos2dxDownloader createDownloader(int id, int timeoutInSeconds, String tempFileNameSufix, int countOfMaxProcessingTasks) {
        Cocos2dxDownloader downloader = new Cocos2dxDownloader();
        downloader._id = id;
        downloader._httpClient.setEnableRedirects(true);
        if (timeoutInSeconds > 0) {
            downloader._httpClient.setTimeout(timeoutInSeconds * 1000);
        }
        AsyncHttpClient.allowRetryExceptionClass(SSLException.class);
        downloader._httpClient.setURLEncodingEnabled(false);
        downloader._tempFileNameSufix = tempFileNameSufix;
        downloader._countOfMaxProcessingTasks = countOfMaxProcessingTasks;
        return downloader;
    }

    public static void createTask(final Cocos2dxDownloader downloader, final int id_, final String url_, final String path_) {
        Runnable taskRunnable = new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxDownloader.3
            @Override // java.lang.Runnable
            public void run() {
                Boolean supportResuming;
                Boolean requestHeader;
                DownloadTask task = new DownloadTask();
                if (path_.length() == 0) {
                    task.handler = new DataTaskHandler(downloader, id_);
                    task.handle = downloader._httpClient.get(Cocos2dxHelper.getActivity(), url_, task.handler);
                }
                if (path_.length() != 0) {
                    try {
                        URI uri = new URI(url_);
                        String domain = uri.getHost();
                        String host = domain.startsWith("www.") ? domain.substring(4) : domain;
                        if (Cocos2dxDownloader._resumingSupport.containsKey(host)) {
                            Boolean supportResuming2 = (Boolean) Cocos2dxDownloader._resumingSupport.get(host);
                            supportResuming = supportResuming2;
                            requestHeader = false;
                        } else {
                            supportResuming = false;
                            requestHeader = true;
                        }
                        if (!requestHeader.booleanValue()) {
                            File tempFile = new File(path_ + downloader._tempFileNameSufix);
                            if (!tempFile.isDirectory()) {
                                File parent = tempFile.getParentFile();
                                if (parent.isDirectory() || parent.mkdirs()) {
                                    File finalFile = new File(path_);
                                    if (!finalFile.isDirectory()) {
                                        task.handler = new FileTaskHandler(downloader, id_, tempFile, finalFile);
                                        Header[] headers = null;
                                        long fileLen = tempFile.length();
                                        if (supportResuming.booleanValue() && fileLen > 0) {
                                            List<Header> list = new ArrayList<>();
                                            list.add(new BasicHeader("Range", "bytes=" + fileLen + "-"));
                                            headers = (Header[]) list.toArray(new Header[list.size()]);
                                        } else if (fileLen > 0) {
                                            try {
                                                PrintWriter writer = new PrintWriter(tempFile);
                                                writer.print("");
                                                writer.close();
                                            } catch (FileNotFoundException e) {
                                            }
                                        }
                                        task.handle = downloader._httpClient.get(Cocos2dxHelper.getActivity(), url_, headers, (RequestParams) null, task.handler);
                                    }
                                }
                            }
                        } else {
                            task.handler = new HeadTaskHandler(downloader, id_, host, url_, path_);
                            task.handle = downloader._httpClient.head(Cocos2dxHelper.getActivity(), url_, null, null, task.handler);
                        }
                    } catch (URISyntaxException e2) {
                    }
                }
                if (task.handle != null) {
                    downloader._taskMap.put(Integer.valueOf(id_), task);
                    return;
                }
                final String errStr = "Can't create DownloadTask for " + url_;
                Cocos2dxHelper.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxDownloader.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        downloader.nativeOnFinish(downloader._id, id_, 0, errStr, null);
                    }
                });
            }
        };
        downloader.enqueueTask(taskRunnable);
    }

    public static void cancelAllRequests(Cocos2dxDownloader downloader) {
        Cocos2dxHelper.getActivity().runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxDownloader.4
            @Override // java.lang.Runnable
            public void run() {
              /*  for (Map.Entry entry : Cocos2dxDownloader.this._taskMap.entrySet()) {
                    DownloadTask task = (DownloadTask) entry.getValue();
                    if (task.handle != null) {
                        task.handle.cancel(true);
                    }
                }*/
            }
        });
    }

    public void enqueueTask(Runnable taskRunnable) {
        synchronized (this._taskQueue) {
            if (this._runningTaskCount < this._countOfMaxProcessingTasks) {
                Cocos2dxHelper.getActivity().runOnUiThread(taskRunnable);
                this._runningTaskCount++;
            } else {
                this._taskQueue.add(taskRunnable);
            }
        }
    }

    public void runNextTaskIfExists() {
        synchronized (this._taskQueue) {
            Runnable taskRunnable = this._taskQueue.poll();
            if (taskRunnable != null) {
                Cocos2dxHelper.getActivity().runOnUiThread(taskRunnable);
            } else {
                this._runningTaskCount--;
            }
        }
    }
}
