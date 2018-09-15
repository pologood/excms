package cn.lonsun.process.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.process.dao.IActivityUserSetDao;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.vo.ActivityUserSetQueryVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-18.
 */
@Repository
public class ActivityUserSetDaoImpl extends BaseDao<ActivityUserSetEO> implements IActivityUserSetDao {

    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getPagination(ActivityUserSetQueryVO queryVO) {
        Map<String,Object> params = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from ActivityUserSetEO t where 1=1 ");
        if(null != queryVO.getUserId()){
            hql.append(" and t.userIds like :userIds ");
            params.put("userIds", "%" + SqlUtil.prepareParam4Query(queryVO.getUserId()) + "%");
        }
        if(null != queryVO.getUserName()){
            hql.append(" and t.userNames like :userNames ");
            params.put("userNames", "%" + SqlUtil.prepareParam4Query(queryVO.getUserName()) + "%");
        }
        if(!AppUtil.isEmpty(queryVO.getSiteId())) {
            hql.append(" and t.siteId = :siteId ");
            params.put("siteId", queryVO.getSiteId());
        }
        if(null != queryVO.getActivityId()){
            hql.append(" and t.activityId = :activityId");
            params.put("activityId", queryVO.getActivityId());
        }
        if(null != queryVO.getProcessId()){
            hql.append(" and t.processId = :processId");
            params.put("processId", queryVO.getProcessId());
        }

        if(!AppUtil.isEmpty(queryVO.getSortField())) {
            hql.append(" order by t.").append(queryVO.getSortField()).append(" ").append(queryVO.getSortOrder());
        }
        return getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),params);
    }
}
