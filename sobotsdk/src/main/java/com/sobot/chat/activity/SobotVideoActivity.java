package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.camera.StVideoView;
import com.sobot.chat.camera.listener.StVideoListener;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.http.db.SobotDownloadManager;
import com.sobot.chat.core.http.download.SobotDownload;
import com.sobot.chat.core.http.download.SobotDownloadListener;
import com.sobot.chat.core.http.download.SobotDownloadTask;
import com.sobot.chat.core.http.model.SobotProgress;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.SobotPathManager;

import java.io.File;

/**
 * @author Created by jinxl on 2018/12/3.
 */
public class SobotVideoActivity extends FragmentActivity implements View.OnClickListener {
    private static final String EXTRA_VIDEO_FILE_DATA = "EXTRA_VIDEO_FILE_DATA";
    private static final String EXTRA_IMAGE_FILE_PATH = "EXTRA_IMAGE_FILE_PATH";
    private static final String EXTRA_VIDEO_FILE_PATH = "EXTRA_VIDEO_FILE_PATH";
    private static final String SOBOT_TAG_DOWNLOAD_ACT_VIDEO = "SOBOT_TAG_DOWNLOAD_ACT_VIDEO";
    private static final int RESULT_CODE = 103;

    public static final int ACTION_TYPE_PHOTO = 0;
    public static final int ACTION_TYPE_VIDEO = 1;

    private StVideoView mVideoView;
    private TextView st_tv_play;
    private ImageView st_iv_pic;
    private ProgressBar progressBar;

    private SobotCacheFile mCacheFile;
    private SobotDownloadTask mTask;
    private SobotDownloadListener mDownloadListener;

    /**
     * @param context 应用程序上下文
     * @return
     */
    public static Intent newIntent(Context context, SobotCacheFile cacheFile) {
        if (cacheFile == null) {
            return null;
        }
        Intent intent = new Intent(context, SobotVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_VIDEO_FILE_DATA, cacheFile);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(ResourceUtils.getResLayoutId(getApplicationContext(), "sobot_activity_video"));
        mVideoView = (StVideoView) findViewById(ResourceUtils.getResId(getApplicationContext(), "sobot_videoview"));
        st_tv_play = (TextView) findViewById(ResourceUtils.getResId(getApplicationContext(), "st_tv_play"));
        st_iv_pic = (ImageView) findViewById(ResourceUtils.getResId(getApplicationContext(), "st_iv_pic"));
        progressBar = (ProgressBar) findViewById(ResourceUtils.getResId(getApplicationContext(), "sobot_msgProgressBar"));
        st_tv_play.setOnClickListener(this);
        mDownloadListener = new SobotDownloadListener(SOBOT_TAG_DOWNLOAD_ACT_VIDEO) {
            @Override
            public void onStart(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onProgress(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onError(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onFinish(File result, SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onRemove(SobotProgress progress) {

            }
        };
        initData();
        mVideoView.setVideoLisenter(new StVideoListener() {

            @Override
            public void onStart() {
                st_tv_play.setVisibility(View.GONE);
            }

            @Override
            public void onEnd() {
                LogUtils.i("progress---onEnd");
                st_tv_play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                //错误监听
                showErrorUi();
            }

            @Override
            public void onCancel() {
                finish();
            }

        });
    }

    private void initData() {
        try {
            Intent intent = getIntent();
            mCacheFile = (SobotCacheFile) intent.getSerializableExtra(EXTRA_VIDEO_FILE_DATA);
            if (mCacheFile == null || TextUtils.isEmpty(mCacheFile.getMsgId())) {
                return;
            }

            SobotDownload.getInstance().setFolder(SobotPathManager.getInstance().getVideoDir());
            if (!TextUtils.isEmpty(mCacheFile.getFilePath())) {
                showFinishUi(mCacheFile.getFilePath());
            } else {
                restoreTask();
            }
        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }
    }

    /**
     * 恢复任务
     */
    private void restoreTask() {
        //更新数据
        SobotProgress progress = SobotDownloadManager.getInstance().get(mCacheFile.getMsgId());
        if (progress != null) {
            if (progress.status != SobotProgress.FINISH) {
                downloadFile(progress);
            } else {
                if (!TextUtils.isEmpty(progress.filePath) && new File(progress.filePath).exists()) {
                    refreshUI(progress);
                } else {
                    downloadFile(progress);
                }
            }
        } else {
            downloadFile(null);
        }
    }

    private void downloadFile(SobotProgress progress) {
        if (progress != null) {
            mTask = SobotDownload.restore(progress);
            if (mTask != null) {
                mTask.remove(true);
            }
        }
        mTask = HttpUtils.getInstance().addDownloadFileTask(mCacheFile.getMsgId(), mCacheFile.getUrl(), mCacheFile.getFileName(), null);
        if (mTask != null) {
            mTask.register(mDownloadListener).start();
        }
    }


    /**
     * 根据任务状态显示对应的ui
     *
     * @param progress
     */
    private void refreshUI(SobotProgress progress) {
        switch (progress.status) {
            case SobotProgress.NONE:
            case SobotProgress.WAITING:
                st_tv_play.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                st_iv_pic.setVisibility(View.VISIBLE);
                SobotBitmapUtil.display(this, mCacheFile.getSnapshot(), st_iv_pic,0,0);
                break;
            case SobotProgress.ERROR:
                SobotDownload.getInstance().removeTask(progress.tag);
                showErrorUi();
                break;
            case SobotProgress.PAUSE:
            case SobotProgress.LOADING:
                showLoadingUi(progress.fraction, progress.currentSize, progress.totalSize);
                break;
            case SobotProgress.FINISH:
                mCacheFile.setFilePath(progress.filePath);
                showFinishUi(progress.filePath);
                break;
        }
    }

    private void showLoadingUi(float fraction, long pcurrentSize, long ptotalSize) {
        st_tv_play.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        st_iv_pic.setVisibility(View.VISIBLE);
        SobotBitmapUtil.display(this, mCacheFile.getSnapshot(), st_iv_pic,0,0);
    }

    private void showErrorUi() {
        st_tv_play.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        st_iv_pic.setVisibility(View.VISIBLE);
        SobotBitmapUtil.display(this, mCacheFile.getSnapshot(), st_iv_pic,0,0);
    }

    private void showFinishUi(String videoFile) {
        if (!TextUtils.isEmpty(videoFile)) {
            File file = new File(videoFile);
            if (file.exists() && file.isFile()) {
                st_tv_play.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                st_iv_pic.setVisibility(View.GONE);

                //设置视频保存路径
                mVideoView.setVideoPath(videoFile);
//                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.playVideo();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    protected void onPause() {
        mVideoView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SobotDownload.getInstance().unRegister(SOBOT_TAG_DOWNLOAD_ACT_VIDEO);
        if (mTask != null && (mTask.progress.status == SobotProgress.FINISH
                || mTask.progress.status == SobotProgress.NONE
                || mTask.progress.status == SobotProgress.PAUSE
                || mTask.progress.status == SobotProgress.ERROR)) {
            SobotDownload.getInstance().removeTask(mTask.progress.tag);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == st_tv_play) {
            st_tv_play.setSelected(!st_tv_play.isSelected());
            mVideoView.switchVideoPlay(st_tv_play.isSelected());
        }
    }
}
