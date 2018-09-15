package cn.lonsun.weibo.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.weibo.dao.IWeiboConfDao;
import cn.lonsun.weibo.entity.WeiboConfEO;

/**
 * @author gu.fei
 * @version 2015-12-8 17:12
 */
@Repository
public class WeiboConfDao extends BaseDao<WeiboConfEO> implements IWeiboConfDao {

    @Override
    public WeiboConfEO getByType(String type,Long siteId) {
        WeiboConfEO eo = this.getEntityByHql("from WeiboConfEO where type = ? and siteId = ?", new Object[]{type,siteId});
        if(null == eo) {
            return new WeiboConfEO();
        }

        return eo;
    }

    @Override
    public void clearDate(Long siteId) {
        String hql1 = "delete from SinaWeiboContentEO where siteId=?";
        String hql2 = "delete from SinaWeiboCommentByMeEO where siteId=?";
        String hql3 = "delete from SinaWeiboCommentToMeEO where siteId=?";
        String hql4 = "delete from SinaWeiboContentFEO where siteId=?";
        String hql5 = "delete from SinaWeiboContentMEO where siteId=?";
        String hql6 = "delete from SinaWeiboContentSEO where siteId=?";
        String hql7 = "delete from SinaWeiboUserInfoEO where siteId=?";
        this.executeUpdateByHql(hql1.toString(), new Object[]{siteId});
        this.executeUpdateByHql(hql2.toString(), new Object[]{siteId});
        this.executeUpdateByHql(hql3.toString(), new Object[]{siteId});
        this.executeUpdateByHql(hql4.toString(), new Object[]{siteId});
        this.executeUpdateByHql(hql5.toString(),new Object[]{siteId});
        this.executeUpdateByHql(hql6.toString(), new Object[]{siteId});
        this.executeUpdateByHql(hql7.toString(), new Object[]{siteId});
    }
}
