package cn.lonsun.projectInformation.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.projectInformation.internal.dao.IProjectInformationDao;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxx on 2017/3/3.
 */
@Repository
public class ProjectInformationDaoImpl extends MockDao<ProjectInformationEO> implements IProjectInformationDao{

    @Override
    public Pagination getPageEntities(ProjectInformationQueryVO queryVO) {

        StringBuffer hql = new StringBuffer();
        List values = new ArrayList();
        hql.append("select t.id as id,t.buildUnitName as buildUnitName,t.projectName as projectName,t.certificationNum as certificationNum,")
                .append(" t.certificationDate as certificationDate,t.projectAddress as projectAddress,t.area as area,t.contentId as contentId")
                .append(" from ProjectInformationEO t where t.recordStatus=?");

        values.add(AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(queryVO.getProjectName())) {
            hql.append(" and projectName like ?");
            values.add("%"+queryVO.getProjectName()+"%");
        }
        if(!AppUtil.isEmpty(queryVO.getBuildUnitName())) {
            hql.append(" and buildUnitName like ?");
            values.add("%"+queryVO.getBuildUnitName()+"%");
        }
        if(!AppUtil.isEmpty(queryVO.getColumnId())) {
            hql.append(" and t.columnId=?");
            values.add(queryVO.getColumnId());
        }

        hql.append(" order by t.createDate desc");
        return getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),values.toArray(),ProjectInformationEO.class);
    }

    @Override
    public List<ProjectInformationEO> getPageEntities(ProjectInformationQueryVO queryVO, Long[] ids) {
        StringBuffer hql = new StringBuffer();
        Map<String,Object> map = new HashMap<String,Object>();
        hql.append("select t.id as id,t.buildUnitName as buildUnitName,t.projectName as projectName,t.certificationNum as certificationNum,")
                .append(" t.certificationDate as certificationDate,t.columnId as columnId,t.projectAddress as projectAddress,t.area as area,t.contentId as contentId")
                .append(" from ProjectInformationEO t where t.recordStatus=:recordStatus");

        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());

        if(null != ids && ids.length>0) {
            hql.append(" and t.columnId in :ids");
            map.put("ids",ids);
        }

        hql.append(" order by t.createDate desc");
        return (List<ProjectInformationEO>) getBeansByHql(hql.toString(),map,ProjectInformationEO.class,null);
    }
}
