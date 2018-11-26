package com.sobot.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotCusFieldDataInfo;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */

public class SobotCusFieldAdapter extends SobotBaseAdapter<SobotCusFieldDataInfo> {

    private MyViewHolder myViewHolder;
    private Context mContext;
    private int fieldType;

    public SobotCusFieldAdapter(Context context, List<SobotCusFieldDataInfo> list, int fieldType) {
        super(context, list);
        this.mContext = context;
        this.fieldType = fieldType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(mContext, ResourceUtils.getIdByName(mContext, "layout", "sobot_activity_cusfield_listview_items"), null);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        myViewHolder.categorySmallTitle.setText(list.get(position).getDataName());

        if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType){
            myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            myViewHolder.categorySmallCheckBox.setVisibility(View.VISIBLE);
            if (list.get(position).isChecked()){
                myViewHolder.categorySmallCheckBox.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_post_category_checkbox_pressed"));
            } else {
                myViewHolder.categorySmallCheckBox.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_post_category_checkbox_normal"));
            }
        } else {
            myViewHolder.categorySmallCheckBox.setVisibility(View.GONE);
            if (list.get(position).isChecked()){
                myViewHolder.categorySmallIshave.setVisibility(View.VISIBLE);
                myViewHolder.categorySmallIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_work_order_selected_mark"));
            } else {
                myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            }
        }

        if (list.size() >= 2){
            if (position == list.size() -1){
                myViewHolder.categorySmallline.setVisibility(View.GONE);
            } else {
                myViewHolder.categorySmallline.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.categorySmallline.setVisibility(View.GONE);
        }

        return convertView;
    }

    class MyViewHolder{

        private TextView categorySmallTitle;
        private ImageView categorySmallIshave;
        private ImageView categorySmallCheckBox;
        private View categorySmallline;

        MyViewHolder(View view){
            categorySmallTitle = (TextView)view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_title"));
            categorySmallIshave = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_ishave"));
            categorySmallCheckBox = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_checkbox"));
            categorySmallline = view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_line"));
        }
    }
}