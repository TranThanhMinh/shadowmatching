package org.cocos2dx.lib;

import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.widget.FrameLayout;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* loaded from: classes2.dex */
public class Cocos2dxWebViewHelper {
    private static Cocos2dxActivity sCocos2dxActivity;
    private static Handler sHandler;
    private static FrameLayout sLayout;
    private static SparseArray<Cocos2dxWebView> webViews;
    private static final String TAG = Cocos2dxWebViewHelper.class.getSimpleName();
    private static int viewTag = 0;

    private static native void didFailLoading(int i, String str);

    private static native void didFinishLoading(int i, String str);

    private static native void onJsCallback(int i, String str);

    private static native boolean shouldStartLoading(int i, String str);

    public Cocos2dxWebViewHelper(FrameLayout layout) {
        sLayout = layout;
        sHandler = new Handler(Looper.myLooper());
        sCocos2dxActivity = (Cocos2dxActivity) Cocos2dxActivity.getContext();
        webViews = new SparseArray<>();
    }

    public static boolean _shouldStartLoading(int index, String message) {
        return !shouldStartLoading(index, message);
    }

    public static void _didFinishLoading(int index, String message) {
        didFinishLoading(index, message);
    }

    public static void _didFailLoading(int index, String message) {
        didFailLoading(index, message);
    }

    public static void _onJsCallback(int index, String message) {
        onJsCallback(index, message);
    }

    public static int createWebView() {
        final int index = viewTag;
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.1
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = new Cocos2dxWebView(Cocos2dxWebViewHelper.sCocos2dxActivity, index);
                FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(-2, -2);
                Cocos2dxWebViewHelper.sLayout.addView(webView, lParams);
                Cocos2dxWebViewHelper.webViews.put(index, webView);
            }
        });
        int i = viewTag;
        viewTag = i + 1;
        return i;
    }

    public static void removeWebView(final int index) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.2
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    Cocos2dxWebViewHelper.webViews.remove(index);
                    Cocos2dxWebViewHelper.sLayout.removeView(webView);
                }
            }
        });
    }

    public static void setVisible(final int index, final boolean visible) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.3
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.setVisibility(visible ? 0 : 8);
                }
            }
        });
    }

    public static void setBackgroundTransparent(final int index) {
        if (Build.VERSION.SDK_INT > 10) {
            sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.4
                @Override // java.lang.Runnable
                public void run() {
                    Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                    if (webView != null) {
                        webView.setBackgroundColor(0);
                        try {
                            Method method = webView.getClass().getMethod("setLayerType", Integer.TYPE, Paint.class);
                            method.invoke(webView, 1, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public static void setOpacityWebView(final int index, final float opacity) {
        if (Build.VERSION.SDK_INT > 10) {
            sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.5
                @Override // java.lang.Runnable
                public void run() {
                    Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                    if (webView != null) {
                        try {
                            Method method = webView.getClass().getMethod("setAlpha", Float.TYPE);
                            method.invoke(webView, Float.valueOf(opacity));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public static float getOpacityWebView(final int index) {
        if (Build.VERSION.SDK_INT > 10) {
            FutureTask<Float> futureResult = new FutureTask<>(new Callable<Float>() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.6
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Float call() throws Exception {
                    Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                    Object valueToReturn = null;
                    if (webView != null) {
                        try {
                            Method method = webView.getClass().getMethod("getAlpha", new Class[0]);
                            valueToReturn = method.invoke(webView, new Object[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return (Float) valueToReturn;
                }
            });
            sCocos2dxActivity.runOnUiThread(futureResult);
            try {
                return futureResult.get().floatValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 1.0f;
            }
        }
        return 1.0f;
    }

    public static void setWebViewRect(final int index, final int left, final int top, final int maxWidth, final int maxHeight) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.7
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.setWebViewRect(left, top, maxWidth, maxHeight);
                }
            }
        });
    }

    public static void setJavascriptInterfaceScheme(final int index, final String scheme) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.8
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.setJavascriptInterfaceScheme(scheme);
                }
            }
        });
    }

    public static void loadData(final int index, final String data, final String mimeType, final String encoding, final String baseURL) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.9
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.loadDataWithBaseURL(baseURL, data, mimeType, encoding, null);
                }
            }
        });
    }

    public static void loadHTMLString(final int index, final String data, final String baseUrl) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.10
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.loadDataWithBaseURL(baseUrl, data, null, null, null);
                }
            }
        });
    }

    public static void loadUrl(final int index, final String url, final boolean cleanCachedData) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.11
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.getSettings().setCacheMode(cleanCachedData ? 2 : -1);
                    webView.loadUrl(url);
                }
            }
        });
    }

    public static void loadFile(final int index, final String filePath) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.12
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.loadUrl(filePath);
                }
            }
        });
    }

    public static void stopLoading(final int index) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.13
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.stopLoading();
                }
            }
        });
    }

    public static void reload(final int index) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.14
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.reload();
                }
            }
        });
    }

    public static <T> T callInMainThread(Callable<T> call) throws ExecutionException, InterruptedException {
        FutureTask<T> task = new FutureTask<>(call);
        sHandler.post(task);
        return task.get();
    }

    public static boolean canGoBack(final int index) {
        Callable<Boolean> callable = new Callable<Boolean>() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.15
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                return Boolean.valueOf(webView != null && webView.canGoBack());
            }
        };
        try {
            return ((Boolean) callInMainThread(callable)).booleanValue();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e2) {
            return false;
        }
    }

    public static boolean canGoForward(final int index) {
        Callable<Boolean> callable = new Callable<Boolean>() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.16
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                return Boolean.valueOf(webView != null && webView.canGoForward());
            }
        };
        try {
            return ((Boolean) callInMainThread(callable)).booleanValue();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e2) {
            return false;
        }
    }

    public static void goBack(final int index) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.17
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.goBack();
                }
            }
        });
    }

    public static void goForward(final int index) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.18
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.goForward();
                }
            }
        });
    }

    public static void evaluateJS(final int index, final String js) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.19
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.loadUrl("javascript:" + js);
                }
            }
        });
    }

    public static void setScalesPageToFit(final int index, final boolean scalesPageToFit) {
        sCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxWebViewHelper.20
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxWebView webView = (Cocos2dxWebView) Cocos2dxWebViewHelper.webViews.get(index);
                if (webView != null) {
                    webView.setScalesPageToFit(scalesPageToFit);
                }
            }
        });
    }
}
