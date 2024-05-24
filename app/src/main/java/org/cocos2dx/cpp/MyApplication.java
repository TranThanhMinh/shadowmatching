package org.cocos2dx.cpp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.yourcompany.shadowmatching.R;

import java.util.Date;

/* loaded from: classes2.dex */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    public static Context context;
    public static int screenWidth;
    public Application application;
    private AppOpenAdManager appOpenAdManager;
    public  Activity currentActivity;
    public static Activity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        screenWidth = getScreenWidth(this);
        context = getApplicationContext();
        MultiDex.install(this);
        this.application = this;
        registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, initializationStatus -> {
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        this.appOpenAdManager = new AppOpenAdManager();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver();
        registerReceiver(broadcastReceiver, broadcastReceiver.getDataFilter());
    }

    public static int getScreenWidth(Context context2) {
        return ((WindowManager) context2.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (this.appOpenAdManager.isShowingAd) {
            return;
        }
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // this.appOpenAdManager.showAdIfAvailable(this);
        Log.e("ON_START","ON_START");
        //if(!ConnectionClass.premium) {
            this.appOpenAdManager.showAdIfAvailable(this.currentActivity);
       // }
    }



    public class AppOpenAdManager {
        private static final String LOG_TAG = "AppOpenAdManager";
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;
        private long loadTime = 0;
        public AppOpenAdManager() {
        }

        public void loadAd(Context context) {

            this.isLoadingAd = true;
            AppOpenAd.load(context, getString(R.string.AdMob_OpenApp), new AdRequest.Builder().build(), 1, new AppOpenAd.AppOpenAdLoadCallback() { // from class: com.demo.rizzyfydating.ads.MyApplication.AppOpenAdManager.1
                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdLoaded(AppOpenAd appOpenAd) {
                    AppOpenAdManager.this.appOpenAd = appOpenAd;
                    AppOpenAdManager.this.isLoadingAd = false;
                    AppOpenAdManager.this.loadTime = new Date().getTime();
                    Log.d(AppOpenAdManager.LOG_TAG, "onAdLoaded.");
                }

                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    AppOpenAdManager.this.isLoadingAd = false;
                    Log.d(AppOpenAdManager.LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                }
            });
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long j) {
            return new Date().getTime() - this.loadTime < j * 3600000;
        }

        private boolean isAdAvailable() {
            return this.appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4L);
        }


        public void showAdIfAvailable(Activity activity) {
            showAdIfAvailable(activity, new OnShowAdCompleteListener() { // from class: com.demo.rizzyfydating.ads.MyApplication.AppOpenAdManager.2
                @Override // com.demo.rizzyfydating.ads.MyApplication.OnShowAdCompleteListener
                public void onShowAdComplete() {
                }
            });
        }

        public void showAdIfAvailable(final Activity activity, final OnShowAdCompleteListener onShowAdCompleteListener) {
            if (this.isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
            } else {
                Log.d(LOG_TAG, "Will show ad.");
                this.appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: com.demo.rizzyfydating.ads.MyApplication.AppOpenAdManager.3
                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdDismissedFullScreenContent() {
                        AppOpenAdManager.this.appOpenAd = null;
                        AppOpenAdManager.this.isShowingAd = false;
                        Log.d(AppOpenAdManager.LOG_TAG, "onAdDismissedFullScreenContent.");
                        onShowAdCompleteListener.onShowAdComplete();
                        AppOpenAdManager.this.loadAd(activity);
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AppOpenAdManager.this.appOpenAd = null;
                        AppOpenAdManager.this.isShowingAd = false;
                        Log.d(AppOpenAdManager.LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        onShowAdCompleteListener.onShowAdComplete();
                        AppOpenAdManager.this.loadAd(activity);
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdShowedFullScreenContent() {
                        Log.d(AppOpenAdManager.LOG_TAG, "onAdShowedFullScreenContent.");
                    }
                });
                this.isShowingAd = true;
                this.appOpenAd.show(activity);
            }
        }
    }

}
