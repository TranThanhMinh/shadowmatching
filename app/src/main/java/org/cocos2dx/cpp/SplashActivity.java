package org.cocos2dx.cpp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.yourcompany.shadowmatching.R;

/* loaded from: classes2.dex */
public class SplashActivity extends Activity {
    public InterstitialAd mMobInterstitialAds;
    LinearLayout adContainerView;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    public final void ShowFunUAds() {
        InterstitialAd interstitialAd = this.mMobInterstitialAds;
        if (interstitialAd != null) {
            interstitialAd.show(this);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        refreshItem();
        MyApplication.mActivity = this;
    }

    public void refreshItem() {
        if (isNetworkConnected()) {
            new Handler().postDelayed(new Runnable() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    SplashActivity.this.OpenAppAds();
                }
            }, 6000L);
            return;
        }
    }

    public void OpenAppAds() {
        try {
        //    if (!ConnectionClass.premium) {
                String str = getString(R.string.AdMob_OpenApp);
                this.loadCallback = new AppOpenAd.AppOpenAdLoadCallback() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.5
                    @Override // com.google.android.gms.ads.AdLoadCallback
                    public void onAdLoaded(AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.5.1
                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                goStart();
                                Log.d("Splash",adError.toString());
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                Log.d("Splash","onAdShowedFullScreenContent");
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                goStart();
                                Log.d("Splash","onAdDismissedFullScreenContent");
                            }
                        });
                        appOpenAd.show(SplashActivity.this);
                    }

                    @Override // com.google.android.gms.ads.AdLoadCallback
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        goStart();
                    }
                };
                AppOpenAd.load(this, str, new AdRequest.Builder().build(), 1, this.loadCallback);
//            } else {
//                goStart();
//            }
        } catch (Exception e) {
            Log.d("Splash","Exception "+e.toString());
            e.printStackTrace();
        }
    }

    public final boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void goStart(){
        SplashActivity.this.startActivity(new Intent(SplashActivity.this,  AppActivity.class));
        SplashActivity.this.ShowFunUAds();
        SplashActivity.this.finish();
    }
}
