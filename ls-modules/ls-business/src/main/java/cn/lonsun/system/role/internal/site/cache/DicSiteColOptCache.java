package cn.lonsun.system.role.internal.site.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class DicSiteColOptCache {

    public static String DICT_SITE_CODE = "role_site_opt";
    public static String DICT_COLUMN_CODE = "role_column_opt";

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IDataDictItemService dataDictItemService;

    static {
        cache = new ConcurrentHashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        dataDictItemService = SpringContextHolder.getBean("dataDictItemService");
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static DataDictItemEO get(String key) {
        String[] str = key.split("_");
        read.lock();
        try{
            if(AppUtil.isEmpty(cache.get(key))) {
                DataDictItemEO eo = dataDictItemService.getDataDictItemByCode(str[0],str[1]);
                read.unlock(); //解锁，写入
                if(!AppUtil.isEmpty(eo))
                    DicSiteColOptCache.put(key, eo);
                read.lock();
            }
        }finally {
            read.unlock();
            return (DataDictItemEO) cache.get(key);
        }
    }

    /*
    * 写入操作
    * */
    public static void put(String key,Object object) {
        write.lock();
        try{
            cache.put(key, object);
        }finally {
            write.unlock();
        }
    }

    public static void refresh() {
        write.lock();
        try{
            cache.clear();
        }finally {
            write.unlock();
        }
    }

}
