package cn.lonsun.supervise.column.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.supervise.column.internal.dao.IColumnUpdateDao;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.LoginPersonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class ColumnUpdateDao extends MockDao<ColumnUpdateEO> implements IColumnUpdateDao {

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        Long siteId = LoginPersonUtil.getSiteId();
        StringBuilder hql = new StringBuilder("from ColumnUpdateEO where 1=1 and siteId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{siteId});
    }


    @Override
    public void physDelEOs(Long[] ids) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ids", ids);
        this.executeUpdateByJpql("delete from ColumnUpdateEO where id in (:ids)", param);
    }
}
