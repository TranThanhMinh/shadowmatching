package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.core.view.InputDeviceCompat;

/* loaded from: classes2.dex */
public class Cocos2dxEditBox extends EditText {
    public static final int kEndActionNext = 1;
    public static final int kEndActionReturn = 3;
    public static final int kEndActionUnknown = 0;
    private static final int kTextHorizontalAlignmentCenter = 1;
    private static final int kTextHorizontalAlignmentLeft = 0;
    private static final int kTextHorizontalAlignmentRight = 2;
    private static final int kTextVerticalAlignmentBottom = 2;
    private static final int kTextVerticalAlignmentCenter = 1;
    private static final int kTextVerticalAlignmentTop = 0;
    private Boolean changedTextProgrammatically;
    int endAction;
    private final int kEditBoxInputFlagInitialCapsAllCharacters;
    private final int kEditBoxInputFlagInitialCapsSentence;
    private final int kEditBoxInputFlagInitialCapsWord;
    private final int kEditBoxInputFlagLowercaseAllCharacters;
    private final int kEditBoxInputFlagPassword;
    private final int kEditBoxInputFlagSensitive;
    private final int kEditBoxInputModeAny;
    private final int kEditBoxInputModeDecimal;
    private final int kEditBoxInputModeEmailAddr;
    private final int kEditBoxInputModeNumeric;
    private final int kEditBoxInputModePhoneNumber;
    private final int kEditBoxInputModeSingleLine;
    private final int kEditBoxInputModeUrl;
    private final int kKeyboardReturnTypeDefault;
    private final int kKeyboardReturnTypeDone;
    private final int kKeyboardReturnTypeGo;
    private final int kKeyboardReturnTypeNext;
    private final int kKeyboardReturnTypeSearch;
    private final int kKeyboardReturnTypeSend;
    private int mInputFlagConstraints;
    private int mInputModeConstraints;
    private int mMaxLength;
    private float mScaleX;

    public Boolean getChangedTextProgrammatically() {
        return this.changedTextProgrammatically;
    }

    public void setChangedTextProgrammatically(Boolean changedTextProgrammatically) {
        this.changedTextProgrammatically = changedTextProgrammatically;
    }

    public Cocos2dxEditBox(Context context) {
        super(context);
        this.kEditBoxInputModeAny = 0;
        this.kEditBoxInputModeEmailAddr = 1;
        this.kEditBoxInputModeNumeric = 2;
        this.kEditBoxInputModePhoneNumber = 3;
        this.kEditBoxInputModeUrl = 4;
        this.kEditBoxInputModeDecimal = 5;
        this.kEditBoxInputModeSingleLine = 6;
        this.kEditBoxInputFlagPassword = 0;
        this.kEditBoxInputFlagSensitive = 1;
        this.kEditBoxInputFlagInitialCapsWord = 2;
        this.kEditBoxInputFlagInitialCapsSentence = 3;
        this.kEditBoxInputFlagInitialCapsAllCharacters = 4;
        this.kEditBoxInputFlagLowercaseAllCharacters = 5;
        this.kKeyboardReturnTypeDefault = 0;
        this.kKeyboardReturnTypeDone = 1;
        this.kKeyboardReturnTypeSend = 2;
        this.kKeyboardReturnTypeSearch = 3;
        this.kKeyboardReturnTypeGo = 4;
        this.kKeyboardReturnTypeNext = 5;
        this.changedTextProgrammatically = false;
        this.endAction = 0;
    }

