package com.sobot.chat.widget.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.sobot.chat.utils.ResourceUtils;

public class FileTypeConfig {
    public static final int MSGTYPE_FILE_RAR = 19;
    public static final int MSGTYPE_FILE_DOC = 13;
    public static final int MSGTYPE_FILE_PPT = 14;
    public static final int MSGTYPE_FILE_XLS = 15;
    public static final int MSGTYPE_FILE_PDF = 16;
    public static final int MSGTYPE_FILE_MP3 = 17;
    public static final int MSGTYPE_FILE_MP4 = 18;
    public static final int MSGTYPE_FILE_TXT = 20;
    public static final int MSGTYPE_FILE_PIC=1;
    public static final int MSGTYPE_FILE_OTHER = 10;


    public static int getFileType(String endWith) {
        if (TextUtils.isEmpty(endWith)){
            return MSGTYPE_FILE_OTHER;
        }
        switch (endWith) {
            case "zip":
            case "rar":
                return MSGTYPE_FILE_RAR;
            case "doc":
            case "docx":
                return MSGTYPE_FILE_DOC;
            case "ppt":
            case "pptx":
                return MSGTYPE_FILE_PPT;
            case "xls":
            case "xlsx":
                return MSGTYPE_FILE_XLS;
            case "pdf":
                return MSGTYPE_FILE_PDF;
            case "mp3":
                return MSGTYPE_FILE_MP3;
            case "mp4":
                return MSGTYPE_FILE_MP4;
            case "txt":
                return MSGTYPE_FILE_TXT;
            case "jpg":
            case "png":
            case "gif":
                return MSGTYPE_FILE_PIC;

            default:
                return MSGTYPE_FILE_OTHER;
        }

    }

    /**
     * 根据文件类型获取文件icon
     *
     * @param context
     * @param fileType
     * @return
     */
    public static int getFileIcon(Context context, int fileType) {
        int tmpResId;
        if (context == null) {
            return 0;
        }
        switch (fileType) {
            case MSGTYPE_FILE_DOC:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_doc");
                break;
            case MSGTYPE_FILE_PPT:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_ppt");
                break;
            case MSGTYPE_FILE_XLS:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_xls");
                break;
            case MSGTYPE_FILE_PDF:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_pdf");
                break;
            case MSGTYPE_FILE_MP3:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_mp3");
                break;
            case MSGTYPE_FILE_MP4:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_mp4");
                break;
            case MSGTYPE_FILE_RAR:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_rar");
                break;
            case MSGTYPE_FILE_TXT:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_txt");
                break;
            case MSGTYPE_FILE_OTHER:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_unknow");
                break;
            default:
                tmpResId = ResourceUtils.getIdByName(context, "drawable", "sobot_icon_file_unknow");
                break;
        }
        return tmpResId;
    }

}
