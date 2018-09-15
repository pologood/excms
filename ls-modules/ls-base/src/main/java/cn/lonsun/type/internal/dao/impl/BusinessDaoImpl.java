package cn.lonsun.type.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.type.internal.dao.IBusinessTypeDao;
import cn.lonsun.type.internal.entity.BusinessTypeEO;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;

/**
 * Created by yy on 2014/8/12.
 */
@Repository
public class BusinessDaoImpl extends MockDao<BusinessTypeEO> implements IBusinessTypeDao {

	@Override
	public Long getCountByParentId(Long parentId) {
		String hql = "from BusinessTypeEO t where t.parentId=? and t.recordStatus=?";
		return getCount(hql, new Object[]{parentId,AMockEntity.RecordStatus.Normal.toString()});
	}

	@Override
	public List<BusinessTypeEO> getBusinessTypes(Long parentId,String type) {
		StringBuffer hql = new StringBuffer("from BusinessTypeEO t where 1=1");
		List<Object> values = new ArrayList<Object>();
		if(parentId==null||parentId<=0){
			hql.append(" and t.parentId is null");
		}else{
			hql.append(" and t.parentId=?");
			values.add(parentId);
		}
		hql.append("and t.type=? and t.recordStatus=?");
		values.add(type);
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<BusinessTypeEO> getBusinessTypeByCase(Long caseId,
			String caseType) {
		String hql = "from BusinessTypeEO t where t.caseId=? and t.caseType=? and t.recordStatus=?";
		return getEntitiesByHql(hql, new Object[]{caseId,caseType,AMockEntity.RecordStatus.Normal.toString()});
	}

    @Override
    public List<BusinessTypeEO> getByTypeWithCaseCode(String type, String caseCode) {
        
        String hql = "from BusinessTypeEO t where t.type=? and t.caseCode=? and t.recordStatus=?";
        return getEntitiesByHql(hql, new Object[]{type,caseCode,AMockEntity.RecordStatus.Normal.toString()});
    }

}