    public void setEditBoxViewRect(int left, int top, int maxWidth, int maxHeight) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        layoutParams.width = maxWidth;
        layoutParams.height = maxHeight;
        layoutParams.gravity = 51;
        setLayoutParams(layoutParams);
    }

    public float getOpenGLViewScaleX() {
        return this.mScaleX;
    }

    public void setOpenGLViewScaleX(float mScaleX) {
        this.mScaleX = mScaleX;
    }

    public void setMaxLength(int maxLength) {
        this.mMaxLength = maxLength;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.mMaxLength)});
    }

    public void setMultilineEnabled(boolean flag) {
        this.mInputModeConstraints |= 131072;
    }

    public void setReturnType(int returnType) {
        switch (returnType) {
            case 0:
                setImeOptions(268435457);
                return;
            case 1:
                setImeOptions(268435462);
                return;
            case 2:
                setImeOptions(268435460);
                return;
            case 3:
                setImeOptions(268435459);
                return;
            case 4:
                setImeOptions(268435458);
                return;
            case 5:
                setImeOptions(268435461);
                return;
            default:
                setImeOptions(268435457);
                return;
        }
    }

    public void setTextHorizontalAlignment(int alignment) {
        int gravity;
        int gravity2 = getGravity();
        switch (alignment) {
            case 0:
                gravity = (gravity2 & (-6)) | 3;
                break;
            case 1:
                gravity = (gravity2 & (-6) & (-4)) | 1;
                break;
            case 2:
                gravity = (gravity2 & (-4)) | 5;
                break;
            default:
                gravity = (gravity2 & (-6)) | 3;
                break;
        }
        setGravity(gravity);
    }

    public void setTextVerticalAlignment(int alignment) {
        int gravity;
        int gravity2 = getGravity();
        int padding = Cocos2dxEditBoxHelper.getPadding(this.mScaleX);
        switch (alignment) {
            case 0:
                setPadding(padding, (padding * 3) / 4, 0, 0);
                gravity = (gravity2 & (-81)) | 48;
                break;
            case 1:
                setPadding(padding, 0, 0, padding / 2);
                gravity = (gravity2 & (-49) & (-81)) | 16;
                break;
            case 2:
                gravity = (gravity2 & (-49)) | 80;
                break;
            default:
                setPadding(padding, 0, 0, padding / 2);
                gravity = (gravity2 & (-49) & (-81)) | 16;
                break;
        }
        setGravity(gravity);
    }

    public void setInputMode(int inputMode) {
        setTextHorizontalAlignment(0);
        setTextVerticalAlignment(1);
        switch (inputMode) {
            case 0:
                setTextVerticalAlignment(0);
                this.mInputModeConstraints = 131073;
                break;
            case 1:
                this.mInputModeConstraints = 33;
                break;
            case 2:
                this.mInputModeConstraints = InputDeviceCompat.SOURCE_TOUCHSCREEN;
                break;
            case 3:
                this.mInputModeConstraints = 3;
                break;
            case 4:
                this.mInputModeConstraints = 17;
                break;
            case 5:
                this.mInputModeConstraints = 12290;
                break;
            case 6:
                this.mInputModeConstraints = 1;
                break;
        }
        setInputType(this.mInputModeConstraints | this.mInputFlagConstraints);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int pKeyCode, KeyEvent pKeyEvent) {
        switch (pKeyCode) {
            case 4:
                Cocos2dxActivity activity = (Cocos2dxActivity) getContext();
                activity.getGLSurfaceView().requestFocus();
                return true;
            default:
                return super.onKeyDown(pKeyCode, pKeyEvent);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return super.onKeyPreIme(keyCode, event);
    }

    public void setInputFlag(int inputFlag) {
        switch (inputFlag) {
            case 0:
                this.mInputFlagConstraints = 129;
                setTypeface(Typeface.DEFAULT);
                setTransformationMethod(new PasswordTransformationMethod());
                break;
            case 1:
                this.mInputFlagConstraints = 524288;
                break;
            case 2:
                this.mInputFlagConstraints = 8192;
                break;
            case 3:
                this.mInputFlagConstraints = 16384;
                break;
            case 4:
                this.mInputFlagConstraints = 4096;
                break;
            case 5:
                this.mInputFlagConstraints = 1;
                break;
        }
        setInputType(this.mInputFlagConstraints | this.mInputModeConstraints);
    }
}
