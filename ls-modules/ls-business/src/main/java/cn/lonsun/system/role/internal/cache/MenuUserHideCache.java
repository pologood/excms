package cn.lonsun.system.role.internal.cache;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.service.IMenuUserHideService;

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
public class MenuUserHideCache {

    private static Map<String,RbacMenuUserHideEO> cache;
    private static ReentrantReadWriteLock lock;
    private static Lock read;
    private static Lock write;
    private static IMenuUserHideService menuUserHideService;

    static {
        cache = new ConcurrentHashMap<String, RbacMenuUserHideEO>();
        lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();
        menuUserHideService = SpringContextHolder.getBean("menuUserHideService");
    }
    /*
    * 读取操作
    * key : code;
    * 组成字符串
    * */
    public static RbacMenuUserHideEO get(Long menuId,Long organId,Long userId) {
        String key = String.valueOf(menuId).concat("_").concat(String.valueOf(organId)).concat(String.valueOf(userId));
        read.lock();
        try{
            if(AppUtil.isEmpty(cache.get(key))) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("menuId",menuId);
                map.put("organId",organId);
                map.put("userId",userId);
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                RbacMenuUserHideEO eo = menuUserHideService.getEntity(RbacMenuUserHideEO.class, map);
                read.unlock(); //解锁，写入
                if(!AppUtil.isEmpty(eo))
                    MenuUserHideCache.put(key, eo);
                read.lock();
            }
        }finally {
            read.unlock();
            return cache.get(key);
        }
    }

    /*
    * 写入操作
    * */
    public static void put(String key,RbacMenuUserHideEO eo) {
        write.lock();
        try{
            cache.put(key, eo);
        }finally {
            write.unlock();
        }
    }

    public static void refresh() {
        write.lock();
        try{
            cache.clear();
            List<RbacMenuUserHideEO> list = menuUserHideService.getEntities(RbacMenuUserHideEO.class,new HashMap<String, Object>());
            for(RbacMenuUserHideEO eo : list) {
                String key = String.valueOf(eo.getMenuId()).concat("_").concat(String.valueOf(eo.getOrganId())).concat(String.valueOf(eo.getUserId()));
                MenuUserHideCache.put(key,eo);
            }
        }finally {
            write.unlock();
        }
    }
}
