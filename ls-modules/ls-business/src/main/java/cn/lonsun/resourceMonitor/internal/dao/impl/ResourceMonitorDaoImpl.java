package cn.lonsun.resourceMonitor.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.resourceMonitor.internal.dao.IResourceMonitorDao;
import cn.lonsun.resourceMonitor.internal.entity.ResourceMonitorEO;
import cn.lonsun.resourceMonitor.vo.ResourceMonitorQueryVO;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxx on 2017/4/11.
 */
@Repository
public class ResourceMonitorDaoImpl  extends MockDao<ResourceMonitorEO> implements IResourceMonitorDao{

    @Override
    public Pagination getPageEntities(ResourceMonitorQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        Map<String,Object> map = new HashMap<String, Object>();
        hql.append(" from ResourceMonitorEO t ")
           .append(" where t.recordStatus =:recordStatus");
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(queryVO.getResourceName())) {
            hql.append(" and t.resourceName like :resourceName");
            map.put("resourceName","%"+queryVO.getResourceName()+"%");
        }
        if(!AppUtil.isEmpty(queryVO.getStartDate())) {
            hql.append(" and createDate >=:startDate ");
            map.put("startDate",queryVO.getStartDate());
        }

        if(!AppUtil.isEmpty(queryVO.getEndDate())) {
            hql.append(" and createDate <=:endDate ");
            map.put("endDate", DateUtils.addDays(queryVO.getEndDate(),1));
        }
        hql.append(" order by t.createDate desc");

        return getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),map);
    }

    @Override
    public ResourceMonitorEO getEOByTitle(ResourceMonitorEO resourceEO) {
        StringBuffer hql = new StringBuffer();
        List<Object> values = new ArrayList<Object>();

        hql.append(" from ResourceMonitorEO where recordStatus = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(resourceEO.getResourceName())) {
            hql.append(" and resourceName = ?");
            values.add(resourceEO.getResourceName());
        }
        /*if(null != favoritesEO.getSiteId()) {
            hql.append(" and siteId = ?");
            values.add(favoritesEO.getSiteId());
        }*/
        if(null != resourceEO.getCreateUserId()) {
            hql.append(" and createUserId = ?");
            values.add(resourceEO.getCreateUserId());
        }
        return (ResourceMonitorEO)getObject(hql.toString(),values.toArray());
    }
}
