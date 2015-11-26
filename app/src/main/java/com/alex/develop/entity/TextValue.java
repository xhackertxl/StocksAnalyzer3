package com.alex.develop.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.alex.develop.util.UnitHelper;

/**
 * Created by alex on 15-8-25.
 * 绘制一个带边框的文本
 */
public class TextValue {

    public TextValue() {
        rect = new RectF();

        pen = new Paint();
        pen.setAntiAlias(true);

        padding = UnitHelper.dp2px(2);

        textColor = Color.WHITE;
        rectColor = Color.GRAY;

        alpha = 200;
        showBorder = true;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void setText(String text) {
        this.text = text;
        measureText();
    }

    public void setText(float text) {
        this.text = String.format("%.2f", text);
        measureText();
    }

    public void setTextSize(float fontSize) {
        pen.setTextSize(fontSize);
        measureText();
    }

    public void setTextColor(int color) {
        textColor = color;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setRectColor(int color) {
        rectColor = color;
    }

    public void setPadding(float padding) {
        this.padding = padding;
        measureText();
    }

    public RectF getBound() {
        return rect;
    }

    public void draw(float x, float y, Canvas canvas) {

        if(null != text) {

            if(showBorder) {

                pen.setColor(rectColor);
                pen.setAlpha(alpha);

                pen.setStyle(Paint.Style.FILL_AND_STROKE);

                rect.offsetTo(x, y);
                canvas.drawRect(rect, pen);
            }

            pen.setColor(textColor);
            pen.setAlpha(alpha);

            pen.setStyle(Paint.Style.FILL);

            canvas.drawText(text, x + padding, y - pen.getFontMetrics().top, pen);
        }
    }

    private void measureText() {

        if(null == text) {
            return;
        }

        Paint.FontMetrics fontMetrics = pen.getFontMetrics();
        rect.left = 0;
        rect.top = 0;
        rect.right = pen.measureText(text) + padding * 2;
        rect.bottom = fontMetrics.bottom - fontMetrics.top;
    }

    private Paint pen;
    private RectF rect;
    private String text;

    private int rectColor;
    private int textColor;
    private int alpha;

    private boolean showBorder;

    private float padding;
}
