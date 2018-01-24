package com.sobot.chat.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChooseCityActivity;
import com.sobot.chat.activity.SobotCusFieldActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotProvinInfo;
import com.sobot.chat.listener.ISobotCusField;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by jinxl on 2018/1/3.
 */
public class StCusFieldPresenter {

    /**
     * 获取要提交给接口的自定义字段的json
     *  留言接口使用
     * @param field
     * @return
     */
    public static String getSaveFieldVal(ArrayList<SobotFieldModel> field) {
        List<Map<String, String>> listModel = null;
        if (field != null && field.size() > 0) {
            listModel = new ArrayList<>();
            for (int i = 0; i < field.size(); i++) {
                Map<String, String> model = new HashMap<>();
                SobotCusFieldConfig cusFieldConfig = field.get(i).getCusFieldConfig();
                if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldId())
                        && !TextUtils.isEmpty(cusFieldConfig.getValue())) {
                    model.put("id", field.get(i).getCusFieldConfig().getFieldId());
                    model.put("value", field.get(i).getCusFieldConfig().getValue());
                    listModel.add(model);
                }
            }
        }
        if (listModel != null && listModel.size() > 0) {
            JSONArray jsonArray = new JSONArray(listModel);//把  List 对象  转成json数据
            return jsonArray.toString();
        }
        return null;
    }

    /**
     * 打开时间或日期选择器的逻辑
     * @param act
     * @param view
     * @param fieldType
     */
    public static void openTimePicker(Activity act,View view,int fieldType){
        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(view.getContext(), "id", "work_order_customer_date_text_click"));
        String content = textClick.getText().toString();
        Date date = null;
        if (!TextUtils.isEmpty(content)) {
            date = DateUtil.parse(content, fieldType == ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? DateUtil.DATE_FORMAT2 : DateUtil.DATE_FORMAT0);
        }
        KeyboardUtil.hideKeyboard(textClick);
        DateUtil.openTimePickerView(act, textClick, date, fieldType == ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
    }

    /**
     * 获取要提交给接口的自定义字段的json
     *  询前表单使用
     * @param field
     * @return
     */
    public static String getCusFieldVal(ArrayList<SobotFieldModel> field,final SobotProvinInfo.SobotProvinceModel finalData) {
        Map<String, String> tmpMap = new HashMap<>();
        if (field != null && field.size() > 0) {
            for (int i = 0; i < field.size(); i++) {
                SobotCusFieldConfig cusFieldConfig = field.get(i).getCusFieldConfig();
                if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldId())
                        && !TextUtils.isEmpty(cusFieldConfig.getValue())) {
                    tmpMap.put(field.get(i).getCusFieldConfig().getFieldId(),field.get(i).getCusFieldConfig().getValue());
                }
            }
        }
        if (finalData != null) {
            tmpMap.put("proviceId", finalData.provinceId);
            tmpMap.put("proviceName", finalData.provinceName);
            tmpMap.put("cityId", finalData.cityId);
            tmpMap.put("cityName", finalData.cityName);
            tmpMap.put("areaId", finalData.areaId);
            tmpMap.put("areaName", finalData.areaName);
        }
        if (tmpMap.size() > 0) {
            return GsonUtil.map2Json(tmpMap);
        }
        return null;
    }

    /**
     * 启动自定义字段下一级选择的逻辑
     *
     * @param act
     * @param cusFieldList
     */
    public static void startSobotCusFieldActivity(Activity act, SobotFieldModel cusFieldList) {
        SobotCusFieldConfig cusFieldConfig = cusFieldList.getCusFieldConfig();
        Intent intent = new Intent(act, SobotCusFieldActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", cusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        bundle.putSerializable("cusFieldList", cusFieldList);
        intent.putExtra("bundle", bundle);
        act.startActivityForResult(intent, cusFieldConfig.getFieldType());
    }

    /**
     * 启动城市选择的act
     * @param act
     * @param info 省的信息
     * @param cusField 字段的信息
     */
    public static void startChooseCityAct(Activity act, SobotProvinInfo info,SobotFieldModel cusField) {
        Intent intent = new Intent(act, SobotChooseCityActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_PROVININFO, info);
        SobotCusFieldConfig cusFieldConfig = cusField.getCusFieldConfig();
        if (cusFieldConfig != null) {
            bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD_ID, cusFieldConfig.getFieldId());
        }
        intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA, bundle);
        act.startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_CITY_INFO);
    }

    /**
     * 选择子集的回调
     * @param context
     * @param data
     * @param field
     * @param post_customer_field
     */
    public static void onStCusFieldActivityResult(Context context, Intent data, ArrayList<SobotFieldModel> field, ViewGroup post_customer_field) {
        if (data != null && "CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && -1 != data.getIntExtra("fieldType", -1)) {
            String value = data.getStringExtra("category_typeName");
            String id = data.getStringExtra("category_fieldId");
            String dataValue = data.getStringExtra("category_typeValue");
            if (field != null && !TextUtils.isEmpty(value) && !TextUtils.isEmpty(dataValue)) {

                for (int i = 0; i < field.size(); i++) {
                    SobotCusFieldConfig model = field.get(i).getCusFieldConfig();
                    if (model != null && model.getFieldId() != null && model.getFieldId().equals(id)) {
                        model.setChecked(true);
                        model.setValue(dataValue);
                        model.setId(id);
                        View view = post_customer_field.findViewWithTag(model.getFieldId());
                        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                        textClick.setText(value.endsWith(",") ? value.substring(0, value.length() - 1) : value);
                    }
                }
            }
        }
    }

    /**
     * 提交前将数据同步到最外层属性中
     * @param sobot_container
     * @param field
     */
    public static void formatCusFieldVal(Context context,ViewGroup sobot_container,List<SobotFieldModel> field) {
        if (field != null && field.size() != 0) {
            for (int j = 0; j < field.size(); j++) {
                if (field.get(j).getCusFieldConfig() == null) {
                    continue;
                }
                View view = sobot_container.findViewWithTag(field.get(j).getCusFieldConfig().getFieldId());
                if (view != null) {
                    if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText singleContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_single"));
                        field.get(j).getCusFieldConfig().setValue(singleContent.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText moreContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_more_content"));
                        field.get(j).getCusFieldConfig().setValue(moreContent.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == field.get(j).getCusFieldConfig().getFieldType()
                            || ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE== field.get(j).getCusFieldConfig().getFieldType() ) {
                        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                        field.get(j).getCusFieldConfig().setValue(textClick.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText numberContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_number"));
                        field.get(j).getCusFieldConfig().setValue(numberContent.getText() + "");
                    }
                }
            }
        }
    }

    //创建工单自定义字段
    public static void addWorkOrderCusFields(final Context context, final ArrayList<SobotFieldModel> cusFieldList, ViewGroup containerLayout, final ISobotCusField cusFieldInterface) {
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                int size = cusFieldList.size();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    final SobotFieldModel model = cusFieldList.get(i);
                    final SobotCusFieldConfig cusFieldConfig = model.getCusFieldConfig();
                    if (cusFieldConfig == null){
                        continue;
                    }
                    View view = View.inflate(context, ResourceUtils.getIdByName(context, "layout", "sobot_post_msg_cusfield_list_item"), null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_bootom_line"));
                    if (cusFieldList.size() == 1 ||  i == (size -1)) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_more_relativelayout"));
                    TextView fieldMoreName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_more_text_lable"));
                    final EditText moreContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_more_content"));

                    RelativeLayout ll_text_layout = (RelativeLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text"));
                    TextView fieldName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_lable"));
                    final TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                    EditText fieldValue = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_content"));
                    EditText numberContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_number"));
                    final EditText singleContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_single"));
                    ImageView fieldImg = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_img"));

                    if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }
                        singleContent.setSingleLine(true);
                        singleContent.setMaxEms(11);
                        singleContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldMoreName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldMoreName.setText(cusFieldConfig.getFieldName());
                        }
                        moreContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        //设置EditText的显示方式为多行文本输入
                        moreContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        //文本显示的位置在EditText的最上方
                        moreContent.setGravity(Gravity.TOP);
                        //改变默认的单行模式
                        moreContent.setSingleLine(false);
                        //水平滚动设置为False
                        moreContent.setHorizontallyScrolling(false);

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldConfig.getFieldName());
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.VISIBLE);
                        numberContent.setSingleLine(true);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }


                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE == cusFieldConfig.getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cusFieldInterface != null) {
                                cusFieldInterface.onClickCusField(v,cusFieldConfig.getFieldType(),model);
                            }
                        }
                    });
                    containerLayout.addView(view);
                }
            }
        }
    }
}
