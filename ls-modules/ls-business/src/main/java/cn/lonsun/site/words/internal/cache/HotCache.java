package cn.lonsun.site.words.internal.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.service.IWordsHotConfService;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class HotCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IWordsHotConfService wordsHotConfService;

    static {
        cache = new HashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        wordsHotConfService = SpringContextHolder.getBean("wordsHotConfService");
        cache = wordsHotConfService.getMaps();
    }
    /*
    * 读取操作
    * 组成字符串
    * */
    public static WordsHotConfEO get(String key) {
        read.lock();
        try{
            if(cache == null) {
                read.unlock();
                cache = wordsHotConfService.getMaps();
                read.lock();
            }
        }finally {
            read.unlock();
            return (WordsHotConfEO) cache.get(key);
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
                cache = wordsHotConfService.getMaps();
            }
        }finally {
            write.unlock();
        }
    }

}
