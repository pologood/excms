package cn.lonsun.weibo.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.weibo.dao.ISinaWeiboContentMDao;
import cn.lonsun.weibo.entity.SinaWeiboContentMEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-24 13:53
 */
@Repository
public class SinaWeiboContentMDao extends MockDao<SinaWeiboContentMEO> implements ISinaWeiboContentMDao {

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuffer hql = new StringBuffer(" from SinaWeiboContentMEO where 1=1 and siteId = ?");
        vo.setSortField("weiboId");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getSiteId()});
    }

    @Override
    public void delByWeiboId(String weiboId) {
        this.executeUpdateByHql("delete from SinaWeiboContentMEO where weiboId = ?",new Object[] {weiboId});
    }

    @Override
    public SinaWeiboContentMEO getByWeiboId(String weiboId) {
        return this.getEntityByHql("from SinaWeiboContentMEO where weiboId = ?",new Object[]{weiboId});
    }
}
