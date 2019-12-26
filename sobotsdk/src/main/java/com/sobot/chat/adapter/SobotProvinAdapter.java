package com.sobot.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotProvinInfo;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

/**
 * @author Created by jinxl on 2018/1/8.
 */
public class SobotProvinAdapter extends SobotBaseAdapter<SobotProvinInfo.SobotProvinceModel> {

    private Context mContext;
    private SobotProvinAdapter.ViewHolder myViewHolder;

    public SobotProvinAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, ResourceUtils.getIdByName(mContext, "layout", "sobot_activity_post_category_items"), null);
            myViewHolder = new SobotProvinAdapter.ViewHolder(mContext, convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (SobotProvinAdapter.ViewHolder) convertView.getTag();
        }
        myViewHolder.bindData(list.get(position));

        if (list.size() >= 2) {
            if (position == list.size() - 1) {
                myViewHolder.work_order_category_line.setVisibility(View.GONE);
            } else {
                myViewHolder.work_order_category_line.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.work_order_category_line.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        private TextView categoryTitle;
        private ImageView categoryIshave;
        private View work_order_category_line;
        Context mContext;

        ViewHolder(Context context, View view) {
            mContext = context;
            categoryTitle = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_title"));
            categoryIshave = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_ishave"));
            work_order_category_line = view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_line"));
        }

        void bindData(SobotProvinInfo.SobotProvinceModel data) {
            if (data == null) {
                return;
            }
            switch (data.level) {
                case 0:
                    categoryTitle.setText(data.provinceName);
                    break;
                case 1:
                    categoryTitle.setText(data.cityName);
                    break;
                case 2:
                    categoryTitle.setText(data.areaName);
                    break;
            }

            if (data.nodeFlag) {
                categoryIshave.setVisibility(View.VISIBLE);
                categoryIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_right_arrow_icon"));
            } else {
                categoryIshave.setVisibility(View.GONE);
            }


            if (data.isChecked) {
                categoryIshave.setVisibility(View.VISIBLE);
                categoryIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_work_order_selected_mark"));
            }


        }
    }
}