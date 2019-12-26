package com.sobot.chat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseHelpCenterActivity;
import com.sobot.chat.adapter.SobotHelpCenterAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.StCategoryModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ToastUtil;

import java.util.List;

/**
 * 帮助中心
 */
public class SobotHelpCenterActivity extends SobotBaseHelpCenterActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //空态页面
    private View mEmptyView;
    private View mBottomBtn;
    private GridView mGridView;
    private SobotHelpCenterAdapter mAdapter;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_help_center");
    }

    @Override
    protected void initView() {
        setTitle(getResString("sobot_help_center_title"));
        showLeftMenu(getResDrawableId("sobot_btn_back_grey_selector"), getResString("sobot_back"), true);
        mEmptyView = findViewById(getResId("ll_empty_view"));
        mBottomBtn = findViewById(getResId("ll_bottom"));
        mGridView = findViewById(getResId("sobot_gv"));
        mBottomBtn.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        ZhiChiApi api = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        api.getCategoryList(SobotHelpCenterActivity.this, mInfo.getApp_key(), new StringResultCallBack<List<StCategoryModel>>() {
            @Override
            public void onSuccess(List<StCategoryModel> datas) {
                if (datas != null && datas.size() > 0) {
                    mEmptyView.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    if (mAdapter == null) {
                        mAdapter = new SobotHelpCenterAdapter(getApplicationContext(), datas);
                        mGridView.setAdapter(mAdapter);
                    } else {
                        List<StCategoryModel> list = mAdapter.getDatas();
                        list.clear();
                        list.addAll(datas);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
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
        if (v == mBottomBtn) {
            SobotApi.startSobotChat(getApplicationContext(), mInfo);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<StCategoryModel> datas = mAdapter.getDatas();
        StCategoryModel data = datas.get(position);
        Intent intent = SobotProblemCategoryActivity.newIntent(getApplicationContext(), mInfo, data);
        startActivity(intent);
    }
}