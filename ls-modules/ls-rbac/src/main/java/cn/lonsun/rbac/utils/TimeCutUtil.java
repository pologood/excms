package cn.lonsun.rbac.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchao on 2016/8/3.
 */
public class TimeCutUtil {


    public enum CacheKey{
        SimplePersons,//选人界面Cache名称
        SimpleOrgans1,//单位、部门和虚拟单位
        SimpleOrgans2,//单位和部门
        SimpleOrgans3,//单位
        PersonUpdateDate,//人员缓存修改时间
        OrganUpdateDate,//Organ缓存修改时间
        IndicatorSaveOrUpdateDate
    }

    private static Map<String,String> timeCutMap = new HashMap<String,String>();


    public static void put(String key,String times){
        timeCutMap.put(key,times);
    }

    public static String get(String key){
        String timeCut = timeCutMap.get(key);
        return timeCut;
    }

}
