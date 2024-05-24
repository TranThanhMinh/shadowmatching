package org.cocos2dx.cpp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.yourcompany.shadowmatching.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxRenderer;

/* loaded from: classes2.dex */
public class AppActivity extends Cocos2dxActivity {
    private static final boolean IS_TESTING = false;
    static final int RC_REQUEST = 10001;
    public static final int WRITE_STORAGE_REQUEST_ID = 97483;
    private static AppActivity _this;
    private static AdView bannerAdView;
    private static ConsentForm consentForm;
    private static ConsentInformation consentInformation;
    private static InterstitialAd mInterstitialAd;
    public static Cocos2dxRenderer renderer;
    private static RelativeLayout rl;
    String PhotoName = "ss.png";
    public static boolean admobfullpageavailable = false;
    private static boolean isInAppRunning = false;
    public static int POS = 0;

    public static native void scaleView(float f);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        _this = this;
        AdView adView = new AdView(_this);
        bannerAdView = adView;
        adView.setAdSize(getBannerAdSize());
        bannerAdView.setAdUnitId(_this.getString(R.string.admob_banner_id));
        checkConsentStatus();
    }

    public static boolean checkSUBSCRIBED() {
        return false;
    }

    private static void checkConsentStatus() {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(true).build();
        ConsentInformation consentInformation2 = UserMessagingPlatform.getConsentInformation(_this);
        consentInformation = consentInformation2;
        consentInformation2.requestConsentInfoUpdate(_this, params, new ConsentInformation.OnConsentInfoUpdateSuccessListener() { // from class: org.cocos2dx.cpp.AppActivity.1
            @Override // com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener
            public void onConsentInfoUpdateSuccess() {
                if (AppActivity.consentInformation.isConsentFormAvailable()) {
                    Log.w("PTag", "Ram 1");
                    AppActivity.loadForm();
                } else {
                    Log.w("PTag", "Ram 2");
                    AppActivity.initializeAds();
                }
            }
        }, new ConsentInformation.OnConsentInfoUpdateFailureListener() { // from class: org.cocos2dx.cpp.AppActivity.2
            @Override // com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener
            public void onConsentInfoUpdateFailure(FormError formError) {
                Log.w("PTag", "Ram 3");
                AppActivity.loadForm();
            }
        });
    }

    public static void loadForm() {
        UserMessagingPlatform.loadConsentForm(_this, new UserMessagingPlatform.OnConsentFormLoadSuccessListener() { // from class: org.cocos2dx.cpp.AppActivity.3
            @Override // com.google.android.ump.UserMessagingPlatform.OnConsentFormLoadSuccessListener
            public void onConsentFormLoadSuccess(ConsentForm consentForm1) {
                ConsentForm unused = AppActivity.consentForm = consentForm1;
                if (AppActivity.consentInformation.getConsentStatus() == 2) {
                    AppActivity.consentForm.show(AppActivity._this, new ConsentForm.OnConsentFormDismissedListener() { // from class: org.cocos2dx.cpp.AppActivity.3.1
                        @Override // com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
                        public void onConsentFormDismissed(FormError formError) {
                            Log.w("PTag", "Ram 4");
                            AppActivity.loadForm();
                        }
                    });
                } else {
                    Log.w("PTag", "Ram 5");
                    AppActivity.initializeAds();
                }
            }
        }, new UserMessagingPlatform.OnConsentFormLoadFailureListener() { // from class: org.cocos2dx.cpp.AppActivity.4
            @Override // com.google.android.ump.UserMessagingPlatform.OnConsentFormLoadFailureListener
            public void onConsentFormLoadFailure(FormError formError) {
                Log.w("PTag", "Ram 6");
                AppActivity.initializeAds();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void initializeAds() {
        MobileAds.initialize(_this, new OnInitializationCompleteListener() { // from class: org.cocos2dx.cpp.AppActivity.5
            @Override // com.google.android.gms.ads.initialization.OnInitializationCompleteListener
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder().setTagForChildDirectedTreatment(1).build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        if (!checkSUBSCRIBED() && mInterstitialAd == null) {
            loadInterstitialAd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loadBannerAd() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (consentInformation.getConsentStatus() == 1) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }
        bannerAdView.loadAd(builder.build());
        bannerAdView.setAdListener(new AdListener() { // from class: org.cocos2dx.cpp.AppActivity.6
            @Override // com.google.android.gms.ads.AdListener
            public void onAdLoaded() {
                RelativeLayout unused = AppActivity.rl = new RelativeLayout(AppActivity._this);
                AppActivity.rl.setGravity(81);
                RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(-1, -1);
                lay.addRule(12);
                lay.addRule(13);
                LinearLayout linearLayout = new LinearLayout(AppActivity._this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.gravity = 81;
                linearLayout.setLayoutParams(params);
                linearLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                if (AppActivity.bannerAdView.getParent() == null) {
                    LinearLayout.LayoutParams adPar = new LinearLayout.LayoutParams(-2, -2);
                    adPar.gravity = 81;
                    adPar.setMargins(0, 4, 0, 0);
                    linearLayout.addView(AppActivity.bannerAdView, adPar);
                    AppActivity.rl.addView(linearLayout);
                    AppActivity._this.addContentView(AppActivity.rl, lay);
                }
                float height = AppActivity.bannerAdView.getAdSize().getHeightInPixels(AppActivity._this);
                AppActivity.scaleView(height);
                AppActivity.bannerAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    private static void loadInterstitialAd() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (consentInformation.getConsentStatus() == 1) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }
        AppActivity appActivity = _this;
        InterstitialAd.load(appActivity, appActivity.getString(R.string.admob_interstitial_id), builder.build(), new InterstitialAdLoadCallback() { // from class: org.cocos2dx.cpp.AppActivity.7
            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdLoaded(InterstitialAd interstitialAd) {
                InterstitialAd unused = AppActivity.mInterstitialAd = interstitialAd;
            }

            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                InterstitialAd unused = AppActivity.mInterstitialAd = null;
            }
        });
    }

    public static void bannerEnabled() {
        _this.runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.8
            @Override // java.lang.Runnable
            public void run() {
                if (AppActivity.bannerAdView != null && !AppActivity.checkSUBSCRIBED()) {
                    AppActivity.loadBannerAd();
                }
            }
        });
    }

    public static void HideBanner() {
        _this.runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.9
            @Override // java.lang.Runnable
            public void run() {
                AdView adView = AppActivity.bannerAdView;
                AdView unused = AppActivity.bannerAdView;
                adView.setVisibility(View.GONE);
                AppActivity.scaleView(0.0f);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (mInterstitialAd == null && !checkSUBSCRIBED()) {
            loadInterstitialAd();
        }
    }

    public static boolean isInterstitialAvailable() {
        return admobfullpageavailable;
    }

    private static AdSize getBannerAdSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        _this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int adWidth = (int) (outMetrics.widthPixels / outMetrics.density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(_this, adWidth);
    }

    public static void showInterstitial() {
        _this.runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.10
            @Override // java.lang.Runnable
            public void run() {
                if (AppActivity.mInterstitialAd != null && !AppActivity.checkSUBSCRIBED()) {
                    AppActivity.mInterstitialAd.show(AppActivity._this);
                    AppActivity.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: org.cocos2dx.cpp.AppActivity.10.1
                        @Override // com.google.android.gms.ads.FullScreenContentCallback
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            InterstitialAd unused = AppActivity.mInterstitialAd = null;
                        }

                        @Override // com.google.android.gms.ads.FullScreenContentCallback
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.d("TAG", "The ad failed to show.");
                            InterstitialAd unused = AppActivity.mInterstitialAd = null;
                        }

                        @Override // com.google.android.gms.ads.FullScreenContentCallback
                        public void onAdShowedFullScreenContent() {
                            InterstitialAd unused = AppActivity.mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void BuyNormal(String id) {
        _this.runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.11
            @Override // java.lang.Runnable
            public void run() {
            }
        });
    }

    static void shareGame(String title) {
        ContextWrapper c = new ContextWrapper(_this);
        String path = c.getFilesDir().getPath() + "/" + title;
        File imgFile = new File(path);
        Uri pngUri = FileProvider.getUriForFile(_this, _this.getApplicationContext().getPackageName() + ".provider", imgFile);
        if (pngUri != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(pngUri, _this.getContentResolver().getType(pngUri));
            intent.putExtra("android.intent.extra.TEXT", R.string.app_name);
            intent.putExtra("android.intent.extra.STREAM", pngUri);
            intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + _this.getPackageName());
            _this.startActivity(Intent.createChooser(intent, "Choose an app"));
        }
    }

    static void saveImage(String title) {
        if (!isPermissionGranted("android.permission.WRITE_EXTERNAL_STORAGE")) {
            requestPermission("android.permission.WRITE_EXTERNAL_STORAGE", WRITE_STORAGE_REQUEST_ID);
        } else {
            _this.saveImageToGallery(title);
        }
    }

    void saveImageToGallery(String title) {
        File file;
        OutputStream output;
        String dirName = getContext().getString(R.string.app_name);
        ContextWrapper c = new ContextWrapper(_this);
        String path = c.getFilesDir().getPath() + "/" + title;
        File imgFile = new File(path);
        System.out.println("Path to check : " + path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures//" + dirName + "/");
        dir.mkdirs();
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
        if (Build.VERSION.SDK_INT >= 29) {
            file = null;
        } else {
            file = new File(dir, dirName + " " + timeStamp + ".png");
        }
        try {
            ContentValues values = new ContentValues();
            if (Build.VERSION.SDK_INT >= 29) {
                values.put("datetaken", Long.valueOf(System.currentTimeMillis()));
                values.put("_display_name", dirName + " " + timeStamp + ".png");
            } else {
                values.put("_data", file.getAbsolutePath());
            }
            values.put("mime_type", "image/png");
            ContentResolver contentResolver = _this.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (Build.VERSION.SDK_INT >= 29) {
                output = contentResolver.openOutputStream(uri);
            } else {
                output = new FileOutputStream(file);
            }
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.12
                @Override // java.lang.Runnable
                public void run() {
                    Toast.makeText(AppActivity._this, " Saved Successfully.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean isPermissionGranted(String permission) {
        return Build.VERSION.SDK_INT < 23 || _this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    static void requestPermission(String permission, int requestCode) {
        if (!isPermissionGranted(permission)) {
            ActivityCompat.requestPermissions(_this, new String[]{permission}, requestCode);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 97483) {
            if (grantResults.length == 1 && grantResults[0] == 0) {
                saveImageToGallery(this.PhotoName);
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Permission needed");
                if (shouldShowRequestPermissionRationale("android.permission.READ_EXTERNAL_STORAGE")) {
                    alert.setMessage("Please grant permission to open gallery!");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: org.cocos2dx.cpp.AppActivity.13
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            AppActivity.saveImage(AppActivity.this.PhotoName);
                        }
                    });
                    alert.setNegativeButton("No", (DialogInterface.OnClickListener) null);
                } else {
                    alert.setMessage("Please grant permission from settings.");
                    alert.setPositiveButton("OK", (DialogInterface.OnClickListener) null);
                }
                alert.show();
            }
        }
    }

    public static void OpenMoreGame() {
        _this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(_this.getString(R.string.more_game_url))));
    }

    @SuppressLint("WrongConstant")
    public static void openRateGame() {
        String rateURL = getContext().getApplicationContext().getString(R.string.rate_game_url);
        Intent storeintent = new Intent("android.intent.action.VIEW", Uri.parse(rateURL));
        storeintent.addFlags(270532608);
        _this.startActivity(storeintent);
    }

    public static AppActivity getInstance() {
        Log.i("Call", "getInstance");
        return _this;
    }

    public static void BackButtonClicked() {
        _this.runOnUiThread(new Runnable() { // from class: org.cocos2dx.cpp.AppActivity.14
            @Override // java.lang.Runnable
            public void run() {
                AppActivity._this.finish();
            }
        });
    }
}
