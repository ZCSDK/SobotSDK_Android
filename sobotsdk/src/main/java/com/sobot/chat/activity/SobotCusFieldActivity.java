package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotCusFieldAdapter;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotCusFieldDataInfo;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.camera.util.DeviceUtil;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

public class SobotCusFieldActivity extends SobotDialogBaseActivity {

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
    private LinearLayout sobot_btn_cancle;
    private TextView sobot_tv_title;
    private Button sobot_btn_submit;
    private EditText sobot_et_search;
    private LinearLayout sobot_ll_search;
    private LinearLayout sobot_ll_submit;
    private float screenHeight70;

    @Override
    protected int getRootViewLayoutId() {
        return ResourceUtils.getResLayoutId(this, "sobot_activity_cusfield");
    }

    @Override
    public void initView() {
        screenHeight70=ScreenUtils.getScreenHeight(this)*0.7f;
        sobot_tv_title = (TextView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_tv_title"));
        sobot_btn_cancle = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_cancle"));
        sobot_et_search = (EditText) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_et_search"));
        sobot_ll_search = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_ll_search"));
        sobot_btn_submit = (Button) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_submit"));
        sobot_ll_submit = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_ll_submit"));
        mListView = (ListView) findViewById(ResourceUtils.getResId(getBaseContext(), "sobot_activity_cusfield_listview"));
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
        sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishPageOrSDK();
            }
        });
        sobot_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSumbitClick();
            }
        });
        sobot_et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter==null){
                    return;
                }
                SobotCusFieldAdapter.MyFilter m = adapter.getFilter();
                m.filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void onSumbitClick() {
        if (dataName.length() != 0 && fieldId.length() != 0 && dataValue.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
            intent.putExtra("fieldType", fieldType);
            intent.putExtra("category_typeName", dataName + "");
            intent.putExtra("category_typeValue", dataValue + "");
            intent.putExtra("category_fieldId", fieldId + "");
            setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
            intent.putExtra("fieldType", fieldType);
            intent.putExtra("category_typeName", "");
            intent.putExtra("category_typeValue", "");
            intent.putExtra("category_fieldId", fieldId + "");
            setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
        }
        finish();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                finishPageOrSDK();
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(SobotCusFieldActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }

    @Override
    public void initData() {
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
            sobot_tv_title.setText(cusFieldConfig.getFieldName());
        }

        if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            //多选 显示提交按钮
            sobot_ll_submit.setVisibility(View.VISIBLE);
            sobot_ll_search.setVisibility(View.GONE);
        } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE == fieldType ) {
            //单选  显示搜索框 选中直接返回
            sobot_ll_submit.setVisibility(View.GONE);
            sobot_ll_search.setVisibility(View.VISIBLE);
        }

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
            setListViewHeight(mListView,5,0);
        }
    }


    // 使用String的split 方法把字符串截取为字符串数组
    private String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    private void finishPageOrSDK() {
        String last_current_appkey = SharedPreferencesUtil.getStringData(getBaseContext(), ZhiChiConstant.sobot_last_current_appkey, "");
        int initType = SharedPreferencesUtil.getIntData(
                getApplicationContext(), last_current_appkey + "_" + ZhiChiConstant.initType, -1);
        if (initType == ZhiChiConstant.type_custom_only) {
            finish();
            sendCloseIntent(1);
        } else {
            finish();
            sendCloseIntent(2);

        }
    }

    private void sendCloseIntent(int type) {
        Intent intent = new Intent();
        if (type == 1) {
            intent.setAction(ZhiChiConstants.sobot_close_now_clear_cache);
        } else {
            intent.setAction(ZhiChiConstants.sobot_click_cancle);
        }
        CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }

    @Override
    public void onBackPressed() {
        finishPageOrSDK();
    }


    private void setListViewHeight(ListView listView,int maxline,int height){
        ListAdapter listAdapter = listView.getAdapter(); //得到ListView 添加的适配器
        if(listAdapter == null){
            return;
        }

        View itemView = listAdapter.getView(0, null, listView); //获取其中的一项
        itemView.measure(0,0);
        int itemHeight = itemView.getMeasuredHeight(); //一项的高度
        int itemCount = listAdapter.getCount();//得到总的项数
        LinearLayout.LayoutParams layoutParams = null; //进行布局参数的设置
        if (screenHeight70<ScreenUtils.dip2px(this,60)+itemHeight*itemCount){
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , (int) (screenHeight70-ScreenUtils.dip2px(this,60)));
        }else{
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,itemHeight*itemCount);
        }

        listView.setLayoutParams(layoutParams);
    }
}