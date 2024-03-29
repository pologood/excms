package cn.lonsun.site.site.internal.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class DictItemCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;

    static {
        cache = new ConcurrentHashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static List<DataDictVO> get(String dictCode) {
        read.lock();
        try{
            if(AppUtil.isEmpty(cache.get(dictCode))) {
                List<DataDictVO> dictVOs= DataDictionaryUtil.getDDList(dictCode);
                read.unlock(); //解锁，写入
                if(!AppUtil.isEmpty(dictVOs))
                    DictItemCache.put(dictCode, dictVOs);
                read.lock();
            }
        }finally {
            read.unlock();
            return (List<DataDictVO>) cache.get(dictCode);
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
