package cn.lonsun.supervise.errhref.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.supervise.errhref.internal.dao.IHrefResultDao;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.vo.SupervisePageVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class HrefResultDao extends MockDao<HrefResultEO> implements IHrefResultDao {

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("from HrefResultEO where 1=1 and taskId = ? ");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getTaskId()});
    }

    @Override
    public List<HrefResultEO> getByTaskId(Long taskId) {
        return this.getEntitiesByHql("from HrefResultEO where taskId = ?",new Object[]{taskId});
    }

    @Override
    public void physDelEOs(Long taskId) {
        this.executeUpdateByHql("delete from HrefResultEO where taskId = ?", new Object[]{taskId});
    }

    @Override
    public void physDelEO(Long resultId) {
        this.executeUpdateByHql("delete from HrefResultEO where id = ?", new Object[]{resultId});
    }

    @Override
    public Long getCountByTaskId(Long taskId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("taskId",taskId);
        return this.getCount("from HrefResultEO where taskId = :taskId",param);
    }
}
