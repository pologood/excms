package cn.lonsun.supervise.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-4-7 9:26
 */
public class CronUtil {

    /**
     * 根据类型获取cron表达式
     * @param eo
     * @return
     */
    public static String createCron(CronConfEO eo) {
        String cron = null;
        if("day".equals(eo.getTimeMode())) {
            int space = eo.getSpaceOfDay();
            cron = "0 0 0 /" + space + " * ? *";
        } else {
            cron = eo.getCronExpress();
        }

        return cron;
    }

    public static Date getNextCronTime(String cron) {
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            int count = 1;
            Date date = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(cronTriggerImpl, null,count);
            while(date.getTime() <= new Date().getTime()) {
                date = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(cronTriggerImpl, null,++count);
            }
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getNextCronTime(String cron,Date pre) {
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            int count = 1;
            Date date = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(cronTriggerImpl, null,count);
            while(date.getTime() <= pre.getTime()) {
                date = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(cronTriggerImpl, null,++count);
            }
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDateByPattern(Date date,String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     * @param max  指定范围最大值
     * @param min  指定范围最小值
     * @param n  随机数个数
     * @return int[] 随机数结果集
     */
    public static HashSet<Integer> randomArray(int min, int max, int n){
        int len = max-min+1;

        if(max < min || n > len){
            return null;
        }
        Random random = new Random();
        Integer s = null;
        HashSet<Integer> set = new HashSet<Integer>();
        while (set.size() < n) {
            s = random.nextInt(max)%(max-min+1) + min;
            set.add(s);
        }

        return set;
    }

    public static void main(String[] args){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(CronUtil.getNextCronTime("0 0/1 * * * ?",new Date())));
    }
}
