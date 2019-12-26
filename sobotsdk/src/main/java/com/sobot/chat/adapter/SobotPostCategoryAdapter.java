package com.sobot.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotTypeModel;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class SobotPostCategoryAdapter extends SobotBaseAdapter<SobotTypeModel> {

    private Context mContext;
    private ViewHolder myViewHolder;

    public SobotPostCategoryAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(mContext, ResourceUtils.getIdByName(mContext, "layout", "sobot_activity_post_category_items"),null);
            myViewHolder = new ViewHolder(mContext,convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }

        myViewHolder.categoryTitle.setText(list.get(position).getTypeName());
        if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_NO == list.get(position).getNodeFlag()){
            myViewHolder.categoryIshave.setVisibility(View.GONE);
        } else {
            myViewHolder.categoryIshave.setVisibility(View.VISIBLE);
            myViewHolder.categoryIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_right_arrow_icon"));
        }

        if (list.get(position).isChecked()){
            myViewHolder.categoryIshave.setVisibility(View.VISIBLE);
            myViewHolder.categoryIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_work_order_selected_mark"));
        }

        if (list.size() >= 2){
            if (position == list.size() -1){
                myViewHolder.work_order_category_line.setVisibility(View.GONE);
            } else {
                myViewHolder.work_order_category_line.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.work_order_category_line.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder{
        private TextView categoryTitle;
        private ImageView categoryIshave;
        private View work_order_category_line;

        ViewHolder(Context context,View view){
            categoryTitle = (TextView)view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_title"));
            categoryIshave = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_ishave"));
            work_order_category_line = view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_category_line"));
        }
    }
}