package cn.lonsun.system.role.internal.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.service.IMenuRightsService;

/**
 * @author gu.fei
 * @version 2015-10-12 9:35
 */
public class MenuRightsCache {

    private static Map<String,Object> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IMenuRightsService menuRightsService;

    static {
        cache = new ConcurrentHashMap<String, Object>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        menuRightsService = SpringContextHolder.getBean("menuRightsService");
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static RbacMenuRightsEO get(Long roleId,Long menuId,String optCode,Long siteId) {
        String key = String.valueOf(roleId).concat("_").concat(menuId.toString()).concat("_").concat(optCode).concat("_").concat(String.valueOf(siteId));
        read.lock();
        try{
            if(AppUtil.isEmpty(cache.get(key))) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("roleId",roleId);
                map.put("menuId",menuId);
                map.put("optCode",optCode);
                map.put("siteId",siteId);
                RbacMenuRightsEO eo = menuRightsService.getEntity(RbacMenuRightsEO.class, map);
                read.unlock(); //解锁，写入
                if(!AppUtil.isEmpty(eo))
                    MenuRightsCache.put(key, eo);
                read.lock();
            }
        }finally {
            read.unlock();
            return (RbacMenuRightsEO) cache.get(key);
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
