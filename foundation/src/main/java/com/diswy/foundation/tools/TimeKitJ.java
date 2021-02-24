package com.diswy.foundation.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeKitJ {

    /**
     * @return 时间戳
     */
    public static Long formatTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String YMD(String time) {
        if (time == null)
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(new Date(formatTime(time)));
    }

    public static String YMDHM(String time) {
        if (time == null)
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
        return format.format(new Date(formatTime(time)));
    }

    public static String YMDHMS(String time) {
        if (time == null)
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return format.format(new Date(formatTime(time)));
    }

    public static String TFULL(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return format.format(calendar.getTime());
    }

    /**
     * 根据开始时间和答题时间返回剩余答题时间
     *
     * @param durationStart 开始时间
     * @param duration      限时任务时长
     * @return 秒
     */
    public static long getDurationByStart(String durationStart, int duration) {
        Date date;
        if (TextUtils.isEmpty(durationStart)) {
            return duration * 60;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            date = formatter.parse(durationStart);
            long current = System.currentTimeMillis();
            long seconds = date.getTime() - current + duration * 60 * 1000;
            if (seconds > 0) {
                seconds = seconds / 1000;
                return seconds;
            }
        } catch (Exception e) {
            return duration * 60;
        }
        return 0;
    }

    /**
     * @param durationEnd 系统截至交卷时间
     * @param curTime     当前时间
     * @return 秒
     */
    public static long getDurationByEnd(String durationEnd, long curTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = formatter.parse(durationEnd);
            long seconds = date.getTime() - curTime;
            if (seconds > 0) {
                seconds = seconds / 1000;
                return seconds;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}

