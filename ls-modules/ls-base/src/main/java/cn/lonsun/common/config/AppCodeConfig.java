/**
 *TODO          <br/>
 *@date 2014-11-15 11:27  <br/> 
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
package cn.lonsun.common.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by zsy on 2014-11-15.
 */
public class AppCodeConfig {





    public static Map<String,String> propertiesMap;

    public static Properties p = new Properties();

    static{
        load();
    }

    public static void load(){
        propertiesMap = new HashMap<String, String>();
        try {
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("appCode.properties"));
            Set<Object> keys = p.keySet();
            String key = "";
            for(Object obj : keys){
                key = (String)obj;
                propertiesMap.put(key,p.getProperty(key));
            }

        } catch (IOException e) {
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

}
