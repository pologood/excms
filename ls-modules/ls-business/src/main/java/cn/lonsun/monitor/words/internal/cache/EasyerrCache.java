package cn.lonsun.monitor.words.internal.cache;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.service.IWordsEasyerrService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class EasyerrCache {

    private static Logger log = LoggerFactory.getLogger(EasyerrCache.class);

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IWordsEasyerrService wordsEasyerrService;

    static {
        cache = new HashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        wordsEasyerrService = SpringContextHolder.getBean("wordsEasyerrService");
        cache = wordsEasyerrService.getMaps();
    }
    /*
    * 读取操作
    * 组成字符串
    * */
    public static WordsEasyerrEO get(String key) {
        read.lock();
        try{
            if(cache == null) {
                read.unlock();
                cache = wordsEasyerrService.getMaps();
                read.lock();
            }
        }finally {
            read.unlock();if(key.contains("钟俊")){
                log.info("词库：{}", JSONObject.toJSONString(cache));
            }
            return (WordsEasyerrEO) cache.get(key);
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
                log.info("刷新易错词缓存");
                cache = wordsEasyerrService.getMaps();
                log.info("刷新完成易错词缓存");
            }
        }finally {
            write.unlock();
        }
    }

}
