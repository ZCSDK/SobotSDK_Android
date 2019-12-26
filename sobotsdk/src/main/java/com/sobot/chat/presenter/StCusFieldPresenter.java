package com.sobot.chat.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.R;
import com.sobot.chat.activity.SobotChooseCityActivity;
import com.sobot.chat.activity.SobotCusFieldActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotProvinInfo;
import com.sobot.chat.listener.ISobotCusField;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by jinxl on 2018/1/3.
 */
public class StCusFieldPresenter {

    /**
     * 获取要提交给接口的自定义字段的json
     * 留言接口使用
     *
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
                if (cusFieldConfig != null && !StringUtils.isEmpty(cusFieldConfig.getFieldId())
                        && !StringUtils.isEmpty(cusFieldConfig.getValue())) {
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
     *
     * @param act
     * @param view
     * @param fieldType
     */
    public static void openTimePicker(Activity act, View view, int fieldType) {
        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(view.getContext(), "id", "work_order_customer_date_text_click"));
        String content = textClick.getText().toString();
        Date date = null;
        if (!StringUtils.isEmpty(content)) {
            date = DateUtil.parse(content, fieldType == ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? DateUtil.DATE_FORMAT2 : DateUtil.DATE_FORMAT0);
        }
        KeyboardUtil.hideKeyboard(textClick);
        DateUtil.openTimePickerView(act, view, textClick, date, fieldType == ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
    }

