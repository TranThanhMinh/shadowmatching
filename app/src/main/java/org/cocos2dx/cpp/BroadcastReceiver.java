package org.cocos2dx.cpp;

import static org.cocos2dx.cpp.MyApplication.mActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.yourcompany.shadowmatching.R;

import java.util.Objects;

/* loaded from: classes.dex */
public class BroadcastReceiver extends android.content.BroadcastReceiver {

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            refreshItem(mActivity);
            if (isNetworkConnected(context)) {
               Log.e("MInh tran","co");
            }else    Log.e("MInh tran","khong");
        } catch ( NullPointerException e) {
            e.printStackTrace();
        }

    }

    public IntentFilter getDataFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        return intentFilter;
    }

    public final boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void refreshItem(Context context) {
        if (!isNetworkConnected(context)) {
            Dialog dialog = new Dialog(context, R.style.DialogTheme);
            dialog.setContentView(R.layout.no_internet);
            dialog.setCancelable(false);
            CardView cardView = (CardView) dialog.findViewById(R.id.refresh);
            CardView cardView2 = (CardView) dialog.findViewById(R.id.exit);
            final ImageView imageView = (ImageView) dialog.findViewById(R.id.img);
            final Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
            imageView.startAnimation(loadAnimation);
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    imageView.startAnimation(loadAnimation);
                }
            });
            cardView.setOnClickListener(new View.OnClickListener() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    refreshItem(context);
                    dialog.dismiss();
                }
            });
            cardView2.setOnClickListener(new View.OnClickListener() { // from class: com.demo.rizzyfydating.splashAds.SplashActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }

}
