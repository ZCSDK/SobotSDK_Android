package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotImageScaleAdapter;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotDeleteWorkOrderDialog;
import com.sobot.chat.widget.photoview.HackyViewPager;

import java.util.ArrayList;

/**
 * 图片列表
 * Created by jxl on 2016/5/22.
 */
public class SobotPhotoListActivity extends SobotBaseActivity {

    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list;//全部的图片集合
    private int currentPic;//当前的图片
    private HackyViewPager viewPager;//骇客
    protected SobotDeleteWorkOrderDialog seleteMenuWindow;
    private SobotImageScaleAdapter adapter;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_photo_list");
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            Intent intent = getIntent();
            pic_list = (ArrayList<ZhiChiUploadAppFileModelResult>) intent.getSerializableExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
            currentPic = intent.getIntExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, 0);
        } else {
            pic_list = (ArrayList<ZhiChiUploadAppFileModelResult>) savedInstanceState.getSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
            currentPic = savedInstanceState.getInt(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putInt(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, currentPic);
        outState.putSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST,pic_list);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initData() {

        viewPager = (HackyViewPager) findViewById(getResId("sobot_viewPager"));
        //填充数据
        adapter = new SobotImageScaleAdapter(SobotPhotoListActivity.this, pic_list);
        viewPager.setAdapter(adapter);
        //设置默认选中的页
        viewPager.setCurrentItem(currentPic);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTitlePageNum(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setTitlePageNum(int currentPic) {
        setTitle((currentPic + 1) + "/" + pic_list.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        showRightMenu(getResDrawableId("sobot_pic_delete_selector"), "", true);
        setTitlePageNum(currentPic);
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
    }

    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            seleteMenuWindow.dismiss();
            if(v.getId() == getResId("btn_pick_photo")){
                //删除
                Intent intent = new Intent();
                intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST,pic_list);
                setResult(ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS, intent);
                pic_list.remove(viewPager.getCurrentItem());
                if (pic_list.size() == 0) {
                    finish();
                } else {
                    adapter = new SobotImageScaleAdapter(SobotPhotoListActivity.this, pic_list);
                    viewPager.setAdapter(adapter);
                }
            }
        }
    };

    @Override
    protected void onRightMenuClick(View view) {
        seleteMenuWindow = new SobotDeleteWorkOrderDialog(SobotPhotoListActivity.this,"要删除这张图片吗？", itemsOnClick);
        seleteMenuWindow.show();
    }
}
