package cn.lonsun.publicInfo.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.internal.dao.IPublicLeadersDao;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;

/**
 * 单位领导dao <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Repository
public class PublicLeadersDaoImpl extends MockDao<PublicLeadersEO> implements IPublicLeadersDao {

    @Override
    public Pagination getPagination(PublicLeadersQueryVO queryVO) {
        StringBuffer hql = new StringBuffer("select p from PublicLeadersEO p,OrganEO o where p.organId = o.organId and p.recordStatus=? ");
        List<Object> param = new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());

        if (queryVO != null) {
            // if(!AppUtil.isEmpty(queryVO.getSiteId())){
            // hql.append(" and p.siteId = ? ");
            // param.add(queryVO.getSiteId());
            // }

            if (!AppUtil.isEmpty(queryVO.getOrganId())) {
                hql.append(" and p.organId = ? ");
                param.add(queryVO.getOrganId());
            }

            if (!AppUtil.isEmpty(queryVO.getStatus())) {
                hql.append(" and p.status = ? ");
                param.add(queryVO.getStatus());
            }

            if (!AppUtil.isEmpty(queryVO.getPost())) {
                hql.append(" and p.post like ? ");
                param.add("%" + SqlUtil.prepareParam4Query(queryVO.getPost().trim()) + "%");
            }

            if (!AppUtil.isEmpty(queryVO.getLeadersNum())) {
                hql.append(" and p.leadersNum = ? ");
                param.add(queryVO.getLeadersNum());
            }

            if (!AppUtil.isEmpty(queryVO.getLeadersName())) {
                hql.append(" and p.leadersName like ? ");
                param.add("%" + SqlUtil.prepareParam4Query(queryVO.getLeadersName().trim()) + "%");
            }
        }

        hql.append(" order by o.sortNum asc, p.sortNum desc, p.createDate desc ");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), param.toArray());
    }

    @Override
    public List<PublicLeadersEO> getPublicLeadersList(PublicLeadersQueryVO queryVO) {
        StringBuffer hql = new StringBuffer("from PublicLeadersEO p where p.recordStatus = :recordStatus ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (!AppUtil.isEmpty(queryVO.getStatus())) {
            hql.append(" and p.status = :status");
            paramMap.put("status", queryVO.getStatus());
        }
        if (!AppUtil.isEmpty(queryVO.getOrganId())) {
            hql.append(" and p.organId = :organId");
            paramMap.put("organId", queryVO.getOrganId());
        }
        hql.append(" order by p.sortNum desc, p.createDate desc ");
        return getEntitiesByHql(hql.toString(), paramMap);
    }
}