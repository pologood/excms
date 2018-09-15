package cn.lonsun.process.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.process.dao.IProcessFormDao;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by zhu124866 on 2015-12-18.
 */
@Repository
public class ProcessFormDaoImpl extends BaseDao<ProcessFormEO> implements IProcessFormDao {

    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getPagination(ProcessFormQueryVO queryVO) {
        Map<String,Object> params = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from ProcessFormEO t where 1=1 ");
        if(null != queryVO.getUserId()){
            hql.append(" and t.createUserId = :createUserId ");
            params.put("createUserId", queryVO.getUserId());
        }
        if(null != queryVO.getUnitId()){
            hql.append(" and t.createUnitId = :createUnitId ");
            params.put("createUnitId", queryVO.getUnitId());
        }
        if(!AppUtil.isEmpty(queryVO.getTitle())) {
            hql.append(" and t.title like :title ");
            params.put("title", "%" + SqlUtil.prepareParam4Query(queryVO.getTitle()) + "%");
        }
        if(null != queryVO.getStartDate()){
            hql.append(" and t.createDate >= :startDate");
            params.put("startDate", queryVO.getStartDate());
        }
        if(null != queryVO.getEndDate()){
            hql.append(" and t.createDate <= :endDate");
            Calendar date = Calendar.getInstance();
            date.setTime(queryVO.getEndDate());
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);//结束日期增加一天
            params.put("endDate", date);
        }
        if(null != queryVO.getFormStatus()){
            hql.append(" and t.formStatus in (:formStatus)");
            params.put("formStatus", queryVO.getFormStatus());
        }
        if(!AppUtil.isEmpty(queryVO.getModuleCode())){
            hql.append(" and t.moduleCode = :moduleCode");
            params.put("moduleCode", queryVO.getModuleCode());
        }
        if(!AppUtil.isEmpty(queryVO.getCurActivityName())){
            hql.append(" and t.curActivityName like :curActivityName");
            params.put("curActivityName", "%" + SqlUtil.prepareParam4Query(queryVO.getCurActivityName()) + "%");
        }
        if(!AppUtil.isEmpty(queryVO.getSortField())) {
            hql.append(" order by t.").append(queryVO.getSortField()).append(" ").append(queryVO.getSortOrder());
        }else{
            hql.append(" order by t.createDate desc ");
        }
        return getPagination(queryVO.getPageIndex(),queryVO.getPageSize(),hql.toString(),params);
    }

    @Override
    public boolean getTitleIsExist(Long processFormId, String title) {
        String hql = "select count(t.processFormId) from ProcessFormEO t where t.title = ? ";
        List<Object> values = new ArrayList<Object>();
        values.add(title);
        if(null != processFormId){
            hql += " and t.processFormId <> ? ";
            values.add(processFormId);
        }
        Long count = (Long)getObject(hql,values.toArray());
        return count.longValue() > 0;
    }
}
