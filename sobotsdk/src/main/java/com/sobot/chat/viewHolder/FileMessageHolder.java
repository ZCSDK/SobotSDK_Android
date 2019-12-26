package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.SobotFileDetailActivity;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.core.http.model.SobotProgress;
import com.sobot.chat.core.http.upload.SobotUpload;
import com.sobot.chat.core.http.upload.SobotUploadListener;
import com.sobot.chat.core.http.upload.SobotUploadTask;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.SobotSectorProgressView;

/**
 * 文件消息
 * Created by jinxl on 2018/11/13.
 */
public class FileMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private SobotSectorProgressView sobot_progress;
    private TextView sobot_file_name;
    private TextView sobot_file_size;
    private ImageView sobot_msgStatus;
    private RelativeLayout sobot_ll_file_container;

    private ZhiChiMessageBase mData;
    private String mTag;
    private int mResNetError;
    private int mResRemove;

    public FileMessageHolder(Context context, View convertView) {
        super(context, convertView);
        sobot_progress = (SobotSectorProgressView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_progress"));
        sobot_file_name = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_file_name"));
        sobot_file_size = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_file_size"));
        sobot_msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgStatus"));
        sobot_ll_file_container = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_file_container"));
        mResNetError = ResourceUtils.getIdByName(context, "drawable", "sobot_re_send_selector");
        mResRemove = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_remove");
        if (sobot_msgStatus != null) {
            sobot_msgStatus.setOnClickListener(this);
        }
        sobot_ll_file_container.setOnClickListener(this);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        mData = message;
        if (message.getAnswer() != null && message.getAnswer().getCacheFile() != null) {
            SobotCacheFile cacheFile = message.getAnswer().getCacheFile();
            sobot_file_name.setText(cacheFile.getFileName());
            sobot_file_size.setText(cacheFile.getFileSize());
            SobotBitmapUtil.display(mContext, ChatUtils.getFileIcon(mContext, cacheFile.getFileType()), sobot_progress);
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
            if (sobot_ll_file_container == v) {
                if (mData.getAnswer() != null && mData.getAnswer().getCacheFile() != null) {
                    // 打开详情页面
                    Intent intent = new Intent(mContext, SobotFileDetailActivity.class);
                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, mData.getAnswer().getCacheFile());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }

            if (sobot_msgStatus == v) {
                if (sobot_msgStatus.isSelected()) {
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
                } else {
                    //取消
                    if (SobotUpload.getInstance().hasTask(mTag)) {
                        SobotUploadTask uploadTask = SobotUpload.getInstance().getTask(mTag);
                        uploadTask.remove();
                    }
                    notifyFileTaskRemove();
                }
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
                msgProgressBar.setVisibility(View.GONE);
            }
           // sobot_progress.setProgress(100);
            return;
        }
        if (sobot_msgStatus == null) {
            return;
        }
        switch (progress.status) {
            case SobotProgress.NONE:
                sobot_msgStatus.setVisibility(View.GONE);
                msgProgressBar.setVisibility(View.GONE);
               // sobot_progress.setProgress(progress.fraction * 100);
                break;
            case SobotProgress.ERROR:
                sobot_msgStatus.setVisibility(View.VISIBLE);
                sobot_msgStatus.setBackgroundResource(mResNetError);
                sobot_msgStatus.setSelected(true);
              //  sobot_progress.setProgress(100);
                msgProgressBar.setVisibility(View.GONE);
                break;
            case SobotProgress.FINISH:
                sobot_msgStatus.setVisibility(View.GONE);
             //   sobot_progress.setProgress(100);
                msgProgressBar.setVisibility(View.GONE);
                break;
            case SobotProgress.PAUSE:
            case SobotProgress.WAITING:
            case SobotProgress.LOADING:
                msgProgressBar.setVisibility(View.VISIBLE);
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_msgStatus.setBackgroundResource(mResRemove);
                sobot_msgStatus.setSelected(false);
            //    sobot_progress.setProgress(progress.fraction * 100);
                break;
        }
    }

    private static class ListUploadListener extends SobotUploadListener {

        private FileMessageHolder holder;

        ListUploadListener(Object tag, FileMessageHolder holder) {
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
