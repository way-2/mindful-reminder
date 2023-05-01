package com.way2.mindful_reminder.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.way2.mindful_reminder.R;

public class CircleProgressBar extends View {

    private float strokeWidth = 4;
    private float progress = 0;
    private int min = 0;
    private int max = 100;

    private int startAngle = -90;
    private int color = Color.DKGRAY;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint textPaint;
    private String progressText;
    private float textSize;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0);
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress);
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color);
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
            progressText = typedArray.getString(R.styleable.CircleProgressBar_progressText);
            textSize = typedArray.getDimension(R.styleable.CircleProgressBar_progressTextSize, 30f);
        } finally {
            typedArray.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);
        float angle = 360 * progress / max;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);

        float textHeight = textPaint.descent() + textPaint.ascent();
        canvas.drawText(progressText, (getWidth() - textPaint
                .measureText(progressText)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public String getProgressText() {
        return progressText;
    }

}
