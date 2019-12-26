package com.sobot.chat.widget.attachment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.api.model.SobotFileModel;
import com.sobot.chat.imageloader.SobotImageLoader;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;

/**
 * 自定义 附件 View
 */
public class AttachmentView extends FrameLayout {

    private int position;
    private SobotFileModel fileModel;


    private Context mContext;
    private View rootView;

    private String fileUrl;
    private RelativeLayout sobotAttachmentRootView;
    private TextView sobotFileName;
    private String fileName;
    private ImageView sobotFileTypeIcon;
    private TextView sobotFilePreview;

    private ImageView imageView;

    private Listener listener;
    private int type;


    public AttachmentView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AttachmentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AttachmentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = inflate(context, ResourceUtils.getResLayoutId(context, "layout_attachment_view"), this);
        sobotAttachmentRootView = (RelativeLayout) findViewById(ResourceUtils.getResId(context, "sobot_attachment_root_view"));
        sobotFileName = (TextView) findViewById(ResourceUtils.getResId(context, "sobot_file_name"));
        sobotFileTypeIcon = (ImageView) findViewById(ResourceUtils.getResId(context, "sobot_file_type_icon"));
        sobotFilePreview = (TextView) findViewById(ResourceUtils.getResId(context, "sobot_file_download"));
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }

                if (type == FileTypeConfig.MSGTYPE_FILE_MP4) {
                    listener.previewMp4(fileModel, position);
                } else if (type == FileTypeConfig.MSGTYPE_FILE_PIC) {
                    listener.previewPic(fileUrl, fileName, position);
                } else {
                    listener.downFileLister(fileModel, position);
                }
            }
        });

        imageView = (ImageView) findViewById(ResourceUtils.getResId(context, "sobot_file_image_view"));

    }

    public void setFileName(CharSequence string) {
        fileName = string.toString();
        if (sobotFileName != null) {
            sobotFileName.setText(string);
        }

    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;

    }

    public void setFileTypeIcon(int type) {
        this.type = type;
        if (sobotFileTypeIcon == null) {
            return;
        }
        if (type == FileTypeConfig.MSGTYPE_FILE_PIC) {
            imageView.setVisibility(VISIBLE);
            sobotAttachmentRootView.setVisibility(GONE);
            SobotBitmapUtil.display(mContext, fileUrl, imageView);

//        } else if (type == FileTypeConfig.MSGTYPE_FILE_MP4) {
//            imageView.setVisibility(VISIBLE);
//            sobotAttachmentRootView.setVisibility(GONE);
//            SobotBitmapUtil.display(mContext, fileUrl, imageView);

        } else {
            imageView.setVisibility(GONE);
            sobotAttachmentRootView.setVisibility(VISIBLE);
            sobotFileTypeIcon.setImageResource(FileTypeConfig.getFileIcon(mContext, type));
        }


    }

    public void setFileModel(SobotFileModel fileModel) {
        this.fileModel = fileModel;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setFileNameColor(int color) {
        if (sobotFileName != null) {
            sobotFileName.setTextColor(color);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface Listener {
        void downFileLister(SobotFileModel fileModel, int position);

        void previewMp4(SobotFileModel fileModel, int position);

        void previewPic(String fileUrl, String fileName, int position);
    }


}