    /**
     * 获取要提交给接口的自定义字段的json
     * 询前表单使用
     *
     * @param field
     * @return
     */
    public static String getCusFieldVal(ArrayList<SobotFieldModel> field, final SobotProvinInfo.SobotProvinceModel finalData) {
        Map<String, String> tmpMap = new HashMap<>();
        if (field != null && field.size() > 0) {
            for (int i = 0; i < field.size(); i++) {
                SobotCusFieldConfig cusFieldConfig = field.get(i).getCusFieldConfig();
                if (cusFieldConfig != null && !StringUtils.isEmpty(cusFieldConfig.getFieldId())
                        && !StringUtils.isEmpty(cusFieldConfig.getValue())) {
                    tmpMap.put(field.get(i).getCusFieldConfig().getFieldId(), field.get(i).getCusFieldConfig().getValue());
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
        startSobotCusFieldActivity(act, null, cusFieldList);
    }

    /**
     * 启动自定义字段下一级选择的逻辑
     *
     * @param act
     * @param cusFieldList
     */
    public static void startSobotCusFieldActivity(Activity act, Fragment fragment, SobotFieldModel cusFieldList) {
        SobotCusFieldConfig cusFieldConfig = cusFieldList.getCusFieldConfig();
        Intent intent = new Intent(act, SobotCusFieldActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", cusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        bundle.putSerializable("cusFieldList", cusFieldList);
        intent.putExtra("bundle", bundle);
        if (fragment != null) {
            fragment.startActivityForResult(intent, cusFieldConfig.getFieldType());
        } else {
            act.startActivityForResult(intent, cusFieldConfig.getFieldType());
        }
    }

    /**
     * 启动城市选择的act
     *
     * @param act
     * @param info     省的信息
     * @param cusField 字段的信息
     */
    public static void startChooseCityAct(Activity act, SobotProvinInfo info, SobotFieldModel cusField) {
        Intent intent = new Intent(act, SobotChooseCityActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cusFieldConfig", cusField.getCusFieldConfig());
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
     *
     * @param context
     * @param data
     * @param field
     * @param post_customer_field
     */
    public static void onStCusFieldActivityResult(Context context, Intent data, ArrayList<SobotFieldModel> field, ViewGroup post_customer_field) {
        if (data != null && "CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && -1 != data.getIntExtra("fieldType", -1)) {
            String value = data.getStringExtra("category_typeName");
            String id = data.getStringExtra("category_fieldId");
            if ("null".equals(id)||TextUtils.isEmpty(id)){
                return;
            }
            String dataValue = data.getStringExtra("category_typeValue");
            if (field != null && !StringUtils.isEmpty(value) && !StringUtils.isEmpty(dataValue)) {
                for (int i = 0; i < field.size(); i++) {
                    SobotCusFieldConfig model = field.get(i).getCusFieldConfig();
                    if (model != null && model.getFieldId() != null && model.getFieldId().equals(id)) {
                        model.setChecked(true);
                        model.setValue(dataValue);
                        model.setId(id);
                        View view = post_customer_field.findViewWithTag(model.getFieldId());
                        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                        textClick.setText(value.endsWith(",") ? value.substring(0, value.length() - 1) : value);
                        TextView fieldName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_lable"));
                        LinearLayout work_order_customer_field_ll = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_ll"));
                        work_order_customer_field_ll.setVisibility(View.VISIBLE);
                        fieldName.setTextColor(Color.parseColor("#ACB5C4"));
                        fieldName.setTextSize(12);
                    }
                }
            } else {
                //还原样式
                if (StringUtils.isEmpty(dataValue)) {
                    for (int i = 0; i < field.size(); i++) {
                        //清空上次选中
                        SobotCusFieldConfig model = field.get(i).getCusFieldConfig();
                        if (model != null && model.getFieldId() != null && model.getFieldId().equals(id)) {
                            model.setChecked(false);
                            model.setValue(dataValue);
                            model.setId(id);
                        }
                    }
                }
                View view = post_customer_field.findViewWithTag(id);
                if (view != null) {
                    TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                    textClick.setText(value.endsWith(",") ? value.substring(0, value.length() - 1) : value);
                    TextView fieldName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_lable"));
                    LinearLayout work_order_customer_field_ll = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_ll"));
                    work_order_customer_field_ll.setVisibility(View.GONE);
                    fieldName.setTextColor(Color.parseColor("#515A7C"));
                    fieldName.setTextSize(14);
                }
            }
        }
    }

    /**
     * 提交前将数据同步到最外层属性中
     *
     * @param sobot_container
     * @param field
     * @return String 自定义表单校验结果:为空,可以提交;不为空,说明自定义字段校验不通过，不能提交留言表单;
     */
    public static String formatCusFieldVal(Context context, ViewGroup sobot_container, List<SobotFieldModel> field) {
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
                        if (StringUtils.isNumber(field.get(j).getCusFieldConfig().getLimitOptions()) && field.get(j).getCusFieldConfig().getLimitOptions().contains("7")) {
                            if (!ScreenUtils.isEmail(singleContent.getText().toString().trim())) {
                                return field.get(j).getCusFieldConfig().getFieldName() + ResourceUtils.getResString(context,"sobot_input_type_err_email");
                            }
                        }
                        if (StringUtils.isNumber(field.get(j).getCusFieldConfig().getLimitOptions()) && field.get(j).getCusFieldConfig().getLimitOptions().contains("8")) {
                            if (!ScreenUtils.isMobileNO(singleContent.getText().toString().trim())) {
                                return field.get(j).getCusFieldConfig().getFieldName() + ResourceUtils.getResString(context,"sobot_input_type_err_phone");
                            }
                        }
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText moreContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_more_content"));
                        field.get(j).getCusFieldConfig().setValue(moreContent.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == field.get(j).getCusFieldConfig().getFieldType()
                            || ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                        field.get(j).getCusFieldConfig().setValue(textClick.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText numberContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_number"));
                        field.get(j).getCusFieldConfig().setValue(numberContent.getText() + "");
                        if (StringUtils.isNumber(field.get(j).getCusFieldConfig().getLimitOptions()) && field.get(j).getCusFieldConfig().getLimitOptions().contains("3")) {
                            if (!StringUtils.isNumber(numberContent.getText().toString().trim())) {
                                return field.get(j).getCusFieldConfig().getFieldName() + ResourceUtils.getResString(context,"sobot_input_type_err");
                            }
                        }
                    }
                }
            }
        }
        return "";
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
                    if (cusFieldConfig == null) {
                        continue;
                    }
                    View view = View.inflate(context, ResourceUtils.getIdByName(context, "layout", "sobot_post_msg_cusfield_list_item"), null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_bootom_line"));
//                    if (cusFieldList.size() == 1 || i == (size - 1)) {
//                        bottomLine.setVisibility(View.GONE);
//                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
//                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_more_relativelayout"));
                    final TextView fieldMoreName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_more_text_lable"));
                    final TextView editHintLabel2 = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_edit_hint_text_label_2"));
                    editHintLabel2.setVisibility(View.GONE);
                    final EditText moreContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_more_content"));
                    moreContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                if (StringUtils.isEmpty(moreContent.getText().toString().trim())) {
                                    fieldMoreName.setTextSize(14);
                                    fieldMoreName.setTextColor(Color.parseColor("#515A7C"));
                                    moreContent.setVisibility(View.GONE);
                                    editHintLabel2.setVisibility(View.VISIBLE);
                                }
                            } else {
                                fieldMoreName.setTextColor(Color.parseColor("#ACB5C4"));
                                fieldMoreName.setTextSize(12);
                                editHintLabel2.setVisibility(View.GONE);
                                moreContent.setVisibility(View.VISIBLE);

                            }
                        }
                    });


                    LinearLayout ll_text_layout = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text"));
                    final TextView fieldName = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_lable"));
                    final TextView editHintLabel1 = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_edit_hint_text_label"));
                    editHintLabel1.setVisibility(View.GONE);

                    final TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_date_text_click"));
                    EditText fieldValue = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_content"));
                    final EditText numberContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_number"));
                    final EditText singleContent = (EditText) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_single"));
                    ImageView fieldImg = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_text_img"));
                    final LinearLayout work_order_customer_field_ll = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "work_order_customer_field_ll"));


