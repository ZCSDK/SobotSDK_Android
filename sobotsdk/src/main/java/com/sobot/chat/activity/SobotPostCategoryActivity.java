package com.sobot.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotPostCategoryAdapter;
import com.sobot.chat.api.model.SobotTypeModel;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class SobotPostCategoryActivity extends SobotBaseActivity {

    private SobotPostCategoryAdapter categoryAdapter;
    private ListView listView;

    private List<SobotTypeModel> types = new ArrayList<>();
    private SparseArray<List<SobotTypeModel>> tmpMap = new SparseArray<>();
    private List<SobotTypeModel> tmpDatas = new ArrayList<>();
    private int currentLevel = 1;
    private String typeName;
    private String typeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_post_category"));
        setTitle("选择分类");
        types.clear();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        ArrayList<SobotTypeModel> typeTemp = (ArrayList<SobotTypeModel>) bundle.getSerializable("types");
        if (typeTemp != null) {
            types.addAll(typeTemp);
        }

        typeName = bundle.getString("typeName");
        typeId = bundle.getString("typeId");
        //存贮一级List
        currentLevel = 1;
        tmpMap.put(1, types);

        initView();
    }

    @Override
    public void forwordMethod() {
        backPressed();
    }

    private void initView() {

        sobot_tv_left.setOnClickListener(this);
        listView = (ListView) findViewById(getResId("sobot_activity_post_category_listview"));
        if (types != null && types.size() != 0) {
            showDataWithLevel(-1);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_YES == tmpMap.get(currentLevel).get(position).getNodeFlag()) {
                    currentLevel++;
                    showDataWithLevel(position);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("category_typeName", tmpMap.get(currentLevel).get(position).getTypeName());
                    intent.putExtra("category_typeId", tmpMap.get(currentLevel).get(position).getTypeId());
                    setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
                    for (int i = 0; i < tmpMap.get(currentLevel).size(); i++) {
                        tmpMap.get(currentLevel).get(i).setChecked(i == position);
                    }
                    categoryAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    private void showDataWithLevel(int position) {
        if (position >= 0) {
            tmpMap.put(currentLevel, tmpMap.get(currentLevel - 1).get(position).getItems());
        }

        ArrayList<SobotTypeModel> currentList = (ArrayList<SobotTypeModel>) tmpMap.get(currentLevel);
        if (currentList != null) {
            resetChecked(currentList);
            notifyListData(currentList);
        }

    }

    private void notifyListData(List<SobotTypeModel> currentList) {
        tmpDatas.clear();
        tmpDatas.addAll(currentList);
        if (categoryAdapter != null) {
            categoryAdapter.notifyDataSetChanged();
        } else {
            categoryAdapter = new SobotPostCategoryAdapter(SobotPostCategoryActivity.this, tmpDatas);
            listView.setAdapter(categoryAdapter);
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
            currentLevel--;
            List<SobotTypeModel> sobotTypeModels = tmpMap.get(currentLevel);
            notifyListData(sobotTypeModels);
        }

    }

    private void resetChecked(ArrayList<SobotTypeModel> type) {
        for (int i = 0; i < type.size(); i++) {
            if (!TextUtils.isEmpty(typeId) && typeId.equals(type.get(i).getTypeId())) {
                type.get(i).setChecked(true);
            }
        }
    }
}