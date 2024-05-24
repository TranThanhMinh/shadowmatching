package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class Cocos2dxTypefaces {
    private static final HashMap<String, Typeface> sTypefaceCache = new HashMap<>();

    public static synchronized Typeface get(Context context, String assetName) {
        Typeface typeface;
        Typeface typeface2;
        synchronized (Cocos2dxTypefaces.class) {
            HashMap<String, Typeface> hashMap = sTypefaceCache;
            if (!hashMap.containsKey(assetName)) {
                if (assetName.startsWith("/")) {
                    typeface2 = Typeface.createFromFile(assetName);
                } else {
                    typeface2 = Typeface.createFromAsset(context.getAssets(), assetName);
                }
                hashMap.put(assetName, typeface2);
            }
            typeface = hashMap.get(assetName);
        }
        return typeface;
    }
}
