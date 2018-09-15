package cn.lonsun.weibo.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.weibo.dao.ISinaWeiboContentFDao;
import cn.lonsun.weibo.entity.SinaWeiboContentFEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-24 13:53
 */
@Repository
public class SinaWeiboContentFDao extends MockDao<SinaWeiboContentFEO> implements ISinaWeiboContentFDao {

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuffer hql = new StringBuffer(" from SinaWeiboContentFEO where 1=1 and siteId = ? ");
        vo.setSortField("weiboId");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getSiteId()});
    }

    @Override
    public void delByWeiboId(String weiboId) {
        this.executeUpdateByHql("delete from SinaWeiboContentFEO where weiboId = ?",new Object[] {weiboId});
    }

    @Override
    public SinaWeiboContentFEO getByWeiboId(String weiboId) {
        return this.getEntityByHql("from SinaWeiboContentFEO where weiboId = ?",new Object[]{weiboId});
    }


}
