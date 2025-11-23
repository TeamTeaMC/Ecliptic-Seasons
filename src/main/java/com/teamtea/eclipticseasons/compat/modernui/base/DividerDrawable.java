package com.teamtea.eclipticseasons.compat.modernui.base;

import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;

public class DividerDrawable extends Drawable {

    private Paint paint;
    private final int dividerHeight;
    private final int dividerColor;

    public DividerDrawable(int color, int heightPx) {
        paint = new Paint();
        paint.setColor(color);
        dividerHeight = heightPx;
        dividerColor = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        RectF rectF = new RectF(bounds);

        // 绘制边框
        paint.setColor(dividerColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRoundRect(rectF, 2, 2, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

}

