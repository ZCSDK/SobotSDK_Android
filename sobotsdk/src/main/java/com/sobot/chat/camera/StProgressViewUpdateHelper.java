package com.sobot.chat.camera;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * @author Created by jinxl on 2018/12/4.
 */
public class StProgressViewUpdateHelper extends Handler {
    private static final int CMD_REFRESH_PROGRESS_VIEWS = 1;

    private static final int MIN_INTERVAL = 20;
    private static final int UPDATE_INTERVAL_PLAYING = 1000;
    private static final int UPDATE_INTERVAL_PAUSED = 500;

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private Callback callback;
    private int intervalPlaying;
    private int intervalPaused;

    public void start() {
        queueNextRefresh(1);
    }

    public void stop() {
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS);
    }

    public StProgressViewUpdateHelper(MediaPlayer mediaPlayer, Context context, Callback callback) {
        super(Looper.getMainLooper());
        this.mContext = context;
        this.mMediaPlayer = mediaPlayer;
        this.callback = callback;
        this.intervalPlaying = UPDATE_INTERVAL_PLAYING;
        this.intervalPaused = UPDATE_INTERVAL_PAUSED;
    }

    public StProgressViewUpdateHelper(MediaPlayer mediaPlayer, Callback callback, int intervalPlaying, int intervalPaused) {
        super(Looper.getMainLooper());
        this.callback = callback;
        this.mMediaPlayer = mediaPlayer;
        this.intervalPlaying = intervalPlaying;
        this.intervalPaused = intervalPaused;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == CMD_REFRESH_PROGRESS_VIEWS) {
            int interval = refreshProgressViews();
            if (interval != -1) {
                queueNextRefresh(interval);
            }
        }
    }

    private int refreshProgressViews() {
        int progressMillis;
        int totalMillis;
        try {
            progressMillis = mMediaPlayer.getCurrentPosition();
            totalMillis = mMediaPlayer.getDuration();

            callback.onUpdateProgressViews(progressMillis, totalMillis);

            if (!mMediaPlayer.isPlaying()) {
//                    return intervalPaused;
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }


        final int remainingMillis = intervalPlaying - progressMillis % intervalPlaying;

        return Math.max(MIN_INTERVAL, remainingMillis);
    }

    private void queueNextRefresh(final long delay) {
        final Message message = obtainMessage(CMD_REFRESH_PROGRESS_VIEWS);
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS);
        sendMessageDelayed(message, delay);
    }

    public interface Callback {
        void onUpdateProgressViews(int progress, int total);
    }
}
