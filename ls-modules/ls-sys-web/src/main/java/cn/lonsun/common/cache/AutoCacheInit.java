package cn.lonsun.common.cache;

import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.role.internal.cache.SiteRightsCache;
import cn.lonsun.weibo.util.PropertiesUtil;
import org.springframework.beans.factory.InitializingBean;

/**
 * 自定义缓存初始化
 *
 * @author gu.fei
 * @version 2015-12-2 9:47
 */
public class AutoCacheInit implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        //刷新站点权限缓存
        SiteRightsCache.refresh();

        //站点操作权限字典项刷新
        RightDictCache.refresh();

        //微博问政配置项初始化
        PropertiesUtil.init();

        //同步全局搜索索引
//        SolrFactory.syncIndexFromDb();
    }
}
