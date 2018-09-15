package cn.lonsun.system.sitechart.webutil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatDateUtil {
//
//	private static SimpleDateFormat df1=new SimpleDateFormat("yyyy-MM-dd HH:00:00");
//	private static SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
//	private static SimpleDateFormat df3=new SimpleDateFormat("HH");
//	private static SimpleDateFormat df4=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static SimpleDateFormat df5=new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");

    public static String yesterDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String day = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
        return day;
    }

    /**
     * 获取今日时间戳
     **/
    public static Long getToday() {
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df2.parse(df2.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getTodayDate() {
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;
        date = df2.format(new Date());
        return date;
    }

    public static String getToday2Str() {
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;
        date = df2.format(new Date());
        return date;
    }

    public static Integer getSurplusTimeOfToday() {
        long k1 = StatDateUtil.getToday() / 1000 + 24 * 60 * 60;
        long k2 = new Date().getTime() / 1000;
        return Integer.parseInt(String.valueOf((k1 - k2)));
    }

    /**
     * 获取昨日时间戳
     **/
    public static Long getYesterday() {
        Long td = getToday();
        return td - (24 * 60 * 60 * 1000L);
    }

    /**
     * 获取昨日此时
     **/
    public static Long getYesterday2Now() {
        return (new Date().getTime()) - (24 * 60 * 60 * 1000L);
    }

    /**
     * 获取近90日(含今日)
     **/
    public static Long get90Time() {
        Long td = getToday() - (89 * 24 * 60 * 60 * 1000L);
        return td;
    }

    /**
     * 获取近90日(不含今日)
     **/
    public static Long get30Time() {
        Long td = getToday() - (30 * 24 * 60 * 60 * 1000L);
        return td;
    }

    public static Long getTime2(int day) {
        Long td = getToday() - (day * 24 * 60 * 60 * 1000L);
        return td;
    }

    /**
     * 获取近7日(含今日)
     **/
    public static Long get7Time() {
        Long td = getToday() - (6 * 24 * 60 * 60 * 1000L);
        return td;
    }

    public static Long getTime(int day) {
        Long td = getToday() - ((day - 1) * 24 * 60 * 60 * 1000L);
        return td;
    }

    public static List<String> getTodayForHour() {
        List<String> list = new ArrayList<String>();
        Long timeDay = StatDateUtil.getToday();
        Long hour1 = 60 * 60 * 1000L;
        Long now = new Date().getTime();
        SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");
        while (timeDay <= (now)) {
            String t = df5.format(new Date(timeDay));
            timeDay = timeDay + hour1;
            list.add(t);
        }
        return list;
    }

    public static String getNowForHour() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");
        return df.format(new Date());
    }

    public static List<String> get7DayForHour() {
        List<String> list = new ArrayList<String>();
        Long time7Day = StatDateUtil.get7Time();
        Long hour1 = 60 * 60 * 1000L;
        Long now = getToday() + (24 * 60 * 60 * 1000L);
        SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");
        while (time7Day <= (now - hour1)) {
            String t = df5.format(new Date(time7Day));
            time7Day = time7Day + hour1;
            list.add(t);
        }
        return list;
    }

    public static List<String> getDayForHour(int day) {
        List<String> list = new ArrayList<String>();
        Long timeDay = StatDateUtil.getTime(day);
        Long hour1 = 60 * 60 * 1000L;
        Long now = getToday() + (24 * 60 * 60 * 1000L);
        SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");
        while (timeDay <= (now - hour1)) {
            String t = df5.format(new Date(timeDay));
            timeDay = timeDay + hour1;
            list.add(t);
        }
        return list;
    }

    public static List<String> getDayForHour2(int day) {
        List<String> list = new ArrayList<String>();
        Long timeDay = StatDateUtil.getTime(day);
        Long hour1 = 60 * 60 * 1000L;
        Long now = getToday() + (24 * 60 * 60 * 1000L);
        SimpleDateFormat df5 = new SimpleDateFormat("HH:00-HH:59");
        while (timeDay <= (now - hour1)) {
            String t = df5.format(new Date(timeDay));
            timeDay = timeDay + hour1;
            list.add(t);
        }
        return list;
    }

    public static List<String> getDayForHour(Date st, Date ed) {
        List<String> list = new ArrayList<String>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (st == null) {
            st = new Date();
        }
        String d = df.format(st);
        Date dt = null;
        try {
            dt = df.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long timeDay = dt.getTime();
        Long hour1 = 60 * 60 * 1000L;
        Long now = null;
        if (ed == null) {
            now = getToday() + (24 * 60 * 60 * 1000L);
        } else {
            try {
                now = (df.parse(df.format(ed))).getTime() + 24 * 60 * 60 * 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:00-HH:59");
        while (timeDay <= (now - hour1)) {
            String t = df5.format(new Date(timeDay));
            timeDay = timeDay + hour1;
            list.add(t);
        }
        return list;
    }

    public static void main(String[] args) {
//		System.out.println(StatDateUtil.getYesterday());
//		Date d=new Date(StatDateUtil.getYesterday());
//		SimpleDateFormat df4=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println(df4.format(get90Time()));
//		getTodayForHour();
        System.out.println(getTodayForHour());

    }
}
