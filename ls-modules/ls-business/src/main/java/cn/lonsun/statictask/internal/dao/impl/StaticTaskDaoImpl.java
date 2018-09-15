package cn.lonsun.statictask.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statictask.internal.dao.IStaticTaskDao;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 生成静态任务Dao层实现类<br/>
 * 
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Repository("staticTaskDao")
public class StaticTaskDaoImpl extends MockDao<StaticTaskEO> implements IStaticTaskDao {
    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, Long userId) {
        Long siteId = LoginPersonUtil.getSiteId();
        StringBuffer hql =
        // 暂去掉用户条件，因要判断当前任务不能同时创建2个
                new StringBuffer(" from StaticTaskEO where createUserId='" + userId + "' and recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        if (siteId != null) {
            hql.append(" and siteId=" + siteId);
        }
        hql.append(" order by id desc, createDate desc");
        Pagination page = getPagination(pageIndex, pageSize, hql.toString(), new Object[] {});
        return page;
    }

    @Override
    public void deleteByStatus(Long status) {
        String hql = "delete from StaticTaskEO where siteId = ? and status = ?";
        executeUpdateByHql(hql, new Object[] { LoginPersonUtil.getSiteId(), status });
    }

    @Override
    public void deleteInitDoingTask() {
        String hql = "delete from StaticTaskEO t where t.status <> ?";
        executeUpdateByHql(hql, new Object[] { StaticTaskEO.COMPLETE });
    }

    @Override
    public void updateInitDoingToOver() {
        String hql = "update StaticTaskEO t set t.time = 0, t.status = ? where t.status = ? or t.status = ?";
        executeUpdateByHql(hql, new Object[] { StaticTaskEO.OVER, StaticTaskEO.INIT, StaticTaskEO.DOING });
    }

    @Override
    public List<StaticTaskEO> checkTask(Long siteId, Long columnID, Long scope, Long source) {
        StringBuffer hql = new StringBuffer();
        hql.append("from StaticTaskEO where recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' and STATUS<>3 and scope=" + scope + "");

        // 如果有首页，判断站点ID，栏目和文章判断栏目ID
        if (scope == 1) {
            hql.append("and siteId=" + siteId);
        } else {
            hql.append("and columnId=" + columnID);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        List<StaticTaskEO> eo = this.getEntitiesByHql(hql.toString(), map);

        return eo;
    }
}