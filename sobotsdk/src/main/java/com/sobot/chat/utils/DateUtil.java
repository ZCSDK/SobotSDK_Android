package com.sobot.chat.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.widget.timePicker.SobotTimePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间工具类
 */
public class DateUtil {
    /**
     * 时:分
     */
    public final static SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    /**
     * 年-月-日 时:分:秒
     */
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * 年-月-日
     */
    public final static SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    /**
     * 时:分
     */
    public final static SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    /**
     * 分:秒
     */
    public final static SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat(
            "mm:ss", Locale.getDefault());

    /**
     * x月x日
     */
    public final static SimpleDateFormat DATE_FORMAT5 = new SimpleDateFormat(
            "MM月dd日", Locale.getDefault());

    /**
     * 将毫秒级整数转换为字符串格式时间
     *
     * @param millisecondDate 毫秒级时间整数
     * @param format          要转换成的时间格式(参见 DateUtil常量)
     * @return 返回相应格式的时间字符串
     */
    public static String toDate(long millisecondDate, SimpleDateFormat format) {
        String time = "";
        try {
            time = format.format(new Date(millisecondDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    public static long stringToLongMs(String date) {
        if (!TextUtils.isEmpty(date)) {
            try {
                Calendar seconds = Calendar.getInstance();
                seconds.setTime(DATE_FORMAT4.parse(date));
                return seconds.get(Calendar.SECOND);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static long stringToLong(String date) {
        if (!TextUtils.isEmpty(date)) {
            try {
                return DATE_FORMAT.parse(date).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 格式化时间
     *
     * @param time 不显示HH:mm，并且不显示“今天”
     * @return
     */
    public static String formatDateTime(String time) {
        return formatDateTime(time, false, "");
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDateTime(String time, boolean showHours, String showToday) {
        if (time == null || "".equals(time) || time.length() < 19) {
            return "";
        }
        Date date = null;
        try {
            date = DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        if (date != null) {
            current.setTime(date);
        }

        if (current.after(today)) {
            return showToday + " " + time.split(" ")[1].substring(0, 5);
        } else {
            int index = time.indexOf("-") + 1;
            if (showHours) {
                return time.substring(index, time.length()).substring(0, 11);
            } else {
                return time.substring(index, time.length()).substring(0, 5);
            }
        }
    }

    /**
     * 格式化时间
     * 时间是当日内时，显示小时和分钟，时间不是当日内时，时间显示月-日
     * @param time
     * @return
     */
    public static String formatDateTime2(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            Calendar current = Calendar.getInstance();
            Calendar today = Calendar.getInstance();    //今天
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            current.setTime(new Date(Long.parseLong(time)));
            if (current.before(today)) {
                return toDate(Long.parseLong(time),DATE_FORMAT5);
            } else {
                return toDate(Long.parseLong(time),DATE_FORMAT3);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }


    public static void main(String[] args) {

        String time = formatDateTime("2016-01-07 15:41:00", true, "今天");
        System.out.println("time:" + time);
        time = formatDateTime("2016-01-03 11:41:00");
        System.out.println("time:" + time);
        time = formatDateTime("2016-01-01 15:43:00");
        System.out.println("time:" + time);
    }

    /**
     * 将时间戳格式化
     *
     * @param seconds
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || TextUtils.isEmpty(seconds) || seconds.equals("null")) {
            return "";
        }
        if (format == null || TextUtils.isEmpty(format)) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return toDate(System.currentTimeMillis(), DATE_FORMAT);
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(String timeStr) {

        StringBuffer sb = new StringBuffer();

        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day > 7) {
            sb.append(DateUtil.timeStamp2Date(timeStr, "yyyy-MM-dd"));
            return sb.toString();
        } else if (day > 1 && day <= 7) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }


    public static Date parse(String str, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    /**
     * 要回显的view 也就是textView
     *
     * @param context      应该为activity
     * @param selectedTime 选中的日期  可以为null 如果为null那么取当前时间
     * @param displayType  显示的类型 0 为 年月日  1为 时分
     */
    public static void openTimePickerView(Context context, View v, Date selectedTime, final int displayType) {
        Calendar selectedDate = Calendar.getInstance();
        if (selectedTime != null) {
            selectedDate.setTime(selectedTime);
        }
        boolean[] typeArray = displayType == 0 ? new boolean[]{true, true, true, false, false, false} : new boolean[]{false, false, false, true, true, false};
        //时间选择器
        SobotTimePickerView pvTime = new SobotTimePickerView.Builder(context, new SobotTimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                if (v != null && v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.setText(displayType == 0 ? DATE_FORMAT2.format(date) : DATE_FORMAT0.format(date));
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(typeArray)
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.parseColor("#dadada"))
                .setContentSize(17)
                .setSubCalSize(14)
                .setTitleBgColor(Color.WHITE)
                .setCancelColor(Color.parseColor("#0DAEAF"))
                .setSubmitColor(Color.parseColor("#0DAEAF"))
                .setDate(selectedDate)
                .setBgColor(Color.WHITE)
                .setBackgroundId(0x80000000) //设置外部遮罩颜色
                .setDecorView(null)
                .setLineSpacingMultiplier(2.0f)
                .build();

        pvTime.show(v);
    }

}