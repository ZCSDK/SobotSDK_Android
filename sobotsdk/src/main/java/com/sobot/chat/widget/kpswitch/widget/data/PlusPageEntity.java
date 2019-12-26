package com.sobot.chat.widget.kpswitch.widget.data;

import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.widget.kpswitch.view.plus.SobotPlusPageView;

import java.util.List;

/**
 * 更多面板中的 单页实体
 * @author Created by jinxl on 2018/7/31.
 */
public class PlusPageEntity<T> extends PageEntity<PlusPageEntity> {

    /**
     * 数据源
     */
    private List<T> mDataList;
    /**
     * 每页行数
     */
    private int mLine;
    /**
     * 每页列数
     */
    private int mRow;

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public int getLine() {
        return mLine;
    }

    public void setLine(int line) {
        this.mLine = line;
    }

    public int getRow() {
        return mRow;
    }

    public void setRow(int row) {
        this.mRow = row;
    }


    public PlusPageEntity() { }

    @Override
    public View instantiateItem(final ViewGroup container, int position, PlusPageEntity pageEntity) {
        if(mPageViewInstantiateListener != null){
            return mPageViewInstantiateListener.instantiateItem(container, position, this);
        }
        if (getRootView() == null) {
            SobotPlusPageView pageView = new SobotPlusPageView(container.getContext());
            pageView.setNumColumns(mRow);
            setRootView(pageView);
        }
        return getRootView();
    }
}