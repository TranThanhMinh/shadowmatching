package org.cocos2dx.lib;

import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes2.dex */
public class Cocos2dxWebView extends WebView {
    private static final String TAG = Cocos2dxWebViewHelper.class.getSimpleName();
    private String mJSScheme;
    private int mViewTag;

    public Cocos2dxWebView(Context context) {
        this(context, -1);
    }

    public Cocos2dxWebView(Context context, int viewTag) {
        super(context);
        this.mViewTag = viewTag;
        this.mJSScheme = "";
        setFocusable(true);
        setFocusableInTouchMode(true);
        getSettings().setSupportZoom(false);
        getSettings().setDomStorageEnabled(true);
        getSettings().setJavaScriptEnabled(true);
        try {
            Method method = getClass().getMethod("removeJavascriptInterface", String.class);
            method.invoke(this, "searchBoxJavaBridge_");
        } catch (Exception e) {
            Log.d(TAG, "This API level do not support `removeJavascriptInterface`");
        }
        setWebViewClient(new Cocos2dxWebViewClient());
        setWebChromeClient(new WebChromeClient());
    }

    public void setJavascriptInterfaceScheme(String scheme) {
        this.mJSScheme = scheme != null ? scheme : "";
    }

    public void setScalesPageToFit(boolean scalesPageToFit) {
        getSettings().setSupportZoom(scalesPageToFit);
    }

    /* loaded from: classes2.dex */
    class Cocos2dxWebViewClient extends WebViewClient {
        Cocos2dxWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, final String urlString) {
            Cocos2dxActivity activity = (Cocos2dxActivity) Cocos2dxWebView.this.getContext();
            try {
                URI uri = URI.create(urlString);
                if (uri != null && uri.getScheme().equals(Cocos2dxWebView.this.mJSScheme)) {
                    activity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebView.Cocos2dxWebViewClient.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Cocos2dxWebViewHelper._onJsCallback(Cocos2dxWebView.this.mViewTag, urlString);
                        }
                    });
                    return true;
                }
            } catch (Exception e) {
                Log.d(Cocos2dxWebView.TAG, "Failed to create URI from url");
            }
            boolean[] result = {true};
            CountDownLatch latch = new CountDownLatch(1);
            activity.runOnGLThread(new ShouldStartLoadingWorker(latch, result, Cocos2dxWebView.this.mViewTag, urlString));
            try {
                latch.await();
            } catch (InterruptedException e2) {
                Log.d(Cocos2dxWebView.TAG, "'shouldOverrideUrlLoading' failed");
            }
            return result[0];
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, final String url) {
            super.onPageFinished(view, url);
            Cocos2dxActivity activity = (Cocos2dxActivity) Cocos2dxWebView.this.getContext();
            activity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebView.Cocos2dxWebViewClient.2
                @Override // java.lang.Runnable
                public void run() {
                    Cocos2dxWebViewHelper._didFinishLoading(Cocos2dxWebView.this.mViewTag, url);
                }
            });
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Cocos2dxActivity activity = (Cocos2dxActivity) Cocos2dxWebView.this.getContext();
            activity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebView.Cocos2dxWebViewClient.3
                @Override // java.lang.Runnable
                public void run() {
                    Cocos2dxWebViewHelper._didFailLoading(Cocos2dxWebView.this.mViewTag, failingUrl);
                }
            });
        }
    }

    public void setWebViewRect(int left, int top, int maxWidth, int maxHeight) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        layoutParams.width = maxWidth;
        layoutParams.height = maxHeight;
        layoutParams.gravity = 51;
        setLayoutParams(layoutParams);
    }
}
