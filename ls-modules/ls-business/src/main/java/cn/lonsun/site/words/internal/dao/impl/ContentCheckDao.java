package cn.lonsun.site.words.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.site.words.internal.dao.IContentCheckDao;
import cn.lonsun.site.words.internal.entity.ContentCheckEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2015-12-31 13:44
 */
@Repository
public class ContentCheckDao extends MockDao<ContentCheckEO> implements IContentCheckDao {

    @Override
    public Pagination getPageEO(WordsPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("");
        Long siteId = LoginPersonUtil.getSiteId();
        hql.append("from ContentCheckEO where 1=1 and siteId = " + siteId + " and checkType = '" + vo.getCheckType() + "' ");
        if(!AppUtil.isEmpty(vo.getType())) {
            hql.append(" and type='" + vo.getType() + "'");
        }
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{});
    }

    @Override
    public void deleteById(Long id) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK WHERE ID = ?");
        this.executeUpdateBySql(sql.toString(), new Object[]{id});
    }

    @Override
    public void deleteByCheckType(String checkType,Long siteId) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK WHERE CHECK_TYPE = '" + checkType + "'");
        sql.append(" AND SITE_ID = " + siteId);

        this.executeUpdateBySql(sql.toString(), new Object[]{});
    }

    @Override
    public void deleteByCheckType(Long contentId, String checkType) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK WHERE CONTENT_ID = ? AND CHECK_TYPE = ?");
        Long siteId = LoginPersonUtil.getSiteId();
        sql.append(" AND SITE_ID = " + siteId);

        this.executeUpdateBySql(sql.toString(), new Object[]{contentId,checkType});
    }
}
