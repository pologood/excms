package cn.lonsun.system.role.internal.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class SiteRightsCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static ISiteRightsService siteRightsService;

    static {
        cache = new ConcurrentHashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        siteRightsService = SpringContextHolder.getBean("siteRightsService");
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static RbacSiteRightsEO get(Long roleId,Long indicatorId,String optCode) {
        String key = String.valueOf(roleId).concat("_").concat(indicatorId.toString()).concat("_").concat(optCode);
        read.lock();
        try{
            if(null == key) {
                return null;
            }
            return (RbacSiteRightsEO) cache.get(key);
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
            List<RbacSiteRightsEO> list = siteRightsService.getEntities(RbacSiteRightsEO.class,new HashMap<String, Object>());

            for(RbacSiteRightsEO eo : list) {
                String key = String.valueOf(eo.getRoleId()).concat("_").concat(eo.getIndicatorId().toString()).concat("_").concat(null == eo.getOptCode()?"":eo.getOptCode());
                SiteRightsCache.put(key,eo);
            }

        }finally {
            write.unlock();
        }
    }

}
