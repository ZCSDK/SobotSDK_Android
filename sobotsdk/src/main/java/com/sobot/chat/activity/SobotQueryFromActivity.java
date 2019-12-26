package com.sobot.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.SobotCityResult;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotProvinInfo;
import com.sobot.chat.api.model.SobotQueryFormModel;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.ISobotCusField;
import com.sobot.chat.presenter.StCusFieldPresenter;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;

/**
 * @author Created by jinxl on 2018/1/4.
 */
public class SobotQueryFromActivity extends SobotBaseActivity implements ISobotCusField, View.OnClickListener {
    private Bundle mIntentBundleData;
    private String mGroupId;
    private SobotQueryFormModel mQueryFormModel;
    private String mGroupName;
    private String mUid;
    private int mTransferType;
    private ArrayList<SobotFieldModel> mField;
    private SobotProvinInfo.SobotProvinceModel mFinalData;

    private LinearLayout sobot_container;
    private TextView sobot_tv_doc;
    private Button sobot_btn_submit;
    //防止多次提交
    private boolean isSubmitting = false;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_query_from");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mIntentBundleData = getIntent().getBundleExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA);
        } else {
            mIntentBundleData = savedInstanceState.getBundle(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA);
        }
        if (mIntentBundleData != null) {
            initIntent(mIntentBundleData);
        }
    }

    private void initIntent(Bundle mIntentBundleData) {
        mGroupId = mIntentBundleData.getString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID);
        mGroupName = mIntentBundleData.getString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME);
        mQueryFormModel = (SobotQueryFormModel) mIntentBundleData.getSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD);
        mUid = mIntentBundleData.getString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_UID);
        mTransferType = mIntentBundleData.getInt(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_TRANSFER_TYPE, 0);
        if (mQueryFormModel != null) {
            mField = mQueryFormModel.getField();
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        sobot_btn_submit = (Button) findViewById(getResId("sobot_btn_submit"));
        sobot_btn_submit.setOnClickListener(this);
        sobot_container = (LinearLayout) findViewById(getResId("sobot_container"));
        sobot_tv_doc = (TextView) findViewById(getResId("sobot_tv_doc"));
        if (mQueryFormModel != null) {
            setTitle(mQueryFormModel.getFormTitle());
            sobot_tv_doc.setText(mQueryFormModel.getFormDoc());
        }
        StCusFieldPresenter.addWorkOrderCusFields(SobotQueryFromActivity.this, mField, sobot_container, SobotQueryFromActivity.this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putBundle(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA, mIntentBundleData);
        super.onSaveInstanceState(outState);
    }


    private void submit() {
        //提交信息
        if (isSubmitting) {
            return;
        }
        isSubmitting = true;
        zhiChiApi.submitForm(SobotQueryFromActivity.this, mUid, StCusFieldPresenter.getCusFieldVal(mField, mFinalData), new StringResultCallBack<CommonModel>() {
            @Override
            public void onSuccess(CommonModel data) {
                isSubmitting = false;
                if (data != null && "1".equals(data.getCode())) {
                    CustomToast.makeText(getBaseContext(), ResourceUtils.getResString(getBaseContext(), "sobot_leavemsg_success_tip"), 1000,
                            ResourceUtils.getDrawableId(getBaseContext(), "sobot_iv_login_right")).show();
                }
                saveIntentWithFinish();
            }

            @Override
            public void onFailure(Exception e, String des) {
                isSubmitting = false;
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }

    private void saveIntentWithFinish() {
        // 保存返回值 并且结束当前页面
        try {
            KeyboardUtil.hideKeyboard(SobotQueryFromActivity.this.getCurrentFocus());
            Intent intent = new Intent();
            intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID, mGroupId);
            intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME, mGroupName);
            intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_TRANSFER_TYPE, mTransferType);
            setResult(ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM, intent);
            finish();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    /**
     * 检查字段是否都填写
     *
     * @param field
     * @return
     */
    private boolean checkInput(ArrayList<SobotFieldModel> field) {
        if (field != null && field.size() != 0) {
            for (int i = 0; i < field.size(); i++) {
                if (field.get(i).getCusFieldConfig() != null) {
                    if (1 == field.get(i).getCusFieldConfig().getFillFlag()) {
                        if ("city".equals(field.get(i).getCusFieldConfig().getFieldId())) {
                            if (field.get(i).getCusFieldConfig().getProvinceModel() == null) {
                                ToastUtil.showToast(getApplicationContext(), field.get(i).getCusFieldConfig().getFieldName() + "  " + getResString("sobot__is_null"));
                                return false;
                            }
                        } else if (TextUtils.isEmpty(field.get(i).getCusFieldConfig().getValue())) {
                            ToastUtil.showToast(getApplicationContext(), field.get(i).getCusFieldConfig().getFieldName() + "  " + getResString("sobot__is_null"));
                            return false;
                        }
                    }

                    if ("email".equals(field.get(i).getCusFieldConfig().getFieldId())
                            && !TextUtils.isEmpty(field.get(i).getCusFieldConfig().getValue())
                            && !ScreenUtils.isEmail(field.get(i).getCusFieldConfig().getValue())) {
                        ToastUtil.showToast(getApplicationContext(), getResString("sobot_email_dialog_hint"));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        setResult(ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM_CANCEL, new Intent());
        finish();
    }

    @Override
    public void onClickCusField(final View view, final int fieldType, final SobotFieldModel cusField) {
        switch (fieldType) {
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE:
                StCusFieldPresenter.openTimePicker(SobotQueryFromActivity.this, view, fieldType);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE:
                StCusFieldPresenter.startSobotCusFieldActivity(SobotQueryFromActivity.this, cusField);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE:
                LogUtils.i("点击了城市");
                SobotDialogUtils.startProgressDialog(SobotQueryFromActivity.this);
                zhiChiApi.queryCity(SobotQueryFromActivity.this, null, null, new StringResultCallBack<SobotCityResult>() {
                    @Override
                    public void onSuccess(SobotCityResult result) {
                        SobotDialogUtils.stopProgressDialog(SobotQueryFromActivity.this);
                        SobotProvinInfo data = result.getData();
                        if (data.getProvinces() != null && data.getProvinces().size() > 0) {
                            // 启动城市选择
                            StCusFieldPresenter.startChooseCityAct(SobotQueryFromActivity.this, data, cusField);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        SobotDialogUtils.stopProgressDialog(SobotQueryFromActivity.this);
                        ToastUtil.showToast(getApplicationContext(), des);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SobotDialogUtils.stopProgressDialog(SobotQueryFromActivity.this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StCusFieldPresenter.onStCusFieldActivityResult(SobotQueryFromActivity.this, data, mField, sobot_container);

        if (data != null) {
            switch (requestCode) {
                case ZhiChiConstant.REQUEST_COCE_TO_CITY_INFO:
                    String fieldId = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD_ID);
                    mFinalData = (SobotProvinInfo.SobotProvinceModel) data.getSerializableExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_PROVININFO);
                    if (mField != null && mFinalData != null && !TextUtils.isEmpty(fieldId)) {

                        for (int i = 0; i < mField.size(); i++) {
                            SobotCusFieldConfig model = mField.get(i).getCusFieldConfig();
                            if (model != null && fieldId.equals(model.getFieldId())) {
                                model.setChecked(true);
                                model.setProvinceModel(mFinalData);
                                View view = sobot_container.findViewWithTag(fieldId);
                                if (view != null) {
                                    TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(getApplicationContext(), "id", "work_order_customer_date_text_click"));
                                    String pStr = mFinalData.provinceName == null ? "" : mFinalData.provinceName;
                                    String cStr = mFinalData.cityName == null ? "" : mFinalData.cityName;
                                    String aStr = mFinalData.areaName == null ? "" : mFinalData.areaName;
                                    String str = pStr + cStr + aStr;
                                    textClick.setText(str);
                                    TextView fieldName = (TextView) view.findViewById(ResourceUtils.getIdByName(getBaseContext(), "id", "work_order_customer_field_text_lable"));
                                    LinearLayout work_order_customer_field_ll = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(getBaseContext(), "id", "work_order_customer_field_ll"));
                                    work_order_customer_field_ll.setVisibility(View.VISIBLE);
                                    fieldName.setTextColor(Color.parseColor("#ACB5C4"));
                                    fieldName.setTextSize(12);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_btn_submit) {
            //自定义表单校验结果:为空,校验通过,可以提交;不为空,说明自定义字段校验不通过，不能提交留言表单;
            String checkCustomFieldResult = StCusFieldPresenter.formatCusFieldVal(SobotQueryFromActivity.this, sobot_container, mField);
            if (!TextUtils.isEmpty(checkCustomFieldResult)) {
                return;
            }
            if (!checkInput(mField)) {
                return;
            }

            submit();
        }
    }
}