package cn.lonsun.weibo.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.weibo.entity.WeiboConfEO;

/**
 * @author gu.fei
 * @version 2015-12-8 17:12
 */
public interface IWeiboConfDao extends IBaseDao<WeiboConfEO> {

    public WeiboConfEO getByType(String type,Long siteId);

    public void clearDate(Long siteId);
}
