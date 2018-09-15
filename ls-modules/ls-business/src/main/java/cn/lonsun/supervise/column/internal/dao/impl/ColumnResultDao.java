package cn.lonsun.supervise.column.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.supervise.column.internal.dao.IColumnResultDao;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class ColumnResultDao extends MockDao<ColumnResultEO> implements IColumnResultDao {

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        Map<String,Object> param = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from ColumnResultEO where 1=1 and taskId = :taskId");
        param.put("taskId",vo.getTaskId());
        if(!AppUtil.isEmpty(vo.getSiteId())) {
            hql.append(" and cSiteId=:cSiteId");
            param.put("cSiteId",vo.getSiteId());
        }
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), param);
    }

    @Override
    public void physDelEOs(Long taskId) {
        this.executeUpdateByHql("delete from ColumnResultEO where taskId = ?", new Object[]{taskId});
    }
}
