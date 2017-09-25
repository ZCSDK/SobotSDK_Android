package com.sobot.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotPicListAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.PostParamModel;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotLeaveMsgParamModel;
import com.sobot.chat.api.model.SobotPostCusFieldModel;
import com.sobot.chat.api.model.SobotTypeModel;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.ThankDialog;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.dialog.SobotSelectPicDialog;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("HandlerLeak")
public class SobotPostMsgActivity extends SobotBaseActivity implements OnClickListener {

    private EditText sobot_post_email, sobot_et_content, sobot_post_phone;
    private TextView sobot_tv_post_msg, sobot_post_email_lable, sobot_post_phone_lable, sobot_post_lable, sobot_post_question_type;
    private ImageView sobot_img_clear_email, sobot_img_clear_phone;
    private View sobot_frist_line;
    private GridView sobot_post_msg_pic;
    private LinearLayout sobot_enclosure_container, sobot_post_customer_field;
    private RelativeLayout sobot_post_email_rl, sobot_post_phone_rl, sobot_post_question_rl;
    private List<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
    private SobotPicListAdapter adapter;
    private SobotSelectPicDialog menuWindow;
    private SobotCusFieldConfig mCusFieldConfig;
    private ArrayList<SobotFieldModel> field;
    private ArrayList<SobotTypeModel> types;

    private LinearLayout sobot_post_msg_layout;
    private String uid = "";
    private String companyId = "";
    private String groupId = "";
    private String msgTmp = "";
    private String msgTxt = "";
    private boolean flag_exit_sdk;
    private boolean telFlag;
    private boolean telShowTvFlag;
    private boolean emailFlag;
    private boolean emailShowTvFlag;
    private boolean enclosureShowFlag;
    private boolean enclosureFlag;

    private int flag_exit_type = -1;
    private ThankDialog d;

