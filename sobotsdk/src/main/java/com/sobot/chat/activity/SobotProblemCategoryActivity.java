package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.activity.base.SobotBaseHelpCenterActivity;
import com.sobot.chat.adapter.SobotCategoryAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.StCategoryModel;
import com.sobot.chat.api.model.StDocModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.List;

/**
 * 帮助中心问题分类
 */
public class SobotProblemCategoryActivity extends SobotBaseHelpCenterActivity implements AdapterView.OnItemClickListener {
    public static final String EXTRA_KEY_CATEGORY = "EXTRA_KEY_CATEGORY";
    private StCategoryModel mCategory;
    private ListView mListView;
    private View mEmpty;
    private SobotCategoryAdapter mAdapter;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_problem_category");
    }

    public static Intent newIntent(Context context, Information information, StCategoryModel data) {
        Intent intent = new Intent(context, SobotProblemCategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.putExtra(EXTRA_KEY_CATEGORY, data);
        return intent;
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        super.initBundleData(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mCategory = (StCategoryModel) intent.getSerializableExtra(EXTRA_KEY_CATEGORY);
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_grey_selector"), getResString("sobot_back"), true);
        mListView = (ListView) findViewById(getResId("sobot_listview"));
        mEmpty = findViewById(getResId("sobot_tv_empty"));
        setTitle(mCategory.getCategoryName());

        mListView.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {
        ZhiChiApi api = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        api.getHelpDocByCategoryId(SobotProblemCategoryActivity.this, mCategory.getAppId(), mCategory.getCategoryId(), new StringResultCallBack<List<StDocModel>>() {
            @Override
            public void onSuccess(List<StDocModel> datas) {
                if (datas != null) {
                    if (mAdapter == null) {
                        mAdapter = new SobotCategoryAdapter(getApplicationContext(), datas);
                        mListView.setAdapter(mAdapter);
                    } else {
                        List<StDocModel> list = mAdapter.getDatas();
                        list.clear();
                        list.addAll(datas);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (datas == null || datas.size() == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<StDocModel> datas = mAdapter.getDatas();
        StDocModel stDocModel = datas.get(position);
        Intent intent = SobotProblemDetailActivity.newIntent(getApplicationContext(), mInfo, stDocModel);
        startActivity(intent);
    }
}