package cn.lonsun.db;

import cn.lonsun.GlobalConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhusy on 2016-7-19.
 */
public class DBConfig {

    public static Map<String,String> propertiesMap;

    public static Properties p = new Properties();

    static{
        load();
    }

    public static void load(){
        propertiesMap = new HashMap<String, String>();
        try {
            propertiesMap.put("jdbc.driverClassName", SpringContextHolder.getBean(GlobalConfig.class).getJdbcDriverClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key){
        String value = propertiesMap.get(key);
        return null != value ? value : "";
    }

    public static boolean isContainValue(String value){
        return propertiesMap.containsValue(value);
    }

    public static String getDriverClass(){
        return getValue("jdbc.driverClassName");
    }

    public static String getDaoNameByCurDBType(String daoNamePrefix){
        String driverClass = getDriverClass();
        String clazzName = "";
        if(driverClass.equals(DBConstant.DRIVER_ORACLE)) {
            clazzName = daoNamePrefix + DBConstant.BEAN_SUFFIX_ORACLE;
        } else if(driverClass.equals(DBConstant.DRIVER_MYSQL)) {
            clazzName = daoNamePrefix + DBConstant.BEAN_SUFFIX_MYSQL;
        } else if(driverClass.equals(DBConstant.DRIVER_MSSQL)) {
            clazzName = daoNamePrefix + DBConstant.BEAN_SUFFIX_MSSQL;
        } else if(driverClass.equals(DBConstant.DRIVER_SYBASE)) {
            clazzName = daoNamePrefix + DBConstant.BEAN_SUFFIX_SYBASE;
        } else if(driverClass.equals(DBConstant.DRIVER_DB2)) {
            clazzName = daoNamePrefix + DBConstant.BEAN_SUFFIX_DB2;
        }
        return clazzName;
    }


}
