package com.sobot.chat.camera;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.sobot.chat.camera.listener.StVideoListener;
import com.sobot.chat.camera.util.AudioUtil;
import com.sobot.chat.camera.util.ScreenUtils;
import com.sobot.chat.camera.util.StCmeraLog;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;

import java.io.File;


public class StVideoView extends FrameLayout implements SurfaceHolder.Callback, View.OnClickListener
        , StProgressViewUpdateHelper.Callback, MediaPlayer.OnCompletionListener {

    private ImageView mBack;
    private TextView st_currentTime;
    private TextView st_totalTime;
    private ImageButton ib_playBtn;
    private LinearLayout st_progress_container;
    private SeekBar st_seekbar;
    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    private StPlayPauseDrawable playPauseDrawable;
    private StProgressViewUpdateHelper mUpdateHelper;

    private int layout_width;

    private String mFirstFrameUrl;      //第一帧图片
    private String mVideoUrl = "";        //视频URL

    public StVideoView(Context context) {
        this(context, null);
    }

    public StVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
        initData();
        initView();
    }

    private void initAttrs() {

    }

    private void initData() {
        layout_width = ScreenUtils.getScreenWidth(getContext());
    }

    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(getContext()).inflate(ResourceUtils.getIdByName(getContext(), "layout", "sobot_video_view"), this);
        mVideoView = (VideoView) view.findViewById(ResourceUtils.getResId(getContext(), "video_preview"));
        mBack = (ImageView) view.findViewById(ResourceUtils.getResId(getContext(), "iv_back"));
        ib_playBtn = (ImageButton) view.findViewById(ResourceUtils.getResId(getContext(), "ib_playBtn"));
        st_currentTime = (TextView) view.findViewById(ResourceUtils.getResId(getContext(), "st_currentTime"));
        st_totalTime = (TextView) view.findViewById(ResourceUtils.getResId(getContext(), "st_totalTime"));
        st_seekbar = (SeekBar) view.findViewById(ResourceUtils.getResId(getContext(), "st_seekbar"));
        st_progress_container = (LinearLayout) view.findViewById(ResourceUtils.getResId(getContext(), "st_progress_container"));
        playPauseDrawable = new StPlayPauseDrawable(getContext());

        ib_playBtn.setImageDrawable(playPauseDrawable);
        ib_playBtn.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mVideoView.getHolder().addCallback(this);
        setOnClickListener(this);
        mBack.setOnClickListener(this);
        ib_playBtn.setOnClickListener(this);
    }

    //生命周期onResume
    public void onResume() {
        startVideo();
    }

    //生命周期onPause
    public void onPause() {
        stopUpdateHelper();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    //SurfaceView生命周期
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        StCmeraLog.i("JCameraView SurfaceCreated");
        // 播放
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StCmeraLog.i("JCameraView SurfaceDestroyed");

        releaseMediaPlayer();
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        releaseUpdateHelper();
    }

    private void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            videoViewParam.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
//        LogUtils.i("progress:" + progress + "  total:" + total);
        if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
            return;
        }
        st_seekbar.setMax(total);
        st_seekbar.setProgress(progress);
        st_totalTime.setText(AudioUtil.getReadableDurationString(total));
        st_currentTime.setText(AudioUtil.getReadableDurationString(progress));
    }

    private void postStart() {
        if (mVideoListener != null) {
            mVideoListener.onStart();
        }
    }

    private void postError() {
        if (mVideoListener != null) {
            mVideoListener.onError();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == StVideoView.this) {
            StCmeraLog.i("dd");
            if (mBack.getVisibility() == GONE) {
                mBack.setVisibility(VISIBLE);
                st_progress_container.setVisibility(VISIBLE);
            } else {
                mBack.setVisibility(GONE);
                st_progress_container.setVisibility(GONE);
            }
        }

        if (mBack == v) {
            if (mVideoListener != null) {
                mVideoListener.onCancel();
            }
        }

        if (ib_playBtn == v) {
            switchVideoPlay(!isPlaying());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playPauseDrawable.setPlay(true);
        if (mVideoListener != null) {
            mVideoListener.onEnd();
        }
        st_seekbar.setProgress(0);
    }

    /**************************************************
     * 对外提供的API                     *
     **************************************************/

    public void setVideoPath(String path) {
        mVideoUrl = path;
    }

    private StVideoListener mVideoListener;

    //播放错误回调
    public void setVideoLisenter(StVideoListener videoListener) {
        this.mVideoListener = videoListener;
    }

    public void playVideo() {
        if (TextUtils.isEmpty(mVideoUrl)) {
            postError();
            return;
        }
        File file = new File(mVideoUrl);
        if (!file.exists() || !file.isFile()) {
            postError();
            return;
        }
        try {
            Surface surface = mVideoView.getHolder().getSurface();
            StCmeraLog.i("surface.isValid():" + surface.isValid());
            //surface没准备好时不执行播放
            if (!surface.isValid()) {
                return;
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.reset();
            }
            mMediaPlayer.setDataSource(mVideoUrl);
            mMediaPlayer.setSurface(surface);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                    .OnVideoSizeChangedListener() {
                @Override
                public void
                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                            .getVideoHeight());
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startVideo();
                }
            });
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(this);

        } catch (Exception e) {
            e.printStackTrace();
            postError();
        }

    }

    private void startUpdateHelper() {
        if (mUpdateHelper == null) {
            mUpdateHelper = new StProgressViewUpdateHelper(mMediaPlayer, getContext(), StVideoView.this);
        }
        mUpdateHelper.start();
    }

    private void stopUpdateHelper() {
        if (mUpdateHelper != null) {
            mUpdateHelper.stop();
        }
    }

    private void releaseUpdateHelper() {
        stopUpdateHelper();
        mUpdateHelper = null;
    }

    private void startVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            postStart();
            startUpdateHelper();
        }
    }

    public void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void switchVideoPlay(boolean isStart) {
        if (mMediaPlayer != null) {
            if (isStart) {
                startVideo();
            } else {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                stopUpdateHelper();
            }

            if (!isPlaying()) {
                playPauseDrawable.setPlay(true);
            } else {
                playPauseDrawable.setPause(true);
            }
        }
    }

}
