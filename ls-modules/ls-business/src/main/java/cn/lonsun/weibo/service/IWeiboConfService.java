package cn.lonsun.weibo.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.weibo.entity.WeiboConfEO;

/**
 * @author gu.fei
 * @version 2015-12-8 17:13
 */
public interface IWeiboConfService extends IBaseService<WeiboConfEO> {

    public void saveEO(WeiboConfEO eo,String type) throws Exception;

    public WeiboConfEO getByType(String type,Long siteId);

}
