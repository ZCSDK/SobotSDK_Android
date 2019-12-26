package com.sobot.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.widget.image.SobotRCImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxl on 2017/4/11.
 */

public class SobotPicListAdapter extends SobotBaseAdapter<ZhiChiUploadAppFileModelResult> {

    public static final int PIC = 1;
    public static final int ADD = 0;
    public static final int DEL = 2;


    ViewClickListener listener;

    public SobotPicListAdapter(Context context, List<ZhiChiUploadAppFileModelResult> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ZhiChiUploadAppFileModelResult message = list.get(position);
        SobotFileHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", "sobot_piclist_item"), null);
            viewHolder = new SobotFileHolder(context, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SobotFileHolder) convertView.getTag();
        }
        viewHolder.setPosition(position);
        viewHolder.setListener(listener);
        viewHolder.bindData(message);
        return convertView;
    }

    @Override
    public ZhiChiUploadAppFileModelResult getItem(int position) {
        if (position < 0 || position >= list.size()) {
            return null;
        }
        return list.get(position);
    }

    public void addData(ZhiChiUploadAppFileModelResult data) {
        if (list == null) {
            return;
        }
        int lastIndex = (list.size() - 1) < 0 ? 0 : list.size() - 1;
        list.add(lastIndex, data);
        if (list.size() >= 4) {
            ZhiChiUploadAppFileModelResult lastBean = list.get(lastIndex);
            if (lastBean != null && 0 == lastBean.getViewState()) {
                list.remove(lastIndex);
            }
        }
        restDataView();
    }

    public void addDatas(List<ZhiChiUploadAppFileModelResult> tmpList) {
        list.clear();
        list.addAll(tmpList);
        restDataView();
    }

    public void restDataView() {
        if (list.size() == 0) {
            ZhiChiUploadAppFileModelResult addFile = new ZhiChiUploadAppFileModelResult();
            addFile.setViewState(0);
            list.add(addFile);
        } else {
            int lastIndex = (list.size() - 1) < 0 ? 0 : list.size() - 1;
            ZhiChiUploadAppFileModelResult result = list.get(lastIndex);
            if (list.size() < 4 && result.getViewState() != 0) {
                ZhiChiUploadAppFileModelResult addFile = new ZhiChiUploadAppFileModelResult();
                addFile.setViewState(0);
                list.add(addFile);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<ZhiChiUploadAppFileModelResult> getPicList() {
        ArrayList<ZhiChiUploadAppFileModelResult> tmplist = new ArrayList<>();//所有图片的地址
        for (int i = 0; i < list.size(); i++) {
            ZhiChiUploadAppFileModelResult picFile = list.get(i);
            if (picFile.getViewState() != 0) {
                tmplist.add(picFile);
            }
        }
        return tmplist;
    }

    @Override
    public int getCount() {
        if (list.size() < 5) {
            return list.size();
        } else {
            return 4;
        }
    }


    private static class SobotFileHolder {
        private Context mContext;
        SobotRCImageView sobot_iv_pic;
        LinearLayout sobot_iv_pic_add_ll;
        ImageView sobot_iv_pic_add;
        private ImageView sobot_remove;
        private ViewClickListener listener;
        private int position;

        SobotFileHolder(Context context, View convertView) {
            this.mContext = context;
            sobot_iv_pic = (SobotRCImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_iv_pic"));
            sobot_iv_pic_add = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_iv_pic_add"));
            sobot_iv_pic_add_ll = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_iv_pic_add_ll"));
            sobot_remove = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_iv_pic_delete"));
        }

        public void setListener(ViewClickListener listener) {
            this.listener = listener;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        void bindData(ZhiChiUploadAppFileModelResult message) {
            if (message.getViewState() == 0) {
                sobot_iv_pic.setVisibility(View.GONE);
                sobot_iv_pic_add_ll.setVisibility(View.VISIBLE);
                sobot_remove.setVisibility(View.GONE);
            } else {
                sobot_iv_pic.setVisibility(View.VISIBLE);
                sobot_iv_pic_add_ll.setVisibility(View.GONE);
                sobot_remove.setVisibility(View.VISIBLE);
                SobotBitmapUtil.display(mContext, message.getFileLocalPath(), sobot_iv_pic, ResourceUtils
                        .getIdByName(mContext, "drawable", "sobot_default_pic"), ResourceUtils
                        .getIdByName(mContext, "drawable", "sobot_default_pic_err"));
            }

            sobot_iv_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.clickView(v, position, PIC);
                }
            });
            sobot_iv_pic_add_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.clickView(v, position, ADD);
                }
            });
            sobot_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.clickView(v, position, DEL);
                }
            });
        }
    }

    public void setOnClickItemViewListener(ViewClickListener listener) {
        this.listener = listener;
    }

    public interface ViewClickListener {
        void clickView(View view, int position, int type);
    }
}