package com.alex.develop.util;

import android.util.Log;

import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.Constant;
import com.alex.develop.entity.Enum;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alex on 15-7-19.
 * 日期处理类
 */
public class DateHelper {

    /**
     * 获取今天的日期
     * @return eg:“20150719”
     */
    public static String today() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return sdf.format(new Date()).trim();
    }

    /**
     *
     * @return
     */
    public static Enum.Month month() {
        int ordinal = Calendar.getInstance().get(Calendar.MONTH);
        Enum.Month month = Enum.Month.build(ordinal);
        return month;
    }

    /**
     * 计算距离今天dayCount天之前或之后那天的日期
     * @param dayCount <0,表示dayCount天之前; >0 表示dayCount天之后
     * @return eg:"21050519"
     */
    public static String offset(String date, int dayCount) {

        // year
        int year = Integer.valueOf(date.substring(0, 4));

        // 闰年2月多一天
        if ((0 == year % 4 && 0 != year % 100) || 0 == year % 400) {
            days[1] = 29;
        } else {
            days[1] = 28;
        }

        // month(每月从0开始计数，即1实际上表示2月)
        int month = Integer.valueOf(date.substring(4, 6)) - 1;

        // day
        int day = Integer.valueOf(date.substring(6, 8));

        day += dayCount;

        while (true) {
            int dayOfMonth = days[month];
            if (0 < day && day <= dayOfMonth) {
                break;
            } else if (0 == day) {

                --month;
                // 考虑偏移的天数太多，跨年的情况
                if (-1 == month) {
                    month = days.length - 1;
                    --year;
                }

                day = days[month];
                break;
            } else if (dayOfMonth < day) {
                day -= dayOfMonth;

                ++month;
                if (days.length == month) {
                    month = 0;
                    ++year;

                    // 闰年2月多一天
                    if ((0 == year % 4 && 0 != year % 100) || 0 == year % 400) {
                        days[1] = 29;
                    } else {
                        days[1] = 28;
                    }
                }

            } else {
                --month;
                if (-1 == month) {
                    month = days.length - 1;
                    --year;

                    // 闰年2月多一天
                    if ((0 == year % 4 && 0 != year % 100) || 0 == year % 400) {
                        days[1] = 29;
                    } else {
                        days[1] = 28;
                    }
                }
                day += dayOfMonth;
            }
        }

        return String.format("%d%02d%02d", year, month + 1, day);
    }

    /**
     * 比较两个日期谁前谁后
     * @param left 日期1
     * @param right 日期2
     * @return -1，left在right前面；0，left与right为同一天；1，left在right后面
     */
    public static int compare(String left, String right) {
        int flag = 0;

        if(left.equals(right)) {// 日期相等
            return flag;
        }

        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

        try {
            long lt = df.parse(left).getTime();
            long rt = df.parse(right).getTime();

            if(0 > lt-rt) {
                flag = -1;
            } else {
                flag = 1;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public static String getDateFromNow(String dateWithTime) {

        // TODO here has BUG

        String result = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {

            final long now = new Date().getTime() / 1000;
            final long date = df.parse(dateWithTime).getTime() / 1000;

            long differ = now - date;

            if(60 > differ) {
                result = differ + " " + Analyzer.getContext().getString(R.string.news_time_second);
            } else if(3600 > differ) {
                differ /= 60;
                result = differ + " " + Analyzer.getContext().getString(R.string.news_time_minute);
            } else if(86400 > differ) {
                differ /= 3600;
                result = differ + " " + Analyzer.getContext().getString(R.string.news_time_hour);
            } else {
                result = dateWithTime.split(" ")[0];
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据时间判断市场是否开盘
     * @return
     */
    public static boolean isMarketOpen() {
        boolean flag = false;

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        // 周末不开市
        if(Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek) {
            return flag;
        }

        final String today = today();
        DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.CHINA);

        try {
            long open = df.parse(today + " " + Constant.MARKET_OPEN).getTime();
            long close = df.parse(today + " " + Constant.MARKET_CLOSE).getTime();
            long current = System.currentTimeMillis();

            if(open < current && current < close) {
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    private static int[] days = {
            31,// 1月
            28,// 2月
            31,// 3月
            30,// 4月
            31,// 5月
            30,// 6月
            31,// 7月
            31,// 8月
            30,// 9月
            31,// 10月
            30,// 11月
            31 // 12月
    };

    private DateHelper() {
    }
}