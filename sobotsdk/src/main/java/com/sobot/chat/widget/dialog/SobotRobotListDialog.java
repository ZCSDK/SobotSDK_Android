package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.sobot.chat.adapter.SobotRobotListAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.api.model.SobotRobotGuess;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;

import java.util.List;

/**
 * 机器人列表界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotRobotListDialog extends SobotActionSheet implements AdapterView.OnItemClickListener, View.OnClickListener {
    private final String CANCEL_TAG = SobotRobotListDialog.class.getSimpleName();
    private LinearLayout coustom_pop_layout;
    private LinearLayout sobot_negativeButton;
    private GridView sobot_gv;

    private String mUid;
    private String mRobotFlag;
    private SobotRobotListListener mListener;

    private SobotRobotListAdapter mListAdapter;

    private SobotRobotListDialog(Activity context) {
        super(context);
    }

    public SobotRobotListDialog(Activity context, String uid, String robotFlag, SobotRobotListListener listListener) {
        super(context);
        mUid = uid;
        mRobotFlag = robotFlag;
        mListener = listListener;
    }

    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_switch_robot";
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initView() {
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_gv = (GridView) findViewById(getResId("sobot_gv"));
        sobot_gv.setOnItemClickListener(this);
        sobot_negativeButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(getContext()).getZhiChiApi();
        zhiChiApi.getRobotSwitchList(CANCEL_TAG, mUid, new StringResultCallBack<List<SobotRobot>>() {

            @Override
            public void onSuccess(List<SobotRobot> sobotRobots) {
                for (SobotRobot bean : sobotRobots) {
                    if (bean.getRobotFlag() != null && bean.getRobotFlag().equals(mRobotFlag)) {
                        bean.setSelected(true);
                        break;
                    }
                }
                if (mListAdapter == null) {
                    mListAdapter = new SobotRobotListAdapter(getContext(), sobotRobots);
                    sobot_gv.setAdapter(mListAdapter);
                } else {
                    List<SobotRobot> datas = mListAdapter.getDatas();
                    datas.clear();
                    datas.addAll(sobotRobots);
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }


    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        OkHttpUtils.getInstance().cancelTag(CANCEL_TAG);
        super.onDetachedFromWindow();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            SobotRobot item = (SobotRobot) mListAdapter.getItem(position);
            if (item.getRobotFlag() != null && !item.getRobotFlag().equals(mRobotFlag)) {
                mListener.onSobotRobotListItemClick((SobotRobot) mListAdapter.getItem(position));
            }
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            dismiss();
        }
    }

    public interface SobotRobotListListener {
        void onSobotRobotListItemClick(SobotRobot sobotRobot);
    }
}