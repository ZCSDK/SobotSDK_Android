package com.sobot.chat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * 扩展的imageView
 */
public class SobotImageView extends ImageView {
    private int mDefaultImageId;
    private int mCornerRadius = 0;
    private boolean mIsCircle = false;
    private boolean mIsSquare = false;
    private int mBorderWidth = 0;
    private int mBorderColor = Color.WHITE;
    private RectF mRect;

    private Paint mBorderPaint;
    private OnDrawableChangedCallback mOnDrawableChangedCallback;

    public SobotImageView(Context context) {
        this(context, null);
    }

    public SobotImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SobotImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initCustomAttrs(context, attrs);

        initBorderPaint();

        setDefaultImage();

        mRect = new RectF();
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SobotImageView);
//        final int N = typedArray.getIndexCount();
//        for (int i = 0; i < N; i++) {
//            initCustomAttr(typedArray.getIndex(i), typedArray);
//        }
//        typedArray.recycle();
        mDefaultImageId = 0;
//        mCornerRadius = dip2px(4);
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale+0.5f);
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
//        if (attr == R.styleable.SobotImageView_android_src) {
//            mDefaultImageId = typedArray.getResourceId(attr, 0);
//        } else if (attr == R.styleable.SobotImageView_sobot_iv_isCircle) {
//            mIsCircle = typedArray.getBoolean(attr, mIsCircle);
//        } else if (attr == R.styleable.SobotImageView_sobot_iv_cornerRadius) {
//            mCornerRadius = typedArray.getDimensionPixelSize(attr, mCornerRadius);
//        } else if (attr == R.styleable.SobotImageView_sobot_iv_isSquare) {
//            mIsSquare = typedArray.getBoolean(attr, mIsSquare);
//        } else if (attr == R.styleable.SobotImageView_sobot_iv_borderWidth) {
//            mBorderWidth = typedArray.getDimensionPixelSize(attr, mBorderWidth);
//        } else if (attr == R.styleable.SobotImageView_sobot_iv_borderColor) {
//            mBorderColor = typedArray.getColor(attr, mBorderColor);
//        }

    }

    public void setCornerRadius(int radius) {
        mCornerRadius = dip2px(radius);
        invalidate();
    }

    public void setIsCircle(boolean isCircle) {
        mIsCircle = isCircle;
        invalidate();
    }

    private void setDefaultImage() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(getRoundedDrawable(getContext(), bitmap, mCornerRadius));
            } else {
                super.setImageDrawable(drawable);
            }
        } else if (drawable instanceof BitmapDrawable && mIsCircle) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(getCircleDrawable(getContext(), bitmap));
            } else {
                super.setImageDrawable(drawable);
            }
        } else {
            super.setImageDrawable(drawable);
        }

        notifyDrawableChanged(drawable);
    }

    private void notifyDrawableChanged(Drawable drawable) {
        if (mOnDrawableChangedCallback != null) {
            mOnDrawableChangedCallback.onDrawableChanged(drawable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsCircle || mIsSquare) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childWidthSize = getMeasuredWidth();
            heightMeasureSpec = widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
            canvas.save();

            canvas.restore();
            if (mBorderWidth > 0) {
                if (mIsCircle) {
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mBorderWidth / 2, mBorderPaint);
                } else {
                    mRect.left = 0;
                    mRect.top = 0;
                    mRect.right = getWidth();
                    mRect.bottom = getHeight();
                    canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mBorderPaint);
                }
            }
        } catch (Exception e) {

        }
    }

    public void setDrawableChangedCallback(OnDrawableChangedCallback onDrawableChangedCallback) {
        mOnDrawableChangedCallback = onDrawableChangedCallback;
    }

    public interface OnDrawableChangedCallback {
        void onDrawableChanged(Drawable drawable);
    }

    public static RoundedBitmapDrawable getCircleDrawable(Context context, Bitmap src) {
        Bitmap dst;
        //将长方形图片裁剪成正方形图片
        if (src.getWidth() >= src.getHeight()) {
            dst = Bitmap.createBitmap(src, src.getWidth() / 2 - src.getHeight() / 2, 0, src.getHeight(), src.getHeight()
            );
        } else {
            dst = Bitmap.createBitmap(src, 0, src.getHeight() / 2 - src.getWidth() / 2, src.getWidth(), src.getWidth()
            );
        }
        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), dst);
        circleDrawable.setAntiAlias(true);
        circleDrawable.setCornerRadius(Math.min(dst.getWidth(), dst.getHeight()) / 2.0f);
        return circleDrawable;
    }

    public static RoundedBitmapDrawable getCircleDrawable(Context context, @DrawableRes int resId) {
        return getCircleDrawable(context, BitmapFactory.decodeResource(context.getResources(), resId));
    }

    public static RoundedBitmapDrawable getRoundedDrawable(Context context, Bitmap bitmap, float cornerRadius) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        roundedBitmapDrawable.setCornerRadius(cornerRadius);
        return roundedBitmapDrawable;
    }

    public static RoundedBitmapDrawable getRoundedDrawable(Context context, @DrawableRes int resId, float cornerRadius) {
        return getRoundedDrawable(context, BitmapFactory.decodeResource(context.getResources(), resId), cornerRadius);
    }
}
