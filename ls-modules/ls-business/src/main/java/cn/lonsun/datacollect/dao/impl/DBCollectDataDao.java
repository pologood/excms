package cn.lonsun.datacollect.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectDataDao;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.site.template.util.SqlHelper;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("dbCollectDataDao")
public class DBCollectDataDao extends MockDao<DBCollectDataEO> implements IDBCollectDataDao {

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("from DBCollectDataEO where 1=1 and taskId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getTaskId()});
    }


    @Override
    public void deleteByTaskId(Long taskId) {
        this.executeUpdateBySql("DELETE FROM CMS_DB_COLLECT_DATA WHERE TASK_ID = ?", new Object[]{taskId});
    }

    @Override
    public void deleteById(Long id) {
        this.executeUpdateBySql("DELETE FROM CMS_DB_COLLECT_DATA WHERE ID = ?", new Object[]{id});
    }
}
