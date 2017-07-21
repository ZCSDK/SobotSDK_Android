package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.SatisfactionSet;
import com.sobot.chat.api.model.SatisfactionSetBase;
import com.sobot.chat.api.model.SobotCommentParam;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.SobotEditTextLayout;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotEvaluateDialog extends SobotActionSheet {

    private Activity context;
    private int score;
    private boolean isFinish;
    private ZhiChiInitModeBase initModel;
    private int current_model;
    private int commentType;/*commentType 评价类型 主动评价1 邀请评价0*/
    private String customName;
    private List<SatisfactionSetBase> satisFactionList ;
    private SatisfactionSetBase satisfactionSetBase;
    private LinearLayout sobot_negativeButton;
    private LinearLayout coustom_pop_layout;
    private LinearLayout sobot_robot_relative;//评价 机器人布局
    private LinearLayout sobot_custom_relative;//评价人工布局
    private LinearLayout sobot_hide_layout;//评价机器人和人工未解决时显示出来的布局
    private RadioGroup sobot_readiogroup;//
    private RadioButton sobot_btn_ok_robot;//评价  已解决
    private RadioButton sobot_btn_no_robot;//评价  未解决
    private Button sobot_close_now;//提交评价按钮

    private EditText sobot_add_content;//评价  添加建议
    private TextView sobot_tv_evaluate_title;//评价   当前是评价机器人还是评价人工客服
    private TextView sobot_robot_center_title;//评价  机器人或人工是否解决了问题的标题
    private TextView sobot_text_other_problem;//评价  机器人或人工客服存在哪些问题的标题
    private TextView sobot_custom_center_title;//评价  对 哪个人工客服 打分  的标题
    private TextView sobot_ratingBar_title;//评价  对人工客服打分不同显示不同的内容
    private TextView sobot_evaluate_cancel;//评价  暂不评价
    private TextView sobot_tv_evaluate_title_hint;//评价  提交后结束评价
    private RatingBar sobot_ratingBar;//评价  打分

    private LinearLayout sobot_evaluate_ll_lable1;//评价  用来放前两个标签，标签最多可以有六个
    private LinearLayout sobot_evaluate_ll_lable2;//评价  用来放中间两个标签
    private LinearLayout sobot_evaluate_ll_lable3;//评价  用来放最后两个标签
    private CheckBox sobot_evaluate_cb_lable1;//六个评价标签
    private CheckBox sobot_evaluate_cb_lable2;
    private CheckBox sobot_evaluate_cb_lable3;
    private CheckBox sobot_evaluate_cb_lable4;
    private CheckBox sobot_evaluate_cb_lable5;
    private CheckBox sobot_evaluate_cb_lable6;
    private SobotEditTextLayout setl_submit_content;//评价框

    private List<CheckBox> checkBoxList = new ArrayList<>();

    public SobotEvaluateDialog(Activity context) {
        super(context);
    }

    public SobotEvaluateDialog(Activity context, boolean isFinish, ZhiChiInitModeBase initModel, int current_model, int commentType, String customName, int score) {
        super(context);
        this.context = context;
        this.score = score;
        this.isFinish = isFinish;
        this.initModel = initModel;
        this.current_model = current_model;
        this.commentType = commentType;
        this.customName = customName;
    }

    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_evaluate";
    }

    @Override
    protected View getDialogContainer() {
        if(coustom_pop_layout == null){
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_evaluate_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initView() {
        sobot_close_now = (Button) findViewById(getResId("sobot_close_now"));
        sobot_readiogroup = (RadioGroup) findViewById(getResId("sobot_readiogroup"));
        sobot_tv_evaluate_title = (TextView) findViewById(getResId("sobot_tv_evaluate_title"));
        sobot_robot_center_title = (TextView) findViewById(getResId("sobot_robot_center_title"));
        sobot_text_other_problem = (TextView) findViewById(getResId("sobot_text_other_problem"));
        sobot_custom_center_title = (TextView) findViewById(getResId("sobot_custom_center_title"));
        sobot_ratingBar_title = (TextView) findViewById(getResId("sobot_ratingBar_title"));
        sobot_tv_evaluate_title_hint = (TextView) findViewById(getResId("sobot_tv_evaluate_title_hint"));
        sobot_evaluate_cancel = (TextView) findViewById(getResId("sobot_evaluate_cancel"));
        if (isFinish){
            sobot_evaluate_cancel.setVisibility(View.VISIBLE);
        } else {
            sobot_evaluate_cancel.setVisibility(View.GONE);
        }
        sobot_ratingBar = (RatingBar) findViewById(getResId("sobot_ratingBar"));
        sobot_evaluate_ll_lable1 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable1"));
        sobot_evaluate_ll_lable2 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable2"));
        sobot_evaluate_ll_lable3 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable3"));
        sobot_evaluate_cb_lable1 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable1"));
        sobot_evaluate_cb_lable2 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable2"));
        sobot_evaluate_cb_lable3 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable3"));
        sobot_evaluate_cb_lable4 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable4"));
        sobot_evaluate_cb_lable5 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable5"));
        sobot_evaluate_cb_lable6 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable6"));
        checkBoxList.add(sobot_evaluate_cb_lable1);
        checkBoxList.add(sobot_evaluate_cb_lable2);
        checkBoxList.add(sobot_evaluate_cb_lable3);
        checkBoxList.add(sobot_evaluate_cb_lable4);
        checkBoxList.add(sobot_evaluate_cb_lable5);
        checkBoxList.add(sobot_evaluate_cb_lable6);
        sobot_add_content = (EditText) findViewById(getResId("sobot_add_content"));
        sobot_btn_ok_robot = (RadioButton) findViewById(getResId("sobot_btn_ok_robot"));
        sobot_btn_ok_robot.setChecked(true);
        sobot_btn_no_robot = (RadioButton) findViewById(getResId("sobot_btn_no_robot"));
        sobot_robot_relative = (LinearLayout) findViewById(getResId("sobot_robot_relative"));
        sobot_custom_relative = (LinearLayout) findViewById(getResId("sobot_custom_relative"));
        sobot_hide_layout = (LinearLayout) findViewById(getResId("sobot_hide_layout"));
        setl_submit_content = (SobotEditTextLayout) findViewById(getResId("setl_submit_content"));
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobotEvaluateDialog.this.dismiss();
            }
        });
        setViewGone();
        setViewListener();
    }

    @Override
    protected void initData() {
        if (current_model == ZhiChiConstant.client_model_customService){
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
            zhiChiApi.satisfactionMessage(initModel.getUid(), new ResultCallBack<SatisfactionSet>() {
                @Override
                public void onSuccess(SatisfactionSet satisfactionSet) {
                    if (satisfactionSet != null && "1".equals(satisfactionSet.getCode())  && satisfactionSet.getData() != null && satisfactionSet.getData().size() != 0){
                        satisFactionList  = satisfactionSet.getData();
                        sobot_ratingBar.setRating(score);

                        setCustomLayoutViewVisible(score,satisFactionList);

                        try {
                            sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                            sobot_ratingBar_title.setVisibility(View.VISIBLE);
                        } catch (Exception e){

                        }

                        if (satisFactionList.get(0).getIsQuestionFlag()){
                            sobot_robot_relative.setVisibility(View.VISIBLE);
                        } else {
                            sobot_robot_relative.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String des) { }

                @Override
                public void onLoading(long total, long current, boolean isUploading) { }
            });
        }
    }

    private void setViewListener(){
        sobot_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    int score = (int) Math.ceil(sobot_ratingBar.getRating());
                if(score > 0 && score <= 5){
                    sobot_close_now.setSelected(true);
                    setCustomLayoutViewVisible(score,satisFactionList);
                }
            }
        });

        sobot_readiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (current_model == ZhiChiConstant.client_model_robot && initModel != null){
                    if (checkedId == getResId("sobot_btn_ok_robot")){
                        sobot_hide_layout.setVisibility(View.GONE);
                        setl_submit_content.setVisibility(View.GONE);
                    } else if (checkedId == getResId("sobot_btn_no_robot")){
                        sobot_hide_layout.setVisibility(View.VISIBLE);
                        setl_submit_content.setVisibility(View.VISIBLE);
                        String tmpData[]  = convertStrToArray(initModel.getRobotCommentTitle());
                        if (tmpData != null && tmpData.length > 0){
                            setLableViewVisible(tmpData);
                        } else {
                            sobot_hide_layout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        sobot_close_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subMitEvaluate();
            }
        });

        sobot_evaluate_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
                intent.setAction(ZhiChiConstants.sobot_close_now);/*立即结束*/
                CommonUtils.sendLocalBroadcast(context.getApplicationContext(), intent);
            }
        });
    }

    private void setViewGone(){
        sobot_hide_layout.setVisibility(View.GONE);
        setl_submit_content.setVisibility(View.GONE);
        sobot_evaluate_ll_lable1.setVisibility(View.GONE);
        sobot_evaluate_ll_lable2.setVisibility(View.GONE);
        sobot_evaluate_ll_lable3.setVisibility(View.GONE);
        sobot_evaluate_cb_lable1.setVisibility(View.GONE);
        sobot_evaluate_cb_lable2.setVisibility(View.GONE);
        sobot_evaluate_cb_lable3.setVisibility(View.GONE);
        sobot_evaluate_cb_lable4.setVisibility(View.GONE);
        sobot_evaluate_cb_lable5.setVisibility(View.GONE);
        sobot_evaluate_cb_lable6.setVisibility(View.GONE);
        sobot_ratingBar_title.setVisibility(View.GONE);

        if (current_model == ZhiChiConstant.client_model_robot){
            sobot_tv_evaluate_title.setText(getResString("sobot_robot_customer_service_evaluation"));
            sobot_robot_center_title.setText(String.format(ChatUtils.getResString(context, "sobot_question"), initModel.getRobotName()));
            sobot_text_other_problem.setText(getResString("sobot_what_are_the_problems"));
            sobot_robot_relative.setVisibility(View.VISIBLE);
            sobot_custom_relative.setVisibility(View.GONE);
        } else {
            boolean isExitTalk = SharedPreferencesUtil.getBooleanData(context,ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT,false);
            if (isExitTalk){
                sobot_tv_evaluate_title_hint.setText(getResString("sobot_evaluation_completed_exit"));
                sobot_tv_evaluate_title_hint.setVisibility(View.VISIBLE);
            } else {
                sobot_tv_evaluate_title_hint.setVisibility(View.GONE);
            }
            sobot_tv_evaluate_title.setText(getResString("sobot_please_evaluate_this_service"));
            sobot_robot_center_title.setText(String.format(ChatUtils.getResString(context, "sobot_question"), customName));
            sobot_custom_center_title.setText(String.format(ChatUtils.getResString(context, "sobot_please_evaluate"), customName));
            sobot_robot_relative.setVisibility(View.GONE);
            sobot_custom_relative.setVisibility(View.VISIBLE);
        }
    }

    //设置人工客服评价的布局显示逻辑
    private void setCustomLayoutViewVisible(int score,List<SatisfactionSetBase> satisFactionList){
        satisfactionSetBase = getSatisFaction(score,satisFactionList);
        for (int i = 0; i < checkBoxList.size(); i++) {
            checkBoxList.get(i).setChecked(false);
        }
        if (satisfactionSetBase != null){
            sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
            sobot_ratingBar_title.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(satisfactionSetBase.getInputLanguage())){
                if (satisfactionSetBase.getIsInputMust()){
                    sobot_add_content.setHint(getResString("sobot_required") + satisfactionSetBase.getInputLanguage().replace("<br/>", "\n"));
                } else {
                    sobot_add_content.setHint(satisfactionSetBase.getInputLanguage().replace("<br/>", "\n"));
                }
            } else {
                sobot_add_content.setHint(String.format(ChatUtils.getResString(context, "sobot_edittext_hint")));
            }

            if (!TextUtils.isEmpty(satisfactionSetBase.getLabelName())){
                String tmpData[] = convertStrToArray(satisfactionSetBase.getLabelName());
                setLableViewVisible(tmpData);
            } else {
                setLableViewVisible(null);
            }

            if (score == 5){
                sobot_hide_layout.setVisibility(View.GONE);
                setl_submit_content.setVisibility(View.GONE);
                sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                sobot_ratingBar_title.setVisibility(View.VISIBLE);
            } else {
                setl_submit_content.setVisibility(View.VISIBLE);
            }
        } else {
            sobot_ratingBar_title.setVisibility(View.GONE);
        }
    }

    private SatisfactionSetBase getSatisFaction(int score,List<SatisfactionSetBase> satisFactionList){
        if(satisFactionList == null){
            return null;
        }
        for (int i = 0; i < satisFactionList.size(); i++) {
            if (satisFactionList.get(i).getScore().equals(score+"")){
                return satisFactionList.get(i);
            }
        }
        return null;
    }

    //设置评价标签的显示逻辑
    private void setLableViewVisible(String tmpData[]){
        if ( tmpData == null){
            sobot_hide_layout.setVisibility(View.GONE);
            return;
        } else {
            sobot_hide_layout.setVisibility(View.VISIBLE);
            if (current_model == ZhiChiConstant.client_model_customService){
                if (satisfactionSetBase != null){
                    if (satisfactionSetBase.getIsTagMust()){
                        sobot_text_other_problem.setText(getResString("sobot_what_are_the_problems") + getResString("sobot_required"));
                    } else {
                        sobot_text_other_problem.setText(getResString("sobot_what_are_the_problems"));
                    }
                } else {
                    sobot_text_other_problem.setText(getResString("sobot_what_are_the_problems"));
                }
            } else {
                sobot_text_other_problem.setText(getResString("sobot_what_are_the_problems"));
            }
        }

        switch (tmpData.length) {
            case 1:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                break;
            case 2:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                break;
            case 3:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                break;
            case 4:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                break;
            case 5:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable5.setText(tmpData[4]);
                sobot_evaluate_cb_lable5.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable6.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.VISIBLE);
                break;
            case 6:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable5.setText(tmpData[4]);
                sobot_evaluate_cb_lable5.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable6.setText(tmpData[5]);
                sobot_evaluate_cb_lable6.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private int getResovled(){
        if(current_model ==ZhiChiConstant.client_model_robot){
            if(sobot_btn_ok_robot.isChecked()){
                return 0;
            } else {
                return 1;
            }
        } else if (current_model ==ZhiChiConstant.client_model_customService){
            if (satisfactionSetBase != null && satisfactionSetBase.getIsQuestionFlag()){
                if(sobot_btn_ok_robot.isChecked()){
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        return -1;
    }

    private SobotCommentParam getCommentParam(){
        SobotCommentParam param = new SobotCommentParam();
        String type = current_model == ZhiChiConstant.client_model_robot?"0":"1";
        int score = (int) Math.ceil(sobot_ratingBar.getRating());
        String problem = checkBoxIsChecked();
        String suggest = sobot_add_content.getText().toString();
        param.setType(type);
        param.setProblem(problem);
        param.setSuggest(suggest);
        param.setIsresolve(getResovled());
        param.setCommentType(commentType);
        if(current_model == ZhiChiConstant.client_model_robot){
            param.setRobotFlag(initModel.getCurrentRobotFlag());
        } else {
            param.setScore(score+"");
        }
        return param;
    }

    //提交评价
    private void subMitEvaluate(){
        if(!checkInput()){
            return;
        }

        comment();
    }

    /**
     * 检查是否能提交评价
     * @return
     */
    private boolean checkInput(){
        if(current_model == ZhiChiConstant.client_model_customService){
            if (satisfactionSetBase != null){
                SobotCommentParam commentParam = getCommentParam();
                if (!"5".equals(commentParam.getScore())){
                    if(!TextUtils.isEmpty(satisfactionSetBase.getLabelName()) && satisfactionSetBase.getIsTagMust()){
                        if (TextUtils.isEmpty(commentParam.getProblem())){
                            ToastUtil.showToast(context,getResString("sobot_the_label_is_required"));//标签必选
                            return false;
                        }
                    }

                    if(satisfactionSetBase.getIsInputMust()){
                        if (TextUtils.isEmpty(commentParam.getSuggest())){
                            ToastUtil.showToast(context,getResString("sobot_suggestions_are_required"));//建议必填
                            return false;
                        }
                    }
                }
            }
        } else if(current_model == ZhiChiConstant.client_model_robot){
            return true;
        }

        return true;
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private static String[] convertStrToArray(String str) {
        String[] strArray = null;
        if (!TextUtils.isEmpty(str)){
            strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        }
        return strArray;
    }

    //提交评价调用接口
    private void comment() {

        ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
        final SobotCommentParam commentParam = getCommentParam();
        zhiChiApi.comment(initModel.getCid(), initModel.getUid(), commentParam,
                new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        //评论成功 发送广播
                        Intent intent = new Intent();
                        intent.setAction(ZhiChiConstants.dcrc_comment_state);
                        intent.putExtra("commentState", true);
                        intent.putExtra("isFinish", isFinish);
                        intent.putExtra("commentType", commentType);
                        if (!TextUtils.isEmpty(commentParam.getScore())){
                            intent.putExtra("score", Integer.parseInt(commentParam.getScore()));
                        }
                        intent.putExtra("isResolved", commentParam.getIsresolve());

                        CommonUtils.sendLocalBroadcast(context, intent);
                        dismiss();
                    }

                    @Override
                    public void onFailure(Exception arg0, String arg1) {
                        LogUtils.i("失败" + arg1 + "***" + arg0.toString());
                    }
                });
    }

    //检测呗选中的标签
    private String checkBoxIsChecked(){
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).isChecked()){
                str.append(checkBoxList.get(i).getText() + ",");
            }
        }
        return str + "";
    }
}