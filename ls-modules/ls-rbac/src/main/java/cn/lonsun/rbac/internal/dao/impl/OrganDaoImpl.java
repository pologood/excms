package cn.lonsun.rbac.internal.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.rbac.internal.vo.OrganVO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Type;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IOrganDao;
import cn.lonsun.rbac.internal.entity.OrganEO;

@Repository("organDao")
public class OrganDaoImpl extends MockDao<OrganEO> implements IOrganDao {

	@Override
	public void updateIsPublic(Long... ids){
		if(ids == null || ids.length == 0){
			return;
		}
		StringBuilder hql = new StringBuilder();
		hql.append("update OrganEO set isPublic = 1 where organId in (");
		for(Long id : ids){
			hql.append(id).append(",");
		}
		int length = hql.length();
		hql.replace(length - 1, length, "");
		hql.append(") ");
		super.executeUpdateByHql(hql.toString(), new Object[]{});
	}

	@Override
	public List<Object> getOrgans(Long pageIndex,int pageSize){
		String hql = "from OrganEO o order by o.organId";
		int first = Pagination.getStartNumber(pageIndex, pageSize).intValue();
		int max = Pagination.getPageSize(pageSize);
		List<Object> persons = getPaginationRecores(hql, first, max, new Object[]{});
		return persons;
	}
	
	@Override
	public void test(){
		String sql = "select m.rabc_id, s.sortorder,s.parentuniqueid from rbac_data_move m, rbac_old_orgobjectsort s where s.childuniqueid = m.uniqueid and m.type in ('DEPART', 'ORGAN')";
		List<?> objects = getObjectsBySql(sql, new Object[]{});
		System.out.println("总部门数量为："+objects.size());
		int count = 0 ;
		if(objects!=null){
			int i=0;
			for(Object obj:objects){
				Object[] objs = (Object[])obj;
				Long organId = Long.valueOf(objs[0].toString());
				Long sortNum = Long.valueOf(objs[1]==null?(500L+i+""):objs[1].toString());
				if(organId!=null){
					OrganEO organ = getEntity(OrganEO.class, organId);
					organ.setSortNum(sortNum);
					update(organ);
				}
				count++;
				System.out.println("已经更新了第"+count+"个部门");
			}
		}
	}
	
