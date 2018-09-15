package cn.lonsun.monitor.words.internal.cache;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class SensitiveCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IWordsSensitiveService wordsSensitiveService;

    static {
        cache = new HashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        wordsSensitiveService = SpringContextHolder.getBean("wordsSensitiveService");
        cache = wordsSensitiveService.getMaps();
    }
    /*
    * 读取操作
    * 组成字符串 key = siteId_words 组成
    * */
    public static WordsSensitiveEO get(String key) {
        read.lock();
        try{
            if(cache == null) {
                read.unlock();
                cache = wordsSensitiveService.getMaps();
                read.lock();
            }
        }finally {
            read.unlock();
            return (WordsSensitiveEO) cache.get(key);
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
            if(cache != null) {
                cache.clear();
                cache = wordsSensitiveService.getMaps();
            }
        }finally {
            write.unlock();
        }
    }

}
