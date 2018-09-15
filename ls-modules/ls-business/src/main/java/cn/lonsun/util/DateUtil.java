package cn.lonsun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-1-29<br/>
 */

public class DateUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

    private static SimpleDateFormat format1 = new SimpleDateFormat("yyyyMM");
    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");

    public static void main(String str[]) {

        System.out.println(getCurrentQuarterStartTime());
    }


    //现在
    public static String getStrNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(new Date());
        return now;
    }

    //今天凌晨
    public static Date getToday(){
        Date today = null;
        try {
            today = format.parse(getStrToday());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;
    }

    //今天凌晨
    public static String getStrToday() {
        String today = format.format(new Date());
        return today;
    }

    //昨天凌晨
    public static Date getYesterday(){
        Date yesterday= null;
        try {
            yesterday = format.parse(getStrYesterday());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yesterday;
    }

   //昨天凌晨
    public static String getStrYesterday() {
        String yesterday = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        yesterday = format.format(cal.getTime());
        return yesterday;
    }

    /**
     * 根据传参获取今天的前后日期，如num=1是明天日期，num=-1是昨天日期
     * @param num
     * @return
     */
    public static Date getAnyday(int num){
        Date yesterday= null;
        try {
            yesterday = format.parse(getStrAnyday(num));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yesterday;
    }

    //昨天凌晨
    public static String getStrAnyday(int num) {
        String yesterday = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, num);
        yesterday = format.format(cal.getTime());
        return yesterday;
    }

    //本周星期一
    public static Date getWeek(){
        Date week= null;
        try {
            week = format.parse(getStrWeek());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    //本周星期一
    public static String getStrWeek() {
        String week = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        week = format.format(cal.getTime());
        return week;
    }

    //本月的第一天
    public static Date getMonth(){
        Date month= null;
        try {
            month = format.parse(getStrMonth());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return month;
    }

    //本月的第一天
    public static String getStrMonth() {
        String month = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        month = format.format(cal.getTime());
        return month;
    }

    //上个月的第一天
    public static Date getLastMonth(){
        Date month= null;
        try {
            month = format.parse(getStrLastMonth());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return month;
    }

    //上个月的第一天
    public static String getStrLastMonth(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String month = format.format(cal.getTime());
        return month;
    }

    //本年第一天
    public static Date getYear(){
        Date year= null;
        try {
            year = format.parse(getStrYear());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return year;
    }

    //本季度的第一天
    public static Date getQuarter() {
        Date quarter= null;
        try {
            quarter = format.parse(getCurrentQuarterStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quarter;
    }

    //去年第一天
    public static Date getLastYear(){
        Date lastYear= null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getYear());
        calendar.add(Calendar.YEAR, -1);
        lastYear = calendar.getTime();
        return lastYear;
    }

    //本年第一天
    public static String getStrYear(){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear);
        String yearDate = format.format(calendar.getTime());
        return yearDate;
    }

    //本季度的开始时间
    public static String getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String startTime=null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 6);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
             startTime = format.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startTime;
    }

    //本季度的结束时间
    public static String getCurrentQuarterEndTime() throws ParseException {
        Calendar cal = Calendar.getInstance();
        String time = getCurrentQuarterStartTime();
        Date times = format.parse(time);
        cal.setTime(times);
        cal.add(Calendar.MONTH, 3);
        String endTime = format.format(cal.getTime());
        return endTime;
    }



    public static String getDateStr(){
        Calendar now = Calendar.getInstance();
        int year=now.get(Calendar.YEAR);
        int month=now.get(Calendar.MONTH)+1;
        int day=now.get(Calendar.DAY_OF_MONTH);
        String mm="";
        String dd="";
        String time="";
        if(month<10){
            mm="0"+month;
        }else{
            mm=""+month;
        }
        if(day<10){
            dd="0"+day;
        }else{
            dd=""+day;
        }
        time=(year-2000)+mm+dd;
        return time;
    }

    public static String getYearMonthStr(){
        Date date=new Date();
        String time=format1.format(date);
        return time;
    }

    /**
     * 获取指定日期当天零点日期
     * @param date
     * @return
     */
    public static Date getDayDate(Date date){
        Date day = null;
        try {
            day = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    /**
     * 获取指定日期前后指定天数的零点日期
     * @param date
     * @param dayOffset
     *        正数表示向前推指定天数
     *        负数表示表后推指定天数
     * @return
     */
    public static Date getDayDate(Date date,Integer dayOffset){
        Date day = null;
        if(null != date && null != dayOffset){
            date = getDayDate(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dayOffset);
            day =  calendar.getTime();
        }
        return day;
    }

    public static Date getStrToDate(String str,SimpleDateFormat sdf){
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static Integer getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    /**
     * 取得日期：年
     *
     * @param date
     * @return
     */
    public static Integer getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 取得日期：月
     *
     * @param date
     * @return
     */
    public static Integer getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month + 1;
    }

    /**
     * 根据日期获取星期几
     * @param date
     * @return
     */
    public static String getWeekBySimpleDateFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(date);
    }

    /**
     * 根据日期获取星期几
     * @param date
     * @return
     */
    public static String getWeek(Date date){
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }
    public static String getYearMonthDayStr(){
        Date date=new Date();
        String time=format2.format(date);
        return time;
    }
}