	@Override
	public List<OrganEO> getOrgans(Long[] organIds,Boolean isContainsExternal){
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1");
		List<Object> values = new ArrayList<Object>();
		//如果organIds为空，那么不做限制
		if(organIds!=null&&organIds.length>0){
			hql.append(" and (1=0");
			for(Long organId:organIds){
				hql.append(" or o.organId=?");
				values.add(organId);
			}
			hql.append(")");
		}
		if(isContainsExternal!=null&&!isContainsExternal){
			hql.append(" and o.isExternalOrgan=?");
			values.add(Boolean.FALSE);
		}
		hql.append(" and o.recordStatus=? order by o.sortNum");
		values.add(RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public List<OrganEO> getSubOrgans(Long[] parentIds,Boolean isContainsExternal){
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1");
		List<Object> values = new ArrayList<Object>();
		if(parentIds==null||parentIds.length<=0){
			hql.append(" and o.parentId is null");
		}else{
			hql.append(" and (1=0");
			for(Long parentId:parentIds){
				hql.append(" or o.parentId=?");
				values.add(parentId);
			}
			hql.append(")");
		}
		if(isContainsExternal!=null&&!isContainsExternal){
			hql.append(" and o.isExternalOrgan=?");
			values.add(Boolean.FALSE);
		}
		hql.append(" and o.recordStatus=? order by o.sortNum");
		values.add(RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	/**
	 * 获取organId在organIds的组织
	 *
	 * @param organIds
	 * @return
	 */
	public List<OrganEO> getOrgansByOrganIds(Long[] organIds){
		StringBuffer hql = new StringBuffer("from OrganEO o where o.recordStatus=? and (1=0 ");
		List<Object> values = new ArrayList<Object>();
		values.add(RecordStatus.Normal.toString());
		for(Long organId:organIds){
			if(organId!=null){
				hql.append(" or o.organId=?");
				values.add(organId);
			}
		}
		hql.append(") order by o.sortNum");
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	/**
	 * 根据单位ID集合删除单位记录
	 *
	 * @param organIds
	 */
	@Override
	public void deleteByOrganIds(List<Long> organIds){
		StringBuffer hql = new StringBuffer("update OrganEO o set o.recordStatus=? where 1=1 and");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Removed.toString());
		hql.append(" (1=0 or");
		for(Long organId:organIds){
			hql.append(" or o.organId=?");
			values.add(organId);
		}
		hql.append(") and o.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		executeUpdateByHql(hql.toString(), values.toArray());
	}
	
	/**
	 * 获取所有的子孙单位、部门和虚拟部门主键集合
	 *
	 * @param organId
	 * @return
	 */
	public List<Long> getDescendantOrganIds(Long organId){
		String sql = "select o.organ_id from rbac_organ o start with o.organ_id=(:organId) and o.record_status=(:recordStatus) connect by prior o.organ_id=o.parent_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("organId", organId);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<?> objects = getObjectsBySql(sql, params);
		List<Long> organIds = null; 
		if(objects!=null&&objects.size()>0){
			organIds = new ArrayList<Long>(objects.size());
			for(Object obj:objects){
				Long id = Long.valueOf(((BigDecimal)obj).toString());
				organIds.add(id);
			}
		}
		return organIds;
	}
	
	@Override
	public void updateHasOrgans(Integer hasVirtualNode,Integer hasOrgans,Integer hasOrganUnits,Integer hasFictitiousUnits,Long organId){
		StringBuffer hql = new StringBuffer("update OrganEO o set ");
		List<Object> values = new ArrayList<Object>();
		String updated = null;
		if(hasVirtualNode!=null){
			updated = "o.hasVirtualNode=?";
			values.add(hasVirtualNode);
		}
		if(hasOrgans!=null){
			updated = "o.hasOrgans=?";
			values.add(hasOrgans);
		}
		if(hasOrganUnits!=null){
			if(updated==null){
				updated = "o.hasOrganUnits=?";
			}else{
				updated = updated+",o.hasOrganUnits=?";
			}
			values.add(hasOrganUnits);
		}
		if(hasFictitiousUnits!=null){
			if(updated==null){
				updated = "o.hasFictitiousUnits=?";
			}else{
				updated = updated+",o.hasFictitiousUnits=?";
			}
			values.add(hasFictitiousUnits);
		}
		if(updated!=null){
			hql.append(updated);
		}
		hql.append(" where o.organId=?");
		values.add(organId);
		executeUpdateByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public void deleteCodes(Long[] organIds){
		StringBuffer hql = new StringBuffer("update OrganEO o set o.code=null where o.code is not null and o.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		hql.append(" and (1=0");
		for(int i=0;i<organIds.length;i++){
			Long organId = organIds[i];
			hql.append(" or o.organId=?");
			values.add(organId);
		}
		hql.append(")");
		executeUpdateByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public void updateCode(Long organId,String code){
		String hql = "update OrganEO o set o.code=? where o.organId=? and (o.code is null or o.code!=?) and o.recordStatus=?";
		executeUpdateByHql(hql, new Object[]{code,organId,code,AMockEntity.RecordStatus.Normal.toString()});
	}
	
	@Override
	public Pagination getPagination4Code(PageQueryVO query){
		String hql = "from OrganEO o where o.code is not null and o.recordStatus=?";
		return getPagination(query.getPageIndex(), query.getPageSize(), hql, new Object[]{AMockEntity.RecordStatus.Normal.toString()});
	}
	
	@Override
	public int updateIsExternal(String organDn,Boolean isExternal){
		String hql = "update OrganEO o set o.isExternal=? where o.dn like ? and(o.isExternal is null or o.isExternal!=?) and o.recordStatus=?";
		Object[] values = new Object[]{isExternal,"%"+organDn,isExternal,AMockEntity.RecordStatus.Normal.toString()};
		return executeUpdateByHql(hql, values);
	}
	
	@Override
	public List<?> getOrganInfos(String type,Integer isFictitious){
		StringBuilder hql = new StringBuilder("select o.organId,o.name,o.type,o.parentId,replace(o.dn,',','-'),o.simplePy,o.fullPy,o.isFictitious from OrganEO o where o.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(type)){
			hql.append(" and o.type=?");
			values.add(type);
		}
		if(isFictitious!=null){
			hql.append(" and o.isFictitious=?");
			values.add(isFictitious);
		}
		return getObjects(hql.toString(), values.toArray());
	}
	
	@Override
	public List<?> getPersonInfosByPlatformCode(String type,Integer isFictitious,String platformCode){
		return getPersonInfosByPlatformCode(null, type, isFictitious, platformCode);
	}

	@Override
	public List<?> getPersonInfosByPlatformCode(Long[] organIds,String type,Integer isFictitious,String platformCode){
		StringBuilder hql = new StringBuilder("select o.organId,o.name,o.type,o.parentId,o.dn,o.simplePy,o.fullPy,o.isFictitious,o.isExternalOrgan,o.platformCode,o.simpleName from OrganEO o where o.recordStatus= ? ");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(type)){
			hql.append(" and o.type = ?");
			values.add(type);
		}
		if(isFictitious!=null){
			hql.append(" and o.isFictitious= ?");
			values.add(isFictitious);
		}
		if(!StringUtils.isEmpty(platformCode)){
			hql.append(" and o.platformCode= ?");
			values.add(platformCode);
		}
		if(organIds != null && organIds.length > 0){
			hql.append(" and (");
			for(int i = 0; i < organIds.length; i++){
				if(i != 0){
					hql.append(" or ");
				}
				hql.append(" o.organId = ? ");
				values.add(organIds[i]);
			}
			hql.append(") ");
		}
		hql.append(" order by o.sortNum");
		return getObjects(hql.toString(), values.toArray());
	}

	@Override
	public List<String> getNames(String[] simpleDns,int scope,String blurryNameOrPY){
		List<Object> values = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select distinct(o.NAME_) from rbac_organ o where o.record_Status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		sql.append(" and (o.NAME_ like ? escape '\\' or o.full_PY like ? escape '\\' or o.simple_PY like ? escape '\\')");
		//特殊字符处理
		blurryNameOrPY = SqlUtil.prepareParam4Query(blurryNameOrPY);
		blurryNameOrPY = "%".concat(blurryNameOrPY).concat("%");
		values.add(blurryNameOrPY);
		values.add(blurryNameOrPY);
		values.add(blurryNameOrPY);
		if(simpleDns!=null&&simpleDns.length>0){
			int size = simpleDns.length;
			sql.append(" and (1=0");
			for(int i=0;i<size;i++){
				String dn = simpleDns[i];
				if(!StringUtils.isEmpty(dn)){
					sql.append(" or o.dn_ like ? escape '\\'");
					values.add("%".concat(dn).concat("%"));
				}
			}
			sql.append(")");
		}
		//组织类型过滤,scope=1,所有的部门单位都可以获取，因此无需任何处理
		switch(scope){
			case 2://单位、部门
				sql.append(" and (o.type_=? or (o.type_=? and o.IS_FICTITIOUS=?))");
				values.add(Type.Organ.toString());
				values.add(Type.OrganUnit.toString());
				values.add(false);
				break;
			case 3://单位
				sql.append(" and o.type_=?");
				values.add(Type.Organ.toString());
				break;
		}
		List<?> objects =getObjectsBySql(sql.toString(), values.toArray());
		List<String> names = null;
		if(objects!=null&&objects.size()>0){
			names = new ArrayList<String>(objects.size());
			for(Object obj:objects){
				names.add(obj.toString());
			}
		}
		return names;
	}
	
	@Override
	public List<OrganEO> getConnectedOrgans(List<String> rootDns,List<String> types,String name){
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1 ");
		List<Object> values = new ArrayList<Object>();
		if(rootDns!=null&&rootDns.size()>0){
			hql.append(" and (1=0");
			for(String rootDn:rootDns){
				hql.append(" or o.dn like ?   escape '\\'");
				String target = "%"+SqlUtil.prepareParam4Query(rootDn);
				values.add(target);
			}
			hql.append(")");
		}
		
		if(types!=null&&types.size()>0){
			hql.append(" and (1=0");
			for(String type:types){
				if(type.equals(TreeNodeVO.Type.Virtual.toString())){
					hql.append(" or (o.type=? and o.isFictitious=?)");
					values.add(TreeNodeVO.Type.OrganUnit.toString());
					values.add(Integer.valueOf(1));
				}else{
					hql.append(" or o.type=?");
					values.add(type);
				}
			}
			hql.append(")");
		}
		if(!StringUtils.isEmpty(name)){
//			hql.append(" and (o.name like ?  escape '\\' or o.fullPy like ?  escape '\\' or o.simplePy like ?  escape '\\' )");
//			String target = "%"+SqlUtil.prepareParam4Query(name)+"%";
//			values.add(target);
//			values.add(target);
//			values.add(target);
			hql.append(" and o.name=?");
			values.add(name);
		}
		
		hql.append(" and o.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrganEO> getOrganUnits(Long[] unitIds) {
		String sql = "select o.organ_id as organId,o.NAME_ as name,o.SIMPLE_NAME as simpleName,o.TYPE_ as type,o.DESCRIPTION_ as description,o.PARENT_ID as parentId,o.DN_ as dn,o.SORT_NUM as sortNum,o.HAS_ORGANS as hasOrgans,o.HAS_ORGAN_UNITS as hasOrganUnits,o.HAS_FICTITIOUS_UNITS as hasFictitiousUnits,o.HAS_PERSONS as hasPersons,o.HAS_ROLES as hasRoles,o.IS_FICTITIOUS as isFictitious "
	    +" from rbac_organ o start with o.organ_id in (:unitIds) connect by prior  o.organ_id=o.parent_id  and o.type_=(:type) and o.record_status=(:recordStatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("unitIds", unitIds);
		params.put("type", TreeNodeVO.Type.OrganUnit.toString());
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		String[] filedNames = new String[]{"organId","name","simpleName","type","description","parentId","dn","sortNum","hasOrgans","hasOrganUnits","hasFictitiousUnits","hasPersons","hasRoles","isFictitious"};
		List<OrganEO> organs = (List<OrganEO>)getBeansBySql(sql, params, OrganEO.class,filedNames);
		//递归查询获取的结果中包含了单位自身，因此在此进行排除
		if(organs!=null&&organs.size()>0){
			Iterator<OrganEO> iterator = organs.iterator(); 
			while(iterator.hasNext()){
				OrganEO organ = iterator.next();
				if(TreeNodeVO.Type.Organ.toString().equals(organ.getType())){
					iterator.remove();
					break;
				}
			}
		}
		return organs;
	}

	@Override
	public List<OrganEO> getOrgans(Long parentId) {
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1");
		List<Object> values = new ArrayList<Object>();
		if (parentId == null || parentId <= 0) {
			hql.append(" and o.parentId is null");
		} else {
			hql.append(" and o.parentId=?");
			values.add(parentId);
		}
		hql.append("  and o.recordStatus=? order by o.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<OrganEO> getOrgans(Long parentId, String type) {
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1");
		List<Object> values = new ArrayList<Object>();
		hql.append(" and o.type=?");
		values.add(type);
		if (parentId == null || parentId <= 0) {
			hql.append(" and o.parentId is null");
		} else {
			hql.append(" and o.parentId=?");
			values.add(parentId);
		}
		hql.append("  and o.recordStatus=? order by o.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<OrganEO> getOrgansByTypeAndName(String name, String type) {
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1");
		List<Object> values = new ArrayList<Object>();
		hql.append(" and o.type=?");
		values.add(type);
		if(!AppUtil.isEmpty(name)){
			hql.append(" and o.name like ?");
			values.add("%".concat(name.trim()).concat("%"));
		}
		hql.append("  and o.recordStatus=? order by o.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public Long getMaxSortNum(Long parentId) {
		Long maxSortNum = null;
		// String hql =
		// "select max(o.sortNum) from OrganEO as o where o.parentId=? and o.recordStatus=? ";
		StringBuffer sb = new StringBuffer(
				"select max(o.sortNum) from OrganEO as o where 1=1");
		if (parentId == null || parentId <= 0) {
			sb.append(" and o.parentId is null");
		} else {
			sb.append(" and  o.parentId=?");
		}
		sb.append(" and o.recordStatus=?");
		Query query = getCurrentSession().createQuery(sb.toString());
		if (parentId == null || parentId <= 0) {
			query.setParameter(0, AMockEntity.RecordStatus.Normal.toString());
		} else {
			query.setParameter(0, parentId);
			query.setParameter(1, AMockEntity.RecordStatus.Normal.toString());
		}
		@SuppressWarnings("rawtypes")
		List list = query.list();
		if (list != null && list.size() > 0) {
			maxSortNum = Long.valueOf(list.get(0) == null ? "0" : list.get(0)
					.toString());
		}
		return maxSortNum;
	}

	@Override
	public Long getSunOrgansCount(Long organId) {
		String hql = "from OrganEO o where o.parentId=? and o.recordStatus=?";
		return getCount(hql, new Object[] { organId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public Long getSunPersonsCount(Long organId) {
		String hql = "from PersonEO p where p.organId=? and p.recordStatus=?";
		return getCount(hql, new Object[] { organId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public void updateHasPersons(Long organId, Integer hasPersons) {
		StringBuilder hql = new StringBuilder("update OrganEO o set o.hasPersons=? where o.organId=? and o.hasPersons!=? and o.recordStatus=?");
		Query query = getCurrentSession().createQuery(hql.toString());
		setParameters(new Object[] { hasPersons, organId, hasPersons,
				RecordStatus.Normal.toString() }, query);
		query.executeUpdate();
	}

	@Override
	public OrganEO getOrganByDn(String dn) {
		String hql = "from OrganEO o where o.recordStatus=? and o.dn=?";
		return getEntityByHql(hql,
				new Object[] { RecordStatus.Normal.toString(), dn });
	}

	@Override
	public List<OrganEO> getSubOrgans(Long[] organIds, List<String> types,
			boolean isContainsFictitious, String fuzzyName) {
		StringBuffer hql = new StringBuffer("from OrganEO o where 1=1 ");
		List<Object> values = new ArrayList<Object>();
		// 父节点ID
		if (organIds == null || organIds.length == 0) {
			hql.append(" and o.parentId is null");
		} else {
			int length = organIds.length;
			for (int i = 0; i < length; i++) {
				Long organId = organIds[i];
				if (i == 0) {
					hql.append(" and (o.parentId=?");
				} else {
					hql.append(" or o.parentId=?");
				}
				values.add(organId);
				if (i == length - 1) {
					hql.append(")");
				}
			}
		}
		for (int i = 0; i < types.size(); i++) {
			String type = types.get(i);
			if (i == 0) {
				hql.append(" and (1=0 ");
			}
			if (type.equals(TreeNodeVO.Type.Organ.toString())) {// 组织
				hql.append(" or o.type=?");
				values.add(type);
			} else {// 单位
				if (isContainsFictitious) {
					hql.append(" or o.type=?");
					values.add(type);
				} else {
					hql.append(" or (o.type=? and o.isFictitious=?)");
					values.add(type);
					values.add(Integer.valueOf(0));// 0表示非虚拟部门/处室
				}
			}
			if (i == types.size() - 1) {
				hql.append(")");
			}
		}
		if (!StringUtils.isEmpty(fuzzyName)) {
			String target = "%".concat(SqlUtil.prepareParam4Query(fuzzyName))
					.concat("%");
			hql.append(" and (o.name like ? escape '\\' or o.simpleName like ? escape '\\')");
			values.add(target);
			values.add(target);
		}

		hql.append(" and o.recordStatus=? order by o.sortNum asc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<OrganEO> getOrgansByDns(List<String> dns) {
		StringBuilder hql = new StringBuilder("from OrganEO o where 1=1 ");
		List<Object> values = new ArrayList<Object>();
		if(dns!=null&&dns.size()>0){
			int size = dns.size();
			for(int i=0;i<size;i++){
				String dn = dns.get(i);
				if(i==0){
					hql.append(" and (o.dn=?");
				}else{
					hql.append(" or o.dn=?");
				}
				values.add(dn);
				if(i==size-1){
					hql.append(")");
				}
			}
		}
		hql.append(" and o.recordStatus=?  order  by o.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		List<OrganEO> organs = getEntitiesByHql(hql.toString(), values.toArray());
		return organs;
	}
	
	@Override
	public List<Long> getOrganIdsByDn(String dn){
		StringBuilder hql = new StringBuilder("select o.organId from OrganEO o where dn like ? and o.recordStatus=?");
		Object[] values = new Object[]{"%"+dn,AMockEntity.RecordStatus.Normal.toString()};
		List<?> objects = getObjects(hql.toString(), values);
		List<Long> organIds = null;
		if(objects!=null&&objects.size()>0){
			organIds = new ArrayList<Long>();
			for(Object obj:objects){
				if(obj!=null){
					organIds.add(Long.valueOf(obj.toString()));
				}
			}
		}
		return organIds;
	}

	@Override
	public List<OrganEO> getAllOrgans(String type) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(type)){
			hql.append(" and o.type = ?");
			values.add(type);
		}
		hql.append(" order by length(o.dn) asc,o.sortNum asc");
		List<OrganEO> organs = getEntitiesByHql(hql.toString(),values.toArray());
		return organs;
	}

	@Override
	public List<OrganEO> getOrgansByDn(String dn,String type) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ? and o.dn like ? ");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(("%").concat(dn));
		if(!StringUtils.isEmpty(type)){
			hql.append(" and o.type = ? ");
			values.add(type);
		}
		hql.append(" order by length(o.dn) asc,o.sortNum asc");
		return getEntitiesByHql(hql.toString(),values.toArray());
	}

	@Override
	public List<OrganEO> getOrgansByType(Long siteId,String type) {
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ? and o.type = ?  and o.siteId = ? order by length(o.dn) asc,o.sortNum asc");
		return getEntitiesByHql(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),type,siteId});
	}
	
	@Override
	public List<OrganEO> getOrgansBySiteId(Long siteId) {
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ? and o.siteId = ? order by length(o.dn) asc,o.sortNum asc");
		return getEntitiesByHql(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),siteId});
	}

	@Override
	public List<OrganEO> getParentOrgansById(Long id) {
		StringBuilder sql = new StringBuilder("select * from rbac_organ where RECORD_STATUS='"+AMockEntity.RecordStatus.Normal.toString()+"' connect by prior parent_id=organ_id start with organ_id='"+id+"'");
		return this.getCurrentSession().createSQLQuery(sql.toString()).addEntity(OrganEO.class).list();
	}

	@Override
	public List<OrganEO> getOrganUnitsByDn(String dn) {
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ? and o.type = ? and o.dn like ? order by length(o.dn) asc,o.sortNum asc");
		return getEntitiesByHql(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),OrganEO.Type.OrganUnit.toString(),"%"+dn});
	}

	@Override
	public List<OrganEO> getPublicOrgans(String dn) {
		StringBuilder hql = new StringBuilder("from OrganEO o where o.recordStatus= ? and o.type = ? and o.dn like ? and o.dn != ? and o.isPublic = 1 order by length(o.dn) asc,o.sortNum asc");
		return getEntitiesByHql(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),OrganEO.Type.Organ.toString(),"%"+dn,dn});
	}

	@Override
	public List<OrganVO> getOrganVOsByDn(String dn, Boolean isRemoveTop) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("select o.organId as organId,o.name as name,o.type as type,o.dn as dn,o.sortNum as sortNum " +
				"from OrganEO o where o.recordStatus= ? and o.type = ? and o.dn like ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(OrganEO.Type.Organ.toString());
		values.add("%"+dn);
		if(isRemoveTop){
			hql.append(" and o.dn != ?");
			values.add(dn);
		}
		hql.append(" order by length(o.dn) asc,o.sortNum asc");
		return (List<OrganVO>)getBeansByHql(hql.toString(), values.toArray(),OrganVO.class);
	}

}
