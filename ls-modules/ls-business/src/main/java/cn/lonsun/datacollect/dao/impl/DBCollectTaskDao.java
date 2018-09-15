package cn.lonsun.datacollect.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectTaskDao;
import cn.lonsun.datacollect.entity.DBCollectTaskEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("dbCollectTaskDao")
public class DBCollectTaskDao extends MockDao<DBCollectTaskEO> implements IDBCollectTaskDao {

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        Long siteId = LoginPersonUtil.getSiteId();
        StringBuilder hql = new StringBuilder("from DBCollectTaskEO where 1=1 and siteId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{siteId});
    }


    @Override
    public void deleteEOs(Long[] ids) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ids", ids);
        this.executeUpdateByJpql("delete from DBCollectTaskEO where id in (:ids)", param);
    }
}
