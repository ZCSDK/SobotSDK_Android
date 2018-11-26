package com.sobot.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sobot.chat.utils.ResourceUtils;


public class SobotSectorProgressView extends ImageView {
    private int fgColor;
    private Paint fgPaint;
    private RectF oval;

    private Paint mPaint;
    private RectF dstRect;
    private Xfermode mXfermode;


    public SobotSectorProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        PorterDuff.Mode porterDuffMode = PorterDuff.Mode.SRC_ATOP;
        mXfermode = new PorterDuffXfermode(porterDuffMode);

//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
//                R.styleable.SectorProgressView, 0, 0);

        /*try {
            fgColor = a.getColor(R.styleable.SectorProgressView_fgColor, 0xffff765c);
            progress = maxConstant - a.getFloat(R.styleable.SectorProgressView_percent, 0);
            startAngle = a.getFloat(R.styleable.SectorProgressView_startAngle, 0) + 270;

        } finally {
            a.recycle();
        }*/

        init();
    }

    private void init() {
        startAngle = 270;
        fgPaint = new Paint();
        fgColor = getContext().getResources().getColor(ResourceUtils.getIdByName(getContext(), "color", "sobot_sectorProgressView_fgColor"));

        fgPaint.setColor(fgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingBottom() + getPaddingTop());

        float wwd = (float) w - xpad;
        float hhd = (float) h - ypad;
        int centerX = (int) ((w + xpad) / 2);
        int centerY = (int) ((h + ypad) / 2);
        oval = new RectF(centerX - w, centerY - h, centerX + w, centerY + h);

        dstRect = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + wwd, getPaddingTop() + hhd);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.saveLayer(dstRect, mPaint, Canvas.ALL_SAVE_FLAG);
//        canvas.drawBitmap(dstBmp, null, dstRect, mPaint);
        mPaint.setXfermode(mXfermode);
        canvas.drawArc(oval, startAngle, -progress * 3.6f, true, fgPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);

    }

    private static final float maxConstant = 100;
    private float rangePercent = 1;
    private float progress;
    private float mMax = maxConstant;

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle + 270;
        postInvalidate();
//        requestLayout();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        progress = progress < 0 ? 0 : progress;
        progress = progress > mMax ? mMax : progress;

        this.progress = (mMax - progress) * rangePercent;
        postInvalidate();
//        requestLayout();
    }

    private float startAngle;

    public void setMax(int max) {
        if (max < 0) {
            return;
        }
        rangePercent = maxConstant * 1.0f / max;
        mMax = max;
    }

}
