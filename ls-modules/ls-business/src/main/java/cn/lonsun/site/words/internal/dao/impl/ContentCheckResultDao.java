package cn.lonsun.site.words.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.site.words.internal.dao.IContentCheckResultDao;
import cn.lonsun.site.words.internal.entity.ContentCheckResultEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2015-12-31 13:44
 */
@Repository
public class ContentCheckResultDao extends MockDao<ContentCheckResultEO> implements IContentCheckResultDao {

    @Override
    public Pagination getPageEOById(WordsPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        boolean flag = LoginPersonUtil.isRoot();
        StringBuilder hql = new StringBuilder("");

        if(flag) {
            hql.append("from ContentCheckResultEO where 1=1");
        } else {
            Long siteId = LoginPersonUtil.getSiteId();
            hql.append("from ContentCheckResultEO where 1=1 and siteId = " + siteId);
        }

        hql.append(" and checkId = " + vo.getCheckId());

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{});
    }

    @Override
    public List<ContentCheckResultEO> getEOById(Long id) {
        boolean flag = LoginPersonUtil.isRoot();
        StringBuilder hql = new StringBuilder("");

        if(flag) {
            hql.append("from ContentCheckResultEO where 1=1");
        } else {
            Long siteId = LoginPersonUtil.getSiteId();
            hql.append("from ContentCheckResultEO where 1=1 and siteId = " + siteId);
        }

        hql.append(" and checkId = " + id);

        return this.getEntitiesByHql(hql.toString());
    }

    @Override
    public void deleteByCheckType(String checkType) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK_RESULT WHERE CHECK_TYPE = '" + checkType + "'");
        boolean flag = LoginPersonUtil.isRoot();
        if(!flag) {
            Long siteId = LoginPersonUtil.getSiteId();
            sql.append(" AND SITE_ID = " + siteId);
        }

        this.executeUpdateBySql(sql.toString(), new Object[]{});
    }

    @Override
    public void deleteByCheckType(Long checkId, String checkType) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK_RESULT WHERE CHECK_ID = ? AND CHECK_TYPE = '" + checkType + "'");
        boolean flag = LoginPersonUtil.isRoot();
        if(!flag) {
            Long siteId = LoginPersonUtil.getSiteId();
            sql.append(" AND SITE_ID = " + siteId);
        }

        this.executeUpdateBySql(sql.toString(), new Object[]{checkId});
    }

    @Override
    public void deleteByCheckType(Long checkId, String words, String checkType) {
        StringBuilder sql = new StringBuilder("DELETE FROM CMS_CONTENT_CHECK_RESULT WHERE CHECK_ID = ? AND WORDS = ? AND CHECK_TYPE = ?");
        boolean flag = LoginPersonUtil.isRoot();
        if(!flag) {
            Long siteId = LoginPersonUtil.getSiteId();
            sql.append(" AND SITE_ID = " + siteId);
        }

        this.executeUpdateBySql(sql.toString(), new Object[]{checkId,words,checkType});
    }
}
