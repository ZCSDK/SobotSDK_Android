package com.sobot.chat.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.ResourceUtils;

import java.io.File;
import java.util.List;

/**
 * 文件选择适配器
 *
 * @author Created by jinxl on 2018/1/8.
 */
public class SobotFilesAdapter extends SobotBaseAdapter<File> {

    private Context mContext;

    private File mCheckedFile;

    private static final String[] layoutRes = {
            "sobot_choose_file_item",//文件类型布局文件
            "sobot_choose_dir_item",//文件夹类型布局文件
    };

    //文件类型
    public static final int MSG_TYPE_FILE = 0;
    //文件夹类型
    public static final int MSG_TYPE_DIR = 1;

    public SobotFilesAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File data = list.get(position);
        if (data != null) {
            int itemType = getItemViewType(position);
            convertView = initView(convertView, itemType, position, data);
            BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
            holder.bindData(data);
        }
        return convertView;
    }

    private View initView(View convertView, int itemType, int position, final File data) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
            BaseViewHolder holder;
            switch (itemType) {
                case MSG_TYPE_FILE: {
                    holder = new FileViewHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_DIR: {
                    holder = new DirViewHolder(context, convertView);
                    break;
                }
                default:
                    holder = new FileViewHolder(context, convertView);
                    break;
            }
            convertView.setTag(holder);
        }
        return convertView;
    }

    /**
     * @return 返回有多少种UI布局样式
     */
    @Override
    public int getViewTypeCount() {
        if (layoutRes.length > 0) {
            return layoutRes.length;
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        File data = (File) getItem(position);
        return data.isDirectory() ? MSG_TYPE_DIR : MSG_TYPE_FILE;
    }


    public boolean isCheckedFile(File file) {
        return mCheckedFile != null && mCheckedFile.equals(file);
    }

    public void setCheckedFile(File file) {
        mCheckedFile = file;
    }

    public File getCheckedFile() {
        return mCheckedFile;
    }

    static abstract class BaseViewHolder {
        Context mContext;

        BaseViewHolder(Context context, View view) {
            mContext = context;
        }

        abstract void bindData(File data);
    }

    class FileViewHolder extends BaseViewHolder {
        private TextView sobot_tv_radioBtn;
        private TextView sobot_tv_descripe;
        private TextView sobot_tv_name;

        FileViewHolder(Context context, View view) {
            super(context, view);
            sobot_tv_descripe = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_descripe"));
            sobot_tv_name = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_name"));
            sobot_tv_radioBtn = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_radioBtn"));
        }

        void bindData(File data) {
            //显示文件
            sobot_tv_radioBtn.setSelected(!(mCheckedFile == null || !mCheckedFile.equals(data)));
            String descripe = DateUtil.toDate(data.lastModified(), DateUtil.DATE_FORMAT) + "  " + Formatter.formatFileSize(mContext, data.length());
            sobot_tv_descripe.setText(descripe);
            sobot_tv_name.setText(data.getName());
        }
    }

    static class DirViewHolder extends BaseViewHolder {
        private TextView sobot_tv_name;

        DirViewHolder(Context context, View view) {
            super(context, view);
            sobot_tv_name = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_name"));
        }

        void bindData(File data) {
            String name = data.getName();
            sobot_tv_name.setText(name);
        }
    }
}