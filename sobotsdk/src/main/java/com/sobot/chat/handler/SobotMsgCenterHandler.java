package com.sobot.chat.handler;

import android.content.Context;
import android.text.TextUtils;

import com.sobot.chat.SobotApi;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.SobotExecutorService;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCompareNewMsgTime;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息中心逻辑处理类
 *
 * @author Created by jinxl on 2018/10/25.
 */
public class SobotMsgCenterHandler {

    /**
     * 获取本地缓存数据 和服务器数据
     * 合并去重后回调
     *
     * @param cancelTag
     * @param context
     * @param uid
     * @param callBack
     */
    public static void getMsgCenterAllData(final Object cancelTag,final Context context, final String uid, final SobotMsgCenterCallBack callBack) {
        SobotExecutorService.executorService().execute(new Runnable() {
            @Override
            public void run() {
                List<SobotMsgCenterModel> msgCenterList = SobotApi.getMsgCenterList(context.getApplicationContext(), uid);
                if (msgCenterList == null) {
                    msgCenterList = new ArrayList<>();
                }
                SobotCompareNewMsgTime compare = new SobotCompareNewMsgTime();
                Collections.sort(msgCenterList, compare);
                if (callBack != null) {
                    callBack.onLocalDataSuccess(msgCenterList);
                }

                List<SobotMsgCenterModel> dataFromServer = getDataFromServer(cancelTag,context, uid);
                if (dataFromServer != null && dataFromServer.size() > 0) {
                    for (int i = 0; i < dataFromServer.size(); i++) {
                        SobotMsgCenterModel tmpData = dataFromServer.get(i);
                        int indexOf = msgCenterList.indexOf(tmpData);
                        if (indexOf == -1) {
                            msgCenterList.add(tmpData);
                        } else {
                            try {
                                msgCenterList.get(indexOf).setId(tmpData.getId());
                            } catch (Exception e) {
                                //ignor
                            }
                        }
                    }
                    Collections.sort(msgCenterList, compare);
                    if (callBack != null) {
                        callBack.onAllDataSuccess(msgCenterList);
                    }
                }
            }
        });
    }

    /**
     * IO Thread
     * 从服务器获取会话列表
     */
    private static List<SobotMsgCenterModel> getDataFromServer(Object cancelTag,Context context, String currentUid) {
        String platformID = SharedPreferencesUtil.getStringData(context.getApplicationContext(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        List<SobotMsgCenterModel> platformList = null;
        if (!TextUtils.isEmpty(platformID) && !TextUtils.isEmpty(currentUid)) {
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context.getApplicationContext()).getZhiChiApi();
            try {
                platformList = zhiChiApi.getPlatformList(cancelTag, platformID, currentUid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return platformList;
    }

    public interface SobotMsgCenterCallBack {
        //本地数据获取成功的回调
        void onLocalDataSuccess(List<SobotMsgCenterModel> msgCenterList);

        //网络数据获取成功后与本地数据合并后的回调
        void onAllDataSuccess(List<SobotMsgCenterModel> msgCenterList);
    }

}
