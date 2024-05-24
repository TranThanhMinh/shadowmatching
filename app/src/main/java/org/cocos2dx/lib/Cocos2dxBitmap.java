package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes2.dex */
public final class Cocos2dxBitmap {
    private static final int HORIZONTAL_ALIGN_CENTER = 3;
    private static final int HORIZONTAL_ALIGN_LEFT = 1;
    private static final int HORIZONTAL_ALIGN_RIGHT = 2;
    private static final int VERTICAL_ALIGN_BOTTOM = 2;
    private static final int VERTICAL_ALIGN_CENTER = 3;
    private static final int VERTICAL_ALIGN_TOP = 1;
    private static Context sContext;

    private static native void nativeInitBitmapDC(int i, int i2, byte[] bArr);

    public static void setContext(Context context) {
        sContext = context;
    }

    public static int getTextHeight(String text, int maxWidth, float textSize, Typeface typeface) {
        TextPaint paint = new TextPaint(129);
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        int lineCount = 0;
        int length = text.length();
        int index = 0;
        while (index < length) {
            int charsToAdvance = paint.breakText(text, index, length, true, maxWidth, null);
            if (charsToAdvance == 0) {
                index++;
            } else {
                index += charsToAdvance;
                lineCount++;
            }
        }
        float actualHeight = Math.abs(paint.ascent()) + Math.abs(paint.descent());
        return (int) Math.floor(lineCount * actualHeight);
    }

