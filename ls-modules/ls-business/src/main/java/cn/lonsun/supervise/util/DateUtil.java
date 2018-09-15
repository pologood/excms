package cn.lonsun.supervise.util;

import cn.lonsun.common.util.AppUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-4-8 9:46
 */
public class DateUtil {

    /**
     * 获取N天前或后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBeforeOrAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static Date trStr2Date(String date) {
        if(AppUtil.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (Exception e) {
            try {
                return df.parse(date);
            } catch (Exception et) {
                et.printStackTrace();
                return null;
            }
        }
    }

    public static Date addTimeFormat(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date timeFormat(Date date,int hour,int second,int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(null != date) {
            if(calendar.get(calendar.HOUR_OF_DAY) <= 0) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
            }

            if(calendar.get(calendar.SECOND) <= 0) {
                calendar.set(Calendar.SECOND, second);
            }

            if(calendar.get(calendar.MINUTE) <= 0) {
                calendar.set(Calendar.MINUTE, minute);
            }
        }

        return calendar.getTime();
    }

    public static Date getTodayStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date start = calendar.getTime();
        return start;
    }

    public static Date getTodayEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        Date end = calendar.getTime();
        return end;
    }

    public static void main(String[] args){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(getTodayEndDate()));
//        System.out.println(CronUtil.getCron(new Date()));
    }
}

