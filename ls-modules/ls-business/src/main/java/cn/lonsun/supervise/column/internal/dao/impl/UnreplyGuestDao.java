package cn.lonsun.supervise.column.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.supervise.column.internal.dao.IUnreplyGuestDao;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class UnreplyGuestDao extends MockDao<UnreplyGuestEO> implements IUnreplyGuestDao {

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuilder hql = new StringBuilder("from UnreplyGuestEO where columnId = ? and siteId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{dto.getColumnId(),dto.getSiteId()});
    }

    @Override
    public void delete(Long columnId) {
        StringBuilder hql = new StringBuilder("delete UnreplyGuestEO where columnId = ?");
        this.executeUpdateByHql(hql.toString(),new Object[]{columnId});
    }

    public void delete(final Class<UnreplyGuestEO> clazz, final Long id) {
        if (id != null && clazz != null) {
            UnreplyGuestEO entity = this.getEntity(UnreplyGuestEO.class, id);
            if (entity != null) {
                getCurrentSession().delete(entity);
            }
        }
    }
}
