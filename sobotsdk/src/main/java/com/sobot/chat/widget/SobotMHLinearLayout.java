package com.sobot.chat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sobot.chat.R;
import com.sobot.chat.utils.LogUtils;

/**
 * Created by jinxl on 2017/6/13
 * 可以设置最大高度的linearlayout
 * 先判断是否设定了mMaxHeight，如果设定了mMaxHeight，则直接使用mMaxHeight的值，
 * 如果没有设定mMaxHeight，则判断是否设定了mMaxRatio，如果设定了mMaxRatio的值 则使用此值与屏幕高度的乘积作为最高高度
 */
public class SobotMHLinearLayout extends LinearLayout {

    private static final float DEFAULT_MAX_V_RATIO = 0.8f;
    private static final float DEFAULT_MAX_H_RATIO = 1.0f;
    private static final float DEFAULT_MAX_HEIGHT = 0f;

    private float mMaxVRatio = DEFAULT_MAX_V_RATIO;// 优先级高
    private float mMaxVHeight;// 优先级低

    private float mMaxHRatio = DEFAULT_MAX_H_RATIO;// 优先级高
    private float mMaxHHeight;// 优先级低

    public SobotMHLinearLayout(Context context) {
        this(context, null);
    }

    public SobotMHLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SobotMHLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        mMaxVHeight = dip2px(context, DEFAULT_MAX_HEIGHT);
        mMaxHHeight = dip2px(context, DEFAULT_MAX_HEIGHT);
        init();
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SobotMHLinearLayout);
        mMaxVRatio = a.getFloat(R.styleable.SobotMHLinearLayout_sobot_mhv_HeightRatio, DEFAULT_MAX_V_RATIO);
        mMaxVHeight = a.getDimension(R.styleable.SobotMHLinearLayout_sobot_mhv_HeightDimen, DEFAULT_MAX_HEIGHT);

        mMaxHRatio = a.getFloat(R.styleable.SobotMHLinearLayout_sobot_mhH_HeightRatio, DEFAULT_MAX_H_RATIO);
        mMaxHHeight = a.getDimension(R.styleable.SobotMHLinearLayout_sobot_mhH_HeightDimen, DEFAULT_MAX_HEIGHT);
        a.recycle();

    }

    private void init() {
        if (mMaxVHeight <= 0) {
            mMaxVHeight = mMaxVRatio * (float) getScreenHeight(getContext());
        } else {
            mMaxVHeight = Math.min(mMaxVHeight, mMaxVRatio * (float) getScreenHeight(getContext()));
        }

        if (mMaxHHeight <= 0) {
            mMaxHHeight = mMaxHRatio * (float) getScreenHeight(getContext());
        } else {
            mMaxHHeight = Math.min(mMaxHHeight, mMaxHRatio * (float) getScreenHeight(getContext()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        LogUtils.e(heightSize+"\t"+mMaxHHeight);
        if (isScreenOriatationPortrait(getContext())) {//竖屏
            heightSize = getVHeightSize(heightMode, heightSize);
        }else{
            heightSize = getHHeightSize(heightMode, heightSize);
        }
        LogUtils.e(heightSize+"\t"+mMaxHHeight);
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);

    }

    private int getVHeightSize(int heightMode, int heightSize) {
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= mMaxVHeight ? heightSize : (int) mMaxVHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= mMaxVHeight ? heightSize : (int) mMaxVHeight;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= mMaxVHeight ? heightSize : (int) mMaxVHeight;
        }
        return heightSize;
    }

    private int getHHeightSize(int heightMode, int heightSize) {
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= mMaxHHeight ? heightSize : (int) mMaxHHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= mMaxHHeight ? heightSize : (int) mMaxHHeight;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= mMaxHHeight ? heightSize : (int) mMaxHHeight;
        }
        return heightSize;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     */
    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