                    if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldConfig.getFieldType()) {//单行文本
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        editHintLabel1.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        if (1 == cusFieldConfig.getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldConfig.getFieldName() + "<font color='#f9676f'>&nbsp;*</font>"));
                        } else {
                            fieldName.setText(cusFieldConfig.getFieldName());
                        }

                        if (!StringUtils.isEmpty(cusFieldConfig.getLimitChar())) {
                            singleContent.setMaxLines(Integer.parseInt(cusFieldConfig.getLimitChar()));
                        }
                        singleContent.setSingleLine(true);
                        singleContent.setMaxEms(11);
                        singleContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        //限制方式  1禁止输入空格   2 禁止输入小数点  3 小数点后只允许2位  4 禁止输入特殊字符  5只允许输入数字 6最多允许输入字符  7判断邮箱格式  8判断手机格式
                        if (!StringUtils.isEmpty(cusFieldConfig.getLimitOptions())) {
                            if (cusFieldConfig.getLimitOptions().contains("6")) {
                                if (!StringUtils.isEmpty(cusFieldConfig.getLimitChar()))
                                    singleContent.setMaxLines(Integer.parseInt(cusFieldConfig.getLimitChar()));
                            }
                            if (cusFieldConfig.getLimitOptions().contains("5")) {
                                singleContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                            }
                            if (cusFieldConfig.getLimitOptions().contains("7")) {
                                singleContent.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            }
                            if (cusFieldConfig.getLimitOptions().contains("8")) {
                                singleContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                            }

                            singleContent.addTextChangedListener(new TextWatcher() {
                                private CharSequence temp;

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    temp = s;
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (s.length() == 0)
                                        return;
                                    if (cusFieldConfig.getLimitOptions().contains("6")) {
                                        if (!StringUtils.isEmpty(cusFieldConfig.getLimitChar()) && temp.length() > Integer.parseInt(cusFieldConfig.getLimitChar())) {
                                            ToastUtil.showCustomToast(context, cusFieldConfig.getFieldName() + ResourceUtils.getResString(context, "sobot_only_can_write") + Integer.parseInt(cusFieldConfig.getLimitChar()) + ResourceUtils.getResString(context, "sobot_char_length"));
                                            s.delete(temp.length() - 1, temp.length());
                                        }
                                    }
                                    if (cusFieldConfig.getLimitOptions().contains("4")) {
                                        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher match = pattern.matcher(s);
                                        boolean b = match.matches();
                                        if (!b) {
                                            ToastUtil.showCustomToast(context, cusFieldConfig.getFieldName() + ResourceUtils.getResString(context, "sobot_only_can_write") + ResourceUtils.getResString(context, "sobot_number_english_china"));
                                            int ss = temp.length();
                                            s.delete(temp.length() - 1, temp.length());
                                        }
                                    }

                                }
                            });
                        }

                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldConfig.getFieldType()) {//多行文本
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        editHintLabel2.setVisibility(View.VISIBLE);
                        moreContent.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
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
                        editHintLabel1.setVisibility(View.VISIBLE);
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
                        //限制方式  1禁止输入空格   2 禁止输入小数点  3 小数点后只允许2位  4 禁止输入特殊字符  5只允许输入数字 6最多允许输入字符  7判断邮箱格式  8判断手机格式
                        if (!StringUtils.isEmpty(cusFieldConfig.getLimitOptions()) && "[3]".equals(cusFieldConfig.getLimitOptions())) {
                            numberContent.setInputType(InputType.TYPE_CLASS_NUMBER | 8194);
                            numberContent.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before,
                                                          int count) {
                                    if (s.toString().contains(".")) {
                                        if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                                            s = s.toString().subSequence(0,
                                                    s.toString().indexOf(".") + 3);
                                            numberContent.setText(s);
                                            numberContent.setSelection(s.length());
                                        }
                                    }
                                    if (s.toString().trim().substring(0).equals(".")) {
                                        s = "0" + s;
                                        numberContent.setText(s);
                                        numberContent.setSelection(2);
                                    }

                                    if (s.toString().startsWith("0")
                                            && s.toString().trim().length() > 1) {
                                        if (!s.toString().substring(1, 2).equals(".")) {
                                            numberContent.setText(s.subSequence(0, 1));
                                            numberContent.setSelection(1);
                                            return;
                                        }
                                    }
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count,
                                                              int after) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    // TODO Auto-generated method stub

                                }

                            });

                        } else {
                            numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                        }

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

                            if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE==cusFieldConfig.getFieldType()){//多行文本
                                editHintLabel2.setVisibility(View.GONE);
                                moreContent.setVisibility(View.VISIBLE);

                                moreContent.setFocusableInTouchMode(true);
                                moreContent.setFocusable(true);
                                moreContent.requestFocus();
                            }else {

                                for (int m = 0; m < work_order_customer_field_ll.getChildCount(); m++) {
                                    if (work_order_customer_field_ll.getChildAt(m) instanceof EditText && work_order_customer_field_ll.getChildAt(m).getVisibility() == View.VISIBLE) {
                                        work_order_customer_field_ll.setVisibility(View.VISIBLE);
                                        fieldName.setTextColor(Color.parseColor("#ACB5C4"));
                                        fieldName.setTextSize(12);
                                        editHintLabel1.setVisibility(View.GONE);
                                        final EditText et = (EditText) work_order_customer_field_ll.getChildAt(m);
                                        et.setFocusable(true);
                                        KeyboardUtil.showKeyboard(et);
                                        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (!hasFocus) {
                                                    if (StringUtils.isEmpty(et.getText().toString().trim())) {
                                                        fieldName.setTextSize(14);
                                                        fieldName.setTextColor(Color.parseColor("#515A7C"));
                                                        work_order_customer_field_ll.setVisibility(View.GONE);
                                                        //KeyboardUtil.hideKeyboard(et);
                                                        editHintLabel1.setVisibility( View.VISIBLE);
                                                    }

                                                }else{
                                                    editHintLabel1.setVisibility( View.GONE );
                                                }

                                            }
                                        });
                                    }
                                }
                            }

                            if (cusFieldInterface != null) {
                                cusFieldInterface.onClickCusField(v, cusFieldConfig.getFieldType(), model);
                            }
                        }
                    });
                    containerLayout.addView(view);
                }
            }
        }
    }
}
