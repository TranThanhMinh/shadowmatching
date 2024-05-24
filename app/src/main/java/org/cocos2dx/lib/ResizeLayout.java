package org.cocos2dx.lib;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* loaded from: classes2.dex */
public class ResizeLayout extends FrameLayout {
    private boolean mEnableForceDoLayout;

    public ResizeLayout(Context context) {
        super(context);
        this.mEnableForceDoLayout = false;
    }

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnableForceDoLayout = false;
    }

    public void setEnableForceDoLayout(boolean flag) {
        this.mEnableForceDoLayout = flag;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mEnableForceDoLayout) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() { // from class: org.cocos2dx.lib.ResizeLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    ResizeLayout.this.requestLayout();
                    ResizeLayout.this.invalidate();
                }
            }, 41L);
        }
    }
}
