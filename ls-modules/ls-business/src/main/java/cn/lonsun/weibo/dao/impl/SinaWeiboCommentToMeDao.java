package cn.lonsun.weibo.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.weibo.dao.ISinaWeiboCommentToMeDao;
import cn.lonsun.weibo.entity.SinaWeiboCommentToMeEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;

/**
 * @author gu.fei
 * @version 2015-12-24 13:53
 */
@Repository
public class SinaWeiboCommentToMeDao extends MockDao<SinaWeiboCommentToMeEO> implements ISinaWeiboCommentToMeDao {

    @Override
    public WeiboPagination getPageCurComment(WeiboPageVO vo) {
        WeiboPagination weiboPage = new WeiboPagination();
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuffer hql = new StringBuffer(" from SinaWeiboCommentToMeEO where 1=1 and siteId = ?");
        vo.setSortField("createdAtComment");
        Pagination page = this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getSiteId()});
        weiboPage.setData(page.getData());
        weiboPage.setPageIndex(page.getPageIndex());
        weiboPage.setPageSize(page.getPageSize());
        weiboPage.setTotal(page.getTotal());
        return weiboPage;
    }

    @Override
    public void delByCommentId(String commentId) {
        this.executeUpdateByHql("delete from SinaWeiboCommentToMeEO where commentId = ?",new Object[] {commentId});
    }
}