    public Handler handler = new Handler() {
        public void handleMessage(final android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (flag_exit_type == 1) {
                        finishPageOrSDK(true);
                    } else if (flag_exit_type == 2) {
                        setResult(200);
                        finishPageOrSDK(false);
                    } else {
                        finishPageOrSDK(flag_exit_sdk);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_post_msg"));
        initBundleData(savedInstanceState);
        initDate();
        initView();
        msgFilter();
        editTextSetHint();
    }

    private void setCusFieldValue() {
        if (field != null && field.size() != 0) {
            for (int j = 0; j < field.size(); j++) {
                if (field.get(j).getCusFieldConfig() == null) {
                    continue;
                }
                View view = sobot_post_customer_field.findViewWithTag(field.get(j).getCusFieldConfig().getFieldId());
                if (view != null) {
                    if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText singleContent = (EditText) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_field_text_single"));
                        field.get(j).getCusFieldConfig().setValue(singleContent.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText moreContent = (EditText) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_field_text_more_content"));
                        field.get(j).getCusFieldConfig().setValue(moreContent.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == field.get(j).getCusFieldConfig().getFieldType()
                            || ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE== field.get(j).getCusFieldConfig().getFieldType() ) {
                        TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_date_text_click"));
                        field.get(j).getCusFieldConfig().setValue(textClick.getText() + "");
                    } else if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == field.get(j).getCusFieldConfig().getFieldType()) {
                        EditText numberContent = (EditText) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_field_text_number"));
                        field.get(j).getCusFieldConfig().setValue(numberContent.getText() + "");
                    }
                }
            }
        }
        checkSubmit();
    }

    private void checkSubmit() {
        String userPhone = "", userEamil = "";

        if (sobot_post_question_rl.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(sobot_post_question_type.getText().toString())) {
                showHint(getResString("sobot_problem_types")  + "  "+ getResString("sobot__is_null"), false);
                return;
            }
        }

        if (field != null && field.size() != 0) {
            for (int i = 0; i < field.size(); i++) {
                if (1 == field.get(i).getCusFieldConfig().getFillFlag()) {
                    if (TextUtils.isEmpty(field.get(i).getCusFieldConfig().getValue())) {
                        showHint(field.get(i).getCusFieldConfig().getFieldName()  + "  "+ getResString("sobot__is_null"), false);
                        return;
                    }
                }
            }
        }

        if (TextUtils.isEmpty(sobot_et_content.getText().toString().trim())) {
            showHint(getResString("sobot_problem_description") + "  "+ getResString("sobot__is_null"), false);
            return;
        }

        if (enclosureShowFlag && enclosureFlag) {
            if (TextUtils.isEmpty(getFileStr())) {
                showHint(getResString("sobot_please_load"), false);
                return;
            }
        }

        if (emailShowTvFlag) {
            if (emailFlag) {
                if (!TextUtils.isEmpty(sobot_post_email.getText().toString().trim())
                        && ScreenUtils.isEmail(sobot_post_email.getText().toString().trim())) {
                    userEamil = sobot_post_email.getText().toString().trim();
                } else {
                    showHint(getResString("sobot_email_dialog_hint"), false);
                    return;
                }
            } else {
                if (!TextUtils.isEmpty(sobot_post_email.getText().toString().trim())) {
                    String emailStr = sobot_post_email.getText().toString().trim();
                    if (ScreenUtils.isEmail(emailStr)) {
                        userEamil = sobot_post_email.getText().toString().trim();
                    } else {
                        showHint(getResString("sobot_email_dialog_hint"), false);
                        return;
                    }
                }
            }
        }

        if (telShowTvFlag) {
            if (telFlag) {
                if (!TextUtils.isEmpty(sobot_post_phone.getText().toString().trim())
                        && ScreenUtils.isMobileNO(sobot_post_phone.getText().toString().trim())) {
                    userPhone = sobot_post_phone.getText().toString();
                } else {
                    showHint(getResString("sobot_phone_dialog_hint"), false);
                    return;
                }
            } else {
                if (!TextUtils.isEmpty(sobot_post_phone.getText().toString().trim())) {
                    String phoneStr = sobot_post_phone.getText().toString().trim();
                    if (ScreenUtils.isMobileNO(phoneStr)) {
                        userPhone = phoneStr;
                    } else {
                        showHint(getResString("sobot_phone_dialog_hint"), false);
                        return;
                    }
                }
            }
        }

        postMsg(userPhone, userEamil);
    }

    @Override
    public void forwordMethod() {
        setCusFieldValue();
    }

    @SuppressWarnings("deprecation")
    public void showHint(String content, final boolean flag) {
        if (!isFinishing()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(sobot_post_email.getWindowToken(), 0); // 强制隐藏键盘
            imm.hideSoftInputFromWindow(sobot_et_content.getWindowToken(), 0); // 强制隐藏键盘
            if (d != null) {
                d.dismiss();
            }
            ThankDialog.Builder customBuilder = new ThankDialog.Builder(
                    SobotPostMsgActivity.this);
            customBuilder.setMessage(content);
            d = customBuilder.create();
            d.show();

            WindowManager.LayoutParams lp = d.getWindow().getAttributes();
            float dpToPixel = ScreenUtils.dpToPixel(
                    getApplicationContext(), 1);
            lp.width = (int) (dpToPixel * 200); // 设置宽度
            d.getWindow().setAttributes(lp);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        if (d != null) {
                            d.dismiss();
                        }
                        if (flag) {
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            }, 2000);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_tv_left) {
            if (flag_exit_type == 1 || flag_exit_type == 2) {
                finishPageOrSDK(false);
            } else {
                finishPageOrSDK(flag_exit_sdk);
            }
        }

        if (view == sobot_img_clear_email) {
            sobot_post_email.setText("");
            sobot_img_clear_email.setVisibility(View.GONE);
        }

        if (view == sobot_img_clear_phone) {
            sobot_post_phone.setText("");
            sobot_img_clear_phone.setVisibility(View.GONE);
        }

        if (view == sobot_post_question_type) {
            if (types != null && types.size() != 0) {
                Intent intent = new Intent(SobotPostMsgActivity.this, SobotPostCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("types", types);
                if (sobot_post_question_type != null &&
                        !TextUtils.isEmpty(sobot_post_question_type.getText().toString()) &&
                        sobot_post_question_type.getTag() != null &&
                        !TextUtils.isEmpty(sobot_post_question_type.getTag().toString())) {
                    bundle.putString("typeName", sobot_post_question_type.getText().toString());
                    bundle.putString("typeId", sobot_post_question_type.getTag().toString());
                }
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, ZhiChiConstant.work_order_list_display_type_category);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (flag_exit_type == 1 || flag_exit_type == 2) {
            finishPageOrSDK(false);
        } else {
            finishPageOrSDK(flag_exit_sdk);
        }
    }

    private void postMsg(String userPhone, String userEamil) {

        List<Map<String, String>> listModel = new ArrayList<>();
        if (field != null && field.size() != 0) {
            for (int i = 0; i < field.size(); i++) {
                Map<String, String> model = new HashMap<>();
                if (field.get(i).getCusFieldConfig() != null && !TextUtils.isEmpty(field.get(i).getCusFieldConfig().getFieldId()) && !TextUtils.isEmpty(field.get(i).getCusFieldConfig().getValue())){
                    model.put("id", field.get(i).getCusFieldConfig().getFieldId());
                    model.put("value", field.get(i).getCusFieldConfig().getValue());
                    listModel.add(model);
                }
            }
        }

        PostParamModel postParam = new PostParamModel();
        postParam.setUid(uid);
        postParam.setTicketContent(sobot_et_content.getText().toString());
        postParam.setCustomerEmail(userEamil);
        postParam.setCustomerPhone(userPhone);
        postParam.setCompanyId(companyId);
        postParam.setFileStr(getFileStr());
        postParam.setGroupId(groupId);
        if (sobot_post_question_type.getTag() != null && !TextUtils.isEmpty(sobot_post_question_type.getTag().toString())) {
            postParam.setTicketTypeId(sobot_post_question_type.getTag().toString());
        }

        if (listModel.size() != 0) {
            JSONArray jsonArray = new JSONArray(listModel);//把  List 对象  转成json数据
            postParam.setExtendFields(jsonArray.toString());
        }
        zhiChiApi.postMsg(postParam, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase base) {
                if (Integer.parseInt(base.getStatus()) == 0) {
                    showHint(base.getMsg(), false);
                } else if (Integer.parseInt(base.getStatus()) == 1) {
                    showHint(getResString("sobot_leavemsg_success_hint"), true);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(getString(ResourceUtils.getIdByName(getApplicationContext(), "string", "sobot_try_again")), false);
                } catch (Exception e1) {

                }
            }
        });
    }

