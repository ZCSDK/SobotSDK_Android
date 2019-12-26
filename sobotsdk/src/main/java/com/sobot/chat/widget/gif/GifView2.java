package com.sobot.chat.widget.gif;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AndroidException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sobot.chat.R;
import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class GifView2 extends View implements View.OnTouchListener {

    private final int DEFAULT_MOVIE_VIEW_DURATION = 1000;
    private int movieMovieResourceId = 0;
    private Movie movie;
    private long movieStart = 0;
    private int currentAnimationTime;

    private float movieLeft;
    private float movieTop;
    private float movieScale;

    private int movieMeasuredMovieWidth;
    private int movieMeasuredMovieHeight;

    volatile boolean isPaused;

    private boolean isVisible = true;

    int gifResource;
    boolean isPlaying;



    private boolean isCanTouch = false;
    private int point_num = 0;//当前触摸的点数
    public static final float SCALE_MAX = 3.0f; //最大的缩放比例
    private static final float SCALE_MIN = 0.5f;

    private double oldDist = 0;
    private double moveDist = 0;
    //针对控件的坐标系，即控件左上角为原点
    private double moveX = 0;
    private double moveY = 0;

    private double downX = 0;
    private double downY = 0;
    //针对屏幕的坐标系，即屏幕左上角为原点
    private double moveRawX = 0;
    private double moveRawY = 0;

    private boolean isScale=false;

    private String gifUrl;

    LoadFinishListener loadFinishListener;



    public GifView2(Context context) {
        this(context,null);
    }

    public GifView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GifView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);

    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView2, defStyleAttr, android.R.style.Widget);

        movieMovieResourceId = array.getResourceId(R.styleable.GifView2_gif, -1);
        array.recycle();

        if (movieMovieResourceId != -1) {
            movie = movie.decodeStream(getResources().openRawResource(movieMovieResourceId));
        }



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        if (movie != null) {
            movieMeasuredMovieWidth = ScreenUtils.formatDipToPx(getContext(),movie.width());
            movieMeasuredMovieHeight =ScreenUtils.formatDipToPx(getContext(), movie.height());

            if (movieMeasuredMovieWidth==0||movieMeasuredMovieHeight==0){
                setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
                return;
            }

            /*
             * Calculate horizontal scaling
             */
            float scaleW = 1f;


            int[] screen = ScreenUtils
                    .getScreenWH(getContext());
            Log.e( "onMeasure: ","\n"+movieMeasuredMovieWidth+"\t"+movieMeasuredMovieHeight+"\n"+screen[0]+"\t"+screen[1] );

            int measureModeWidth = View.MeasureSpec.getMode(widthMeasureSpec);

            if (measureModeWidth != View.MeasureSpec.UNSPECIFIED) {
                int maximumWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (movieMeasuredMovieWidth > screen[0]) {
                    scaleW = movieMeasuredMovieWidth * 1.0f / screen[0];
                    movieMeasuredMovieHeight = (int) (movieMeasuredMovieHeight / scaleW);
                    movieMeasuredMovieWidth=screen[0];
                }
            }
            /*
             * calculate vertical scaling
             */
            float scaleH = 1f;
            int measureModeHeight = View.MeasureSpec.getMode(heightMeasureSpec);

            if (measureModeHeight != View.MeasureSpec.UNSPECIFIED) {
                int maximumHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (movieMeasuredMovieHeight > screen[1]) {
                    scaleH = movieMeasuredMovieHeight * 1.0f / screen[1];
                    movieMeasuredMovieWidth = (int) (movieMeasuredMovieWidth/ scaleH);
                    movieMeasuredMovieHeight=screen[1];
                }
            }
            /*
             * calculate overall scale
             */
            float scale = getResources().getDisplayMetrics().density;
            movieScale = scale/(scaleW*scaleH);
//            movieMeasuredMovieWidth = (int) (movieMeasuredMovieWidth * movieScale);
//            movieMeasuredMovieHeight = (int) (movieMeasuredMovieHeight * movieScale);
            Log.e( "onMeasure: ","\n"+movieMeasuredMovieWidth+"\t"+movieMeasuredMovieHeight+"\n"+movieScale);
            setMeasuredDimension(movieMeasuredMovieWidth, movieMeasuredMovieHeight);
        } else {
            /*
             * No movie set, just set minimum available size.
             */
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /*
         * Calculate movieLeft / movieTop for drawing in center
         */
        movieLeft = (getWidth() - movieMeasuredMovieWidth) / 2.0f;
        movieTop = (getHeight() - movieMeasuredMovieHeight) / 2.0f;
        isVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {
            if (!isPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    private void invalidateView() {
        if (isVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }


    /**
     * Calculate current animation time
     */
    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (movieStart == 0L) {
            movieStart = now;
        }

        long duration = movie.duration();

        if (duration == 0) {
            duration = DEFAULT_MOVIE_VIEW_DURATION;
        }

        currentAnimationTime = (int) ((now - movieStart) % duration);
    }

    /**
     * Draw current GIF frame
     */
    private void drawMovieFrame(Canvas canvas) {
        if (movieMeasuredMovieHeight==0||movieMeasuredMovieHeight==0){
            canvas.restore();
            return;
        }
        movie.setTime(currentAnimationTime);
        canvas.save();
        canvas.scale(movieScale, movieScale);
        movie.draw(canvas, movieLeft / movieScale, movieTop / movieScale);
        canvas.restore();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        isVisible = screenState == View.SCREEN_STATE_ON;
        invalidateView();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    public void play() {
        if (this.isPaused) {
            this.isPaused = false;
            /**
             * Calculate new movie start time, so that it resumes from the same
             * frame.
             */
            movieStart = android.os.SystemClock.uptimeMillis() - currentAnimationTime;
            invalidate();
        }
    }

    public void pause() {
        if (!this.isPaused) {
            this.isPaused = true;
            invalidate();
        }
    }

    public int getMovieMovieResourceId() {
        return movieMovieResourceId;
    }

    /**
     * 设置gif 资源 流
     *
     * @param movieMovieResourceId 没有 填 -1
     * @param inputStream
     */
    public void setMovieMovieResourceId(int movieMovieResourceId, InputStream inputStream) {
        this.movieMovieResourceId = movieMovieResourceId;
        if (movieMovieResourceId != -1) {
            movie = Movie.decodeStream(getResources().openRawResource(movieMovieResourceId));
        } else if (inputStream != null) {
            movie = Movie.decodeStream(inputStream);
        }
        requestLayout();
    }

    public void setGifImage(FileInputStream inputStream) {
        if (inputStream != null) {
            movie = Movie.decodeStream(inputStream);
        }
        requestLayout();
    }


    /**
     * 触摸使用的移动事件
     *
     * @param lessX
     * @param lessY
     */
    private void setSelfPivot(float lessX, float lessY) {
        if (getScaleX()<=1.0){
            return ;
        }
        float setPivotX = 0;
        float setPivotY = 0;
        setPivotX = getPivotX() + lessX;
        setPivotY = getPivotY() + lessY;
        Log.e("lawwingLog", "setPivotX:" + setPivotX + "  setPivotY:" + setPivotY
                + "  getWidth:" + getWidth() + "  getHeight:" + getHeight());
        if (setPivotX < 0 && setPivotY < 0) {
            setPivotX = 0;
            setPivotY = 0;
        } else if (setPivotX > 0 && setPivotY < 0) {
            setPivotY = 0;
            if (setPivotX > getWidth()) {
                setPivotX = getWidth();
            }
        } else if (setPivotX < 0 && setPivotY > 0) {
            setPivotX = 0;
            if (setPivotY > getHeight()) {
                setPivotY = getHeight();
            }
        } else {
            if (setPivotX > getWidth()) {
                setPivotX = getWidth();
            }
            if (setPivotY > getHeight()) {
                setPivotY = getHeight();
            }
        }
        setPivot(setPivotX, setPivotY);
    }

    /**
     * 计算两个点的距离
     *
     * @param event
     * @return
     */
    private double spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    /**
     * 平移画面，当画面的宽或高大于屏幕宽高时，调用此方法进行平移
     *
     * @param x
     * @param y
     */
    public void setPivot(float x, float y) {
        setPivotX(x);
        setPivotY(y);
    }

    /**
     * 设置放大缩小
     *
     * @param scale
     */
    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    /**
     * 初始化比例，也就是原始比例
     */
    public void setInitScale() {
        setScaleX(1.0f);
        setScaleY(1.0f);
        setPivot(getWidth() / 2, getHeight() / 2);
    }

    public void setIsCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isCanTouch) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                point_num = 1;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                point_num = 0;
                downX = 0;
                downY = 0;
                if (getScaleX()<1.0f){
                    setInitScale();
                }
                isScale=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (point_num == 1&&!isScale) {
                    //只有一个手指的时候才有移动的操作
                    float lessX = (float) (downX - event.getX());
                    float lessY = (float) (downY - event.getY());
                    moveX = event.getX();
                    moveY = event.getY();
                    moveRawX = event.getRawX();
                    moveRawY = event.getRawY();
                    setSelfPivot(lessX, lessY);
                    //setPivot(getPivotX() + lessX, getPivotY() + lessY);
                } else if (point_num == 2) {
                    //只有2个手指的时候才有放大缩小的操作
                    moveDist = spacing(event);
                    double space = moveDist - oldDist;
                    float scale = (float) (getScaleX() + space / getWidth());
                    if (scale > SCALE_MIN && scale < SCALE_MAX) {
                        setScale(scale);
                    } else if (scale < SCALE_MIN) {
                        setScale(SCALE_MIN);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);//两点按下时的距离
                point_num += 1;
                if (point_num>=2){
                    isScale=true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                point_num -= 1;
                break;
        }
        return true;
    }

    public File getImageDir(Context context) {
        File file = getFilesDir(context, "images");
        return file;
    }
    public File getFilesDir(Context context, String tag) {
        if (isSdCardExist() == true) {
            return context.getExternalFilesDir(tag);
        } else {
            return context.getFilesDir();
        }
    }
    public boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void setLoadFinishListener(LoadFinishListener loadFinishListener) {
        this.loadFinishListener = loadFinishListener;
    }

    public void setGifImage(FileInputStream in, String imageUrL) {
        setGifImage(in);
        this.gifUrl=imageUrL;
        if (null==movie||movie.width()==0||movie.height()==0){
            File dirPath = this.getImageDir(getContext());
            String encode = MD5Util.encode(imageUrL);
            File savePath = new File(dirPath, encode);
            displayImage(imageUrL, savePath, this);
        }
    }

    public void displayImage(String url, File saveFile, final GifView2 gifView) {
        // 下载图片
        HttpUtils.getInstance().download(url, saveFile, null, new HttpUtils.FileCallBack() {

            @Override
            public void onResponse(File file) {
                LogUtils.i("down load onSuccess gif"
                        + file.getAbsolutePath());
                // 把图片文件打开为文件流，然后解码为bitmap
                if (null!=loadFinishListener)
                    loadFinishListener.endCallBack(file.getAbsolutePath());
            }

            @Override
            public void onError(Exception e, String msg, int responseCode) {
                LogUtils.w("图片下载失败:" + msg,e);
            }

            @Override
            public void inProgress(int progress) {
                LogUtils.i("gif图片下载进度:" + progress);
            }
        });
    }

    public interface LoadFinishListener{
        void endCallBack(String pathAbsolute);
    }
}
