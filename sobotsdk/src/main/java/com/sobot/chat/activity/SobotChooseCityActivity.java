package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotProvinAdapter;
import com.sobot.chat.api.model.SobotCityResult;
import com.sobot.chat.api.model.SobotProvinInfo;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by jinxl on 2018/1/8.
 */
public class SobotChooseCityActivity extends SobotBaseActivity {
    private Bundle mIntentBundleData;

    private ListView mListView;
    private SobotProvinInfo mProvinInfo;

    private SparseArray<List<SobotProvinInfo.SobotProvinceModel>> tmpMap = new SparseArray<>();
    private List<SobotProvinInfo.SobotProvinceModel> tmpDatas = new ArrayList<>();
    private SobotProvinAdapter categoryAdapter;
    private int currentLevel = 1;
    private boolean isRunning = false;
    private String mFiledId;

    private SobotProvinInfo.SobotProvinceModel mFinalData = new SobotProvinInfo.SobotProvinceModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_cusfield"));
        initBundleData(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void forwordMethod() {

    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mIntentBundleData = getIntent().getBundleExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA);
        } else {
            mIntentBundleData = savedInstanceState.getBundle(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA);
        }
        if (mIntentBundleData != null) {
            initIntent(mIntentBundleData);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putBundle(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA, mIntentBundleData);
        super.onSaveInstanceState(outState);
    }

    private void initIntent(Bundle mIntentBundleData) {
        mProvinInfo = (SobotProvinInfo) mIntentBundleData.getSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_PROVININFO);
        mFiledId = mIntentBundleData.getString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD_ID);
        if (mProvinInfo != null && mProvinInfo.getProvinces() != null) {
            //存贮一级List
            currentLevel = 1;
            tmpMap.put(1, mProvinInfo.getProvinces());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            backPressed();
        }
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        if (currentLevel <= 1) {
            finish();
        } else {
            if (isRunning) {
                return;
            }
            currentLevel--;
            List<SobotProvinInfo.SobotProvinceModel> provinceModels = tmpMap.get(currentLevel);
            notifyListData(provinceModels);
        }
    }

    private void initView() {
        sobot_tv_left.setOnClickListener(this);
        mListView = (ListView) findViewById(getResId("sobot_activity_cusfield_listview"));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SobotProvinInfo.SobotProvinceModel data = tmpDatas.get(position);
                if (data.nodeFlag) {
                    showDataWithLevel(data);
                } else {
                    saveData(currentLevel - 1, data);
                    Intent intent = new Intent();
                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_PROVININFO, mFinalData);
                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD_ID, mFiledId);
                    setResult(ZhiChiConstant.REQUEST_COCE_TO_CITY_INFO, intent);
                    for (int i = 0; i < tmpMap.get(currentLevel).size(); i++) {
                        tmpDatas.get(i).isChecked = (i == position);
                    }
                    categoryAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    private void initData() {
        if (mProvinInfo != null && mProvinInfo.getProvinces() != null) {
            showDataWithLevel(null);
        }
    }

    /**
     * 显示数据
     *
     * @param data 当前选择的bean
     */
    private void showDataWithLevel(final SobotProvinInfo.SobotProvinceModel data) {
        if (data != null) {
            if (isRunning) {
                return;
            }
            isRunning = true;
            // 获取下一级
            SobotDialogUtils.startProgressDialog(SobotChooseCityActivity.this);
            zhiChiApi.queryCity(SobotChooseCityActivity.this,data.level == 0 ? data.provinceId : null, data.level == 1 ? data.cityId : null, new StringResultCallBack<SobotCityResult>() {
                @Override
                public void onSuccess(SobotCityResult result) {
                    isRunning = false;
                    SobotDialogUtils.stopProgressDialog(SobotChooseCityActivity.this);
                    final SobotProvinInfo bean = result.getData();

                    if (bean.getCitys() != null && bean.getCitys().size() > 0) {
                        showData(bean.getCitys(), data);
                    }
                    if (bean.getAreas() != null && bean.getAreas().size() > 0) {
                        showData(bean.getAreas(), data);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    isRunning = false;
                    SobotDialogUtils.stopProgressDialog(SobotChooseCityActivity.this);
                    ToastUtil.showToast(getApplicationContext(), des);
                }
            });

        } else {
            //显示一级list
            fillData(1);
        }

    }

    private void showData(List<SobotProvinInfo.SobotProvinceModel> beans,final SobotProvinInfo.SobotProvinceModel data) {
        saveData(data.level, data);
        currentLevel++;
        tmpMap.put(currentLevel, beans);
        fillData(currentLevel);
    }

    private void saveData(final int level, final SobotProvinInfo.SobotProvinceModel data) {
        switch (level) {
            case 0:
                mFinalData.provinceId = data.provinceId;
                mFinalData.provinceName = data.provinceName;
                break;
            case 1:
                mFinalData.cityId = data.cityId;
                mFinalData.cityName = data.cityName;
                break;
            default:
                mFinalData.areaId = data.areaId;
                mFinalData.areaName = data.areaName;
                break;
        }
    }

    /**
     * 根据level显示列表
     *
     * @param level 当前要显示的级别
     */
    private void fillData(final int level) {
        ArrayList<SobotProvinInfo.SobotProvinceModel> currentList = (ArrayList<SobotProvinInfo.SobotProvinceModel>) tmpMap.get(level);
        if (currentList != null) {
            notifyListData(currentList);
        }
    }

    private void notifyListData(List<SobotProvinInfo.SobotProvinceModel> currentList) {
        tmpDatas.clear();
        tmpDatas.addAll(currentList);
        if (categoryAdapter != null) {
            categoryAdapter.notifyDataSetChanged();
        } else {
            categoryAdapter = new SobotProvinAdapter(SobotChooseCityActivity.this, tmpDatas);
            mListView.setAdapter(categoryAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        SobotDialogUtils.stopProgressDialog(SobotChooseCityActivity.this);
        super.onDestroy();
    }
}
