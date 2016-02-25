/**
 * Copyright (c) 2016 Mattermost, Inc. All Rights Reserved.
 * See License.txt for license information.
 */
package com.mattermost.images;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class AnimatedCircleDrawable extends AnimationDrawable {

    private int bg;
    private int color;

    public AnimatedCircleDrawable(int color, float strokeWidth, int bg, int size) {
        super();

        for (int i = 0; i < 36; i++) {
            addFrame(new CircleDrawable(i * 10, color, strokeWidth, bg, size), 10);
        }
    }

    public static class CircleDrawable extends Drawable {

        private int angle;
        private int size;
        Paint stroke;
        Paint background;

        public CircleDrawable(int angle, int color, float strokeWidth, int bg, int size) {
            super();

            this.size = size;

            stroke = new Paint();
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeWidth(strokeWidth);
            stroke.setAntiAlias(true);
            stroke.setColor(color);

            background = new Paint();
            background.setColor(bg);
            background.setStyle(Paint.Style.FILL);

            this.angle = angle;
        }

        @Override
        public void draw(Canvas canvas) {
            RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            float left = (rect.width() - size) / 2;
            float top = (rect.height() - size) / 2;
            RectF arcRect = new RectF(left, top, left + size, top + size);
            canvas.drawRect(rect, background);
            canvas.drawArc(arcRect, angle, 270, false, stroke);
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}
