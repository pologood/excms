package cn.lonsun.system.datadictionary.internal.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.dao.IDataDictDao;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.vo.DataDictPageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 
 * @ClassName: DataDictDaoImpl
 * @Description: 数据字典数据访问层
 * @author Hewbing
 * @date 2015年10月15日 上午11:30:43
 *
 */
@Repository("dataDictDao")
public class DataDictDaoImpl extends BaseDao<DataDictEO> implements
		IBaseDao<DataDictEO>, IDataDictDao {

	@Override
	public Pagination getPage(DataDictPageVO pageVO) {
		StringBuffer hql=new StringBuffer("from DataDictEO where 1=1");
		Map<String,Object> map=new HashMap<String,Object>();
		if(!AppUtil.isEmpty(pageVO.getName())){
			hql.append(" and name like :name escape'\\'");
			map.put("name","%".concat(pageVO.getName() ).concat("%"));
		}
		if(!AppUtil.isEmpty(pageVO.getCode())){
			hql.append(" and code like :code escape '\\'");
			map.put("code", "%".concat(pageVO.getCode()).concat("%"));
		}
		
		if(LoginPersonUtil.isRoot()){
			hql.append(" and 1=1");
		}else if(LoginPersonUtil.isSuperAdmin()||LoginPersonUtil.isSiteAdmin()){
			hql.append(" and isOpen=1");
		}else{
			hql.append(" and 1!=1");
		}
		hql.append(" order by name desc");
		return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
	}

	@Override
	public DataDictEO getDataDictByCode(String code) {
		String hql="from DataDictEO where code=?";
		return getEntityByHql(hql, new Object[]{code});
	}

	@Override
	public void markUsed(String code, Integer isUsed) {
		String hql="update DataDictEO set isUsed=? where code=?";
		executeUpdateByHql(hql, new Object[]{isUsed,code});
	}

	@Override
	public void changeUsed(Long id, Integer isUsed) {
		String hql="update DataDictEO set isUsed=? where dictId=?";
		executeUpdateByHql(hql, new Object[]{isUsed,id});
	}

}
