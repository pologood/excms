package cn.lonsun.datacollect.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectColumnDao;
import cn.lonsun.datacollect.entity.DBCollectColumnEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.site.template.util.SqlHelper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("dbCollectColumnDao")
public class DBCollectColumnDao extends MockDao<DBCollectColumnEO> implements IDBCollectColumnDao {

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("from DBCollectColumnEO where 1=1 and taskId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getTaskId()});
    }

    @Override
    public List<DBCollectColumnEO> getEOsByTaskId(Long taskId) {
        return this.getEntitiesByHql("from DBCollectColumnEO where taskId = ?",new Object[] {taskId});
    }

    @Override
    public void deleteEOs(Long[] ids) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ids", ids);
        this.executeUpdateByJpql("delete from DBCollectColumnEO WHERE ID IN (:ids)", param);
    }
}
