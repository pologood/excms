package cn.lonsun.govbbs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangchao on 2016/12/27.
 */
public class BbsStaticCenterUtil {

    enum RedYellow{
        normal,//正常
        red,//红牌
        yellow;//黄牌
    }

    //默认主题排序
    public final static String defaultPostOrder = "b.isHeadTop desc,b.isTop desc,b.isEssence desc,b.createDate desc,b.updateDate desc";

    //默认论坛图片排序
    public final static String defaultDateOrder = "b.createDate desc";


    /**
     * 获取时间字符  0 今天  1 昨天
     * @param type
     * @return
     */
    public static String getTimeStr(Integer type) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();
        String day = "";
        switch (type){
            case 0:
                day = format.format(nowDate);
                break;
            case 1:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE,   -1);
                day = format.format(cal.getTime());
                break;
            default:break;
        }
        return day;
    }


    /**
     * 获取红黄牌级别
     * @param isRedYellow
     * @return
     */
    public static Integer getLevel(String isRedYellow) {
        if (isRedYellow.equals(RedYellow.normal.toString())) {
            return 1;
        }
        else if (isRedYellow.equals(RedYellow.yellow.toString())){
            return 2;
        }
        else if (isRedYellow.equals(RedYellow.red.toString())) {
            return 3;
        }
        return null;
    }
}
