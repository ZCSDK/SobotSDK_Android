package com.sobot.chat.utils;

import com.sobot.chat.api.model.SobotMsgCenterModel;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Created by jinxl on 2018/9/5.
 */
public class SobotCompareNewMsgTime implements Comparator<SobotMsgCenterModel> {
    @Override
    public int compare(SobotMsgCenterModel o1, SobotMsgCenterModel o2) {
        return compareNewMsgTime(o1, o2);
    }

    //根据新消息排序
    private int compareNewMsgTime(SobotMsgCenterModel p1, SobotMsgCenterModel p2) {
        long d1 = getFormatTS(p1);
        long d2 = getFormatTS(p2);
        long tmp = d2 - d1;
        if (tmp > 0) {
            return 1;
        } else if (tmp == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    private long getFormatTS(SobotMsgCenterModel data) {
        if (data == null || data.getLastDateTime() == null)
            return 0;
        try {
            return Long.parseLong(data.getLastDateTime());
        } catch (Exception e) {
            return 0;
        }
    }
}
