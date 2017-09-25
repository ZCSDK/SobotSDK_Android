package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotPostCategoryAdapter;
import com.sobot.chat.api.model.SobotTypeModel;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/13.
 */

public class SobotPostCategoryActivity extends SobotBaseActivity {

    private SobotPostCategoryAdapter categoryAdapter;
    private ListView listView;
    private ArrayList<SobotTypeModel> typeList = new ArrayList<>();
    private ArrayList<SobotTypeModel> types = new ArrayList<>();
    private ArrayList<SobotTypeModel> type1;
    private ArrayList<SobotTypeModel> type2;
    private ArrayList<SobotTypeModel> type3;
    private ArrayList<SobotTypeModel> type4;
    private int typeLevel = 0;
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
        typeList = types = (ArrayList<SobotTypeModel>) bundle.getSerializable("types");
        typeName = bundle.getString("typeName");
        typeId = bundle.getString("typeId");

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
            resetChecked(types);
            categoryAdapter = new SobotPostCategoryAdapter(this, types);
            listView.setAdapter(categoryAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_YES == types.get(position).getNodeFlag()
                        && ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODE_LEVEL_FIRST == types.get(position).getTypeLevel()) {
                    typeLevel = types.get(position).getTypeLevel();
                    type1 = types.get(position).getItems();
                    types = type1;
                    resetChecked(type1);
                    categoryAdapter = new SobotPostCategoryAdapter(SobotPostCategoryActivity.this, type1);
                    listView.setAdapter(categoryAdapter);
                } else if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_YES == types.get(position).getNodeFlag()
                        && ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODE_LEVEL_SECOND == types.get(position).getTypeLevel()) {
                    typeLevel = types.get(position).getTypeLevel();
                    type2 = types.get(position).getItems();
                    types = type2;
                    resetChecked(type2);
                    categoryAdapter = new SobotPostCategoryAdapter(SobotPostCategoryActivity.this, type2);
                    listView.setAdapter(categoryAdapter);
                }  else if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_YES == types.get(position).getNodeFlag()
                        && ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODE_LEVEL_THRID== types.get(position).getTypeLevel()) {
                    typeLevel = types.get(position).getTypeLevel();
                    type3 = types.get(position).getItems();
                    types = type3;
                    resetChecked(type3);
                    categoryAdapter = new SobotPostCategoryAdapter(SobotPostCategoryActivity.this, type3);
                    listView.setAdapter(categoryAdapter);
                }  else if (ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODEFLAG_YES == types.get(position).getNodeFlag()
                        && ZhiChiConstant.WORK_WORK_ORDER_CATEGORY_NODE_LEVEL_FOURTH == types.get(position).getTypeLevel()) {
                    typeLevel = types.get(position).getTypeLevel();
                    type4 = types.get(position).getItems();
                    types = type4;
                    resetChecked(type4);
                    categoryAdapter = new SobotPostCategoryAdapter(SobotPostCategoryActivity.this, type4);
                    listView.setAdapter(categoryAdapter);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("category_typeName", types.get(position).getTypeName());
                    intent.putExtra("category_typeId", types.get(position).getTypeId());
                    setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
                    if (types.get(position).isChecked()) {
                        types.get(position).setChecked(false);
                    } else {
                        types.get(position).setChecked(true);
                    }
                    for (int i = 0; i < types.size(); i++) {
                        if (i != position) {
                            types.get(i).setChecked(false);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
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
        if (typeLevel == 4){
            typeLevel = 3;
            types = type3;
            categoryAdapter = new SobotPostCategoryAdapter(this, types);
            listView.setAdapter(categoryAdapter);
        } else if (typeLevel == 3){
            typeLevel = 2;
            types = type2;
            categoryAdapter = new SobotPostCategoryAdapter(this, types);
            listView.setAdapter(categoryAdapter);
        } else if (typeLevel == 2) {
            typeLevel = 1;
            types = type1;
            categoryAdapter = new SobotPostCategoryAdapter(this, types);
            listView.setAdapter(categoryAdapter);
        } else if (typeLevel == 1) {
            typeLevel = 0;
            categoryAdapter = new SobotPostCategoryAdapter(this, typeList);
            listView.setAdapter(categoryAdapter);
        } else {
            finish();
        }
    }

    private void resetChecked(ArrayList<SobotTypeModel> type){
        for (int i = 0; i < type.size(); i++) {
            if (!TextUtils.isEmpty(typeId) && typeId.equals(type.get(i).getTypeId())){
                type.get(i).setChecked(true);
            }
        }
    }
}