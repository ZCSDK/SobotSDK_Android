package com.sobot.chat.listener;

import android.view.View;

import com.sobot.chat.api.model.SobotFieldModel;

/**
 * 打开自定义字段选择的接口
 * @author Created by jinxl on 2018/1/3.
 */
public interface ISobotCusField {
    /**
     * 点击回调
     * @param view  点击的View
     * @param fieldType  自定义字段的类型
     * @param cusField 点击这个字段的bean
     */
    void onClickCusField(View view , int fieldType, SobotFieldModel cusField);
}
