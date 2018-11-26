package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotCusFieldAdapter;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotCusFieldDataInfo;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

public class SobotCusFieldActivity extends SobotBaseActivity {

    private int fieldType;
    private SobotCusFieldConfig cusFieldConfig;
    private List<SobotCusFieldDataInfo> infoLists = new ArrayList<>();
    private SobotFieldModel model;
    private ListView mListView;
    private SobotCusFieldAdapter adapter;
    private Bundle bundle;
    private StringBuffer dataName = new StringBuffer();
    private String fieldId;
    private StringBuffer dataValue = new StringBuffer();

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_cusfield");
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            fieldType = bundle.getInt("fieldType");
            if (bundle.getSerializable("cusFieldConfig") != null) {
                cusFieldConfig = (SobotCusFieldConfig) bundle.getSerializable("cusFieldConfig");
            }

            if (bundle.getSerializable("cusFieldList") != null) {
                model = (SobotFieldModel) bundle.getSerializable("cusFieldList");
            }
        }

        if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldName())) {
            setTitle(cusFieldConfig.getFieldName());
        }
    }

    @Override
    protected void onRightMenuClick(View view) {
        if (dataName.length() != 0 && fieldId.length() != 0 && dataValue.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
            intent.putExtra("fieldType", fieldType);
            intent.putExtra("category_typeName", dataName + "");
            intent.putExtra("category_typeValue", dataValue + "");
            intent.putExtra("category_fieldId", fieldId + "");
            setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
        }
        finish();
    }

    @Override
    public void initView() {
        if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            showRightMenu(0, getResString("sobot_submit"), true);
        }
        mListView = (ListView) findViewById(getResId("sobot_activity_cusfield_listview"));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (infoLists != null && infoLists.size() != 0) {

                    if (fieldType == ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE) {
                        dataName.delete(0, dataName.length());
                        dataValue.delete(0, dataValue.length());

                        if (infoLists.get(position).isChecked()) {
                            infoLists.get(position).setChecked(false);
                        } else {
                            infoLists.get(position).setChecked(true);
                        }

                        fieldId = infoLists.get(0).getFieldId();
                        for (int i = 0; i < infoLists.size(); i++) {
                            if (infoLists.get(i).isChecked()) {
                                dataName.append(infoLists.get(i).getDataName() + ",");
                                dataValue.append(infoLists.get(i).getDataValue() + ",");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                        intent.putExtra("fieldType", fieldType);

                        infoLists.get(position).setChecked(true);
                        for (int i = 0; i < infoLists.size(); i++) {
                            if (i != position) {
                                infoLists.get(i).setChecked(false);
                            }
                        }
                        intent.putExtra("category_typeName", infoLists.get(position).getDataName());
                        intent.putExtra("category_fieldId", infoLists.get(position).getFieldId());
                        intent.putExtra("category_typeValue", infoLists.get(position).getDataValue());
                        setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
                        adapter.notifyDataSetChanged();
                        finish();

                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        if (model != null && model.getCusFieldDataInfoList().size() != 0) {
            infoLists = model.getCusFieldDataInfoList();

            for (int i = 0; i < infoLists.size(); i++) {
                if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
                    String tmpData[];
                    if (!TextUtils.isEmpty(cusFieldConfig.getId())) {
                        tmpData = convertStrToArray(cusFieldConfig.getValue());
                        if (tmpData != null && tmpData.length != 0) {
                            for (int j = 0; j < tmpData.length; j++) {
                                if (tmpData[j].equals(infoLists.get(i).getDataValue())) {
                                    infoLists.get(i).setChecked(true);
                                }
                            }
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(cusFieldConfig.getId()) && cusFieldConfig.getFieldId().equals(infoLists.get(i).getFieldId())
                            && cusFieldConfig.isChecked() && cusFieldConfig.getValue().equals(infoLists.get(i).getDataValue())) {
                        infoLists.get(i).setChecked(true);
                    }
                }
            }

            if (adapter == null) {
                adapter = new SobotCusFieldAdapter(SobotCusFieldActivity.this, infoLists, fieldType);
                mListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }
}