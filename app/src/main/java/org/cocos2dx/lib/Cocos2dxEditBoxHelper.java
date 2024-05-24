package org.cocos2dx.lib;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class Cocos2dxEditBoxHelper {
    private static Cocos2dxActivity mCocos2dxActivity;
    private static SparseArray<Cocos2dxEditBox> mEditBoxArray;
    private static ResizeLayout mFrameLayout;
    private static final String TAG = Cocos2dxEditBoxHelper.class.getSimpleName();
    private static int mViewTag = 0;
    private static float mPadding = 5.0f;

    private static native void editBoxEditingChanged(int i, String str);

    private static native void editBoxEditingDidBegin(int i);

    private static native void editBoxEditingDidEnd(int i, String str, int i2);

    public static void __editBoxEditingDidBegin(int index) {
        editBoxEditingDidBegin(index);
    }

    public static void __editBoxEditingChanged(int index, String text) {
        editBoxEditingChanged(index, text);
    }

    public static void __editBoxEditingDidEnd(int index, String text, int action) {
        editBoxEditingDidEnd(index, text, action);
    }

    public Cocos2dxEditBoxHelper(ResizeLayout layout) {
        mFrameLayout = layout;
        mCocos2dxActivity = (Cocos2dxActivity) Cocos2dxActivity.getContext();
        mEditBoxArray = new SparseArray<>();
    }

    public static int getPadding(float scaleX) {
        return (int) (mPadding * scaleX);
    }

    /* renamed from: org.cocos2dx.lib.Cocos2dxEditBoxHelper$1, reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ int val$height;
        final /* synthetic */ int val$index;
        final /* synthetic */ int val$left;
        final /* synthetic */ float val$scaleX;
        final /* synthetic */ int val$top;
        final /* synthetic */ int val$width;

        AnonymousClass1(float f, int i, int i2, int i3, int i4, int i5) {
            this.val$scaleX = f;
            this.val$left = i;
            this.val$top = i2;
            this.val$width = i3;
            this.val$height = i4;
            this.val$index = i5;
        }

        @Override // java.lang.Runnable
        public void run() {
            final Cocos2dxEditBox editBox = new Cocos2dxEditBox(Cocos2dxEditBoxHelper.mCocos2dxActivity);
            editBox.setFocusable(true);
            editBox.setFocusableInTouchMode(true);
            editBox.setInputFlag(5);
            editBox.setInputMode(6);
            editBox.setReturnType(0);
            editBox.setHintTextColor(-7829368);
            editBox.setVisibility(8);
            editBox.setBackgroundColor(0);
            editBox.setTextColor(-1);
            editBox.setSingleLine();
            editBox.setOpenGLViewScaleX(this.val$scaleX);
            editBox.setPadding(Cocos2dxEditBoxHelper.getPadding(this.val$scaleX), 0, 0, 0);
            FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(-2, -2);
            lParams.leftMargin = this.val$left;
            lParams.topMargin = this.val$top;
            lParams.width = this.val$width;
            lParams.height = this.val$height;
            lParams.gravity = 51;
            Cocos2dxEditBoxHelper.mFrameLayout.addView(editBox, lParams);
            editBox.setTag(false);
            editBox.addTextChangedListener(new TextWatcher() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.1
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(final Editable s) {
                    if (!editBox.getChangedTextProgrammatically().booleanValue() && ((Boolean) editBox.getTag()).booleanValue()) {
                        Cocos2dxEditBoxHelper.mCocos2dxActivity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Cocos2dxEditBoxHelper.__editBoxEditingChanged(AnonymousClass1.this.val$index, s.toString());
                            }
                        });
                    }
                    editBox.setChangedTextProgrammatically(false);
                }
            });
            editBox.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.2
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View v, boolean hasFocus) {
                    editBox.setTag(true);
                    editBox.setChangedTextProgrammatically(false);
                    if (hasFocus) {
                        Cocos2dxEditBoxHelper.mCocos2dxActivity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                editBox.endAction = 0;
                                Cocos2dxEditBoxHelper.__editBoxEditingDidBegin(AnonymousClass1.this.val$index);
                            }
                        });
                        Cocos2dxEditBox cocos2dxEditBox = editBox;
                        cocos2dxEditBox.setSelection(cocos2dxEditBox.getText().length());
                        Cocos2dxEditBoxHelper.mFrameLayout.setEnableForceDoLayout(true);
                        Cocos2dxEditBoxHelper.mCocos2dxActivity.getGLSurfaceView().setSoftKeyboardShown(true);
                        Log.d(Cocos2dxEditBoxHelper.TAG, "edit box get focus");
                        return;
                    }
                    editBox.setVisibility(8);
                    final String text = new String(editBox.getText().toString());
                    Cocos2dxEditBoxHelper.mCocos2dxActivity.runOnGLThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.2.2
                        @Override // java.lang.Runnable
                        public void run() {
                            int action = editBox.endAction;
                            Cocos2dxEditBoxHelper.__editBoxEditingDidEnd(AnonymousClass1.this.val$index, text, action);
                        }
                    });
                    Cocos2dxEditBoxHelper.mCocos2dxActivity.hideVirtualButton();
                    Cocos2dxEditBoxHelper.mFrameLayout.setEnableForceDoLayout(false);
                    Log.d(Cocos2dxEditBoxHelper.TAG, "edit box lose focus");
                }
            });
            editBox.setOnKeyListener(new View.OnKeyListener() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.3
                @Override // android.view.View.OnKeyListener
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 0 && keyCode == 66 && (editBox.getInputType() & 131072) != 131072) {
                        Cocos2dxEditBoxHelper.closeKeyboardOnUiThread(AnonymousClass1.this.val$index);
                        return true;
                    }
                    return false;
                }
            });
            editBox.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.1.4
                @Override // android.widget.TextView.OnEditorActionListener
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 5) {
                        editBox.endAction = 1;
                        Cocos2dxEditBoxHelper.closeKeyboardOnUiThread(AnonymousClass1.this.val$index);
                        return true;
                    }
                    if (actionId == 6 || actionId == 4 || actionId == 3 || actionId == 2) {
                        editBox.endAction = 3;
                        Cocos2dxEditBoxHelper.closeKeyboardOnUiThread(AnonymousClass1.this.val$index);
                        return false;
                    }
                    return false;
                }
            });
            Cocos2dxEditBoxHelper.mEditBoxArray.put(this.val$index, editBox);
        }
    }

    public int createEditBox(int left, int top, int width, int height, float scaleX) {
        int index = mViewTag;
        mCocos2dxActivity.runOnUiThread(new AnonymousClass1(scaleX, left, top, width, height, index));
        int i = mViewTag;
        mViewTag = i + 1;
        return i;
    }

    public static void removeEditBox(final int index) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.2
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    Cocos2dxEditBoxHelper.mEditBoxArray.remove(index);
                    Cocos2dxEditBoxHelper.mFrameLayout.removeView(editBox);
                    Log.e(Cocos2dxEditBoxHelper.TAG, "remove EditBox");
                }
            }
        });
    }

    public static void setFont(final int index, final String fontName, final float fontSize) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.3
            @Override // java.lang.Runnable
            public void run() {
                Typeface tf;
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    if (!fontName.isEmpty()) {
                        if (fontName.endsWith(".ttf")) {
                            try {
                                Cocos2dxActivity unused = Cocos2dxEditBoxHelper.mCocos2dxActivity;
                                tf = Cocos2dxTypefaces.get(Cocos2dxActivity.getContext(), fontName);
                            } catch (Exception e) {
                                Log.e("Cocos2dxEditBoxHelper", "error to create ttf type face: " + fontName);
                                tf = Typeface.create(fontName, 0);
                            }
                        } else {
                            tf = Typeface.create(fontName, 0);
                        }
                    } else {
                        tf = Typeface.DEFAULT;
                    }
                    float f = fontSize;
                    if (f >= 0.0f) {
                        editBox.setTextSize(0, f);
                    }
                    editBox.setTypeface(tf);
                }
            }
        });
    }

    public static void setFontColor(final int index, final int red, final int green, final int blue, final int alpha) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.4
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setTextColor(Color.argb(alpha, red, green, blue));
                }
            }
        });
    }

    public static void setPlaceHolderText(final int index, final String text) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.5
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setHint(text);
                }
            }
        });
    }

    public static void setPlaceHolderTextColor(final int index, final int red, final int green, final int blue, final int alpha) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.6
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setHintTextColor(Color.argb(alpha, red, green, blue));
                }
            }
        });
    }

    public static void setMaxLength(final int index, final int maxLength) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.7
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setMaxLength(maxLength);
                }
            }
        });
    }

    public static void setVisible(final int index, final boolean visible) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.8
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setVisibility(visible ? 0 : 8);
                }
            }
        });
    }

    public static void setText(final int index, final String text) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.9
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setChangedTextProgrammatically(true);
                    editBox.setText(text);
                    int position = editBox.getText().length();
                    editBox.setSelection(position);
                }
            }
        });
    }

    public static void setReturnType(final int index, final int returnType) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.10
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setReturnType(returnType);
                }
            }
        });
    }

    public static void setTextHorizontalAlignment(final int index, final int alignment) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.11
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setTextHorizontalAlignment(alignment);
                }
            }
        });
    }

    public static void setInputMode(final int index, final int inputMode) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.12
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setInputMode(inputMode);
                }
            }
        });
    }

    public static void setInputFlag(final int index, final int inputFlag) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.13
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setInputFlag(inputFlag);
                }
            }
        });
    }

    public static void setEditBoxViewRect(final int index, final int left, final int top, final int maxWidth, final int maxHeight) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.14
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBox editBox = (Cocos2dxEditBox) Cocos2dxEditBoxHelper.mEditBoxArray.get(index);
                if (editBox != null) {
                    editBox.setEditBoxViewRect(left, top, maxWidth, maxHeight);
                }
            }
        });
    }

    public static void openKeyboard(final int index) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.15
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBoxHelper.openKeyboardOnUiThread(index);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void openKeyboardOnUiThread(int index) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Log.e(TAG, "openKeyboardOnUiThread doesn't run on UI thread!");
            return;
        }
        InputMethodManager imm = (InputMethodManager) Cocos2dxActivity.getContext().getSystemService("input_method");
        Cocos2dxEditBox editBox = mEditBoxArray.get(index);
        if (editBox != null) {
            editBox.requestFocus();
            mCocos2dxActivity.getGLSurfaceView().requestLayout();
            imm.showSoftInput(editBox, 0);
            mCocos2dxActivity.getGLSurfaceView().setSoftKeyboardShown(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void closeKeyboardOnUiThread(int index) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Log.e(TAG, "closeKeyboardOnUiThread doesn't run on UI thread!");
            return;
        }
        InputMethodManager imm = (InputMethodManager) Cocos2dxActivity.getContext().getSystemService("input_method");
        Cocos2dxEditBox editBox = mEditBoxArray.get(index);
        if (editBox != null) {
            imm.hideSoftInputFromWindow(editBox.getWindowToken(), 0);
            mCocos2dxActivity.getGLSurfaceView().setSoftKeyboardShown(false);
            mCocos2dxActivity.getGLSurfaceView().requestFocus();
            mCocos2dxActivity.hideVirtualButton();
        }
    }

    public static void closeKeyboard(final int index) {
        mCocos2dxActivity.runOnUiThread(new Runnable() { // from class: org.cocos2dx.lib.Cocos2dxEditBoxHelper.16
            @Override // java.lang.Runnable
            public void run() {
                Cocos2dxEditBoxHelper.closeKeyboardOnUiThread(index);
            }
        });
    }
}
