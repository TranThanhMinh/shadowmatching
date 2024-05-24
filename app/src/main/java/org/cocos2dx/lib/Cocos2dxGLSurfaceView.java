package org.cocos2dx.lib;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class Cocos2dxGLSurfaceView extends GLSurfaceView {
    private static final int HANDLER_CLOSE_IME_KEYBOARD = 3;
    private static final int HANDLER_OPEN_IME_KEYBOARD = 2;
    private static final String TAG = Cocos2dxGLSurfaceView.class.getSimpleName();
    /* access modifiers changed from: private */
    public static Cocos2dxGLSurfaceView mCocos2dxGLSurfaceView;
    /* access modifiers changed from: private */
    public static Cocos2dxTextInputWrapper sCocos2dxTextInputWraper;
    private static Handler sHandler;
    /* access modifiers changed from: private */
    public Cocos2dxEditBox mCocos2dxEditText;
    /* access modifiers changed from: private */
    public Cocos2dxRenderer mCocos2dxRenderer;
    private boolean mMultipleTouchEnabled = true;
    private boolean mSoftKeyboardShown = false;

    public boolean isSoftKeyboardShown() {
        return this.mSoftKeyboardShown;
    }

    public void setSoftKeyboardShown(boolean softKeyboardShown) {
        this.mSoftKeyboardShown = softKeyboardShown;
    }

    public boolean isMultipleTouchEnabled() {
        return this.mMultipleTouchEnabled;
    }

    public void setMultipleTouchEnabled(boolean multipleTouchEnabled) {
        this.mMultipleTouchEnabled = multipleTouchEnabled;
    }

    public Cocos2dxGLSurfaceView(Context context) {
        super(context);
        initView();
    }

    public Cocos2dxGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        setEGLContextClientVersion(2);
        setFocusableInTouchMode(true);
        mCocos2dxGLSurfaceView = this;
        sCocos2dxTextInputWraper = new Cocos2dxTextInputWrapper(this);
        sHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        if (Cocos2dxGLSurfaceView.this.mCocos2dxEditText != null && Cocos2dxGLSurfaceView.this.mCocos2dxEditText.requestFocus()) {
                            Cocos2dxGLSurfaceView.this.mCocos2dxEditText.removeTextChangedListener(Cocos2dxGLSurfaceView.sCocos2dxTextInputWraper);
                            Cocos2dxGLSurfaceView.this.mCocos2dxEditText.setText("");
                            String text = (String) msg.obj;
                            Cocos2dxGLSurfaceView.this.mCocos2dxEditText.append(text);
                            Cocos2dxGLSurfaceView.sCocos2dxTextInputWraper.setOriginText(text);
                            Cocos2dxGLSurfaceView.this.mCocos2dxEditText.addTextChangedListener(Cocos2dxGLSurfaceView.sCocos2dxTextInputWraper);
                            ((InputMethodManager) Cocos2dxGLSurfaceView.mCocos2dxGLSurfaceView.getContext().getSystemService("input_method")).showSoftInput(Cocos2dxGLSurfaceView.this.mCocos2dxEditText, 0);
                            Log.d("GLSurfaceView", "showSoftInput");
                            return;
                        }
                        return;
                    case 3:
                        if (Cocos2dxGLSurfaceView.this.mCocos2dxEditText != null) {
                            Cocos2dxGLSurfaceView.this.mCocos2dxEditText.removeTextChangedListener(Cocos2dxGLSurfaceView.sCocos2dxTextInputWraper);
                            ((InputMethodManager) Cocos2dxGLSurfaceView.mCocos2dxGLSurfaceView.getContext().getSystemService("input_method")).hideSoftInputFromWindow(Cocos2dxGLSurfaceView.this.mCocos2dxEditText.getWindowToken(), 0);
                            Cocos2dxGLSurfaceView.this.requestFocus();
                            ((Cocos2dxActivity) Cocos2dxGLSurfaceView.mCocos2dxGLSurfaceView.getContext()).hideVirtualButton();
                            Log.d("GLSurfaceView", "HideSoftInput");
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public static Cocos2dxGLSurfaceView getInstance() {
        return mCocos2dxGLSurfaceView;
    }

    public static void queueAccelerometer(float x, float y, float z, long timestamp) {
        final float f = x;
        final float f2 = y;
        final float f3 = z;
        final long j = timestamp;
        mCocos2dxGLSurfaceView.queueEvent(new Runnable() {
            public void run() {
                Cocos2dxAccelerometer.onSensorChanged(f, f2, f3, j);
            }
        });
    }

    public void setCocos2dxRenderer(Cocos2dxRenderer renderer) {
        this.mCocos2dxRenderer = renderer;
        setRenderer(renderer);
    }

    private String getContentText() {
        return this.mCocos2dxRenderer.getContentText();
    }

    public Cocos2dxEditBox getCocos2dxEditText() {
        return this.mCocos2dxEditText;
    }

    public void setCocos2dxEditText(Cocos2dxEditBox pCocos2dxEditText) {
        Cocos2dxTextInputWrapper cocos2dxTextInputWrapper;
        this.mCocos2dxEditText = pCocos2dxEditText;
        if (pCocos2dxEditText != null && (cocos2dxTextInputWrapper = sCocos2dxTextInputWraper) != null) {
            pCocos2dxEditText.setOnEditorActionListener(cocos2dxTextInputWrapper);
            requestFocus();
        }
    }

    public void onResume() {
        super.onResume();
        setRenderMode(1);
        queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleOnResume();
            }
        });
    }

    public void onPause() {
        queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleOnPause();
            }
        });
        setRenderMode(0);
        super.onPause();
    }

    public boolean onTouchEvent(MotionEvent pMotionEvent) {
        int pointerNumber = pMotionEvent.getPointerCount();
        final int[] ids = new int[pointerNumber];
        final float[] xs = new float[pointerNumber];
        final float[] ys = new float[pointerNumber];
        if (this.mSoftKeyboardShown) {
            ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(((Activity) getContext()).getCurrentFocus().getWindowToken(), 0);
            requestFocus();
            this.mSoftKeyboardShown = false;
        }
        for (int i = 0; i < pointerNumber; i++) {
            ids[i] = pMotionEvent.getPointerId(i);
            xs[i] = pMotionEvent.getX(i);
            ys[i] = pMotionEvent.getY(i);
        }
        switch (pMotionEvent.getAction() & 255) {
            case 0:
                final int idDown = pMotionEvent.getPointerId(0);
                final float xDown = xs[0];
                final float yDown = ys[0];
                queueEvent(new Runnable() {
                    public void run() {
                        Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idDown, xDown, yDown);
                    }
                });
                break;
            case 1:
                final int idUp = pMotionEvent.getPointerId(0);
                final float xUp = xs[0];
                final float yUp = ys[0];
                queueEvent(new Runnable() {
                    public void run() {
                        Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionUp(idUp, xUp, yUp);
                    }
                });
                break;
            case 2:
                if (this.mMultipleTouchEnabled) {
                    queueEvent(new Runnable() {
                        public void run() {
                            Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionMove(ids, xs, ys);
                        }
                    });
                    break;
                } else {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= pointerNumber) {
                            break;
                        } else if (ids[i2] == 0) {
                            final int[] idsMove = {0};
                            final float[] xsMove = {xs[i2]};
                            final float[] ysMove = {ys[i2]};
                            queueEvent(new Runnable() {
                                public void run() {
                                    Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionMove(idsMove, xsMove, ysMove);
                                }
                            });
                            break;
                        } else {
                            i2++;
                        }
                    }
                }
            case 3:
                if (this.mMultipleTouchEnabled != false) {
                    queueEvent(new Runnable() {
                        public void run() {
                            Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionCancel(ids, xs, ys);
                        }
                    });
                    break;
                } else {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= pointerNumber) {
                            break;
                        } else if (ids[i3] == 0) {
                            final int[] idsCancel = {0};
                            final float[] xsCancel = {xs[i3]};
                            final float[] ysCancel = {ys[i3]};
                            queueEvent(new Runnable() {
                                public void run() {
                                    Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionCancel(idsCancel, xsCancel, ysCancel);
                                }
                            });
                            break;
                        } else {
                            i3++;
                        }
                    }
                }
            case 5:
                int indexPointerDown = pMotionEvent.getAction() >> 8;
                if (this.mMultipleTouchEnabled || indexPointerDown == 0) {
                    final int idPointerDown = pMotionEvent.getPointerId(indexPointerDown);
                    final float xPointerDown = pMotionEvent.getX(indexPointerDown);
                    final float yPointerDown = pMotionEvent.getY(indexPointerDown);
                    queueEvent(new Runnable() {
                        public void run() {
                            Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idPointerDown, xPointerDown, yPointerDown);
                        }
                    });
                    break;
                }
            case 6:
                int indexPointUp = pMotionEvent.getAction() >> 8;
                if (this.mMultipleTouchEnabled || indexPointUp == 0) {
                    final int idPointerUp = pMotionEvent.getPointerId(indexPointUp);
                    final float xPointerUp = pMotionEvent.getX(indexPointUp);
                    final float yPointerUp = pMotionEvent.getY(indexPointUp);
                    queueEvent(new Runnable() {
                        public void run() {
                            Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionUp(idPointerUp, xPointerUp, yPointerUp);
                        }
                    });
                    break;
                }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int pNewSurfaceWidth, int pNewSurfaceHeight, int pOldSurfaceWidth, int pOldSurfaceHeight) {
        if (!isInEditMode()) {
            this.mCocos2dxRenderer.setScreenWidthAndHeight(pNewSurfaceWidth, pNewSurfaceHeight);
        }
    }

    public boolean onKeyDown(final int pKeyCode, KeyEvent pKeyEvent) {
        switch (pKeyCode) {
            case 4:
                Cocos2dxVideoHelper.mVideoHandler.sendEmptyMessage(1000);
                break;
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 66:
            case 82:
            case 85:
                break;
            default:
                return super.onKeyDown(pKeyCode, pKeyEvent);
        }
        queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleKeyDown(pKeyCode);
            }
        });
        return true;
    }

    public boolean onKeyUp(final int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 66:
            case 82:
            case 85:
                queueEvent(new Runnable() {
                    public void run() {
                        Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleKeyUp(keyCode);
                    }
                });
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public static void openIMEKeyboard() {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = mCocos2dxGLSurfaceView.getContentText();
        sHandler.sendMessage(msg);
    }

    public static void closeIMEKeyboard() {
        Message msg = new Message();
        msg.what = 3;
        sHandler.sendMessage(msg);
    }

    public void insertText(final String pText) {
        queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleInsertText(pText);
            }
        });
    }

    public void deleteBackward() {
        queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleDeleteBackward();
            }
        });
    }

    private static void dumpMotionEvent(MotionEvent event) {
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & 255;
        sb.append("event ACTION_");
        sb.append(new String[]{"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"}[actionCode]);
        if (actionCode == 5 || actionCode == 6) {
            sb.append("(pid ");
            sb.append(action >> 8);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#");
            sb.append(i);
            sb.append("(pid ");
            sb.append(event.getPointerId(i));
            sb.append(")=");
            sb.append((int) event.getX(i));
            sb.append(",");
            sb.append((int) event.getY(i));
            if (i + 1 < event.getPointerCount()) {
                sb.append(";");
            }
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }
}