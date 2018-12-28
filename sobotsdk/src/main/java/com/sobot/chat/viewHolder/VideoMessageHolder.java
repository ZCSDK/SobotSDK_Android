package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.core.http.model.SobotProgress;
import com.sobot.chat.core.http.upload.SobotUpload;
import com.sobot.chat.core.http.upload.SobotUploadListener;
import com.sobot.chat.core.http.upload.SobotUploadTask;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.RoundProgressBar;
import com.sobot.chat.widget.SobotImageView;

/**
 * 小视频
 * Created by jinxl on 2018/12/04.
 */
public class VideoMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private RoundProgressBar sobot_progress;
    private ImageView sobot_msgStatus;
    private ImageView st_tv_play;
    private SobotImageView st_iv_pic;
    private RelativeLayout sobot_msg_container;
    private int sobot_bg_default_pic;

    private ZhiChiMessageBase mData;
    private String mTag;

    public VideoMessageHolder(Context context, View convertView) {
        super(context, convertView);
        sobot_progress = (RoundProgressBar) convertView.findViewById(ResourceUtils.getResId(context, "sobot_pic_progress_round"));
        sobot_msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msgStatus"));
        st_tv_play = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "st_tv_play"));
        st_iv_pic = (SobotImageView) convertView.findViewById(ResourceUtils.getResId(context, "st_iv_pic"));
        sobot_msg_container = (RelativeLayout) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msg_container"));
        sobot_bg_default_pic = ResourceUtils.getDrawableId(context, "sobot_bg_default_pic");
        st_iv_pic.setCornerRadius(4);
        sobot_progress.setTextDisplayable(false);
        if (sobot_msgStatus != null) {
            sobot_msgStatus.setOnClickListener(this);
        }
        st_tv_play.setOnClickListener(this);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        mData = message;
        if (message.getAnswer() != null && message.getAnswer().getCacheFile() != null) {
            SobotCacheFile cacheFile = message.getAnswer().getCacheFile();
            SobotBitmapUtil.display(mContext, cacheFile.getSnapshot(), st_iv_pic, sobot_bg_default_pic, sobot_bg_default_pic);
            mTag = cacheFile.getMsgId();
            if (isRight) {
                if (SobotUpload.getInstance().hasTask(mTag)) {
                    SobotUploadTask uploadTask = SobotUpload.getInstance().getTask(mTag);
                    uploadTask.register(new ListUploadListener(mTag, this));

                    refreshUploadUi(uploadTask.progress);
                } else {
                    refreshUploadUi(null);
                }
            } else {
                refreshUploadUi(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mData != null) {
            if (st_tv_play == v) {
                if (mData.getAnswer() != null && mData.getAnswer().getCacheFile() != null) {
                    SobotCacheFile cacheFile = mData.getAnswer().getCacheFile();
                    // 播放视频

                    Intent intent = SobotVideoActivity.newIntent(mContext, cacheFile);
                    mContext.startActivity(intent);
                }
            }

            if (sobot_msgStatus == v) {
                //下载失败
                showReSendDialog(mContext, msgStatus, new ReSendListener() {

                    @Override
                    public void onReSend() {
                        SobotUploadTask uploadTask = SobotUpload.getInstance().getTask(mTag);
                        if (uploadTask != null) {
                            uploadTask.restart();
                        } else {
                            notifyFileTaskRemove();
                        }
                    }
                });
            }
        }
    }

    private void notifyFileTaskRemove() {
        Intent intent = new Intent(ZhiChiConstants.SOBOT_BROCAST_REMOVE_FILE_TASK);
        intent.putExtra("sobot_msgId", mTag);
        CommonUtils.sendLocalBroadcast(mContext, intent);
    }

    private String getTag() {
        return mTag;
    }

    private void refreshUploadUi(SobotProgress progress) {
        if (progress == null) {
            if (sobot_msgStatus != null) {
                sobot_msgStatus.setVisibility(View.GONE);
            }
            sobot_progress.setProgress(100);
            sobot_progress.setVisibility(View.GONE);
            st_tv_play.setVisibility(View.VISIBLE);
            return;
        }
        if (sobot_msgStatus == null) {
            return;
        }
        switch (progress.status) {
            case SobotProgress.NONE:
                sobot_msgStatus.setVisibility(View.GONE);
                st_tv_play.setVisibility(View.VISIBLE);
                sobot_progress.setProgress((int) (progress.fraction * 100));
                break;
            case SobotProgress.ERROR:
                st_tv_play.setVisibility(View.VISIBLE);
                sobot_msgStatus.setVisibility(View.VISIBLE);
                sobot_progress.setProgress(100);
                sobot_progress.setVisibility(View.GONE);
                break;
            case SobotProgress.FINISH:
                st_tv_play.setVisibility(View.VISIBLE);
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_progress.setProgress(100);
                sobot_progress.setVisibility(View.GONE);
                break;
            case SobotProgress.PAUSE:
            case SobotProgress.WAITING:
            case SobotProgress.LOADING:
                st_tv_play.setVisibility(View.GONE);
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_progress.setProgress((int) (progress.fraction * 100));
                sobot_progress.setVisibility(View.VISIBLE);
                break;
        }
    }

    private static class ListUploadListener extends SobotUploadListener {

        private VideoMessageHolder holder;

        ListUploadListener(Object tag, VideoMessageHolder holder) {
            super(tag);
            this.holder = holder;
        }

        @Override
        public void onStart(SobotProgress progress) {

        }

        @Override
        public void onProgress(SobotProgress progress) {
            if (tag == holder.getTag()) {
                holder.refreshUploadUi(progress);
            }
        }

        @Override
        public void onError(SobotProgress progress) {
            if (tag == holder.getTag()) {
                holder.refreshUploadUi(progress);
            }
        }

        @Override
        public void onFinish(CommonModelBase result, SobotProgress progress) {
            if (tag == holder.getTag()) {
                holder.refreshUploadUi(progress);
            }
        }

        @Override
        public void onRemove(SobotProgress progress) {

        }
    }
}
