package cn.lonsun.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * 升级说明：支持多配置文件
 * @author gu.fei
 * @update zhongjun 2018-08-07
 * @version 2016-2-25 11:22
 */
public class PropertiesUtil {

    public static final PropertiesUtil instance = new PropertiesUtil();

    // 日志
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static Map<String, Properties> map = new Hashtable<String, Properties>();

    protected PropertiesUtil(Map<String, String> fileMap) {
        if(fileMap != null && fileMap.size() > 0){
            for(Map.Entry<String, String> item : fileMap.entrySet()){
                map.put(item.getKey(), loadFile(item.getValue()));
            }
        }
    }

    protected PropertiesUtil() {}

    public Properties readProperties(String key, String filePath){
        if(map.get(key) == null){
            map.put(key, loadFile(filePath));
        }
        return map.get(key);
    }

    /**
     * 加载配置文件
     * @param filePath
     * @return
     */
    private Properties loadFile(String filePath){
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(filePath));
        } catch (FileNotFoundException e) {
            logger.error("配置文件不存在", e);
        } catch (IOException e) {
            logger.error("配置文件读取失败", e);
        }
        return props;
    }

    /**
     * 配置文件包含值
     * @param group
     * @param key
     * @return
     */
    public boolean containsKey(String group, String key){
        Properties props = map.get(group);
        if(props == null){
            logger.error("配置文件不存在：group: {}", group);
            return false;
        }
        return props.containsKey(key);
    }

    /**
     * 配置文件包含值
     * @param group
     * @param url
     * @return
     */
    public boolean containsValue(String group, String url){
        Properties props = map.get(group);
        if(props == null){
            logger.error("配置文件不存在：group: {}", group);
            return false;
        }
        return props.containsValue(url);
    }

    public String getValue(String group, String key){
        Properties props = map.get(group);
        if(props == null){
            logger.error("配置文件不存在：group: {}", group);
            return null;
        }
        return map.get(group).getProperty(key);
    }

}
