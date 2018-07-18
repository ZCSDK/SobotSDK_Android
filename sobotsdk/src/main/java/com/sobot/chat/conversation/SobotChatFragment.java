package com.sobot.chat.conversation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.activity.SobotSkillGroupActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.SobotVerControl;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotCommentParam;
import com.sobot.chat.api.model.SobotConnCusParam;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.SobotKeyWordTransfer;
import com.sobot.chat.api.model.SobotLableInfoList;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.api.model.ZhiChiCidsModel;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.api.model.ZhiChiHistoryMessage;
import com.sobot.chat.api.model.ZhiChiHistoryMessageBase;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.AnimationUtil;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ExtAudioRecorder;
import com.sobot.chat.utils.IntenetUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.TimeTools;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.CusEvaluateMessageHolder;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.VoiceMessageHolder;
import com.sobot.chat.voice.AudioPlayCallBack;
import com.sobot.chat.voice.AudioPlayPresenter;
import com.sobot.chat.widget.ClearHistoryDialog;
import com.sobot.chat.widget.ContainsEmojiEditText;
import com.sobot.chat.widget.DropdownListView;
import com.sobot.chat.widget.dialog.SobotEvaluateDialog;
import com.sobot.chat.widget.dialog.SobotRobotListDialog;
import com.sobot.chat.widget.emoji.DisplayRules;
import com.sobot.chat.widget.emoji.Emojicon;
import com.sobot.chat.widget.emoji.InputHelper;
import com.sobot.chat.widget.kpswitch.CustomeChattingPanel;
import com.sobot.chat.widget.kpswitch.util.KPSwitchConflictUtil;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelEmoticonView;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;
import com.sobot.chat.widget.kpswitch.view.CustomeViewFactory;
import com.sobot.chat.widget.kpswitch.widget.KPSwitchPanelLinearLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public class SobotChatFragment extends SobotChatBaseFragment implements View.OnClickListener
        , DropdownListView.OnRefreshListenerHeader,SobotMsgAdapter.SobotMsgCallBack,
        ContainsEmojiEditText.SobotAutoCompleteListener
        ,ChattingPanelEmoticonView.SobotEmoticonClickListener
        ,ChattingPanelUploadView.SobotPlusClickListener,SobotRobotListDialog.SobotRobotListListener {

    //---------------UI控件 START---------------
    public TextView mTitleTextView;
    public TextView sobot_title_conn_status;
    public LinearLayout sobot_container_conn_status;
    public ProgressBar sobot_conn_loading;
    public RelativeLayout net_status_remide;
    public RelativeLayout relative;
    private TextView sobot_tv_satisfaction, notReadInfo, sobot_tv_message,
            sobot_txt_restart_talk;
    private TextView textReConnect;
    private ProgressBar loading_anim_view;
    private TextView txt_loading;
    private ImageView icon_nonet;
    private Button btn_reconnect;
    private RelativeLayout chat_main; // 聊天主窗口;
    private FrameLayout welcome; // 欢迎窗口;
    private DropdownListView lv_message;/* 带下拉的ListView */
    private ContainsEmojiEditText et_sendmessage;// 当前用户输入的信息
    private Button btn_send; // 发送消息按钮
    private ImageButton btn_set_mode_rengong; // 转人工button
    private TextView send_voice_robot_hint;
    private Button btn_upload_view; // 上传图片
    private ImageButton btn_emoticon_view; // 表情面板
    private TextView voice_time_long;/*显示语音时长*/
    private LinearLayout voice_top_image;
    private ImageView image_endVoice;
    private ImageView mic_image;
    private ImageView mic_image_animate; // 图片的动画
    private ImageView recording_timeshort;// 语音太短的图片
    private ImageButton btn_model_edit; // 编辑模式
    private ImageButton btn_model_voice;// 语音模式
    private TextView txt_speak_content; // 发送语音的文字
    private AnimationDrawable animationDrawable;/* 语音的动画 */
    private KPSwitchPanelLinearLayout mPanelRoot; // 聊天下面的面板
    private LinearLayout btn_press_to_speak; // 说话view ;
    private RelativeLayout edittext_layout; // 输入框view;
    private LinearLayout recording_container;// 语音上滑的动画
    private TextView recording_hint;// 上滑的显示文本；
    private RelativeLayout sobot_ll_restart_talk; // 开始新会话布局ID
    private ImageView image_reLoading;
    private LinearLayout sobot_ll_bottom;
    //通告
    private RelativeLayout sobot_announcement; // 通告view ;
    private TextView sobot_announcement_right_icon;
    private TextView sobot_announcement_title;
    //机器人切换按钮
    private LinearLayout sobot_ll_switch_robot;

    private SobotEvaluateDialog mEvaluateDialog;
    private SobotRobotListDialog mRobotListDialog;

    private HorizontalScrollView sobot_custom_menu;//横向滚动布局
    private LinearLayout sobot_custom_menu_linearlayout;
    //---------------UI控件 END---------------

    Information info;

    //-----------
    // 消息列表展示
    private List<ZhiChiMessageBase> messageList = new ArrayList<ZhiChiMessageBase>();
    protected SobotMsgAdapter messageAdapter;

    //--------

    private int showTimeVisiableCustomBtn = 0;/*用户设置几次显示转人工按钮*/
    private List<ZhiChiGroupBase> list_group;

    protected int type = -1;//当前模式的类型
    private boolean isSessionOver = true;//表示此会话是否结束

    private boolean isComment = false;/* 判断用户是否评价过 */
    private boolean isShowQueueTip = true;//是否显示 排队提醒 用以过滤关键字转人工时出现的提醒
    private int queueNum = 0;//排队的人数
    private int queueTimes = 0;//收到排队顺序变化提醒的次数
    private int mUnreadNum = 0;//未读消息数

    private int logCollectTime = 0;//日志上传次数

    //录音相关
    public static final String mVoicePath = ZhiChiConstant.voicePositionPath;
    protected Timer voiceTimer;
    protected TimerTask voiceTimerTask;
    protected int voiceTimerLong = 0;
    protected String voiceTimeLongStr = "00";// 时间的定时的任务
    private int minRecordTime = 60;// 允许录音时间
    private int recordDownTime = minRecordTime - 10;// 允许录音时间 倒计时
    boolean isCutVoice;
    private String voiceMsgId = "";//  语音消息的Id
    private int currentVoiceLong = 0;

    AudioPlayPresenter mAudioPlayPresenter = null;
    AudioPlayCallBack mAudioPlayCallBack = null;
    private String mFileName = null;
    private ExtAudioRecorder extAudioRecorder;

    //以下参数为历史记录需要的接口
    private List<String> cids = new ArrayList<>();//cid的列表
    private int currentCidPosition = 0;//当前查询聊天记录所用的cid位置
    //表示查询cid的接口 当前调用状态 0、未调用 1、调用中 2、调用成功  3、调用失败
    private int queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
    private boolean isInGethistory = false;//表示是否正在查询历史记录
    private boolean isConnCustomerService = false;//控制同一时间 只能调一次转人工接口
    private boolean isNoMoreHistoryMsg = false;

    //键盘相关
    public int currentPanelId = 0;//切换聊天面板时 当前点击的按钮id 为了能切换到对应的view上
    private int mBottomViewtype = 0;//记录键盘的状态

    //---------
    //键盘监听
    private ViewTreeObserver.OnGlobalLayoutListener mKPSwitchListener;

    private MyMessageReceiver receiver;
    //本地广播数据类型实例。
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    public static SobotChatFragment newInstance(Bundle info) {
        Bundle arguments = new Bundle();
        arguments.putBundle("informationBundle", info);
        SobotChatFragment fragment = new SobotChatFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate");
        if (getArguments() != null) {
            Bundle informationBundle = getArguments().getBundle("informationBundle");
            if (informationBundle != null) {
                info = (Information) informationBundle.getSerializable("info");
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("sobot_chat_fragment"), container, false);
        initView(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (info == null) {
            ToastUtil.showToast(mAppContext, getResString("sobot_init_data_is_null"));
            finish();
            return;
        }

        if (TextUtils.isEmpty(info.getAppkey())) {
            Toast.makeText(mAppContext, getResString("sobot_appkey_is_null"), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getAppkey());

        //保存自定义配置
        ChatUtils.saveOptionSet(mAppContext, info);

        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getAppkey());
        SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey()).clearCache();
    }

    @Override
    public void onPause() {
        if (initModel != null){
            if (!isSessionOver){
                //保存会话信息
                saveCache();
            }else{
                //清除会话信息
                clearCache();
            }
            //保存消息列表
            ChatUtils.saveLastMsgInfo(mAppContext,info,info.getAppkey(),initModel,messageList);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        hideReLoading();
        // 取消广播接受者
        if (getActivity() != null) {
            getActivity().unregisterReceiver(receiver);
            KeyboardUtil.detach(getActivity(),mKPSwitchListener);
        }
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(localReceiver);
        }
        // 停止用户的定时任务
        stopUserInfoTimeTask();
        // 停止客服的定时任务
        stopCustomTimeTask();
        stopVoice();
        AudioTools.destory();
        if (mEvaluateDialog != null && mEvaluateDialog.isShowing()) {
            mEvaluateDialog.dismiss();
        }
        if (mRobotListDialog != null && mRobotListDialog.isShowing()) {
            mRobotListDialog.dismiss();
        }
        if(SobotOption.sobotViewListener != null){
            SobotOption.sobotViewListener.onChatActClose(customerState);
        }
        super.onDestroyView();
    }

    private void initView(View rootView) {
        if (rootView == null) {
            return;
        }

        //loading 层
        relative = (RelativeLayout) rootView.findViewById(getResId("sobot_layout_titlebar"));
        mTitleTextView = (TextView) rootView.findViewById(getResId("sobot_text_title"));
        sobot_title_conn_status = (TextView) rootView.findViewById(getResId("sobot_title_conn_status"));
        sobot_container_conn_status = (LinearLayout) rootView.findViewById(getResId("sobot_container_conn_status"));
        sobot_conn_loading = (ProgressBar) rootView.findViewById(getResId("sobot_conn_loading"));
        net_status_remide = (RelativeLayout) rootView.findViewById(getResId("sobot_net_status_remide"));

        relative.setVisibility(View.GONE);
        notReadInfo = (TextView) rootView.findViewById(getResId("notReadInfo"));
        chat_main = (RelativeLayout) rootView.findViewById(getResId("sobot_chat_main"));
        welcome = (FrameLayout) rootView.findViewById(getResId("sobot_welcome"));
        txt_loading = (TextView) rootView.findViewById(getResId("sobot_txt_loading"));
        textReConnect = (TextView) rootView.findViewById(getResId("sobot_textReConnect"));
        loading_anim_view = (ProgressBar) rootView.findViewById(getResId("sobot_image_view"));
        image_reLoading = (ImageView) rootView.findViewById(getResId("sobot_image_reloading"));
        icon_nonet = (ImageView) rootView.findViewById(getResId("sobot_icon_nonet"));
        btn_reconnect = (Button) rootView.findViewById(getResId("sobot_btn_reconnect"));
        btn_reconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                textReConnect.setVisibility(View.GONE);
                icon_nonet.setVisibility(View.GONE);
                btn_reconnect.setVisibility(View.GONE);
                loading_anim_view.setVisibility(View.VISIBLE);
                txt_loading.setVisibility(View.VISIBLE);
                customerInit();
            }
        });

        lv_message = (DropdownListView) rootView.findViewById(getResId("sobot_lv_message"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            lv_message.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        et_sendmessage = (ContainsEmojiEditText) rootView.findViewById(getResId("sobot_et_sendmessage"));
        et_sendmessage.setVisibility(View.VISIBLE);
        et_sendmessage.setTextColor(Color.parseColor("#000000"));
        btn_send = (Button) rootView.findViewById(getResId("sobot_btn_send"));
        btn_set_mode_rengong = (ImageButton) rootView.findViewById(getResId("sobot_btn_set_mode_rengong"));
        send_voice_robot_hint = (TextView) rootView.findViewById(getResId("send_voice_robot_hint"));
        send_voice_robot_hint.setVisibility(View.GONE);
        btn_upload_view = (Button) rootView.findViewById(getResId("sobot_btn_upload_view"));
        btn_emoticon_view = (ImageButton) rootView.findViewById(getResId("sobot_btn_emoticon_view"));
        btn_model_edit = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_edit"));
        btn_model_voice = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_voice"));
        mPanelRoot = (KPSwitchPanelLinearLayout) rootView.findViewById(getResId("sobot_panel_root"));
        btn_press_to_speak = (LinearLayout) rootView.findViewById(getResId("sobot_btn_press_to_speak"));
        edittext_layout = (RelativeLayout) rootView.findViewById(getResId("sobot_edittext_layout"));
        recording_hint = (TextView) rootView.findViewById(getResId("sobot_recording_hint"));
        recording_container = (LinearLayout) rootView.findViewById(getResId("sobot_recording_container"));

        // 开始语音的布局的信息
        voice_top_image = (LinearLayout) rootView.findViewById(getResId("sobot_voice_top_image"));
        // 停止语音
        image_endVoice = (ImageView) rootView.findViewById(getResId("sobot_image_endVoice"));
        // 动画的效果
        mic_image_animate = (ImageView) rootView.findViewById(getResId("sobot_mic_image_animate"));
        // 时长的界面
        voice_time_long = (TextView) rootView.findViewById(getResId("sobot_voiceTimeLong"));
        txt_speak_content = (TextView) rootView.findViewById(getResId("sobot_txt_speak_content"));
        txt_speak_content.setText(getResString("sobot_press_say"));
        recording_timeshort = (ImageView) rootView.findViewById(getResId("sobot_recording_timeshort"));
        mic_image = (ImageView) rootView.findViewById(getResId("sobot_mic_image"));

        sobot_ll_restart_talk = (RelativeLayout) rootView.findViewById(getResId("sobot_ll_restart_talk"));
        sobot_txt_restart_talk = (TextView) rootView.findViewById(getResId("sobot_txt_restart_talk"));
        sobot_tv_message = (TextView) rootView.findViewById(getResId("sobot_tv_message"));
        sobot_tv_satisfaction = (TextView) rootView.findViewById(getResId("sobot_tv_satisfaction"));
        sobot_ll_bottom = (LinearLayout) rootView.findViewById(getResId("sobot_ll_bottom"));
        sobot_ll_switch_robot = (LinearLayout) rootView.findViewById(getResId("sobot_ll_switch_robot"));

        sobot_announcement = (RelativeLayout) rootView.findViewById(getResId("sobot_announcement"));
        sobot_announcement_right_icon = (TextView) rootView.findViewById(getResId("sobot_announcement_right_icon"));
        sobot_announcement_title = (TextView) rootView.findViewById(getResId("sobot_announcement_title"));
        sobot_announcement_title.setSelected(true);

        sobot_custom_menu = (HorizontalScrollView) rootView.findViewById(getResId("sobot_custom_menu"));
        sobot_custom_menu.setVisibility(View.GONE);
        sobot_custom_menu_linearlayout = (LinearLayout) rootView.findViewById(getResId("sobot_custom_menu_linearlayout"));
    }

    /* 处理消息 */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(final android.os.Message msg) {
            if (!isActive()) {
                return;
            }
            switch (msg.what) {
                case ZhiChiConstant.hander_my_senderMessage:/* 我的文本消息 */
                    updateUiMessage(messageAdapter, msg);
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.hander_my_update_senderMessageStatus:
                    updateTextMessageStatus(messageAdapter, msg);
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.update_send_data:
                    ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
                    messageAdapter.updateDataById(myMessage.getId(), myMessage);
                    messageAdapter.notifyDataSetChanged();
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.hander_robot_message:
                    ZhiChiMessageBase zhiChiMessageBasebase = (ZhiChiMessageBase) msg.obj;
                    if(type == ZhiChiConstant.type_robot_first || type == ZhiChiConstant.type_custom_first ){
                        //智能客服模式下，特定问题类型的机器人回答语下显示“转人工”按钮。
                        if(initModel != null && ChatUtils.checkManualType(initModel.getManualType(),
                                zhiChiMessageBasebase.getAnswerType())){
                            //如果此项在工作台上勾选 那就显示转人工按钮
                            zhiChiMessageBasebase.setShowTransferBtn(true);
                        }
                    }

                    if(ZhiChiConstant.type_answer_direct.equals(zhiChiMessageBasebase.getAnswerType())
                            || ZhiChiConstant.type_answer_wizard.equals(zhiChiMessageBasebase.getAnswerType())
                            || "11".equals(zhiChiMessageBasebase.getAnswerType())
                            || "12".equals(zhiChiMessageBasebase.getAnswerType())
                            || "14".equals(zhiChiMessageBasebase.getAnswerType())){
                        if(initModel != null && initModel.isRealuateFlag()){
                            //顶踩开关打开 显示顶踩按钮
                            zhiChiMessageBasebase.setRevaluateState(1);
                        }
                    }

                    if (zhiChiMessageBasebase.getAnswer() != null && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo() != null
                            && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo().getEndFlag()) {
                        // 多轮会话结束时禁用所有多轮会话可点击选项
                        restMultiMsg();
                    }

                    messageAdapter.justAddData(zhiChiMessageBasebase);
                    SobotKeyWordTransfer keyWordTransfer = zhiChiMessageBasebase.getSobotKeyWordTransfer();
                    if (keyWordTransfer != null){
                        if (1 == keyWordTransfer.getTransferFlag()) {
                            transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), false);
                        } else if (2 == keyWordTransfer.getTransferFlag()){
                            ZhiChiMessageBase keyWordBase = new ZhiChiMessageBase();
                            keyWordBase.setSenderFace(zhiChiMessageBasebase.getSenderFace());
                            keyWordBase.setSenderType(ZhiChiConstant.message_sender_type_robot_keyword_msg + "");
                            keyWordBase.setSenderName(zhiChiMessageBasebase.getSenderName());
                            keyWordBase.setSobotKeyWordTransfer(keyWordTransfer);
                            messageAdapter.justAddData(keyWordBase);
                        }
                    }

                    messageAdapter.notifyDataSetChanged();
                    if (SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey()).getInitModel() != null) {
                        //机器人接口比较慢的情况下 用户销毁了view 依旧需要保存好机器人回答
                        SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey()).addMessage(zhiChiMessageBasebase);
                    }
                    // 智能转人工：机器人优先时，如果未知问题或者向导问题则显示转人工
                    if (type == ZhiChiConstant.type_robot_first && (ZhiChiConstant.type_answer_unknown.equals(zhiChiMessageBasebase
                            .getAnswerType()) || ZhiChiConstant.type_answer_guide.equals(zhiChiMessageBasebase
                            .getAnswerType()))) {
                        showTransferCustomer();
                    }

                    gotoLastItem();
                    break;
                case ZhiChiConstant.message_type_wo_sendImage: // 我发送图片 更新ui
                    // 加载更过view隐藏
                    updateUiMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.message_type_send_voice: // 发送语音
                    updateUiMessage(messageAdapter, msg);
                    break;
                // 修改语音的发送状态
                case ZhiChiConstant.message_type_update_voice:
                    updateVoiceStatusMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.message_type_cancel_voice://取消未发送的语音
                    cancelUiVoiceMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_success:
                    setTimeTaskMethod(handler);
                    String id = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, id, ZhiChiConstant.MSG_SEND_STATUS_SUCCESS, 0);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_fail:
                    String resultId = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, resultId, ZhiChiConstant.MSG_SEND_STATUS_ERROR, 0);
                    break;
                case ZhiChiConstant.hander_sendPicIsLoading:
                    String loadId = (String) msg.obj;
                    int uploadProgress = msg.arg1;
                    updateUiMessageStatus(messageAdapter, loadId,ZhiChiConstant.MSG_SEND_STATUS_LOADING, uploadProgress);
                    break;
                case ZhiChiConstant.hander_timeTask_custom_isBusying: // 客服的定时任务
                    // --客服忙碌
                    updateUiMessage(messageAdapter, msg);
                    LogUtils.i("客服的定时任务:" + noReplyTimeCustoms);
                    stopCustomTimeTask();
                    break;
                case ZhiChiConstant.hander_timeTask_userInfo:// 客户的定时任务
                    updateUiMessage(messageAdapter, msg);
                    stopUserInfoTimeTask();
                    LogUtils.i("客户的定时任务的时间  停止定时任务：" + noReplyTimeUserInfo);
                    break;
                case ZhiChiConstant.voiceIsRecoding:
                    // 录音的时间超过一分钟的时间切断进行发送语音
                    if (voiceTimerLong >= minRecordTime * 1000) {
                        isCutVoice = true;
                        voiceCuttingMethod();
                        voiceTimerLong = 0;
                        recording_hint.setText(getResString("sobot_voiceTooLong"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                        recording_timeshort.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        closeVoiceWindows(2);
                        btn_press_to_speak.setPressed(false);
                        currentVoiceLong = 0;
                    } else {
                        final int time = Integer.parseInt(msg.obj.toString());
//					LogUtils.i("录音定时任务的时长：" + time);
                        currentVoiceLong = time;
                        if (time < recordDownTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(voiceTimeLongStr.substring(3) + "''");
                            }
                        } else if (time < minRecordTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(getResString("sobot_count_down") + (minRecordTime * 1000 - time) / 1000);
                            }
                        } else {
                            voice_time_long.setText(getResString("sobot_voiceTooLong"));
                        }
                    }
                    break;
                case ZhiChiConstant.hander_close_voice_view:
                    int longOrShort = msg.arg1;
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.GONE);

                    if (longOrShort == 0){
                        for (int i = messageList.size() - 1; i > 0 ; i--) {
                            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) &&
                                    Integer.parseInt(messageList.get(i).getSenderType()) == 8){
                                messageList.remove(i);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected void initData() {
        setToolBar();
        initBrocastReceiver();
        initListener();
        setupListView();
        loadUnreadNum();
        initSdk(false);
        Intent intent = new Intent(mAppContext, SobotSessionServer.class);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, info.getUid());
        mAppContext.startService(intent);
    }

    private void setToolBar() {
        if (getView() == null) {
            return;
        }

        if (!TextUtils.isEmpty(info.getColor())) {
            relative.setBackgroundColor(Color.parseColor(info.getColor()));
        }

        if (info != null && info.getTitleImgId() != 0) {
            relative.setBackgroundResource(info.getTitleImgId());
        }
        View rootView = getView();
        View toolBar = rootView.findViewById(getResId("sobot_layout_titlebar"));
        View sobot_tv_left = rootView.findViewById(getResId("sobot_tv_left"));
        View sobot_tv_right = rootView.findViewById(getResId("sobot_tv_right"));
        if (toolBar != null) {
            if (sobot_tv_left != null) {
                //找到 Toolbar 的返回按钮,并且设置点击事件,点击关闭这个 Activity
                //设置导航栏返回按钮
                showLeftMenu(sobot_tv_left, getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"));
                sobot_tv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLeftMenuClick(v);
                    }
                });
            }

            if (sobot_tv_right != null) {
                showRightMenu(sobot_tv_right, getResDrawableId("sobot_delete_hismsg_selector"), "");
                sobot_tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightMenuClick(v);
                    }
                });
            }
        }
    }

    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new MyMessageReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 检测网络的状态
        filter.addAction(ZhiChiConstants.chat_remind_post_msg);
        filter.addAction(ZhiChiConstants.sobot_click_cancle);
        filter.addAction(ZhiChiConstants.dcrc_comment_state);/* 人工客服评论成功 */
        filter.addAction(ZhiChiConstants.sobot_close_now);/* 立即结束 */
        filter.addAction(ZhiChiConstants.sobot_close_now_clear_cache);// 立即结束不留缓存
        filter.addAction(ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE);/* 接收通道状态变化 */
        filter.addAction(ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK);/* 机器人转人工关键字  用户选择  技能组  转人工 */
        // 注册广播接收器
        getActivity().registerReceiver(receiver, filter);


        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(mAppContext);
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(ZhiChiConstants.receiveMessageBrocast);
        // 注册广播接收器
        localBroadcastManager.registerReceiver(localReceiver, localFilter);
    }

    private void initListener() {
        //监听聊天的面板
        mKPSwitchListener = KeyboardUtil.attach(getActivity(), mPanelRoot,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        resetEmoticonBtn();
                        if (isShowing) {
                            lv_message.setSelection(messageAdapter.getCount());
                        }
                    }
                });

        notReadInfo.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_upload_view.setOnClickListener(this);
        btn_emoticon_view.setOnClickListener(this);
        btn_model_edit.setOnClickListener(this);
        btn_model_voice.setOnClickListener(this);
        sobot_ll_switch_robot.setOnClickListener(this);

        btn_set_mode_rengong.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doClickTransferBtn();
            }
        });

        lv_message.setDropdownListScrollListener(new DropdownListView.DropdownListScrollListener() {
            @Override
            public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
                if(notReadInfo.getVisibility() == View.VISIBLE && messageList.size() > 0){
                    if (messageList.get(firstVisiableItem) != null && messageList.get(firstVisiableItem).getAnswer() != null
                            && ZhiChiConstant.sobot_remind_type_below_unread == messageList.get(firstVisiableItem).getAnswer().getRemindType()){
                        notReadInfo.setVisibility(View.GONE);
                    }
                }
            }
        });

        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetBtnUploadAndSend();
            }
        });
        et_sendmessage.setSobotAutoCompleteListener(this);

        et_sendmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocused) {
                if (isFocused) {
                    int length = et_sendmessage.getText().toString().trim().length();
                    if (length != 0) {
                        btn_send.setVisibility(View.VISIBLE);
                        btn_upload_view.setVisibility(View.GONE);
                    }
                    //根据是否有焦点切换实际的背景
                    edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_focus"));
                }else{
                    edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_blur"));
                }
            }
        });

        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                resetBtnUploadAndSend();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) { }
        });

        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
        lv_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });

        // 开始新会话
        sobot_txt_restart_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initSdk(true);
            }
        });

        sobot_tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startToPostMsgActivty(false);
            }
        });

        sobot_tv_satisfaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                submitEvaluation(true,5);
            }
        });
    }

    private void setupListView() {
        messageAdapter = new SobotMsgAdapter(getContext(), messageList,this);
        lv_message.setAdapter(messageAdapter);
        lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
        lv_message.setOnRefreshListenerHead(this);
    }

    /**
     * 按住说话动画开始
     */
    private void startMicAnimate(){
        mic_image_animate.setBackgroundResource(getResDrawableId("sobot_voice_animation"));
        animationDrawable = (AnimationDrawable) mic_image_animate.getBackground();
        mic_image_animate.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        recording_hint.setText(getResString("sobot_move_up_to_cancel"));
        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
    }

    public void closeVoiceWindows(int toLongOrShort) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.hander_close_voice_view;
        message.arg1 = toLongOrShort;
        handler.sendMessageDelayed(message,500);
    }

    // 当时间超过1秒的时候自动发送
    public void voiceCuttingMethod() {
        stopVoice();
        sendVoiceMap(1,voiceMsgId);
        voice_time_long.setText("00" + "''");
    }

    /**
     * 开始录音
     */
    private void startVoice() {
        try {
            stopVoice();
            mFileName = mVoicePath + UUID.randomUUID().toString() + ".wav";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                LogUtils.i("sd卡被卸载了");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtils.i("文件夹创建失败");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(false);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    startMicAnimate();
                    startVoiceTimeTask(handler);
                    sendVoiceMap(0,voiceMsgId);
                }

                @Override
                public void onNoPermission() {
                    ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                }
            });
        } catch (Exception e) {
            LogUtils.i("prepare() failed");
        }
    }

    /* 停止录音 */
    private void stopVoice() {
		/* 布局的变化 */
        try {
            if (extAudioRecorder != null) {
                stopVoiceTimeTask();
                extAudioRecorder.stop();
                extAudioRecorder.release();
            }
        } catch (Exception e) {}
    }

    /**
     * 录音的时间控制
     */
    public void startVoiceTimeTask(final Handler handler) {
        voiceTimerLong = 0;
        stopVoiceTimeTask();
        voiceTimer = new Timer();
        voiceTimerTask = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                sendVoiceTimeTask(handler);
            }
        };
        // 500ms进行定时任务
        voiceTimer.schedule(voiceTimerTask, 0, 500);

    }

    /**
     * 发送声音的定时的任务
     *
     * @param handler
     */
    public void sendVoiceTimeTask(Handler handler) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.voiceIsRecoding;
        voiceTimerLong = voiceTimerLong + 500;
        message.obj = voiceTimerLong;
        handler.sendMessage(message);
    }

    public void stopVoiceTimeTask() {
        if (voiceTimer != null) {
            voiceTimer.cancel();
            voiceTimer = null;
        }
        if (voiceTimerTask != null) {
            voiceTimerTask.cancel();
            voiceTimerTask = null;
        }
        voiceTimerLong = 0;
    }

    /**
     * 发送语音的方式
     * @param type 0：正在录制语音。  1：发送语音。2：取消正在录制的语音显示
     * @param voiceMsgId  语音消息ID
     */
    private void sendVoiceMap(int type,String voiceMsgId) {
        // 发送语音的界面
        if (type == 0) {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ANIM, SEND_VOICE, handler);
        }else if(type == 2){
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ERROR, CANCEL_VOICE, handler);
        } else {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            // 发送http 返回发送成功的按钮
            sendVoice(voiceMsgId, voiceTimeLongStr, initModel.getCid(), initModel.getUid(), mFileName, handler);
            lv_message.setSelection(messageAdapter.getCount());
        }
        gotoLastItem();
    }

    /**
     * 获取未读消息
     */
    private void loadUnreadNum(){
        mUnreadNum = SobotMsgManager.getInstance(mAppContext).getUnreadCount(info.getAppkey(),true,info.getUid());
    }

    /**
     * 初始化sdk
     * @param isReConnect 是否是重新接入
     **/
    private void initSdk(boolean isReConnect) {
        if(isReConnect){
            current_client_model = ZhiChiConstant.client_model_robot;
            showTimeVisiableCustomBtn = 0;
            messageList.clear();
            messageAdapter.notifyDataSetChanged();
            cids.clear();
            currentCidPosition = 0;
            queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
            isNoMoreHistoryMsg = false;
            isAboveZero = false;
            isComment = false;// 重新开始会话时 重置为 没有评价过
            customerState = CustomerState.Offline;
            remindRobotMessageTimes = 0;
            queueTimes = 0;
            isSessionOver = false;

            sobot_txt_restart_talk.setVisibility(View.GONE);
            sobot_tv_message.setVisibility(View.GONE);
            sobot_tv_satisfaction.setVisibility(View.GONE);
            image_reLoading.setVisibility(View.VISIBLE);
            AnimationUtil.rotate(image_reLoading);

            lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表

            String last_current_dreceptionistId = SharedPreferencesUtil.getStringData(
                    mAppContext,info.getAppkey()+"_"+ZhiChiConstant.SOBOT_RECEPTIONISTID,"");
            info.setReceptionistId(last_current_dreceptionistId);
            resetUser();
        } else {
            //检查配置项是否发生变化
            if(ChatUtils.checkConfigChange(mAppContext,info.getAppkey(),info)){
                resetUser();
            } else {
                doKeepsessionInit();
            }
        }
    }

    /**
     * 重置用户
     */
    private void resetUser(){
        if (!SobotVerControl.isPlatformVer) {
            zhiChiApi.disconnChannel();
        }
        clearCache();
        SharedPreferencesUtil.saveStringData(mAppContext,
                info.getAppkey()+"_"+ZhiChiConstant.sobot_last_login_group_id, TextUtils.isEmpty(info.getSkillSetId())?"":info.getSkillSetId());
        customerInit();
    }

    /**
     * 调用初始化接口
     */
    private void customerInit() {

        if (info.getInitModeType() == ZhiChiConstant.type_robot_only){
            ChatUtils.userLogout(mAppContext);
        }

        zhiChiApi.sobotInit(SobotChatFragment.this,info, new StringResultCallBack<ZhiChiInitModeBase>() {
            @Override
            public void onSuccess(ZhiChiInitModeBase result) {
                if (!isActive()) {
                    return;
                }
                initModel = result;
                getAnnouncement();
                if(info.getInitModeType() > 0){
                    initModel.setType(info.getInitModeType()+"");
                }
                type = Integer.parseInt(initModel.getType());
                SharedPreferencesUtil.saveIntData(mAppContext,
                        info.getAppkey()+"_"+ZhiChiConstant.initType, type);
                //初始化查询cid
                queryCids();

                //查询自定义标签
                sobotCustomMenu();

                //设置初始layout,无论什么模式都是从机器人的UI变过去的
                showRobotLayout();

                if (!TextUtils.isEmpty(initModel.getUid())){
                    SharedPreferencesUtil.saveStringData(mAppContext, Const.SOBOT_CID,initModel.getUid());
                }
                SharedPreferencesUtil.saveIntData(mAppContext,
                        ZhiChiConstant.sobot_msg_flag, initModel.getMsgFlag());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        "lastCid",initModel.getCid());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        info.getAppkey()+"_"+ZhiChiConstant.sobot_last_current_partnerId,info.getUid());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        ZhiChiConstant.sobot_last_current_appkey,info.getAppkey());

                SharedPreferencesUtil.saveStringData(mAppContext,info.getAppkey()+"_"+ZhiChiConstant.SOBOT_RECEPTIONISTID, TextUtils.isEmpty(info.getReceptionistId())?"":info.getReceptionistId());
                SharedPreferencesUtil.saveStringData(mAppContext,info.getAppkey()+"_"+ZhiChiConstant.SOBOT_ROBOT_CODE, TextUtils.isEmpty(info.getRobotCode())?"":info.getRobotCode());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_TELSHOWFLAG,initModel.isTelShowFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_TELFLAG,initModel.isTelFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_EMAILFLAG,initModel.isEmailFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_EMAILSHOWFLAG,initModel.isEmailShowFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_ENCLOSURESHOWFLAG,initModel.isEnclosureShowFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,ZhiChiConstant.SOBOT_POSTMSG_ENCLOSUREFLAG,initModel.isEnclosureFlag());

                initModel.setColor(info.getColor());

                if (type == ZhiChiConstant.type_robot_only){
                    remindRobotMessage(handler,initModel,info);
                    showSwitchRobotBtn();
                } else if (type == ZhiChiConstant.type_robot_first) {
                    //机器人优先
                    if(initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue){
                        //机器人优先 时需要判断  是否需要保持会话
                        if(initModel.getUstatus() == ZhiChiConstant.ustatus_queue){
                            remindRobotMessage(handler,initModel,info);
                        }
                        //机器人会话保持
                        connectCustomerService("","");
                    }else{
                        //仅机器人或者机器人优先，不需要保持会话
                        remindRobotMessage(handler,initModel,info);
                        showSwitchRobotBtn();
                    }
                } else {
                    if (type == ZhiChiConstant.type_custom_only) {
                        //仅人工客服
                        if (isUserBlack()) {
                            showLeaveMsg();
                        } else {
                            transfer2Custom(null, null, null, true);
                        }
                    } else if (type == ZhiChiConstant.type_custom_first) {
                        //客服优先
                        showSwitchRobotBtn();
                        transfer2Custom(null, null, null, true);
                    }
                }
                isSessionOver = false;
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                if(e instanceof IllegalArgumentException){
                    if(LogUtils.isDebug){
                        ToastUtil.showLongToast(mAppContext,des);
                    }
                    finish();
                }else{
                    showInitError();
                }
                isSessionOver=true;
            }
        });
    }

    /**
     * 会话保持初始化的逻辑
     */
    private void doKeepsessionInit(){
        List<ZhiChiMessageBase> tmpList = SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey()).getMessageList();
        if(tmpList != null && SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey()).getInitModel() != null){
            //有数据
            int lastType =  SharedPreferencesUtil.getIntData(mAppContext,
                    info.getAppkey()+"_"+ZhiChiConstant.initType, -1);
            if(info.getInitModeType() < 0 || lastType == info.getInitModeType()){
                if(!TextUtils.isEmpty(info.getSkillSetId())){
                    //判断是否是上次的技能组
                    String lastUseGroupId = SharedPreferencesUtil.getStringData(mAppContext, info.getAppkey()+"_"+ZhiChiConstant.sobot_last_login_group_id, "");
                    if(lastUseGroupId.equals(info.getSkillSetId())){
                        keepSession(tmpList);
                    } else {
                        resetUser();
                    }
                }else{
                    keepSession(tmpList);
                }
            }else{
                resetUser();
            }
        } else {
            resetUser();
        }
    }

    /**
     * 显示下线的逻辑
     * @param initModel
     * @param outLineType  下线的类型
     */
    public void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
        if(initModel == null){
            return;
        }
        queueNum = 0;
        stopInputListener();
        stopUserInfoTimeTask();
        stopCustomTimeTask();
        customerState = CustomerState.Offline;

        // 设置提醒
        showOutlineTip(initModel,outLineType);
        //更改底部键盘
        setBottomView(ZhiChiConstant.bottomViewtype_outline);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_outline;

        if(Integer.parseInt(initModel.getType()) == ZhiChiConstant.type_custom_only) {
            if(1 == outLineType){
                //如果在排队中 客服离开，那么提示无客服
                showLogicTitle(getResString("sobot_no_access"),false);
            }
        }

        if (6 == outLineType) {
            LogUtils.i("打开新窗口");
        }
        isSessionOver = true;
        // 发送用户离线的广播
        CommonUtils.sendLocalBroadcast(mAppContext,new Intent(Const.SOBOT_CHAT_USER_OUTLINE));
    }

    /**
     * 发出离线提醒
     * @param initModel
     * @param outLineType 下线类型
     */
    private void showOutlineTip(ZhiChiInitModeBase initModel,int outLineType){
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        reply.setMsg(ChatUtils.getMessageContentByOutLineType(mAppContext, initModel, outLineType));
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_outline);
        base.setAnswer(reply);
        if (1 == outLineType) {
            base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
        } else if (2 == outLineType) {
            base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
        } else if (3 == outLineType) {
            base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            if(initModel != null){
                initModel.setIsblack("1");
            }
        } else if (4 == outLineType) {
            base.setAction(ZhiChiConstant.action_remind_past_time);
        } else if (6 == outLineType) {
            base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
        }
        // 提示会话结束
        updateUiMessage(messageAdapter, base);
    }

    /**
     * 显示排队提醒
     */
    private void showInLineHint(){
        // 更新界面的操作
        updateUiMessage(messageAdapter, ChatUtils.getInLineHint(mAppContext,queueNum));
        gotoLastItem();
    }

    //保持会话
    private void keepSession(List<ZhiChiMessageBase> tmpList) {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey());
        initModel =  config.getInitModel();
        updateFloatUnreadIcon();
        mUnreadNum = 0;
        messageAdapter.addData(tmpList);
        messageAdapter.notifyDataSetChanged();
        current_client_model = config.current_client_model;
        type = Integer.parseInt(initModel.getType());
        SharedPreferencesUtil.saveIntData(mAppContext,
                info.getAppkey()+"_"+ZhiChiConstant.initType, type);
        LogUtils.i("sobot----type---->" + type);
        initModel.setColor(info.getColor());
        showLogicTitle(config.activityTitle,false);
        showSwitchRobotBtn();
        customerState = config.customerState;
        remindRobotMessageTimes = config.remindRobotMessageTimes;
        isComment = config.isComment;
        isAboveZero = config.isAboveZero;
        currentUserName=config.currentUserName;
        isNoMoreHistoryMsg=config.isNoMoreHistoryMsg;
        currentCidPosition = config.currentCidPosition;
        queryCidsStatus = config.queryCidsStatus;
        isShowQueueTip = config.isShowQueueTip;
        if(config.cids != null ){
            cids.addAll(config.cids);
        }
        showTimeVisiableCustomBtn = config.showTimeVisiableCustomBtn;
        queueNum = config.queueNum;
        if(isNoMoreHistoryMsg){
            lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
        }
        setAdminFace(config.adminFace);
        mBottomViewtype = config.bottomViewtype;
        setBottomView(config.bottomViewtype);
        if(config.userInfoTimeTask){
            stopUserInfoTimeTask();
            startUserInfoTimeTask(handler);
        }
        if(config.customTimeTask){
            stopCustomTimeTask();
            startCustomTimeTask(handler);
        }
        //设置自动补全参数
        et_sendmessage.setRequestParams(initModel.getUid(),initModel.getCurrentRobotFlag());
        if(customerState == CustomerState.Online &&current_client_model == ZhiChiConstant.client_model_customService){
            createConsultingContent();
            //人工模式关闭自动补全功能
            et_sendmessage.setAutoCompleteEnable(false);
        } else {
            //其他状态下开启自动补全
            et_sendmessage.setAutoCompleteEnable(true);
        }
        lv_message.setSelection(messageAdapter.getCount());
        getAnnouncement();
        sobotCustomMenu();
        config.clearMessageList();
        config.clearInitModel();
        isSessionOver = false;
    }

    /**
     * 机器人智能转人工时，判断是否应该显示转人工按钮
     */
    private void showTransferCustomer(){
        showTimeVisiableCustomBtn++;
        if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()){
            btn_set_mode_rengong.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取客户传入的技能组id 直接转人工
     */
    private void transfer2CustomBySkillId(){
        requestQueryFrom(info.getSkillSetId(),info.getSkillSetName());
    }

    /**
     * 显示表情按钮   如果没有表情资源则不会显示此按钮
     */
    private void showEmotionBtn(){
        Map<String, Integer> mapAll = DisplayRules.getMapAll(mAppContext);
        if(mapAll.size() > 0){
            btn_emoticon_view.setVisibility(View.VISIBLE);
        } else {
            btn_emoticon_view.setVisibility(View.GONE);
        }
    }

    /**
     * 转人工按钮的逻辑封装
     * 如果用户传入了skillId 那么就用这个id直接转人工
     * 如果没有传  那么就检查技能组开关是否打开
     */
    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips){
        if(isUserBlack()){
            connectCustomerService("","", keyword, keywordId, isShowTips);
        } else if (!TextUtils.isEmpty(info.getSkillSetId())){
            //预设技能组转人工
            if(!TextUtils.isEmpty(keyword)){
                connectCustomerService(info.getSkillSetId(),"", keyword, keywordId, isShowTips);
            }else {
                transfer2CustomBySkillId();
            }

        } else {
            if(!TextUtils.isEmpty(keyword)){
                connectCustomerService(tempGroupId, "", keyword, keywordId, isShowTips);
            } else {
                if (initModel.getGroupflag().equals(
                        ZhiChiConstant.groupflag_on) && TextUtils.isEmpty(info.getReceptionistId())) {
                    //如果技能组开启，此时没有指定客服，那么拉取技能组数据
                    getGroupInfo();
                } else {
                    //没有预设技能组，技能组关闭  直接转人工
                    requestQueryFrom("","");
                }
            }

        }
    }

    /**
     * 获取技能组
     */
    private void getGroupInfo() {
        zhiChiApi.getGroupList(SobotChatFragment.this,info.getAppkey(),initModel.getUid(), new StringResultCallBack<ZhiChiGroup>() {
            @Override
            public void onSuccess(ZhiChiGroup zhiChiGroup) {
                if (!isActive()) {
                    return;
                }
                boolean hasOnlineCustom = false;
                if (ZhiChiConstant.groupList_ustatus_time_out.equals(zhiChiGroup.getUstatus())){
                    customerServiceOffline(initModel,4);
                } else {
                    list_group = zhiChiGroup.getData();
                    if (list_group != null && list_group.size() > 0) {
                        for (int i = 0; i < list_group.size(); i++) {
                            if ("true".equals(list_group.get(i).isOnline())) {
                                hasOnlineCustom = true;
                                break;
                            }
                        }
                        if (hasOnlineCustom) {
                            if (list_group.size() >= 2) {
                                if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                                    // 会话保持直接转人工
                                    connectCustomerService("","");
                                } else {
                                    if (!TextUtils.isEmpty(info.getSkillSetId())) {
                                        //指定技能组
                                        transfer2CustomBySkillId();
                                    } else {
                                        Intent intent = new Intent(mAppContext, SobotSkillGroupActivity.class);
                                        intent.putExtra("grouplist", (Serializable) list_group);
                                        intent.putExtra("uid", initModel.getUid());
                                        intent.putExtra("type", type);
                                        intent.putExtra("appkey", info.getAppkey());
                                        intent.putExtra("companyId", initModel.getCompanyId());
                                        intent.putExtra("msgTmp", initModel.getMsgTmp());
                                        intent.putExtra("msgTxt", initModel.getMsgTxt());
                                        intent.putExtra("msgFlag", initModel.getMsgFlag());
                                        startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_GRROUP);
                                    }
                                }
                            } else {
                                //只有一个技能组
                                requestQueryFrom(list_group.get(0).getGroupId(),list_group.get(0).getGroupName());
                            }
                        } else {
                            //技能组没有客服在线
                            connCustomerServiceFail(true);
                        }
                    } else {
                        //没有设置技能组
                        requestQueryFrom("","");
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                ToastUtil.showToast(mAppContext,des);
            }
        });
    }

    /**
     * 转人工失败
     */
    private void connCustomerServiceFail(boolean isShowTips){
        if (type == 2) {
            showLeaveMsg();
        } else {
            showLogicTitle(initModel.getRobotName(),false);
            showSwitchRobotBtn();
            if (isShowTips){
                showCustomerOfflineTip();
            }
            if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                    ZhiChiConstant.client_model_robot) {
                remindRobotMessage(handler,initModel,info);
            }
        }
        gotoLastItem();
    }

    /**
     * 转人工 用户是黑名单
     */
    private void connCustomerServiceBlack(boolean isShowTips) {
        showLogicTitle(initModel.getRobotName(), false);
        showSwitchRobotBtn();
        if (isShowTips){
            showCustomerUanbleTip();
        }
        if (type == ZhiChiConstant.type_custom_first) {
            remindRobotMessage(handler,initModel,info);
        }
    }

    /**
     * 显示机器人的布局
     */
    private void showRobotLayout(){
        if (initModel != null) {
            if (type == 1) {
                //仅机器人
                setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlyrobot;
                showLogicTitle(initModel.getRobotName(),false);
            } else if (type == 3 || type == 4) {
                //机器人优先
                setBottomView(ZhiChiConstant.bottomViewtype_robot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_robot;
                showLogicTitle(initModel.getRobotName(),false);
            }
            //仅人工不需要设置机器人布局
            if (type != ZhiChiConstant.type_custom_only) {
                //除了仅人工模式，打开机器人自动补全功能
                et_sendmessage.setRequestParams(initModel.getUid(),initModel.getCurrentRobotFlag());
                et_sendmessage.setAutoCompleteEnable(true);
            }
        }
    }

    /**
     * 转人工方法
     */
    protected void connectCustomerService(String groupId, String groupName, final String keyword, final String keywordId, final boolean isShowTips) {
        if(isConnCustomerService){
            return;
        }
        isConnCustomerService = true;
        boolean currentFlag = (customerState == CustomerState.Queuing || customerState == CustomerState.Online);

        SobotConnCusParam param = new SobotConnCusParam();
        param.setChooseAdminId(info.getReceptionistId());
        param.setTranFlag(info.getTranReceptionistFlag());
        param.setUid(initModel.getUid());
        param.setCid(initModel.getCid());
        param.setGroupId(groupId);
        param.setGroupName(groupName);
        param.setCurrentFlag(currentFlag);
        param.setKeyword(keyword);
        param.setKeywordId(keywordId);

        zhiChiApi.connnect(SobotChatFragment.this, param,
                new StringResultCallBack<ZhiChiMessageBase>() {
                    @Override
                    public void onSuccess(ZhiChiMessageBase zhichiMessageBase) {
                        isConnCustomerService = false;
                        if (!isActive()) {
                            return;
                        }
                        int status = Integer.parseInt(zhichiMessageBase.getStatus());
                        setAdminFace(zhichiMessageBase.getAface());
                        LogUtils.i("status---:" + status);
                        if (status != 0) {
                            if (status == ZhiChiConstant.transfer_robot_customServeive){
                                //机器人超时下线转人工
                                customerServiceOffline(initModel,4);
                            } else if (status == ZhiChiConstant.transfer_robot_custom_status){
                                //如果设置指定客服的id。并且设置不是必须转入，服务器返回status=6.这个时候要设置receptionistId为null
                                //为null以后继续转人工逻辑。如果技能组开启就弹技能组，如果技能组没有开启，就直接转人工
                                showLogicTitle(initModel.getRobotName(),false);
                                info.setReceptionistId(null);
                                transfer2Custom(null, keyword, keywordId, isShowTips);
                            } else {
                                if (ZhiChiConstant.transfer_customeServeive_success == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_customeServeive_fail == status) {
                                    connCustomerServiceFail(isShowTips);
                                } else if (ZhiChiConstant.transfer_customeServeive_isBalk == status) {
                                    connCustomerServiceBlack(isShowTips);
                                } else if (ZhiChiConstant.transfer_customeServeive_already == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_robot_custom_max_status == status){
                                    if (type == 2){
                                        showLogicTitle(getResString("sobot_wait_full"),true);
                                        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                                        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
                                    }

                                    if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_open){
                                        if (!TextUtils.isEmpty(zhichiMessageBase.getMsg())){
                                            ToastUtil.showLongToast(mAppContext,zhichiMessageBase.getMsg());
                                        } else {
                                            ToastUtil.showLongToast(mAppContext,"抱歉,人工排队已满,请您留言,我们有专项工作人员直接处理您提交的问题~");
                                        }
                                        startToPostMsgActivty(false);
                                    }
                                    showSwitchRobotBtn();
                                }
                            }
                        } else {
                            LogUtils.i("转人工--排队");
                            //开启通道
                            zhiChiApi.connChannel(zhichiMessageBase.getWslinkBak(),
                                    zhichiMessageBase.getWslinkDefault(),initModel.getUid(),zhichiMessageBase.getPuid(),info.getAppkey(),zhichiMessageBase.getWayHttp());
                            customerState = CustomerState.Queuing;
                            isShowQueueTip = isShowTips;
                            createCustomerQueue(zhichiMessageBase.getCount()+"",status, isShowTips);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        isConnCustomerService = false;
                        if (!isActive()) {
                            return;
                        }
                        if(type == 2){
                            setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                            showLogicTitle(getResString("sobot_no_access"),false);
                            isSessionOver=true;
                        }
                        ToastUtil.showToast(mAppContext,des);
                    }
                });
    }

    private void gotoLastItem(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                lv_message.setSelection(messageAdapter.getCount());
            }
        });
    }

    /**
     * 根据未读消息数更新右上角UI  “XX条未读消息”
     */
    private void updateFloatUnreadIcon(){
        if (mUnreadNum >= 10){
            notReadInfo.setVisibility(View.VISIBLE);
            notReadInfo.setText(mUnreadNum + getResString("sobot_new_msg"));
        } else {
            notReadInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 转人工成功的方法
     */
    private void connCustomerServiceSuccess(ZhiChiMessageBase base){
        if (base == null || initModel == null){
            return;
        }
        initModel.setAdminHelloWord(!TextUtils.isEmpty(base.getAdminHelloWord())?base.getAdminHelloWord():initModel.getAdminHelloWord());
        initModel.setAdminTipTime(!TextUtils.isEmpty(base.getServiceOutTime())?base.getServiceOutTime():initModel.getAdminTipTime());
        initModel.setAdminTipWord(!TextUtils.isEmpty(base.getServiceOutDoc())?base.getServiceOutDoc():initModel.getAdminTipWord());

        //开启通道
        zhiChiApi.connChannel(base.getWslinkBak(), base.getWslinkDefault(),initModel.getUid(),
                base.getPuid(),info.getAppkey(),base.getWayHttp());
        createCustomerService(base.getAname(),base.getAface());
    }

    /**
     * 建立与客服的对话
     * @param name 客服的名称
     * @param face  客服的头像
     */
    private void createCustomerService(String name,String face){
        //改变变量
        current_client_model = ZhiChiConstant.client_model_customService;
        customerState = CustomerState.Online;
        isAboveZero = false;
        isComment = false;// 转人工时 重置为 未评价
        queueNum = 0;
        currentUserName = TextUtils.isEmpty(name)?"":name;
        //显示被xx客服接入
        messageAdapter.addData(ChatUtils.getServiceAcceptTip(mAppContext,name));

        //转人工成功以后删除通过机器人关键字选择
        messageAdapter.removeKeyWordTranferItem();

        String adminHelloWord = SharedPreferencesUtil.getStringData(mAppContext,ZhiChiConstant.SOBOT_CUSTOMADMINHELLOWORD,"");
        //显示人工欢迎语
        if (!TextUtils.isEmpty(adminHelloWord)){
            messageAdapter.addData(ChatUtils.getServiceHelloTip(name,face,adminHelloWord));
        } else {
            messageAdapter.addData(ChatUtils.getServiceHelloTip(name,face,initModel.getAdminHelloWord()));
        }
        messageAdapter.notifyDataSetChanged();
        //显示标题
        showLogicTitle(name,false);
        showSwitchRobotBtn();
        //创建咨询项目
        createConsultingContent();
        gotoLastItem();
        //设置底部键盘
        setBottomView(ZhiChiConstant.bottomViewtype_customer);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_customer;

        // 启动计时任务
        restartInputListener();
        stopUserInfoTimeTask();
        is_startCustomTimerTask = false;
        startUserInfoTimeTask(handler);
        hideItemTransferBtn();
        //关闭自动补全功能
        et_sendmessage.setAutoCompleteEnable(false);
    }

    /**
     * 隐藏条目中的转人工按钮
     */
    public void hideItemTransferBtn(){
        if (!isActive()) {
            return;
        }
        // 把机器人回答中的转人工按钮都隐藏掉
        lv_message.post(new Runnable() {

            @Override
            public void run() {

                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
                        continue;
                    }
                    RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                    holder.hideTransferBtn();
                }
            }
        });
    }

    /**
     * 显示客服不在线的提示
     */
    private void showCustomerOfflineTip(){
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsgType(null);
        String adminNoneLineTitle = SharedPreferencesUtil.getStringData(mAppContext,ZhiChiConstant.SOBOT_CUSTOMADMINNONELINETITLE,"");
        if (!TextUtils.isEmpty(adminNoneLineTitle)){
            reply.setMsg(adminNoneLineTitle);
        } else {
            reply.setMsg(initModel.getAdminNonelineTitle());
        }
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_customer_offline);
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAnswer(reply);
        base.setAction(ZhiChiConstant.action_remind_info_post_msg);
        updateUiMessage(messageAdapter, base);
    }

    /**
     * 显示无法转接客服
     */
    private void showCustomerUanbleTip(){
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsgType(null);
        reply.setMsg(getResString("sobot_unable_transfer_to_customer_service"));
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_unable_to_customer);
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAnswer(reply);
        base.setAction(ZhiChiConstant.action_remind_info_post_msg);
        updateUiMessage(messageAdapter, base);
    }

    /**
     * 连接客服时，需要排队
     * 显示排队的处理逻辑
     * @param num		当前排队的位置
     * @param status	当前转人工的返回状态，如果是7，就说明排队已经达到最大值，可以直接留言。
     */
    private void createCustomerQueue(String num, int status, boolean isShowTips){
        if (customerState == CustomerState.Queuing && !TextUtils.isEmpty(num)
                && Integer.parseInt(num) > 0) {
            stopUserInfoTimeTask();
            stopCustomTimeTask();
            stopInputListener();

            queueNum = Integer.parseInt(num);
            //显示当前排队的位置
            if (status != ZhiChiConstant.transfer_robot_custom_max_status && isShowTips){
                showInLineHint();
            }

            if (type == ZhiChiConstant.type_custom_only) {
                showLogicTitle(getResString("sobot_in_line_title"),false);
                setBottomView(ZhiChiConstant.bottomViewtype_onlycustomer_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
            } else {
                showLogicTitle(initModel.getRobotName(),false);
                setBottomView(ZhiChiConstant.bottomViewtype_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
            }

            queueTimes = queueTimes + 1;
            if (type == ZhiChiConstant.type_custom_first) {
                if (queueTimes == 1) {
                    //如果当前为人工优先模式那么在第一次收到
                    remindRobotMessage(handler,initModel,info);
                }
            }
            showSwitchRobotBtn();
        }
    }

    /**
     * 初始化查询cid的列表
     */
    private void queryCids() {
        //如果initmodel 或者  querycid的接口调用中或者已经调用成功那么就不再重复查询
        if(initModel == null || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING
                || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS){
            return;
        }
        long time = SharedPreferencesUtil.getLongData(mAppContext, ZhiChiConstant.SOBOT_CHAT_HIDE_HISTORYMSG_TIME, 0);
        queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_LOADING;
        // 初始化查询cid的列表
        zhiChiApi.queryCids(SobotChatFragment.this,initModel.getUid(),time, new StringResultCallBack<ZhiChiCidsModel>() {

            @Override
            public void onSuccess(ZhiChiCidsModel data) {
                if (!isActive()) {
                    return;
                }
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS;
                cids = data.getCids();
                if(cids != null){
                    boolean hasRepeat = false;
                    for (int i = 0; i < cids.size(); i++) {
                        if(cids.get(i).equals(initModel.getCid())){
                            hasRepeat = true;
                            break;
                        }
                    }
                    if(!hasRepeat){
                        cids.add(initModel.getCid());
                    }
                    Collections.reverse(cids);
                }
                //拉取历史纪录
                getHistoryMessage(true);
            }

            @Override
            public void onFailure(Exception e, String des) {
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE;
            }
        });
    }

    private void showInitError(){
        setTitle(getResString("sobot_prompt"));
        loading_anim_view.setVisibility(View.GONE);
        txt_loading.setVisibility(View.GONE);
        textReConnect.setVisibility(View.VISIBLE);
        icon_nonet.setVisibility(View.VISIBLE);
        btn_reconnect.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.GONE);
        relative.setVisibility(View.GONE);
        welcome.setVisibility(View.VISIBLE);
    }

    /*
	 * 发送咨询内容
	 *
	 */
    @Override
    public void sendConsultingContent(){
        if(customerState == CustomerState.Online && current_client_model == ZhiChiConstant
                .client_model_customService){
            final String title = info.getConsultingContent().getSobotGoodsTitle().trim();
            final String describe = TextUtils.isEmpty(info.getConsultingContent().getSobotGoodsDescribe())?"":info.getConsultingContent().getSobotGoodsDescribe().trim();
            final String lable = TextUtils.isEmpty(info.getConsultingContent().getSobotGoodsLable())?"":info.getConsultingContent().getSobotGoodsLable().trim();
            final String fromUrl = info.getConsultingContent().getSobotGoodsFromUrl().trim();
            if (!TextUtils.isEmpty(fromUrl) && !TextUtils.isEmpty(title)) {
                String content = getResString("sobot_consulting_title") + title + "\n"+
                        (!TextUtils.isEmpty(describe)?getResString("sobot_consulting_describe") + describe + "\n":"")
                        +(!TextUtils.isEmpty(lable)?getResString("sobot_consulting_lable") + lable + "\n":"")
                        + getResString("sobot_consulting_fromurl") + fromUrl;
                sendMsg(content);
            }
        }
    }

    /**
     *
     * @param base
     * @param type
     * @param questionFlag
     * 			0 是正常询问机器人
     * 		    1 是有docId的问答
     * 		    2 是多轮会话
     * @param docId 没有就传Null
     */
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base,int type, int questionFlag, String docId){
        sendMessageToRobot(base,type, questionFlag, docId, null);
    }

    /*发送0、机器人问答 1、文本  2、语音  3、图片*/
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base,int type, int questionFlag, String docId, String multiRoundMsg){
        if (type == 4){
            sendMsgToRobot(base, SEND_TEXT, questionFlag, docId, multiRoundMsg);
        }

		/*图片消息*/
        else if(type == 3){
            // 根据图片的url 上传图片 更新上传图片的进度
            messageAdapter.updatePicStatusById(base.getId(), base.getSendSuccessState());
            messageAdapter.notifyDataSetChanged();
            ChatUtils.sendPicture(mAppContext,initModel.getCid(), initModel.getUid(),
                    base.getContent(), handler, base.getId(), lv_message,messageAdapter);
        }

		/*语音消息*/
        else if (type == 2){
            // 语音的重新上传
            sendVoiceMessageToHandler(base.getId(),  base.getContent(), base.getAnswer()
                    .getDuration(), ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            sendVoice(base.getId(), base.getAnswer().getDuration(), initModel.getCid(),
                    initModel.getUid(), base.getContent(), handler);
        }

		/*文本消息*/
        else if (type == 1){
            // 消息的转换
            sendMsgToRobot(base, UPDATE_TEXT, questionFlag, docId);
        }

		/*机器人问答*/
        else if (type == 0){

            if(!isSessionOver){
                // 消息的转换
                ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                answer.setMsgType(ZhiChiConstant.message_type_text + "");
                answer.setMsg(base.getContent());
                base.setAnswer(answer);
                base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                if (base.getId() == null || TextUtils.isEmpty(base.getId())) {
                    updateUiMessage(messageAdapter, base);
                }
                sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model,  questionFlag, docId);
            }else{
                showOutlineTip(initModel,1);
            }
        }
        gotoLastItem();
    }

    /**
     * 点击了转人工按钮
     */
    @Override
    public void doClickTransferBtn() {
        //转人工按钮
        hidePanelAndKeyboard(mPanelRoot);
        doEmoticonBtn2Blur();
        transfer2Custom(null, null, null, true);
    }

    // 点击播放录音及动画
    @Override
    public void clickAudioItem(ZhiChiMessageBase message) {
        if(mAudioPlayPresenter == null){
            mAudioPlayPresenter = new AudioPlayPresenter(mAppContext);
        }
        if (mAudioPlayCallBack == null) {
            mAudioPlayCallBack = new AudioPlayCallBack() {
                @Override
                public void onPlayStart(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg,true);
                }

                @Override
                public void onPlayEnd(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg,false);
                }
            };
        }
        mAudioPlayPresenter.clickAudio(message,mAudioPlayCallBack);
    }

    public void showVoiceAnim(final ZhiChiMessageBase info, final boolean isShow) {
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                if (info == null) {
                    return;
                }
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof VoiceMessageHolder)) {
                        continue;
                    }
                    VoiceMessageHolder holder = (VoiceMessageHolder) child.getTag();
                    holder.stopAnim();
                    if (holder.message == info) {
                        if (isShow) {
                            holder.startAnim();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void hidePanelAndKeyboard(){
        hidePanelAndKeyboard(mPanelRoot);
    }

    /**
     * 调用顶踩接口
     * @param revaluateFlag true 顶  false 踩
     * @param message 顶踩用的 model
     */
    @Override
    public void doRevaluate(final boolean revaluateFlag,final ZhiChiMessageBase message){
        if(isSessionOver){
            showOutlineTip(initModel,1);
            return;
        }
        zhiChiApi.rbAnswerComment(SobotChatFragment.this,initModel.getUid(), initModel.getCid(), initModel.getCurrentRobotFlag(),
                message.getDocId(), message.getDocName(), revaluateFlag, new StringResultCallBack<CommonModelBase>() {
                    @Override
                    public void onSuccess(CommonModelBase data) {
                        if (!isActive()) {
                            return;
                        }
                        if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(data.getStatus())) {
                            customerServiceOffline(initModel,1);
                        } else if(ZhiChiConstant.client_sendmsg_to_custom_success.equals(data.getStatus())){
                            //改变顶踩按钮的布局
                            message.setRevaluateState(revaluateFlag?2:3);
                            resetRevaluateBtn(message);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        ToastUtil.showToast(mAppContext,"网络错误");
                    }
                });
    }

    /**
     * 客服邀请评价
     * @param evaluateFlag true 直接提交  false 打开评价窗口
     * @param message data
     */
    @Override
    public void doEvaluate(final boolean evaluateFlag,final ZhiChiMessageBase message){
        if(initModel == null || message == null){
            return;
        }
        SobotEvaluateModel sobotEvaluateModel = message.getSobotEvaluateModel();
        if(sobotEvaluateModel == null){
            return;
        }
        if(evaluateFlag){

            SobotCommentParam sobotCommentParam = new SobotCommentParam();
            sobotCommentParam.setType("1");
            sobotCommentParam.setScore("5");
            sobotCommentParam.setCommentType(0);
            sobotCommentParam.setIsresolve(sobotEvaluateModel.getIsResolved());
            zhiChiApi.comment(SobotChatFragment.this,initModel.getCid(), initModel.getUid(), sobotCommentParam, new StringResultCallBack<CommonModel>() {
                @Override
                public void onSuccess(CommonModel commonModel) {
                    if (!isActive()) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction(ZhiChiConstants.dcrc_comment_state);
                    intent.putExtra("commentState", true);
                    intent.putExtra("commentType", 0);
                    intent.putExtra("score", message.getSobotEvaluateModel().getScore());
                    intent.putExtra("isResolved", message.getSobotEvaluateModel().getIsResolved());
                    CommonUtils.sendLocalBroadcast(mAppContext, intent);
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        } else {
            submitEvaluation(false,sobotEvaluateModel.getScore());
        }

    }

    /**
     * 更新 顶踩 按钮
     * @param message
     */
    private void resetRevaluateBtn(final ZhiChiMessageBase message){
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                if (message == null) {
                    return;
                }
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
                        continue;
                    }
                    RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                    holder.resetRevaluateBtn();
                }
            }
        });
    }

    /**
     * 更新 客服邀请评价
     */
    private void resetCusEvaluate(){
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof CusEvaluateMessageHolder)) {
                        continue;
                    }
                    CusEvaluateMessageHolder holder = (CusEvaluateMessageHolder) child.getTag();
                    holder.checkEvaluateStatus();
                }
            }
        });
    }

    //通告设置
    private void getAnnouncement(){
        if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl())){
            sobot_announcement_right_icon.setVisibility(View.VISIBLE);
        } else {
            sobot_announcement_right_icon.setVisibility(View.GONE);
        }

        if (initModel.getAnnounceMsgFlag() && !TextUtils.isEmpty(initModel.getAnnounceMsg())){
            sobot_announcement.setVisibility(View.VISIBLE);
            sobot_announcement_title.setText(initModel.getAnnounceMsg());
            sobot_announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 内部浏览器
                    if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl()) && initModel.getAnnounceClickFlag()){
                        Intent intent = new Intent(mAppContext, WebViewActivity.class);
                        intent.putExtra("url", initModel.getAnnounceClickUrl());
                        startActivity(intent);
                    }
                }
            });
        } else {
            sobot_announcement.setVisibility(View.GONE);
        }
    }

    /**
     * 设置底部键盘UI
     * @param viewType
     */
    public void setBottomView(int viewType){
        welcome.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);
        chat_main.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.VISIBLE);
        sobot_ll_restart_talk.setVisibility(View.GONE);
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        hideReLoading();
        if (isUserBlack()) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            btn_model_voice.setVisibility(View.GONE);
            btn_emoticon_view.setVisibility(View.GONE);
        }
        sobot_tv_satisfaction.setVisibility(View.VISIBLE);
        sobot_txt_restart_talk.setVisibility(View.VISIBLE);
        sobot_tv_message.setVisibility(View.VISIBLE);

        LogUtils.i("setBottomView:"+viewType);
        switch(viewType){
            case ZhiChiConstant.bottomViewtype_onlyrobot:
                // 仅机器人
                showVoiceBtn();
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(false);
                    btn_set_mode_rengong.setVisibility(View.GONE);
                }
                btn_emoticon_view.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                break;
            case ZhiChiConstant.bottomViewtype_robot:
                //机器人对话框
                if (info.isArtificialIntelligence() && type == ZhiChiConstant.type_robot_first){
                    //智能转人工只适用于机器人优先
                    if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()){
                        btn_set_mode_rengong.setVisibility(View.VISIBLE);
                    } else {
                        btn_set_mode_rengong.setVisibility(View.GONE);
                    }
                } else {
                    btn_set_mode_rengong.setVisibility(View.VISIBLE);
                }

                btn_set_mode_rengong.setClickable(true);
                showVoiceBtn();
                if (Build.VERSION.SDK_INT >= 11)
                    btn_set_mode_rengong.setAlpha(1f);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(true);
                    btn_set_mode_rengong.setEnabled(true);
                }
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_emoticon_view.setVisibility(View.GONE);
                break;
            case ZhiChiConstant.bottomViewtype_customer:
                //人工对话框
                hideRobotVoiceHint();
                btn_model_edit.setVisibility(View.GONE);
                btn_set_mode_rengong.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                showEmotionBtn();
                showVoiceBtn();
                btn_model_voice.setEnabled(true);
                btn_model_voice.setClickable(true);
                btn_upload_view.setEnabled(true);
                btn_upload_view.setClickable(true);
                btn_emoticon_view.setClickable(true);
                btn_emoticon_view.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 11){
                    btn_model_voice.setAlpha(1f);
                    btn_upload_view.setAlpha(1f);
                }

                edittext_layout.setVisibility(View.VISIBLE);
                sobot_ll_bottom.setVisibility(View.VISIBLE);
                btn_press_to_speak.setVisibility(View.GONE);
                btn_press_to_speak.setClickable(true);
                btn_press_to_speak.setEnabled(true);
                txt_speak_content.setText(getResString("sobot_press_say"));
                break;
            case ZhiChiConstant.bottomViewtype_onlycustomer_paidui:
                //仅人工排队中
                onlyCustomPaidui();

                hidePanelAndKeyboard(mPanelRoot);
                if(lv_message.getLastVisiblePosition()!=messageAdapter.getCount()){
                    lv_message.setSelection(messageAdapter.getCount());
                }
                break;
            case ZhiChiConstant.bottomViewtype_outline:
                //被提出
                hideReLoading();
                hidePanelAndKeyboard(mPanelRoot);/*隐藏键盘*/
                sobot_ll_bottom.setVisibility(View.GONE);
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);
                sobot_tv_satisfaction.setVisibility(View.VISIBLE);
                sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                btn_model_edit.setVisibility(View.GONE);
                sobot_tv_message.setVisibility(initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close?View
                        .GONE:View.VISIBLE);
                btn_model_voice.setVisibility(View.GONE);
                lv_message.setSelection(messageAdapter.getCount());
                break;
            case ZhiChiConstant.bottomViewtype_paidui:
                //智能模式下排队中
                if (btn_press_to_speak.getVisibility() == View.GONE) {
                    showVoiceBtn();
                }
                btn_set_mode_rengong.setVisibility(View.VISIBLE);
                btn_emoticon_view.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    btn_model_voice.setVisibility(View.GONE);
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                }
                break;
            case ZhiChiConstant.bottomViewtype_custom_only_msgclose:
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);

                sobot_ll_bottom.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE){
                    sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                    sobot_txt_restart_talk.setClickable(true);
                    sobot_txt_restart_talk.setEnabled(true);
                }
                if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close){
                    //留言关闭
                    sobot_tv_satisfaction.setVisibility(View.INVISIBLE);
                    sobot_tv_message.setVisibility(View.INVISIBLE);
                } else {
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                    sobot_tv_message.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    //仅人工时排队UI更新
    private void onlyCustomPaidui() {
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        btn_set_mode_rengong.setVisibility(View.GONE);
        btn_set_mode_rengong.setClickable(false);

        btn_upload_view.setVisibility(View.VISIBLE);
        btn_upload_view.setClickable(false);
        btn_upload_view.setEnabled(false);

        showEmotionBtn();
        btn_emoticon_view.setClickable(false);
        btn_emoticon_view.setEnabled(false);

        showVoiceBtn();
        btn_model_voice.setClickable(false);
        btn_model_voice.setEnabled(false);

        if (Build.VERSION.SDK_INT >= 11){
            btn_upload_view.setAlpha(0.4f);
            btn_model_voice.setAlpha(0.4f);
        }

        edittext_layout.setVisibility(View.GONE);
        btn_press_to_speak.setClickable(false);
        btn_press_to_speak.setEnabled(false);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        txt_speak_content.setText(getResString("sobot_in_line"));

        if (sobot_ll_restart_talk.getVisibility() == View.VISIBLE) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
        }
    }

    private void createConsultingContent() {
        ConsultingContent consultingContent = info.getConsultingContent();
        if(consultingContent != null && !TextUtils.isEmpty(consultingContent.getSobotGoodsTitle()) && !TextUtils.isEmpty(consultingContent.getSobotGoodsFromUrl())){
            ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
            zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_consult_info + "");
            if(!TextUtils.isEmpty(consultingContent.getSobotGoodsImgUrl())){
                zhichiMessageBase.setPicurl(consultingContent.getSobotGoodsImgUrl());
            }
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            zhichiMessageBase.setAnswer(reply);
            zhichiMessageBase.setT(consultingContent.getSobotGoodsTitle());
            zhichiMessageBase.setUrl(consultingContent.getSobotGoodsFromUrl());
            zhichiMessageBase.setCid(initModel == null?"":initModel.getCid());
            zhichiMessageBase.setAname(consultingContent.getSobotGoodsLable());
            zhichiMessageBase.setReceiverFace(consultingContent.getSobotGoodsDescribe());

            zhichiMessageBase.setAction(ZhiChiConstant.action_consultingContent_info);
            updateUiMessage(messageAdapter, zhichiMessageBase);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lv_message.setSelection(messageAdapter.getCount());
                }
            });
        } else {
            if (messageAdapter != null){
                messageAdapter.removeConsulting();
            }
        }
    }

    /**
     * 根据输入框里的内容切换显示  发送按钮还是加号（更多方法）
     */
    private void resetBtnUploadAndSend(){
        if (et_sendmessage.getText().toString().length() > 0) {
            btn_upload_view.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        } else {
            btn_send.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
            btn_upload_view.setEnabled(true);
            btn_upload_view.setClickable(true);
            if (Build.VERSION.SDK_INT >= 11){
                btn_upload_view.setAlpha(1f);
            }
        }
    }

    /**
     * 根据逻辑判断显示当前的title
     * 根据客服传入的title显示模式显示聊天页面的标题
     * @param title 此处传如的值为默认需要显示的昵称 或者提示等等
     * @param ignoreLogic 表示忽略逻辑直接显示
     */
    private void showLogicTitle(String title,boolean ignoreLogic){
        String str = ChatUtils.getLogicTitle(mAppContext,ignoreLogic, title, initModel.getCompanyName());
        if(!TextUtils.isEmpty(str)){
            setTitle(str);
        }
    }

    // 设置标题内容
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    /**
     * 导航栏左边点击事件
     *
     * @param view
     */
    protected void onLeftMenuClick(View view) {
        hidePanelAndKeyboard(mPanelRoot);
        onBackPress();
    }

    /**
     * 导航栏右边点击事件
     *
     * @param view
     */
    protected void onRightMenuClick(View view) {
        hidePanelAndKeyboard(mPanelRoot);
        ClearHistoryDialog clearHistoryDialog = new ClearHistoryDialog(getActivity());
        clearHistoryDialog.setCanceledOnTouchOutside(true);
        clearHistoryDialog.setOnClickListener(new ClearHistoryDialog.DialogOnClickListener() {
            @Override
            public void onSure() {
                clearHistory();
            }
        });
        clearHistoryDialog.show();
    }

    public void clearHistory(){
        zhiChiApi.deleteHisMsg(SobotChatFragment.this,initModel.getUid(), new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase modelBase) {
                if (!isActive()) {
                    return;
                }
                messageList.clear();
                cids.clear();
                messageAdapter.notifyDataSetChanged();
                lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
            }

            @Override
            public void onFailure(Exception e, String des) {}
        });
    }

    /**
     * 隐藏重新开始会话的菊花
     */
    public void hideReLoading(){
        image_reLoading.clearAnimation();
        image_reLoading.setVisibility(View.GONE);
    }

    /**
     * 重置表情按钮的焦点键盘
     */
    public void resetEmoticonBtn() {
        String panelViewTag = getPanelViewTag(mPanelRoot);
        String instanceTag = CustomeViewFactory.getInstanceTag(mAppContext, btn_emoticon_view.getId());
        if (mPanelRoot.getVisibility() == View.VISIBLE && instanceTag.equals(panelViewTag)) {
            doEmoticonBtn2Focus();
        } else {
            doEmoticonBtn2Blur();
        }
    }

    /**
     * 使表情按钮获取焦点
     */
    public void doEmoticonBtn2Focus() {
        btn_emoticon_view.setSelected(true);
    }

    /**
     * 使表情按钮失去焦点
     */
    public void doEmoticonBtn2Blur() {
        btn_emoticon_view.setSelected(false);
    }

    /**
     * 仅人工的无客服在线的逻辑
     */
    private void showLeaveMsg(){
        LogUtils.i("仅人工，无客服在线");
        showLogicTitle(getResString("sobot_no_access"),false);
        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
        if (isUserBlack()){
            showCustomerUanbleTip();
        } else {
            showCustomerOfflineTip();
        }
        isSessionOver = true;
    }

    /**
     * 输入表情的方法
     * @param item
     */
    @Override
    public void inputEmoticon(Emojicon item){
        InputHelper.input2OSC(et_sendmessage,item);
    }

    /**
     * 输入框删除的方法
     */
    @Override
    public void backspace(){
        InputHelper.backspace(et_sendmessage);
    }

    /**
     * 提供给聊天面板执行的方法
     * 图库
     */
    @Override
    public void btnPicture(){
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromLocal();
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * 提供给聊天面板执行的方法
     * 照相
     */
    @Override
    public void btnCameraPicture(){
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromCamera(); // 拍照 上传
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * 提供给聊天面板执行的方法
     * 满意度
     */
    @Override
    public void btnSatisfaction(){
        lv_message.setSelection(messageAdapter.getCount());
        //满意度逻辑 点击时首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
        hidePanelAndKeyboard(mPanelRoot);
        submitEvaluation(true,5);
    }

    @Override
    public void startToPostMsgActivty(boolean FLAG_EXIT_SDK) {
        if (initModel == null){
            return;
        }

        if(SobotOption.sobotLeaveMsgListener  != null){
            SobotOption.sobotLeaveMsgListener.onLeaveMsg();
            return;
        }

        Intent intent = new Intent(mAppContext, SobotPostMsgActivity.class);
        intent.putExtra("uid", initModel.getUid());
        intent.putExtra("companyId", initModel.getCompanyId());
        intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, FLAG_EXIT_SDK);
        intent.putExtra("msgTmp", initModel.getMsgTmp());
        intent.putExtra("msgTxt", initModel.getMsgTxt());
        intent.putExtra("groupId", info.getSkillSetId());
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "push_left_in"),
                    ResourceUtils.getIdByName(mAppContext, "anim", "push_left_out"));
        }
    }

    /**
     * 切换表情按钮焦点
     */
    public void switchEmoticonBtn(){
        boolean flag = btn_emoticon_view.isSelected();
        if(flag){
            doEmoticonBtn2Blur();
        }else{
            doEmoticonBtn2Focus();
        }
    }

    //切换键盘和面板的方法
    public void switchPanelAndKeyboard(final View panelLayout,final View switchPanelKeyboardBtn, final View focusView) {
        if(currentPanelId == 0 || currentPanelId == switchPanelKeyboardBtn.getId()){
            //没选中的时候或者  点击是自身的时候正常切换面板和键盘
            boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
            if (!switchToPanel) {
                KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
            } else {
                KPSwitchConflictUtil.showPanel(panelLayout);
                setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
            }
        } else {
            //之前选过  但是现在点击的不是自己的时候  显示自己的面板
            KPSwitchConflictUtil.showPanel(panelLayout);
            setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
        }
        currentPanelId = switchPanelKeyboardBtn.getId();
    }

    /*
     * 切换键盘和面板的方法   考虑了当键盘为按住说话时的情况 一般都用这个就行
     * 参数是按下的那个按钮
     */
    public void pressSpeakSwitchPanelAndKeyboard(final View switchPanelKeyboardBtn){
        if (btn_press_to_speak.isShown()) {
            btn_model_edit.setVisibility(View.GONE);
            showVoiceBtn();
            btn_press_to_speak.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);

            et_sendmessage.requestFocus();
            KPSwitchConflictUtil.showPanel(mPanelRoot);
            setPanelView(mPanelRoot, switchPanelKeyboardBtn.getId());
            currentPanelId = switchPanelKeyboardBtn.getId();
        }else{
            //切换更多方法的面板
            switchPanelAndKeyboard(mPanelRoot, switchPanelKeyboardBtn, et_sendmessage);
        }
    }

    /**
     * 设置聊天面板的view
     * @param panelLayout
     * @param btnId
     */
    private void setPanelView(final View panelLayout,int btnId){
        if(panelLayout instanceof KPSwitchPanelLinearLayout){
            KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if(childView != null && childView instanceof CustomeChattingPanel){
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                Bundle bundle = new Bundle();
                bundle.putInt("current_client_model",current_client_model);
                customeChattingPanel.setupView(btnId,bundle,SobotChatFragment.this);
            }
        }
    }

    /**
     * 获取当前显示的聊天面板的tag
     *
     * @param panelLayout
     */
    private String getPanelViewTag(final View panelLayout) {
        String str = "";
        if (panelLayout instanceof KPSwitchPanelLinearLayout) {
            KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                str = customeChattingPanel.getPanelViewTag();
            }
        }
        return str;
    }

    /**
     * 隐藏键盘和面板
     * @param layout
     */
    public void hidePanelAndKeyboard(KPSwitchPanelLinearLayout layout){
        et_sendmessage.dismissPop();
        KPSwitchConflictUtil.hidePanelAndKeyboard(layout);
        doEmoticonBtn2Blur();
        currentPanelId = 0;
    }

    /*
     * 弹出提示
     */
    private void showHint(String content) {
        ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        reply.setMsg(content);
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_tip);
        zhichiMessageBase.setAnswer(reply);
        zhichiMessageBase.setAction(ZhiChiConstant.action_remind_no_service);
        updateUiMessage(messageAdapter,zhichiMessageBase);
    }

    @Override
    public void onRobotGuessComplete(String question) {
        //分词联想 选中事件
        et_sendmessage.setText("");
        sendMsg(question);
    }

    @Override
    public void onRefresh() {
        getHistoryMessage(false);
    }

    /**
     * 获取聊天记录
     * @param isFirst 第一次查询历史记录
     */
    public void getHistoryMessage(final boolean isFirst) {
        if (initModel == null)
            return;

        if(queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE){
            //cid列表接口未调用或获取失败的时候重新获取cid
            onLoad();
            queryCids();
        } else if((queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING && !isFirst) || isInGethistory){
            //1.查询cid接口调用中 又不是第一次查询历史记录  那么 直接什么也不做就返回
            //2.如果查询历史记录的接口正在跑   那么什么也不做
            onLoad();
        } else {
            String currentCid = ChatUtils.getCurrentCid(initModel,cids,currentCidPosition);
            if("-1".equals(currentCid)){
                showNoHistory();
                onLoad();
                return;
            }
            isInGethistory = true;
            zhiChiApi.getChatDetailByCid(SobotChatFragment.this,initModel.getUid(), currentCid, new StringResultCallBack<ZhiChiHistoryMessage>() {
                @Override
                public void onSuccess(ZhiChiHistoryMessage zhiChiHistoryMessage) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    onLoad();
                    currentCidPosition++;
                    List<ZhiChiHistoryMessageBase> data = zhiChiHistoryMessage.getData();
                    if(data != null && data.size() > 0){
                        showData(data);
                    } else {
                        //没有数据的时候继续拉
                        getHistoryMessage(false);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    mUnreadNum = 0;
                    updateFloatUnreadIcon();
                    onLoad();
                }
            });
        }
    }

    private void showData(List<ZhiChiHistoryMessageBase> result){
        List<ZhiChiMessageBase> msgLists = new ArrayList<>();
        List<ZhiChiMessageBase> msgList;
        for (int i = 0; i < result.size(); i++) {
            ZhiChiHistoryMessageBase historyMsg = result.get(i);
            msgList = historyMsg.getContent();

            for (ZhiChiMessageBase base : msgList) {
                base.setSugguestionsFontColor(1);
                if (base.getSdkMsg() != null) {
                    ZhiChiReplyAnswer answer = base.getSdkMsg().getAnswer();
                    if (answer != null){
                        if (answer.getMsgType() == null){
                            answer.setMsgType("0");
                        }

                        if (!TextUtils.isEmpty(answer.getMsg()) && answer.getMsg().length() > 4){
                            String msg = answer.getMsg().replace("&lt;/p&gt;","<br>");
                            if (msg.endsWith("<br>")){
                                msg = msg.substring(0, msg.length()-4);
                            }
                            answer.setMsg(msg);
                        }
                    }
                    if (ZhiChiConstant.message_sender_type_robot == Integer
                            .parseInt(base.getSenderType())) {
                        base.setSenderName(TextUtils.isEmpty(base.getSenderName()) ? initModel
                                .getRobotName() : base.getSenderName());
                        base.setSenderFace(TextUtils.isEmpty(base.getSenderFace()) ? initModel
                                .getRobotLogo() : base.getSenderFace());
                    }
                    base.setAnswer(answer);
                    base.setSugguestions(base.getSdkMsg()
                            .getSugguestions());
                    base.setStripe(base.getSdkMsg().getStripe());
                    base.setAnswerType(base.getSdkMsg()
                            .getAnswerType());
                }
            }
            msgLists.addAll(msgList);
        }

        if (msgLists.size() > 0) {
            if(mUnreadNum > 0){
                ZhiChiMessageBase unreadMsg = ChatUtils.getUnreadMode(mAppContext);
                unreadMsg.setCid(msgLists.get(msgLists.size()-1).getCid());
                msgLists.add((msgLists.size() - mUnreadNum) < 0 ? 0:(msgLists.size() - mUnreadNum)
                        ,unreadMsg);
                updateFloatUnreadIcon();
                mUnreadNum = 0;
            }
            messageAdapter.addData(msgLists);
            messageAdapter.notifyDataSetChanged();
            lv_message.setSelection(msgLists.size());
        }
    }

    /**
     * 显示没有更多历史记录
     */
    private void showNoHistory(){
        ZhiChiMessageBase base = new ZhiChiMessageBase();

        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

        ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
        reply1.setRemindType(ZhiChiConstant.sobot_remind_type_nomore);
        reply1.setMsg(getResString("sobot_no_more_data"));
        base.setAnswer(reply1);
        // 更新界面的操作
        updateUiMessageBefore(messageAdapter, base);
        lv_message.setSelection(0);

        lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
        isNoMoreHistoryMsg = true;
        mUnreadNum = 0;
    }

    private void onLoad() {
        lv_message.onRefreshCompleteHeader();
    }

    // 键盘编辑模式转换为语音模式
    private void editModelToVoice(int typeModel, String str) {
        btn_model_edit.setVisibility(View.GONE == typeModel ? View.GONE
                : View.VISIBLE); // 键盘编辑隐藏
        btn_model_voice.setVisibility(View.VISIBLE != typeModel ? View.VISIBLE
                : View.GONE);// 语音模式开启
        btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
                : View.GONE);
        edittext_layout.setVisibility(View.VISIBLE == typeModel ? View.GONE
                : View.VISIBLE);

        if (!TextUtils.isEmpty(et_sendmessage.getText().toString()) && str.equals("123")) {
            btn_send.setVisibility(View.VISIBLE);
            btn_upload_view.setVisibility(View.GONE);
        } else {
            btn_send.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
        }
    }

    public void setShowNetRemind(boolean isShow) {
        net_status_remide.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 广播接受者：
     */
    public class MyMessageReceiver extends BroadcastReceiver {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (!CommonUtils.isNetWorkConnected(mAppContext)) {
                    //没有网络
                    if (welcome.getVisibility() != View.VISIBLE){
                        setShowNetRemind(true);
                    }
                } else {
                    // 有网络
                    setShowNetRemind(false);
                    if(IntenetUtil.isWifiConnected(mAppContext) && logCollectTime == 0){
                        logCollectTime++;
                        zhiChiApi.logCollect(mAppContext,info.getAppkey());
                    }
                }
            } else if (ZhiChiConstants.chat_remind_post_msg.equals(intent.getAction())) {
                startToPostMsgActivty(false);
            } else if (ZhiChiConstants.sobot_click_cancle.equals(intent.getAction())) {
                //打开技能组后点击了取消
                if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                        ZhiChiConstant.client_model_robot) {
                    remindRobotMessage(handler,initModel,info);
                }
            } else if (ZhiChiConstants.dcrc_comment_state.equals(intent.getAction())) {
                //评价完客户后所需执行的逻辑
                isComment = intent.getBooleanExtra("commentState", false);
                boolean isFinish = intent.getBooleanExtra("isFinish", false);
                int commentType = intent.getIntExtra("commentType", 1);

                //如果是邀请评价 更新ui
                int score = intent.getIntExtra("score", 5);
                int isResolved = intent.getIntExtra("isResolved", 0);
                messageAdapter.submitEvaluateData(isResolved,score);
                resetCusEvaluate();

                //配置用户提交人工满意度评价后释放会话
                if(ChatUtils.isEvaluationCompletedExit(mAppContext,isComment,current_client_model)){
                    //如果是人工并且评价完毕就释放会话
                    customerServiceOffline(initModel,1);
                    ChatUtils.userLogout(mAppContext);
                }
                if (isActive()) {
                    ChatUtils.showThankDialog(getActivity(),handler,isFinish);
                }
            } else if (ZhiChiConstants.sobot_close_now.equals(intent.getAction())){
                finish();
            } else if (ZhiChiConstants.sobot_close_now_clear_cache.equals(intent.getAction())){
                isSessionOver = true;
                finish();
            } else if (ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE.equals(intent.getAction())){
                if(customerState == CustomerState.Online || customerState == CustomerState.Queuing){
                    int connStatus = intent.getIntExtra("connStatus", Const.CONNTYPE_IN_CONNECTION);
                    LogUtils.i("connStatus:"+connStatus);
                    switch (connStatus){
                        case Const.CONNTYPE_IN_CONNECTION:
                            sobot_container_conn_status.setVisibility(View.VISIBLE);
                            sobot_title_conn_status.setText(getResString("sobot_conntype_in_connection"));
                            mTitleTextView.setVisibility(View.GONE);
                            sobot_conn_loading.setVisibility(View.VISIBLE);
                            break;
                        case Const.CONNTYPE_CONNECT_SUCCESS:
                            setShowNetRemind(false);
                            sobot_container_conn_status.setVisibility(View.GONE);
                            sobot_title_conn_status.setText(getResString("sobot_conntype_connect_success"));
                            mTitleTextView.setVisibility(View.VISIBLE);
                            sobot_conn_loading.setVisibility(View.GONE);
                            break;
                        case Const.CONNTYPE_UNCONNECTED:
                            sobot_container_conn_status.setVisibility(View.VISIBLE);
                            sobot_title_conn_status.setText(getResString("sobot_conntype_unconnected"));
                            mTitleTextView.setVisibility(View.GONE);
                            sobot_conn_loading.setVisibility(View.GONE);
                            if (welcome.getVisibility() != View.VISIBLE){
                                setShowNetRemind(true);
                            }
                            break;
                    }
                }else{
                    mTitleTextView.setVisibility(View.VISIBLE);
                    sobot_container_conn_status.setVisibility(View.GONE);
                }
            } else if (ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK.equals(intent.getAction())){
                String tempGroupId = intent.getStringExtra("tempGroupId");
                String keyword = intent.getStringExtra("keyword");
                String keywordId = intent.getStringExtra("keywordId");
                transfer2Custom(tempGroupId, keyword, keywordId, true);
            }
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("广播是  :" + intent.getAction());
            if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                // 接受下推的消息
                ZhiChiPushMessage pushMessage = null;
                try {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                    }
                } catch (Exception e) {
                    //ignor
                }
                if(pushMessage == null || !info.getAppkey().equals(pushMessage.getAppId())){
                    return;
                }
                ZhiChiMessageBase base = new ZhiChiMessageBase();
                base.setSenderName(pushMessage.getAname());

                if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
                    setAdminFace(pushMessage.getAface());
                    if (type == 2 || type == 3 || type == 4) {
                        createCustomerService(pushMessage.getAname(),pushMessage.getAface());
                    }
                } else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
                    // 排队的消息类型
                    createCustomerQueue(pushMessage.getCount(), 0, isShowQueueTip);
                } else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                    // 接收到新的消息
                    if (customerState == CustomerState.Online) {
                        base.setMsgId(pushMessage.getMsgId());
                        base.setSender(pushMessage.getAname());
                        base.setSenderName(pushMessage.getAname());
                        base.setSenderFace(pushMessage.getAface());
                        base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                        ZhiChiReplyAnswer reply = null;
                        if(TextUtils.isEmpty(pushMessage.getMsgType())){
                            return;
                        }
                        if ("7".equals(pushMessage.getMsgType())) {
                            reply = GsonUtil.jsonToZhiChiReplyAnswer(pushMessage.getContent());
                        } else {
                            reply = new ZhiChiReplyAnswer();
                            reply.setMsgType(pushMessage.getMsgType() + "");
                            reply.setMsg(pushMessage.getContent());
                        }
                        base.setAnswer(reply);
                        stopCustomTimeTask();
                        startUserInfoTimeTask(handler);
                        // 更新界面的操作
                        messageAdapter.justAddData(base);
                        ChatUtils.msgLogicalProcess(initModel, messageAdapter, pushMessage);
                        messageAdapter.notifyDataSetChanged();
                    }
                } else if (ZhiChiConstant.push_message_outLine == pushMessage.getType()) {
                    // 用户被下线
                    customerServiceOffline(initModel,Integer.parseInt(pushMessage.getStatus()));
                } else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
                    LogUtils.i("用户被转接--->"+pushMessage.getName());
                    //替换标题
                    showLogicTitle(pushMessage.getName(),false);
                    setAdminFace(pushMessage.getFace());
                    currentUserName = pushMessage.getName();
                } else if (ZhiChiConstant.push_message_custom_evaluate == pushMessage.getType()){
                    LogUtils.i("客服推送满意度评价.................");
                    //显示推送消息体
                    if (isAboveZero && !isComment && customerState == CustomerState.Online) {
                        // 满足评价条件，并且之前没有评价过的话 才能 弹评价框
                        ZhiChiMessageBase customEvaluateMode = ChatUtils.getCustomEvaluateMode(pushMessage);
                        // 更新界面的操作
                        updateUiMessage(messageAdapter, customEvaluateMode);
                    }
                } else if (ZhiChiConstant.push_message_retracted == pushMessage.getType()) {
                    if (!TextUtils.isEmpty(pushMessage.getRevokeMsgId())) {
                        List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            ZhiChiMessageBase msgData = datas.get(i);
                            if (pushMessage.getRevokeMsgId().equals(msgData.getMsgId())) {
                                if (!msgData.isRetractedMsg()) {
                                    msgData.setRetractedMsg(true);
                                    messageAdapter.notifyDataSetChanged();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //保存当前的数据，进行会话保持
    private void saveCache() {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getAppkey());
        config.isShowUnreadUi = true;
        config.setMessageList(messageList);
        config.setInitModel(initModel);
        config.current_client_model = current_client_model;
        if (queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS) {
            config.cids = cids;
            config.currentCidPosition = currentCidPosition;
            config.queryCidsStatus = queryCidsStatus;
        }

        config.activityTitle = getActivityTitle();
        config.customerState = customerState;
        config.remindRobotMessageTimes=remindRobotMessageTimes;
        config.isAboveZero=isAboveZero;
        config.isComment=isComment;
        config.adminFace=getAdminFace();
        config.customTimeTask=customTimeTask;
        config.userInfoTimeTask=userInfoTimeTask;
        config.currentUserName=currentUserName;
        config.isNoMoreHistoryMsg=isNoMoreHistoryMsg;
        config.showTimeVisiableCustomBtn = showTimeVisiableCustomBtn;
        config.bottomViewtype = mBottomViewtype;
        config.queueNum = queueNum;
        config.isShowQueueTip = isShowQueueTip;

    }

    @Override
    public void onClick(View view) {
        if (view == notReadInfo) {
            for (int i = messageList.size() - 1; i >= 0; i--) {
                if (messageList.get(i).getAnswer() != null && ZhiChiConstant.
                        sobot_remind_type_below_unread == messageList.get(i).getAnswer().getRemindType()){
                    lv_message.setSelection(i);
                    break;
                }
            }
            notReadInfo.setVisibility(View.GONE);
        }

        if (view == btn_send) {// 发送消息按钮
            //获取发送内容
            final String message_result = et_sendmessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message_result) && !isConnCustomerService) {
                //转人工接口没跑完的时候  屏蔽发送，防止统计出现混乱
                resetEmoticonBtn();
                try {
                    et_sendmessage.setText("");
                    sendMsg(message_result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (view == btn_upload_view) {// 显示上传view
            pressSpeakSwitchPanelAndKeyboard(btn_upload_view);
            doEmoticonBtn2Blur();
            gotoLastItem();
        }

        if(view == btn_emoticon_view){//显示表情面板
            // 切换表情面板
            pressSpeakSwitchPanelAndKeyboard(btn_emoticon_view);
            //切换表情按钮的状态
            switchEmoticonBtn();
            gotoLastItem();
        }

        if (view == btn_model_edit) {// 从编辑模式转换到语音
            hideRobotVoiceHint();
            doEmoticonBtn2Blur();
            // 软件盘的处理
            KPSwitchConflictUtil.showKeyboard(mPanelRoot, et_sendmessage);
            editModelToVoice(View.GONE, "123");// 编辑模式隐藏 ，语音模式显示
        }

        if (view == btn_model_voice) { // 从语音转换到编辑模式
            showRobotVoiceHint();
            doEmoticonBtn2Blur();
            if(!checkStorageAndAudioPermission()){
                return;
            }
            try {
                mFileName = mVoicePath + "sobot_tmp.wav";
                String state = android.os.Environment.getExternalStorageState();
                if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    LogUtils.i("SD Card is not mounted,It is  " + state + ".");
                }
                File directory = new File(mFileName).getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    LogUtils.i("Path to file could not be created");
                }
                extAudioRecorder = ExtAudioRecorder.getInstanse(false);
                extAudioRecorder.setOutputFile(mFileName);
                extAudioRecorder.prepare();
                extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                    @Override
                    public void onHasPermission() {
                        hidePanelAndKeyboard(mPanelRoot);
                        editModelToVoice(View.VISIBLE, "");// 编辑模式显示
                        if (btn_press_to_speak.getVisibility() == View.VISIBLE){
                            btn_press_to_speak.setVisibility(View.VISIBLE);
                            btn_press_to_speak.setClickable(true);
                            btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
                            btn_press_to_speak.setEnabled(true);
                            txt_speak_content.setText(getResString("sobot_press_say"));
                            txt_speak_content.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNoPermission() {
                        ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                    }
                });
                stopVoice();
            } catch (Exception e) {
                LogUtils.i("prepare() failed");
            }

        }

        if (view == sobot_ll_switch_robot) {
            // 打开机器人切换页面
            if (!isSessionOver && (mRobotListDialog == null || !mRobotListDialog.isShowing())) {
                mRobotListDialog = ChatUtils.showRobotListDialog(getActivity(),initModel,this);
            }
        }

    }

    private void showRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(current_client_model == ZhiChiConstant.client_model_robot?View.VISIBLE:View.GONE);
    }

    private void hideRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(View.GONE);
    }

    /**
     * 发送消息的方法
     * @param content
     */
    private void sendMsg(String content) {
        if(initModel == null){
            return;
        }

        String msgId = System.currentTimeMillis() + "";

        if (ZhiChiConstant.client_model_robot == current_client_model) {
            if (type == 2) {
                doClickTransferBtn();
                return;
            } else if ((type == 3 || type == 4 ) && info.getTransferKeyWord() != null) {
                //用户可以输入关键字 进行转人工
                HashSet<String> transferKeyWord = info.getTransferKeyWord();
                if (!TextUtils.isEmpty(content) && transferKeyWord.contains(content)) {
                    sendTextMessageToHandler(msgId, content, handler, 1, SEND_TEXT);
                    doClickTransferBtn();
                    return;
                }
            }
        }

        // 通知Handler更新 我的消息ui
        sendTextMessageToHandler(msgId, content, handler, 2, SEND_TEXT);

        LogUtils.i("当前发送消息模式：" + current_client_model);
        setTimeTaskMethod(handler);
        sendMessageWithLogic(msgId, content, initModel, handler, current_client_model,0,"");
    }

    /**
     * 满意度评价
     * 首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
     * @param isActive 是否是主动评价  true 主动  flase 邀请
     */
    public void submitEvaluation(boolean isActive,int score){
        if (isComment) {
            showHint(getResString("sobot_completed_the_evaluation"));
        } else {
            if (isUserBlack()){
                showHint(getResString("sobot_unable_to_evaluate"));
            } else if (isAboveZero) {
                if (isActive()) {
                    if (mEvaluateDialog == null || !mEvaluateDialog.isShowing()) {
                        mEvaluateDialog = ChatUtils.showEvaluateDialog(getActivity(),false,initModel,current_client_model,isActive?1:0,currentUserName,score);
                    }
                }
            } else {
                showHint(getResString("sobot_after_consultation_to_evaluate_custome_service"));
            }
        }
    }

    public void showVoiceBtn() {
        if (current_client_model == ZhiChiConstant.client_model_robot && type != 2) {
            btn_model_voice.setVisibility(info.isUseVoice() && info.isUseRobotVoice()?View.VISIBLE : View.GONE);
        } else {
            btn_model_voice.setVisibility(info.isUseVoice()?View.VISIBLE : View.GONE);
        }
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId){
        sendMsgToRobot(base, sendType, questionFlag, docId, null);
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId, String multiRoundMsg){
        if (!TextUtils.isEmpty(multiRoundMsg)){
            sendTextMessageToHandler(base.getId(), multiRoundMsg, handler, 2, sendType);
        } else {
            sendTextMessageToHandler(base.getId(), base.getContent(), handler, 2, sendType);
        }
        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
        answer.setMsgType(ZhiChiConstant.message_type_text + "");
        answer.setMsg(base.getContent());
        base.setAnswer(answer);
        base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
        sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
    }

    /**
     * 更新 多轮会话的状态
     */
    private void restMultiMsg(){
        for (int i = 0; i < messageList.size(); i++) {
            ZhiChiMessageBase data = messageList.get(i);
            if (data.getAnswer() != null && data.getAnswer().getMultiDiaRespInfo() != null
                    && !data.getAnswer().getMultiDiaRespInfo().getEndFlag()){
                data.setMultiDiaRespEnd(1);
            }
        }
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            LogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        // 通知handler更新图片
                        ChatUtils.sendPicByUri(mAppContext, handler, selectedImage, initModel, lv_message,messageAdapter);
                    } else {
                        ToastUtil.showLongToast(mAppContext,getResString("sobot_did_not_get_picture_path"));
                    }
                } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                    if (cameraFile != null && cameraFile.exists()) {
                        LogUtils.i("cameraFile.getAbsolutePath()------>>>>" + cameraFile.getAbsolutePath());
                        String id = System.currentTimeMillis() + "";
                        ChatUtils.sendPicLimitBySize(cameraFile.getAbsolutePath(), initModel.getCid(),
                                initModel.getUid(), handler, mAppContext, lv_message,messageAdapter);
                    } else {
                        ToastUtil.showLongToast(mAppContext,getResString("sobot_pic_select_again"));
                    }
                }
                hidePanelAndKeyboard(mPanelRoot);
            }
            if(data != null){
                switch (requestCode) {
                    case ZhiChiConstant.REQUEST_COCE_TO_GRROUP:
                        int groupIndex = data.getIntExtra("groupIndex",-1);
                        LogUtils.i("groupIndex-->" + groupIndex);
                        if (groupIndex >= 0) {
                            requestQueryFrom(list_group.get(groupIndex).getGroupId(),list_group.get(groupIndex).getGroupName());
                        }
                        break;
                    case ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM:
                        //填完询前表单后的回调
                        if (resultCode == ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM) {
                            String groupId = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID);
                            String groupName = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME);
                            connectCustomerService(groupId, groupName);
                        } else {
                            //询前表单取消
                            if (type == ZhiChiConstant.type_custom_only) {
                                //仅人工模式退出聊天
                                isSessionOver = true;
                                finish();
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isCutVoice = false;
            // 获取说话位置的点击事件
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    voiceMsgId = System.currentTimeMillis() + "";
                    // 在这个点击的位置
                    btn_upload_view.setClickable(false);
                    btn_model_edit.setClickable(false);
                    btn_upload_view.setEnabled(false);
                    btn_model_edit.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= 11){
                        btn_upload_view.setAlpha(0.4f);
                        btn_model_edit.setAlpha(0.4f);
                    }
                    stopVoiceTimeTask();
                    v.setPressed(true);
                    voice_time_long.setText("00" + "''");
                    voiceTimeLongStr = "00:00";
                    voiceTimerLong = 0;
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.VISIBLE);
                    voice_top_image.setVisibility(View.VISIBLE);
                    mic_image.setVisibility(View.VISIBLE);
                    mic_image_animate.setVisibility(View.VISIBLE);
                    voice_time_long.setVisibility(View.VISIBLE);
                    recording_timeshort.setVisibility(View.GONE);
                    image_endVoice.setVisibility(View.GONE);
                    txt_speak_content.setText(getResString("sobot_up_send"));
                    // 设置语音的定时任务
                    startVoice();
                    return true;
                // 第二根手指按下
                case MotionEvent.ACTION_POINTER_DOWN:
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (!is_startCustomTimerTask) {
                        noReplyTimeUserInfo = 0;
                    }

                    if (event.getY() < 10) {
                        // 取消界面的显示
                        voice_top_image.setVisibility(View.GONE);
                        image_endVoice.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        recording_timeshort.setVisibility(View.GONE);
                        txt_speak_content.setText(getResString("sobot_up_send_calcel"));
                        recording_hint.setText(getResString("sobot_release_to_cancel"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                    } else {
                        if (voiceTimerLong != 0) {
                            txt_speak_content.setText(getResString("sobot_up_send"));
                            voice_top_image.setVisibility(View.VISIBLE);
                            mic_image_animate.setVisibility(View.VISIBLE);
                            image_endVoice.setVisibility(View.GONE);
                            mic_image.setVisibility(View.VISIBLE);
                            recording_timeshort.setVisibility(View.GONE);
                            recording_hint.setText(getResString("sobot_move_up_to_cancel"));
                            recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
                        }
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    // 手指抬起的操作
                    int toLongOrShort = 0;
                    btn_upload_view.setClickable(true);
                    btn_model_edit.setClickable(true);
                    btn_upload_view.setEnabled(true);
                    btn_model_edit.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= 11){
                        btn_upload_view.setAlpha(1f);
                        btn_model_edit.setAlpha(1f);
                    }
                    v.setPressed(false);
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    stopVoiceTimeTask();
                    stopVoice();
                    if (recording_container.getVisibility() == View.VISIBLE
                            && !isCutVoice) {
                        hidePanelAndKeyboard(mPanelRoot);
                        if(animationDrawable != null){
                            animationDrawable.stop();
                        }
                        voice_time_long.setText("00" + "''");
                        voice_time_long.setVisibility(View.INVISIBLE);
                        if (event.getY() < 0) {
                            recording_container.setVisibility(View.GONE);
                            sendVoiceMap(2,voiceMsgId);
                            return true;
                            // 取消发送语音
                        } else {
                            // 发送语音
                            if (currentVoiceLong < 1 * 1000) {
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voice_can_not_be_less_than_one_second"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                voice_time_long.setVisibility(View.VISIBLE);
                                voice_time_long.setText("00:00");
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                                toLongOrShort = 0;
                                sendVoiceMap(2,voiceMsgId);
                            } else if (currentVoiceLong < minRecordTime * 1000) {
                                recording_container.setVisibility(View.GONE);
                                sendVoiceMap(1,voiceMsgId);
                                return true;
                            } else if (currentVoiceLong > minRecordTime * 1000) {
                                toLongOrShort = 1;
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voiceTooLong"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                            } else {
                                sendVoiceMap(2,voiceMsgId);
                            }
                        }
                        currentVoiceLong = 0;
                        closeVoiceWindows(toLongOrShort);
                    }
                    voiceTimerLong = 0;
                    restartMyTimeTask(handler);
                    // mFileName
                    return true;
                default:
                    closeVoiceWindows(2);
                    return true;
            }
        }
    }

    // 获取标题内容
    public String getActivityTitle() {
        return mTitleTextView.getText().toString();
    }

    /**
     * 返回键监听
     * @return true 消费事件
     */
    public void onBackPress() {
        if (isActive()) {
            //按返回按钮的时候 如果面板显示就隐藏面板  如果面板已经隐藏那么就是用户想退出
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                hidePanelAndKeyboard(mPanelRoot);
                return;
            } else {
                if (info.isShowSatisfaction()) {
                    if (isAboveZero && !isComment) {
                        // 退出时 之前没有评价过的话 才能 弹评价框
                        mEvaluateDialog = ChatUtils.showEvaluateDialog(getActivity(),true,initModel,
                                current_client_model,1,currentUserName,5);
                        return;
                    }
                }
            }
            finish();
        }
    }

    protected String getSendMessageStr(){
        return et_sendmessage.getText().toString().trim();
    }

    private void sobotCustomMenu(){
        if (!initModel.isLableLinkFlag()){
            return;
        }
        final int marginRight = (int) getDimens("sobot_layout_lable_margin_right");
        //自定义菜单获取接口
        zhiChiApi.getLableInfoList(SobotChatFragment.this, initModel.getUid(), new StringResultCallBack<List<SobotLableInfoList>>() {
            @Override
            public void onSuccess(final List<SobotLableInfoList> infoLists) {
                if (!isActive()) {
                    return;
                }

                sobot_custom_menu_linearlayout.removeAllViews();
                if (infoLists != null && infoLists.size() > 0){
                    for (int i = 0; i < infoLists.size(); i++) {
                        final TextView tv = (TextView) View.inflate(getContext(), getResLayoutId("sobot_layout_lable"), null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,0, marginRight,0);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(infoLists.get(i).getLableName());
                        tv.setTag(infoLists.get(i).getLableLink());
                        sobot_custom_menu_linearlayout.addView(tv);
                        if (!TextUtils.isEmpty(tv.getTag() + "")){
                            tv.setOnClickListener(mLableClickListener);
                        }
                    }
                    sobot_custom_menu.setVisibility(View.VISIBLE);
                } else {
                    sobot_custom_menu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                sobot_custom_menu.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示切换机器人业务的按钮
     */
    private void showSwitchRobotBtn() {
        if (initModel != null && type != 2 && current_client_model == ZhiChiConstant.client_model_robot) {
            sobot_ll_switch_robot.setVisibility(initModel.isRobotSwitchFlag()?View.VISIBLE:View.GONE);
        } else {
            sobot_ll_switch_robot.setVisibility(View.GONE);
        }
    }

    /**
     * 机器人切换列表的回调
     */
    @Override
    public void onSobotRobotListItemClick(SobotRobot sobotRobot) {
        if (initModel != null && sobotRobot != null) {
            initModel.setGuideFlag(sobotRobot.getGuideFlag());
            initModel.setCurrentRobotFlag(sobotRobot.getRobotFlag());
            initModel.setRobotLogo(sobotRobot.getRobotLogo());
            initModel.setRobotName(sobotRobot.getRobotName());
            showLogicTitle(initModel.getRobotName(),false);
            List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
            int count = 0;
            for (int i = datas.size() - 1; i >= 0; i--) {
                if ((ZhiChiConstant.message_sender_type_robot_welcome_msg +"").equals(datas.get(i).getSenderType())
                        ||(ZhiChiConstant.message_sender_type_questionRecommend +"").equals(datas.get(i).getSenderType())
                        ||(ZhiChiConstant.message_sender_type_robot_guide +"").equals(datas.get(i).getSenderType())) {
                    datas.remove(i);
                    count++;
                    if (count >= 3) {
                        break;
                    }
                }
            }
            messageAdapter.notifyDataSetChanged();
            //切换机器人后调整UI
            remindRobotMessageTimes = 0;
            remindRobotMessage(handler,initModel,info);
        }
    }
}