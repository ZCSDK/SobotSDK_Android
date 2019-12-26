package com.sobot.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.activity.SobotTicketDetailActivity;
import com.sobot.chat.adapter.SobotTicketInfoAdapter;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 留言列表界面
 *
 * @author Created by jinxl on 2019/3/7.
 */
public class SobotTicketInfoFragment extends SobotBaseFragment {

    private final static int REQUEST_CODE = 0x001;

    private View mRootView;
    private ListView mListView;
    private TextView mEmptyView;
    private SobotTicketInfoAdapter mAdapter;

    private String mUid = "";
    private String mCustomerId = "";
    private String mCompanyId = "";

    private List<SobotUserTicketInfo> mList = new ArrayList<>();

    public static SobotTicketInfoFragment newInstance(Bundle data) {
        Bundle arguments = new Bundle();
        arguments.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, data);
        SobotTicketInfoFragment fragment = new SobotTicketInfoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments().getBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
            if (bundle != null) {
                mUid = bundle.getString(StPostMsgPresenter.INTENT_KEY_UID);
                mCustomerId = bundle.getString(StPostMsgPresenter.INTENT_KEY_CUSTOMERID);
                mCompanyId = bundle.getString(StPostMsgPresenter.INTENT_KEY_COMPANYID);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(getResLayoutId("sobot_fragment_ticket_info"), container, false);
        initView(mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    protected void initView(View rootView) {
        mListView = (ListView) rootView.findViewById(getResId("sobot_listview"));
        mEmptyView = (TextView) rootView.findViewById(getResId("sobot_empty"));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SobotUserTicketInfo item = (SobotUserTicketInfo) mAdapter.getItem(position);
                Intent intent = SobotTicketDetailActivity.newIntent(getContext(), mCompanyId, mUid, item);
                startActivityForResult(intent, REQUEST_CODE);
                item.setNewFlag(false);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initData() {
        if ("null".equals(mCustomerId)) {
            mCustomerId = "";
        }
        if (!isAdded() || TextUtils.isEmpty(mCompanyId) || TextUtils.isEmpty(mUid)) {
            return;
        }
        zhiChiApi.getUserTicketInfoList(SobotTicketInfoFragment.this, mUid, mCompanyId, mCustomerId, new StringResultCallBack<List<SobotUserTicketInfo>>() {

            @Override
            public void onSuccess(List<SobotUserTicketInfo> datas) {
                if (datas != null && datas.size() > 0) {
                    mListView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(datas);
                    mAdapter = new SobotTicketInfoAdapter(getContext(), mList);
                    mListView.setAdapter(mAdapter);
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getContext(), des);
            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            initData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
