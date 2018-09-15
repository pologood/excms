package cn.lonsun.system.role.internal.cache;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import cn.lonsun.system.datadictionary.internal.service.IDataDictService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class RightDictCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IDataDictService dataDictService;
    private static IDataDictItemService dataDictItemService;

    static {
        cache = new ConcurrentHashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        dataDictService = SpringContextHolder.getBean("dataDictService");
        dataDictItemService = SpringContextHolder.getBean("dataDictItemService");
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static List<DataDictItemEO> get(String dictCode) {
        read.lock();
        try{
            if(null == dictCode || null == cache.get(dictCode)) {
                return null;
            }
            return (List<DataDictItemEO>) cache.get(dictCode);
        }finally {
            read.unlock();
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
            List<DataDictEO> list = dataDictService.getEntities(DataDictEO.class,new HashMap<String, Object>());
            for(DataDictEO eo : list) {
                List<DataDictItemEO> items = dataDictItemService.getListByDictId(eo.getDictId());
                if(null != items) {
                    RightDictCache.put(eo.getCode(), items);
                }
            }
        }finally {
            write.unlock();
        }
    }

}
