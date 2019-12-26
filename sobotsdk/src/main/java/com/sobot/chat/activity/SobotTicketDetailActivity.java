package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotPicListAdapter;
import com.sobot.chat.adapter.SobotTicketDetailAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.api.model.StUserDealTicketInfo;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MediaFileUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.dialog.SobotReplyDialog;
import com.sobot.chat.widget.dialog.SobotTicketEvaluateDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SobotTicketDetailActivity extends SobotBaseActivity implements SobotTicketEvaluateDialog.SobotTicketEvaluateCallback,View.OnClickListener {
    public static final String INTENT_KEY_UID = "intent_key_uid";
    public static final String INTENT_KEY_COMPANYID = "intent_key_companyid";
    public static final String INTENT_KEY_TICKET_INFO = "intent_key_ticket_info";

    private String mUid = "";
    private String mCompanyId = "";
    private SobotUserTicketInfo mTicketInfo;
    private int infoFlag;

    private List<Object> mList = new ArrayList<>();
    private ListView mListView;
    private SobotTicketDetailAdapter mAdapter;

    private LinearLayout sobot_evaluate_ll;
    private LinearLayout sobot_reply_ll;

    private SobotReplyDialog replyDialog;

    private SobotUserTicketEvaluate mEvaluate;





    /**
     * @param context 应用程序上下文
     * @return
     */
    public static Intent newIntent(Context context, String companyId, String uid, SobotUserTicketInfo ticketInfo) {
        Intent intent = new Intent(context, SobotTicketDetailActivity.class);
        intent.putExtra(INTENT_KEY_UID, uid);
        intent.putExtra(INTENT_KEY_COMPANYID, companyId);
        intent.putExtra(INTENT_KEY_TICKET_INFO, ticketInfo);
        return intent;
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_ticket_detail");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (getIntent() != null) {
            mUid = getIntent().getStringExtra(INTENT_KEY_UID);
            mCompanyId = getIntent().getStringExtra(INTENT_KEY_COMPANYID);
            mTicketInfo = (SobotUserTicketInfo) getIntent().getSerializableExtra(INTENT_KEY_TICKET_INFO);
            if (mTicketInfo!=null){
                infoFlag=mTicketInfo.getFlag();//保留原始状态
            }
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        setTitle(getResString("sobot_message_details"));
        mListView = (ListView) findViewById(getResId("sobot_listview"));
        sobot_evaluate_ll = (LinearLayout) findViewById(getResId("sobot_evaluate_ll"));
        sobot_reply_ll = (LinearLayout) findViewById(getResId("sobot_reply_ll"));
        sobot_reply_ll.setOnClickListener(this);
        sobot_evaluate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == sobot_evaluate_ll && mEvaluate != null) {
                    ChatUtils.showTicketEvaluateDialog(SobotTicketDetailActivity.this, mEvaluate);
                }
            }
        });



    }

    @Override
    protected void initData() {
        sobot_evaluate_ll.setVisibility(View.GONE);
        sobot_reply_ll.setVisibility(View.GONE);
        if (mTicketInfo == null) {
            return;
        }

        zhiChiApi.getUserDealTicketInfoList(SobotTicketDetailActivity.this, mUid, mCompanyId, mTicketInfo.getTicketId(), new StringResultCallBack<List<StUserDealTicketInfo>>() {

            @Override
            public void onSuccess(List<StUserDealTicketInfo> datas) {
                if (datas != null && datas.size() > 0) {
                    mList.clear();
                    for (StUserDealTicketInfo info:datas){
                        if (info.getFlag()==1){//创建
                            mTicketInfo.setFileList(info.getFileList());
                            break;
                        }
                    }
                    mList.add(mTicketInfo);
                    mList.addAll(datas);

                    for (StUserDealTicketInfo dealTicketInfo:datas) {
//                        StUserDealTicketInfo dealTicketInfo = datas.get(0);
                        if (dealTicketInfo.getFlag() == 3&&mTicketInfo.getFlag()!=3){//有结束标志
                            mTicketInfo.setFlag(3);
                        }

                        if (mTicketInfo.getFlag()!=3&&mTicketInfo.getFlag()<dealTicketInfo.getFlag()){//不是结束
                            mTicketInfo.setFlag(dealTicketInfo.getFlag());
                        }
                        if (dealTicketInfo.getFlag() == 3 && dealTicketInfo.getEvaluate() != null) {
                            mList.add(dealTicketInfo.getEvaluate());
                            mEvaluate = dealTicketInfo.getEvaluate();
                            if (mEvaluate.isOpen()) {
                                if (mEvaluate.isEvalution()) {
                                    //已评价
                                    sobot_evaluate_ll.setVisibility(View.GONE);
                                } else {
                                    sobot_evaluate_ll.setVisibility(View.VISIBLE);
                                    break;

                                }
                            } else {
                                sobot_evaluate_ll.setVisibility(View.GONE);
                            }

                        }
                    }
                    if (mAdapter == null) {
                        mAdapter = new SobotTicketDetailAdapter(SobotTicketDetailActivity.this, mList);
                        mListView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }

                   if (!SobotApi.getSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY)&&mTicketInfo.getFlag()==3){
                        sobot_reply_ll.setVisibility(View.GONE);
                    }else{
                        sobot_reply_ll.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(SobotTicketDetailActivity.this, des);
            }
        });
    }

    @Override
    public void submitEvaluate(final int score, final String remark) {
        zhiChiApi.addTicketSatisfactionScoreInfo(SobotTicketDetailActivity.this, mUid, mCompanyId, mTicketInfo.getTicketId(), score, remark, new StringResultCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                CustomToast.makeText(SobotTicketDetailActivity.this, ResourceUtils.getResString(SobotTicketDetailActivity.this,"sobot_leavemsg_success_tip"), 1000,ResourceUtils.getDrawableId(SobotTicketDetailActivity.this,"sobot_iv_login_right")).show();
                sobot_evaluate_ll.setVisibility(View.GONE);
                for (int i = 0; i < mList.size(); i++) {
                    Object obj = mList.get(i);
                    if (obj instanceof StUserDealTicketInfo) {
                        StUserDealTicketInfo data = (StUserDealTicketInfo) mList.get(i);
                        if (data.getFlag() == 3 && data.getEvaluate() != null) {
                            SobotUserTicketEvaluate evaluate = data.getEvaluate();
                            evaluate.setScore(score);
                            evaluate.setRemark(remark);
                            evaluate.setEvalution(true);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }



            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v==sobot_reply_ll){//回复

            replayDialogShow();
        }

    }


    @Override
    public void onBackPressed() {//返回
        if (mTicketInfo!=null&&infoFlag!=mTicketInfo.getFlag()){
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();

    }

    private SobotReplyDialog.PicCameraListener picCameraListener=new SobotReplyDialog.PicCameraListener() {
        @Override
        public void selectPicFromCameraBySys() {
            if (!CommonUtils.isExitsSdcard()) {
                ToastUtil.showCustomToast(SobotTicketDetailActivity.this, getResString("sobot_sdcard_does_not_exist"),
                        Toast.LENGTH_SHORT);
                return;
            }
            permissionListener = new PermissionListenerImpl() {
                @Override
                public void onPermissionSuccessListener() {
                    if (checkStorageAndCameraPermission() && isCameraCanUse()) {
                        cameraFile = ChatUtils.openCamera(SobotTicketDetailActivity.this);
                    }
                }
            };
            if (!checkStorageAndCameraPermission()) {
                return;
            }
            cameraFile = ChatUtils.openCamera(SobotTicketDetailActivity.this);
        }

        @Override
        public void selectPicFromLocal() {
            permissionListener = new PermissionListenerImpl() {
                @Override
                public void onPermissionSuccessListener() {
                    if (checkStoragePermission()) {
                        ChatUtils.openSelectPic(SobotTicketDetailActivity.this);
                    }
                }
            };
            if (!checkStoragePermission()) {
                return;
            }
            ChatUtils.openSelectPic(SobotTicketDetailActivity.this);

        }

        @Override
        public void selectVedioFromLocal() {
            permissionListener = new PermissionListenerImpl() {
                @Override
                public void onPermissionSuccessListener() {
                    if (checkStoragePermission()) {
                        ChatUtils.openSelectVedio(SobotTicketDetailActivity.this,null);
                    }
                }
            };
            if (!checkStoragePermission()) {
                return;
            }
            ChatUtils.openSelectVedio(SobotTicketDetailActivity.this,null);
        }

        @Override
        public void startPerviewPic(SobotPicListAdapter adapter, int position) {
                         LogUtils.i("当前选择图片位置：" + position);
                        Intent intent = new Intent(SobotTicketDetailActivity.this, SobotPhotoListActivity.class);
                        intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST, adapter.getPicList());
                        intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, position);
                        startActivityForResult(intent, ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS);

        }

        @Override
        public void submitPost(String content,String fileStr) {
            if (TextUtils.isEmpty(content)){
                CustomToast.makeText(SobotTicketDetailActivity.this, ResourceUtils.getResString(SobotTicketDetailActivity.this,"sobot_please_reply_input"),Toast.LENGTH_LONG).show();
                return;
            }
            zhiChiApi.replyTicketContent(this, mUid,mTicketInfo.getTicketId(), content,fileStr,mCompanyId, new StringResultCallBack<String>() {
                @Override
                public void onSuccess(String s) {
                    LogUtils.e(s);

                    if (null!=replyDialog){
                        replyDialog.dismiss();
                        replyDialog=null;

                    }

                    CustomToast.makeText(SobotTicketDetailActivity.this, ResourceUtils.getResString(SobotTicketDetailActivity.this,"sobot_leavemsg_success_tip"), 1000,ResourceUtils.getDrawableId(SobotTicketDetailActivity.this,"sobot_iv_login_right")).show();
                    try {
                        Thread.sleep(1000);//睡眠一秒  延迟拉取数据
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    initData();


                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        }
    };

    private void replayDialogShow(){
        if (replyDialog==null){
            replyDialog=new SobotReplyDialog(this);
            replyDialog.setPicCameraListener(picCameraListener);
        }


        replyDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    String path = ImageUtils.getPath(this, selectedImage);
                    if (MediaFileUtils.isVideoFileType(path)) {
                        MediaPlayer mp = new MediaPlayer();
                        try {
                            mp.setDataSource(this, selectedImage);
                            mp.prepare();
                            int videoTime = mp.getDuration();
                            if (videoTime / 1000 > 15) {
                                ToastUtil.showToast(this, getResString("sobot_upload_vodie_length"));
                                return;
                            }
                            SobotDialogUtils.startProgressDialog(this);
                            sendFileListener.onSuccess(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        SobotDialogUtils.startProgressDialog(this);
                        ChatUtils.sendPicByUriPost(this, selectedImage, sendFileListener);
                    }
                } else {
                    showHint(getResString("sobot_did_not_get_picture_path"));
                }
            } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                if (cameraFile != null && cameraFile.exists()) {
                    SobotDialogUtils.startProgressDialog(this);
                    ChatUtils.sendPicByFilePath(this, cameraFile.getAbsolutePath(), sendFileListener);
                } else {
                    showHint(getResString("sobot_pic_select_again"));
                }
            }
        }

    }

    public void showHint(String content) {
        CustomToast.makeText(this, content, 1000).show();
    }

    private ChatUtils.SobotSendFileListener sendFileListener = new ChatUtils.SobotSendFileListener() {
        @Override
        public void onSuccess(final String filePath) {
            zhiChiApi.fileUploadForPostMsg(SobotTicketDetailActivity.this, mCompanyId, filePath, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {

                    SobotDialogUtils.stopProgressDialog(SobotTicketDetailActivity.this);
                    if (zhiChiMessage.getData() != null) {
                        ZhiChiUploadAppFileModelResult item = new ZhiChiUploadAppFileModelResult();
                        item.setFileUrl(zhiChiMessage.getData().getUrl());
                        item.setFileLocalPath(filePath);
                        item.setViewState(1);
                        replyDialog.addPicView(item);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotDialogUtils.stopProgressDialog(SobotTicketDetailActivity.this);
                    showHint(des);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }
            });
        }

        @Override
        public void onError() {
            SobotDialogUtils.stopProgressDialog(SobotTicketDetailActivity.this);
        }
    };

}