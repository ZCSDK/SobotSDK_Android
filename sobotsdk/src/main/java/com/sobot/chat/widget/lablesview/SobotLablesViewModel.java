package com.sobot.chat.widget.lablesview;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/11.
 */

public class SobotLablesViewModel implements Serializable{

    private String title;
    private String anchor;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Override
    public String toString() {
        return "SobotLablesViewModel{" +
                "title='" + title + '\'' +
                ", anchor='" + anchor + '\'' +
                '}';
    }
}