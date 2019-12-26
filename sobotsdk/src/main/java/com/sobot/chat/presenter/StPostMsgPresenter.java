package com.sobot.chat.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.api.model.SobotPostMsgTemplate;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.dialog.SobotPostMsgTmpListDialog;

import java.util.ArrayList;

/**
 * 留言逻辑处理
 *
 * @author Created by jinxl on 2019/3/5.
 */
public class StPostMsgPresenter {
    public static final String INTENT_KEY_UID = "intent_key_uid";
    public static final String INTENT_KEY_CONFIG = "intent_key_config";
    public static final String INTENT_KEY_GROUPID = "intent_key_groupid";
    public static final String INTENT_KEY_CUSTOMERID = "intent_key_customerid";
    public static final String INTENT_KEY_COMPANYID = "intent_key_companyid";
    public static final String INTENT_KEY_IS_SHOW_TICKET = "intent_key_is_show_ticket";
    public static final String INTENT_KEY_CUS_FIELDS = "intent_key_cus_fields";

    private StPostMsgPresenter() {
    }

    ZhiChiApi mApi;
    private Object mCancelTag;
    private Context mContext;
    private SobotPostMsgTmpListDialog mDialog;
    private ObtainTemplateListDelegate mDelegate;

    //防止获取留言模板列表接口重复调用
    private boolean mIsRunning;

    private boolean mIsActive = true;

    private StPostMsgPresenter(Object cancelTag, Context context) {
        this.mCancelTag = cancelTag;
        this.mContext = context;
        this.mIsActive = true;
        mApi = SobotMsgManager.getInstance(mContext).getZhiChiApi();
    }


    public static StPostMsgPresenter newInstance(Object tag, Context context) {
        return new StPostMsgPresenter(tag, context);
    }

    /**
     * 获取留言模板列表
     */
    public void obtainTemplateList(final String uid, final ObtainTemplateListDelegate delegate) {
        if (TextUtils.isEmpty(uid) || mIsRunning) {
            return;
        }
        mIsRunning = true;
        mDelegate = delegate;
        mApi.getWsTemplate(mCancelTag, uid, new StringResultCallBack<ArrayList<SobotPostMsgTemplate>>() {
            @Override
            public void onSuccess(ArrayList<SobotPostMsgTemplate> datas) {
                if (!mIsActive) {
                    mIsRunning = false;
                    return;
                }
                if (datas != null && datas.size() > 0) {
                    if (datas.size() == 1) {
                        //只有一个 自动点选
                        obtainTmpConfig(uid, datas.get(0).getTemplateId());
                    } else {
                        //弹出列表 进行选择
                        mDialog = showTempListDialog((Activity) mContext, datas, new SobotPostMsgTmpListDialog.SobotDialogListener() {
                            @Override
                            public void onListItemClick(SobotPostMsgTemplate data) {
                                obtainTmpConfig(uid, data.getTemplateId());
                            }
                        });
                        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mIsRunning = false;
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                processReqFailure(e, des);
            }
        });
    }

    /**
     * 启动留言界面
     *
     * @param uid
     * @param config 留言基础配置
     */
    public Intent newPostMsgIntent(String uid, SobotLeaveMsgConfig config) {

        Intent intent = new Intent(mContext, SobotPostMsgActivity.class);
        intent.putExtra(INTENT_KEY_UID, uid);
        intent.putExtra(INTENT_KEY_CONFIG, config);

        return intent;
    }

    public interface ObtainTemplateListDelegate {
        void onSuccess(Intent intent);

    }

    /**
     * 打开留言模板列表
     *
     * @param context
     */
    public SobotPostMsgTmpListDialog showTempListDialog(Activity context, ArrayList<SobotPostMsgTemplate> datas, SobotPostMsgTmpListDialog.SobotDialogListener listener) {
        if (context == null || datas == null || listener == null) {
            return null;
        }

        SobotPostMsgTmpListDialog dialog = new SobotPostMsgTmpListDialog(context, datas, listener);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    /**
     * 获取留言模板的配置
     *
     * @param uid
     * @param templateId
     */
    private void obtainTmpConfig(final String uid, final String templateId) {
        mApi.getMsgTemplateConfig(mCancelTag, uid, templateId, new StringResultCallBack<SobotLeaveMsgConfig>() {
            @Override
            public void onSuccess(SobotLeaveMsgConfig data) {
                if (!mIsActive) {
                    mIsRunning = false;
                    return;
                }
                if (data != null) {
                    if (mDelegate != null) {
                        mDelegate.onSuccess(newPostMsgIntent(uid, data));
                    }
                }
                mIsRunning = false;
            }

            @Override
            public void onFailure(Exception e, String des) {
                processReqFailure(e, des);
            }
        });
    }

    /**
     * 处理接口失败的情况
     *
     * @param e
     * @param des
     */
    private void processReqFailure(Exception e, String des) {
        mIsRunning = false;
        if (!mIsActive) {
            return;
        }
        ToastUtil.showToast(mContext, des);
    }

    /**
     * 注销接口
     */
    public void destory() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mIsActive = false;
        OkHttpUtils.getInstance().cancelTag(mCancelTag);
    }
}
