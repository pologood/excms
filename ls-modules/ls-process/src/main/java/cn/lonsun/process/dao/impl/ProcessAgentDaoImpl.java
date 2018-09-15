
package cn.lonsun.process.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.process.dao.IProcessAgentDao;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.vo.ProcessAgentQueryVO;
import cn.lonsun.process.vo.ProcessAgentVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *数据持久层实现
 *@date 2015-1-7 9:32  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
@Repository
public class ProcessAgentDaoImpl extends BaseDao<ProcessAgentEO> implements IProcessAgentDao {

    /**
     * 获取代填分页列表
     *
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getAgentPagination(ProcessAgentQueryVO queryVO) {
        StringBuilder hql = new StringBuilder("select t.agentId as agentId,t.beAgentPersonName as beAgentPersonName,t.beAgentOrganName as beAgentOrganName,t.agentPersonNames as agentPersonNames,t.agentOrganNames as agentOrganNames,t.createDate as createDate from ProcessAgentEO t");
        hql.append(" where t.createUnitId = ?");
        List<Object> values = new ArrayList<Object>();
        values.add(queryVO.getUnitId());
        if(!AppUtil.isEmpty(queryVO.getBeAgentPersonName())){
            hql.append(" and t.beAgentPersonName like ? ");
            values.add("%" + SqlUtil.prepareParam4Query(queryVO.getBeAgentPersonName()) + "%");
        }
        if(!AppUtil.isEmpty(queryVO.getModuleCode())){
            hql.append(" and t.moduleCode = ? ");
            values.add(queryVO.getModuleCode());
        }
        if(!AppUtil.isEmpty(queryVO.getSortField())) {
            hql.append(" order by t.").append(queryVO.getSortField()).append(" ").append(queryVO.getSortOrder());
        }else {
            hql.append(" order by t.createDate desc ");
        }
        return this.getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),values.toArray(), ProcessAgentVO.class);
    }

    @Override
    public boolean existBeAgent(Long beAgentUserId, Long beAgentOrganId, Long agentId, String moduleCode) {

        String hql = "select t.agentId from ProcessAgentEO t where t.beAgentUserId=? and t.beAgentOrganId=?";
        List<Object> values = new ArrayList<Object>();
        values.add(beAgentUserId);
        values.add(beAgentOrganId);
        List<?> result = null;
        if(null != agentId){
            hql += " and t.agentId <> ? ";
            values.add(agentId);
        }
        if(!AppUtil.isEmpty(moduleCode)){
            hql += " and t.moduleCode = ? ";
            values.add(moduleCode);
        }
        result = this.getObjects(hql,values.toArray());
        return null == result ? false : result.size()>0;
    }

    @Override
    public List<ProcessAgentEO> getAgents(ReceiverVO[] receivers, String moduleCode) {
        StringBuilder hql = new StringBuilder("from ProcessAgentEO t ");
        List<Object> values = new ArrayList<Object>();
        hql.append(" where (");
        for(ReceiverVO vo : receivers){
            hql.append("(t.beAgentUserId=? and t.beAgentOrganId=?) or ");
            values.add(vo.getUserId());
            values.add(vo.getOrganId());
        }
        hql.delete(hql.length() - 4, hql.length());
        hql.append(")");
        if(!AppUtil.isEmpty(moduleCode)){
            hql.append(" and t.moduleCode = ? ");
            values.add(moduleCode);
        }
        return getEntitiesByHql(hql.toString(),values.toArray());
    }
}
