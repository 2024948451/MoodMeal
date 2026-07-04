package com.uitm.ict602.moodmeal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SpinWheelView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final String[] icons = {
            "🍔", "🍕", "🌯", "🥤", "🥗", "🍔", "🍕", "🍟"
    };

    private final int[] colors = {
            Color.rgb(255, 139, 34),
            Color.rgb(255, 198, 46),
            Color.rgb(36, 180, 170),
            Color.rgb(111, 203, 136),
            Color.rgb(255, 147, 41),
            Color.rgb(20, 155, 150),
            Color.rgb(188, 181, 234),
            Color.rgb(255, 179, 62)
    };

    public SpinWheelView(Context context) {
        super(context);
        init();
    }

    public SpinWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpinWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setColor(Color.rgb(8, 45, 42));
        textPaint.setTextSize(34f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        centerPaint.setColor(Color.WHITE);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8f);
        borderPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        float padding = 12f;
        float left = (width - size) / 2f + padding;
        float top = (height - size) / 2f + padding;
        float right = left + size - padding * 2;
        float bottom = top + size - padding * 2;

        RectF oval = new RectF(left, top, right, bottom);
        float sweepAngle = 360f / icons.length;

        for (int i = 0; i < icons.length; i++) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colors[i]);
            canvas.drawArc(oval, -90 + i * sweepAngle, sweepAngle, true, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            paint.setColor(Color.WHITE);
            canvas.drawArc(oval, -90 + i * sweepAngle, sweepAngle, true, paint);

            float angle = (float) Math.toRadians(-90 + i * sweepAngle + sweepAngle / 2);
            float radius = size * 0.30f;
            float cx = width / 2f + (float) Math.cos(angle) * radius;
            float cy = height / 2f + (float) Math.sin(angle) * radius + 12f;

            canvas.drawText(icons[i], cx, cy, textPaint);
        }

        canvas.drawCircle(width / 2f, height / 2f, size * 0.16f, centerPaint);

        borderPaint.setColor(Color.rgb(255, 220, 110));
        borderPaint.setStrokeWidth(7f);
        canvas.drawCircle(width / 2f, height / 2f, size * 0.47f, borderPaint);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(5f);
        canvas.drawCircle(width / 2f, height / 2f, size * 0.16f, borderPaint);
    }
}