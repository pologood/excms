package cn.lonsun.supervise.column.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.supervise.column.internal.dao.IColumnUpdateWarnDao;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class ColumnUpdateWarnDao extends MockDao<ColumnUpdateWarnEO> implements IColumnUpdateWarnDao {

    @Override
    public Pagination getPageTasks(SupervisePageVO vo) {
        StringBuilder hql = new StringBuilder("from ColumnUpdateWarnEO where siteId = ?");
        hql.append(" and parentColumnId like '%" + SqlUtil.prepareParam4Query(String.valueOf(vo.getColumnId())) + "%'");
        return this.getPagination(vo.getPageIndex(), vo.getPageSize(), SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{LoginPersonUtil.getSiteId()});
    }
}