    public static Typeface calculateShrinkTypeFace(String text, int width, int height, Layout.Alignment hAlignment, float textSize, TextPaint paint, boolean enableWrap) {
        if (width == 0 || height == 0) {
            return paint.getTypeface();
        }
        float actualWidth = width + 1;
        float actualHeight = height + 1;
        float fontSize = 1.0f;
        float fontSize2 = textSize + 1.0f;
        if (enableWrap) {
            float actualWidth2 = actualWidth;
            float actualHeight2 = actualHeight;
            while (true) {
                if (actualHeight2 <= height && actualWidth2 <= width) {
                    break;
                }
                float fontSize3 = fontSize2 - fontSize;
                Layout layout = new StaticLayout(text, paint, width, hAlignment, 1.0f, 0.0f, false);
                actualWidth2 = layout.getWidth();
                float actualHeight3 = layout.getLineTop(layout.getLineCount());
                paint.setTextSize(fontSize3);
                if (fontSize3 <= 0.0f) {
                    paint.setTextSize(textSize);
                    break;
                }
                actualHeight2 = actualHeight3;
                fontSize2 = fontSize3;
                fontSize = 1.0f;
            }
            return paint.getTypeface();
        }
        while (true) {
            if (actualWidth <= width && actualHeight <= height) {
                break;
            }
            fontSize2 -= 1.0f;
            actualWidth = (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint));
            actualHeight = getTextHeight(text, (int) actualWidth, fontSize2, paint.getTypeface());
            paint.setTextSize(fontSize2);
            if (fontSize2 <= 0.0f) {
                paint.setTextSize(textSize);
                break;
            }
        }
        return paint.getTypeface();
    }

    public static boolean createTextBitmapShadowStroke(byte[] bytes, String fontName, int fontSize, int fontTintR, int fontTintG, int fontTintB, int fontTintA, int alignment, int width, int height, float lineSpacing, boolean shadow, float shadowDX, float shadowDY, float shadowBlur, float shadowOpacity, boolean stroke, int strokeR, int strokeG, int strokeB, int strokeA, float strokeSize, boolean enableWrap, int overflow) {
        Layout.Alignment hAlignment;
        int maxWidth;
        TextPaint paint;
        int horizontalAlignment;
        String string;
        Layout layout;
        int horizontalAlignment2;
        TextPaint paint2;
        int i;
        int i2;
        if (bytes != null && bytes.length != 0) {
            String string2 = new String(bytes);
            Layout.Alignment hAlignment2 = Layout.Alignment.ALIGN_NORMAL;
            int horizontalAlignment3 = alignment & 15;
            switch (horizontalAlignment3) {
                case 1:
                default:
                    hAlignment = hAlignment2;
                    break;
                case 2:
                    Layout.Alignment hAlignment3 = Layout.Alignment.ALIGN_OPPOSITE;
                    hAlignment = hAlignment3;
                    break;
                case 3:
                    Layout.Alignment hAlignment4 = Layout.Alignment.ALIGN_CENTER;
                    hAlignment = hAlignment4;
                    break;
            }
            TextPaint paint3 = newPaint(fontName, fontSize);
            if (stroke) {
                paint3.setStyle(Paint.Style.STROKE);
                paint3.setStrokeWidth(strokeSize);
            }
            if (width > 0) {
                maxWidth = width;
            } else {
                int maxWidth2 = (int) Math.ceil(StaticLayout.getDesiredWidth(string2, paint3));
                maxWidth = maxWidth2;
            }
            if (overflow == 1 && !enableWrap) {
                int widthBoundary = (int) Math.ceil(StaticLayout.getDesiredWidth(string2, paint3));
                layout = new StaticLayout(string2, paint3, widthBoundary, hAlignment, 1.0f, lineSpacing, false);
                paint2 = paint3;
                horizontalAlignment2 = horizontalAlignment3;
                i = 1;
                i2 = 2;
            } else {
                if (overflow == 2) {
                    paint = paint3;
                    horizontalAlignment = horizontalAlignment3;
                    string = string2;
                    calculateShrinkTypeFace(string2, width, height, hAlignment, fontSize, paint, enableWrap);
                } else {
                    paint = paint3;
                    horizontalAlignment = horizontalAlignment3;
                    string = string2;
                }
                horizontalAlignment2 = horizontalAlignment;
                paint2 = paint;
                i = 1;
                i2 = 2;
                layout = new StaticLayout(string, paint2, maxWidth, hAlignment, 1.0f, lineSpacing, false);
            }
            int layoutWidth = layout.getWidth();
            int layoutHeight = layout.getLineTop(layout.getLineCount());
            int bitmapWidth = Math.max(layoutWidth, width);
            int bitmapHeight = layoutHeight;
            if (height > 0) {
                bitmapHeight = height;
            }
            if (overflow == i && !enableWrap && width > 0) {
                bitmapWidth = width;
            }
            if (bitmapWidth != 0 && bitmapHeight != 0) {
                int offsetX = 0;
                if (horizontalAlignment2 == 3) {
                    offsetX = (bitmapWidth - layoutWidth) / 2;
                } else if (horizontalAlignment2 == i2) {
                    offsetX = bitmapWidth - layoutWidth;
                }
                int offsetY = 0;
                int verticalAlignment = (alignment >> 4) & 15;
                switch (verticalAlignment) {
                    case 2:
                        offsetY = bitmapHeight - layoutHeight;
                        break;
                    case 3:
                        offsetY = (bitmapHeight - layoutHeight) / 2;
                        break;
                }
                Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.translate(offsetX, offsetY);
                if (stroke) {
                    paint2.setARGB(strokeA, strokeR, strokeG, strokeB);
                    layout.draw(canvas);
                }
                paint2.setStyle(Paint.Style.FILL);
                paint2.setARGB(fontTintA, fontTintR, fontTintG, fontTintB);
                layout.draw(canvas);
                initNativeObject(bitmap);
                return true;
            }
            return false;
        }
        return false;
    }

    private static TextPaint newPaint(String fontName, int fontSize) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        if (fontName.endsWith(".ttf")) {
            try {
                Typeface typeFace = Cocos2dxTypefaces.get(sContext, fontName);
                paint.setTypeface(typeFace);
            } catch (Exception e) {
                Log.e("Cocos2dxBitmap", "error to create ttf type face: " + fontName);
                paint.setTypeface(Typeface.create(fontName, 0));
            }
        } else {
            paint.setTypeface(Typeface.create(fontName, 0));
        }
        return paint;
    }

    private static void initNativeObject(Bitmap bitmap) {
        byte[] pixels = getPixels(bitmap);
        if (pixels == null) {
            return;
        }
        nativeInitBitmapDC(bitmap.getWidth(), bitmap.getHeight(), pixels);
    }

    private static byte[] getPixels(Bitmap bitmap) {
        if (bitmap != null) {
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight() * 4];
            ByteBuffer buf = ByteBuffer.wrap(pixels);
            buf.order(ByteOrder.nativeOrder());
            bitmap.copyPixelsToBuffer(buf);
            return pixels;
        }
        return null;
    }

    public static int getFontSizeAccordingHeight(int height) {
        TextPaint paint = new TextPaint();
        Rect bounds = new Rect();
        paint.setTypeface(Typeface.DEFAULT);
        int text_size = 1;
        boolean found_desired_size = false;
        while (!found_desired_size) {
            paint.setTextSize(text_size);
            paint.getTextBounds("SghMNy", 0, "SghMNy".length(), bounds);
            text_size++;
            if (height - bounds.height() <= 2) {
                found_desired_size = true;
            }
        }
        return text_size;
    }

    private static String getStringWithEllipsis(String string, float width, float fontSize) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        TextPaint paint = new TextPaint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(fontSize);
        return TextUtils.ellipsize(string, paint, width, TextUtils.TruncateAt.END).toString();
    }
}
