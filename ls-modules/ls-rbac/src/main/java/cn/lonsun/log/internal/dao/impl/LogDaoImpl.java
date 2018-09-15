package cn.lonsun.log.internal.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.dao.ILogDao;
import cn.lonsun.log.internal.entity.LogEO;

/**
 * Created by yy on 2014/8/12.
 */
@Repository("rbacLogDao")
public class LogDaoImpl extends MockDao<LogEO> implements ILogDao {

    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize, Date startDate, Date endDate, String type, String key) {
        String hql = " from LogEO t where  t.recordStatus='Normal' ";
        List<Object> values = new ArrayList<Object>();
        
        if(null!=startDate) {
            hql += " and t.createDate>? ";
            values.add(startDate);
        }
        if(null!=endDate) {
            hql += " and t.createDate<? ";
            values.add(endDate);
        }
        if(null!=StringUtils.trimToNull(type) && null!=StringUtils.trimToNull(key)) {
        	if("deion".equals(type)){
        		hql += "and t.description like '%"+key+"%'";
        	}else{
        		hql += "and t."+type+" like '%"+key+"%'";
        	}
        }
        hql += " order by t.createDate desc ";
        return (Pagination) getPagination(pageIndex, pageSize, hql, values.toArray());
    }

	@Override
	public List<LogEO> getAllLogs(Date startDate, Date endDate, String type,
			String key) {
		String hql = " from LogEO t where  t.recordStatus='Normal' ";
        List<Object> values = new ArrayList<Object>();
        
        if(null!=startDate) {
            hql += " and t.createDate>? ";
            values.add(startDate);
        }
        if(null!=endDate) {
            hql += " and t.createDate<? ";
            values.add(endDate);
        }
        if(null!=StringUtils.trimToNull(type) && null!=StringUtils.trimToNull(key)) {
            hql += "and t."+type+" like '%"+key+"%'"; 
        }
        hql += " order by t.createDate desc ";
        return getEntitiesByHql(hql, values.toArray());
	}
    
}