    private void finishPageOrSDK(boolean flag) {
        if (!flag) {
            finish();
            overridePendingTransition(ResourceUtils.getIdByName(
                    getApplicationContext(), "anim", "push_right_in"),
                    ResourceUtils.getIdByName(getApplicationContext(), "anim",
                            "push_right_out"));
        } else {
            MyApplication.getInstance().exit();
        }
    }

    @Override
    protected void onDestroy() {
        SobotDialogUtils.stopProgressDialog(this);
        if (d != null) {
            d.dismiss();
        }
        super.onDestroy();
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("uid", uid);
        outState.putString("companyId", companyId);
        outState.putString("groupId", groupId);
        outState.putInt("flag_exit_type", flag_exit_type);
        outState.putBoolean("flag_exit_sdk", flag_exit_sdk);
        outState.putString("msgTmp", msgTmp);
        outState.putString("msgTxt", msgTxt);
        super.onSaveInstanceState(outState);
    }

    /**
     * 初始化图片选择的控件
     */
    private void initPicListView() {
        sobot_post_msg_pic = (GridView) findViewById(getResId("sobot_post_msg_pic"));
        adapter = new SobotPicListAdapter(SobotPostMsgActivity.this, pic_list);
        sobot_post_msg_pic.setAdapter(adapter);
        sobot_post_msg_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KeyboardUtil.hideKeyboard(view);
                if (pic_list.get(position).getViewState() == 0) {
                    menuWindow = new SobotSelectPicDialog(SobotPostMsgActivity.this, itemsOnClick);
                    menuWindow.show();
                } else {
                    LogUtils.i("当前选择图片位置：" + position);
                    Intent intent = new Intent(SobotPostMsgActivity.this, SobotPhotoListActivity.class);
                    intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST, adapter.getPicList());
                    intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, position);
                    startActivityForResult(intent, ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS);
                }
            }
        });
        adapter.restDataView();
    }

    //对msg过滤
    private void msgFilter() {
        if (!TextUtils.isEmpty(msgTmp)) {
            msgTmp = msgTmp.replace("<br/>", "");
        }

        if (!TextUtils.isEmpty(msgTxt)) {
            msgTxt = msgTxt.replace("<br/>", "");
        }

        sobot_et_content.setHint(Html.fromHtml(msgTmp));
        HtmlTools.getInstance(getApplicationContext()).setRichText(sobot_tv_post_msg, msgTxt,
                ResourceUtils.getIdByName(this, "color", "sobot_postMsg_url_color"));
        sobot_post_msg_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sobot_post_msg_layout.getWindowToken(), 0); // 强制隐藏键盘
            }
        });
    }

    //设置editText的hint提示字体
    private void editTextSetHint() {
        String mustFill = "<font color='red'>&#8201*</font>";

        if (emailFlag) {
            sobot_post_email_lable.setText(Html.fromHtml("<font color='#8B98AD'>" + getResString("sobot_email") + "</font>" + mustFill));
        } else {
            sobot_post_email_lable.setText(Html.fromHtml("<font color='#8B98AD'>" + getResString("sobot_email") + "</font>"));
        }

        if (telFlag) {
            sobot_post_phone_lable.setText(Html.fromHtml("<font color='#8B98AD'>" + getResString("sobot_phone") + "</font>" + mustFill));
        } else {
            sobot_post_phone_lable.setText(Html.fromHtml("<font color='#8B98AD'>" + getResString("sobot_phone") + "</font>"));
        }
    }

    private ChatUtils.SobotSendFileListener sendFileListener = new ChatUtils.SobotSendFileListener() {
        @Override
        public void onSuccess(String filePath) {
            zhiChiApi.fileUploadForPostMsg(companyId, filePath, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {
                    SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
                    if (zhiChiMessage.getData() != null) {
                        ZhiChiUploadAppFileModelResult item = new ZhiChiUploadAppFileModelResult();
                        item.setFileUrl(zhiChiMessage.getData().getUrl());
                        item.setViewState(1);
                        adapter.addData(item);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
                    showHint(des, false);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }
            });
        }

        @Override
        public void onError() {
            SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
        }
    };

    public String getFileStr() {
        String tmpStr = "";
        if (!enclosureShowFlag) {
            return tmpStr;
        }

        ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
        for (int i = 0; i < tmpList.size(); i++) {
            tmpStr += tmpList.get(i).getFileUrl() + ";";
        }
        return tmpStr;
    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (getIntent() != null) {
                uid = getIntent().getStringExtra("uid");
                companyId = getIntent().getStringExtra("companyId");
                groupId = getIntent().getStringExtra("groupId");
                flag_exit_type = getIntent().getIntExtra(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
                flag_exit_sdk = getIntent().getBooleanExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                msgTmp = getIntent().getStringExtra("msgTmp").replaceAll("\n", "<br/>");
                msgTxt = getIntent().getStringExtra("msgTxt").replaceAll("\n", "<br/>");
            }
        } else {
            uid = savedInstanceState.getString("uid");
            companyId = savedInstanceState.getString("companyId");
            groupId = savedInstanceState.getString("groupId");
            flag_exit_type = savedInstanceState.getInt(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
            flag_exit_sdk = savedInstanceState.getBoolean(ZhiChiConstant.FLAG_EXIT_SDK, false);

            msgTmp = savedInstanceState.getString("msgTmp");
            msgTxt = savedInstanceState.getString("msgTxt");
            if (!TextUtils.isEmpty(msgTmp)) {
                msgTmp = msgTmp.replaceAll("\n", "<br/>");
            }

            if (!TextUtils.isEmpty(msgTxt)) {
                msgTxt = msgTxt.replaceAll("\n", "<br/>");
            }
        }
    }

    //初始化View
    private void initView() {
        sobot_post_phone = (EditText) findViewById(getResId("sobot_post_phone"));
        sobot_post_email = (EditText) findViewById(getResId("sobot_post_email"));
        sobot_frist_line = findViewById(getResId("sobot_frist_line"));
        sobot_et_content = (EditText) findViewById(getResId("sobot_post_et_content"));
        sobot_tv_post_msg = (TextView) findViewById(getResId("sobot_tv_post_msg"));
        sobot_post_email_lable = (TextView) findViewById(getResId("sobot_post_email_lable"));
        sobot_post_phone_lable = (TextView) findViewById(getResId("sobot_post_phone_lable"));
        sobot_post_lable = (TextView) findViewById(getResId("sobot_post_question_lable"));
        String test = "<font color='#8B98AD'>" + getResString("sobot_problem_types") + "</font>" + "<font color='#f9676f'>&#8201*</font>";
        sobot_post_lable.setText(Html.fromHtml(test));
        sobot_post_question_type = (TextView) findViewById(getResId("sobot_post_question_type"));
        sobot_post_question_type.setOnClickListener(this);
        sobot_post_msg_layout = (LinearLayout) findViewById(getResId("sobot_post_msg_layout"));
        sobot_img_clear_email = (ImageView) findViewById(getResId("sobot_img_clear_email"));
        sobot_img_clear_phone = (ImageView) findViewById(getResId("sobot_img_clear_phone"));
        sobot_img_clear_phone.setOnClickListener(this);
        sobot_img_clear_email.setOnClickListener(this);
        sobot_enclosure_container = (LinearLayout) findViewById(getResId("sobot_enclosure_container"));
        sobot_post_customer_field = (LinearLayout) findViewById(getResId("sobot_post_customer_field"));


        sobot_post_email_rl = (RelativeLayout) findViewById(getResId("sobot_post_email_rl"));
        sobot_post_phone_rl = (RelativeLayout) findViewById(getResId("sobot_post_phone_rl"));
        sobot_post_question_rl = (RelativeLayout) findViewById(getResId("sobot_post_question_rl"));

        sobot_post_customer_field.setVisibility(View.GONE);

        if (emailShowTvFlag) {
            sobot_post_email_rl.setVisibility(View.VISIBLE);
        } else {
            sobot_post_email_rl.setVisibility(View.GONE);
        }

        if (telShowTvFlag) {
            sobot_post_phone_rl.setVisibility(View.VISIBLE);
        } else {
            sobot_post_phone_rl.setVisibility(View.GONE);
        }

        if (emailShowTvFlag && telShowTvFlag) {
            sobot_frist_line.setVisibility(View.VISIBLE);
        } else {
            sobot_frist_line.setVisibility(View.GONE);
        }

        if (telFlag) {
            sobot_post_phone.setText(SharedPreferencesUtil.getStringData(this, "sobot_user_phone", ""));
        }

        if (emailFlag) {
            sobot_post_email.setText(SharedPreferencesUtil.getStringData(this, "sobot_user_email", ""));
        }

        if (enclosureShowFlag) {
            sobot_enclosure_container.setVisibility(View.VISIBLE);
            initPicListView();
        } else {
            sobot_enclosure_container.setVisibility(View.GONE);
        }

        zhiChiApi.getLeaveMsgParam(uid, new ResultCallBack<SobotLeaveMsgParamModel>() {

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(getString(ResourceUtils.getIdByName(getApplicationContext(), "string", "sobot_try_again")), false);
                } catch (Exception e1) {

                }
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

            }

            @Override
            public void onSuccess(SobotLeaveMsgParamModel result) {
                if (result != null) {
                    if (result.isTicketTypeFlag()) {
                        sobot_post_question_rl.setVisibility(View.VISIBLE);
                    } else {
                        sobot_post_question_rl.setVisibility(View.GONE);
                        sobot_post_question_type.setTag(result.getTicketTypeId());
                    }

                    if (result.getField() != null && result.getField().size() != 0) {
                        field = result.getField();
                        ChatUtils.addWorkOrderCusFields(SobotPostMsgActivity.this, result.getField(), sobot_post_customer_field);
                    }

                    if (result.getType() != null && result.getType().size() != 0) {
                        types = result.getType();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    SobotDialogUtils.startProgressDialog(SobotPostMsgActivity.this);
                    ChatUtils.sendPicByUriPost(this, selectedImage, sendFileListener);
                } else {
                    showHint(getResString("sobot_did_not_get_picture_path"), false);
                }
            } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                if (cameraFile != null && cameraFile.exists()) {
                    SobotDialogUtils.startProgressDialog(SobotPostMsgActivity.this);
                    ChatUtils.sendPicByFilePath(this, cameraFile.getAbsolutePath(), sendFileListener);
                } else {
                    showHint(getResString("sobot_pic_select_again"), false);
                }
            }
        }

        if (data != null) {
            if ("CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && mCusFieldConfig != null) {
                if (-1 != data.getIntExtra("fieldType", -1) && ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == data.getIntExtra("fieldType", -1)) {
                    String value = data.getStringExtra("category_typeName");
                    String id = data.getStringExtra("category_fieldId");
                    String dataValue = data.getStringExtra("category_typeValue");
                    if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(dataValue) && mCusFieldConfig.getFieldId() != null && mCusFieldConfig.getFieldId().equals(id)) {
                        mCusFieldConfig.setValue(dataValue);
                        mCusFieldConfig.setId(id);
                        for (int i = 0; i < field.size(); i++) {
                            SobotCusFieldConfig model = field.get(i).getCusFieldConfig();
                            if (model.getFieldId().equals(mCusFieldConfig.getFieldId())) {
                                mCusFieldConfig.setChecked(true);
                                View view = sobot_post_customer_field.findViewWithTag(model.getFieldId());
                                TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_date_text_click"));
                                textClick.setText(value.substring(0, value.length() - 1));
                            }
                        }
                    }
                } else {
                    if (mCusFieldConfig.getFieldId().equals(data.getStringExtra("category_fieldId"))) {
                        String value = data.getStringExtra("category_typeName");
                        String id = data.getStringExtra("category_fieldId");
                        String dataValue = data.getStringExtra("category_typeValue");
                        if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(dataValue)) {
                            mCusFieldConfig.setValue(dataValue);
                            mCusFieldConfig.setId(id);
                            for (int i = 0; i < field.size(); i++) {
                                SobotCusFieldConfig model = field.get(i).getCusFieldConfig();
                                if (mCusFieldConfig.getFieldId().equals(model.getFieldId())) {
                                    mCusFieldConfig.setChecked(true);
                                    View view = sobot_post_customer_field.findViewWithTag(model.getFieldId());
                                    TextView textClick = (TextView) view.findViewById(ResourceUtils.getIdByName(SobotPostMsgActivity.this, "id", "work_order_customer_date_text_click"));
                                    textClick.setText(value);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                switch (requestCode) {
                    case ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS://图片预览
                        List<ZhiChiUploadAppFileModelResult> tmpList = (List<ZhiChiUploadAppFileModelResult>) data.getExtras().getSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
                        adapter.addDatas(tmpList);
                        break;
                    case ZhiChiConstant.work_order_list_display_type_category:
                        if (!TextUtils.isEmpty(data.getStringExtra("category_typeId"))) {
                            String typeName = data.getStringExtra("category_typeName");
                            String typeId = data.getStringExtra("category_typeId");
                            sobot_post_question_type.setText(typeName);
                            sobot_post_question_type.setTag(typeId);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //根据初始化接口返回的数据，对一些view设置他们的图标颜色是否可以点击等等
    private void initDate() {
        showRightView(0, getResString("sobot_submit"), true);
        Drawable drawable = getResources().getDrawable(getResDrawableId("sobot_btn_back_selector"));
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
        sobot_tv_left.setText(getResString("sobot_back"));
        sobot_tv_left.setOnClickListener(this);

        setTitle(getResString("sobot_str_bottom_message"));
        setShowNetRemind(false);
        String bg_color = SharedPreferencesUtil.getStringData(this,
                "robot_current_themeColor", "");
        if (bg_color != null && bg_color.trim().length() != 0) {
            relative.setBackgroundColor(Color.parseColor(bg_color));
        }

        int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
        if (robot_current_themeImg != 0) {
            relative.setBackgroundResource(robot_current_themeImg);
        }

        //获取本地数据，赋值
        telShowTvFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_TELSHOWFLAG, false);//控制textView是否显示
        telFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_TELFLAG, false);//控制是否必填

        emailShowTvFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_EMAILSHOWFLAG, false);//控制textView是否显示
        emailFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_EMAILFLAG, false);//控制是否必填

        enclosureShowFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_ENCLOSURESHOWFLAG, false);//控制附件是否显示
        enclosureFlag = SharedPreferencesUtil.getBooleanData(this, ZhiChiConstant.SOBOT_POSTMSG_ENCLOSUREFLAG, false);//控制附件是否必填
    }

    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (v.getId() == getResId("btn_take_photo")) {
                LogUtils.i("拍照");
                selectPicFromCamera();
            }
            if (v.getId() == getResId("btn_pick_photo")) {
                LogUtils.i("选择照片");
                selectPicFromLocal();
            }
        }
    };

    public void startSobotCusFieldActivity(SobotCusFieldConfig cusFieldConfig, SobotFieldModel cusFieldList) {
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(this, SobotCusFieldActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", cusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        bundle.putSerializable("cusFieldList", cusFieldList);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, cusFieldConfig.getFieldType());
    }
}