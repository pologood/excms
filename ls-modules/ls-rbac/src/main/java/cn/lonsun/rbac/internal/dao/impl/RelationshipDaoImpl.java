package cn.lonsun.rbac.internal.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IRelationshipDao;
import cn.lonsun.rbac.internal.entity.RelationshipEO;
import cn.lonsun.rbac.vo.RelationshipQueryVO;

/**
 * 人员关系管理orm接口实现
 * 
 * @author xujh
 * @date 2014年10月29日 下午2:46:47
 * @version V1.0
 */
@Repository("relationshipDao")
public class RelationshipDaoImpl extends BaseDao<RelationshipEO> implements
		IRelationshipDao {
	@Override
	public boolean isInRelationship(Long personId) {
		String hql = "from RelationshipEO r where r.leaderPersonId=? or r.personId=?";
		long count = getCount(hql, new Object[] { personId,personId });
		return count>0?true:false;
	}
	
	@Override
	public boolean hasSubordinates(Long leaderPersonId){
		String hql = "from RelationshipEO r where r.leaderPersonId=?";
		long count = getCount(hql, new Object[] { leaderPersonId });
		return count>0?true:false;
	}

	@Override
	public List<RelationshipEO> getSubordinates(Long leaderPersonId) {
		String hql = "from RelationshipEO r where 1=1";
		List<Object> values =  new ArrayList<Object>();
		if (leaderPersonId == null || leaderPersonId <= 0) {
			hql = hql.concat(" and r.leaderPersonId is null");
		} else {
			hql = hql.concat(" and r.leaderPersonId=?");
			values.add(leaderPersonId);
		}
			hql = hql.concat(" order by r.createDate");
		return getEntitiesByHql(hql, values.toArray());
	}
	
	/**
	 * 获取下属
	 * 
	 * @param unitIds
	 * @return
	 */
	public List<RelationshipEO> getSubordinates(List<Long> unitIds){
		String hql = "from RelationshipEO r where r.leaderPersonId is null";
		List<Object> values =  new ArrayList<Object>();
		hql = hql.concat(" and (1=0");
		for(Long unitId:unitIds){
			hql = hql.concat(" or r.unitId=?");
			values.add(unitId);
		}
		hql = hql.concat(") order by r.createDate");
		return getEntitiesByHql(hql, values.toArray());
	}

	@Override
	public void updateSubordinateCount(Long leaderPersonId) {
		StringBuffer sql = new StringBuffer(
				"update rbac_relationship r set r.subordinate_count=(");
		sql.append("select count(rr.relationship_id) from rbac_relationship rr where rr.leader_person_id=?) where r.person_id=?");
		executeUpdateBySql(sql.toString(), new Object[] { leaderPersonId,
				leaderPersonId });
	}

	@Override
	public RelationshipEO getShipByPersonId(Long personId) {
		String hql = "from RelationshipEO r where r.personId=?";
		return getEntityByHql(hql, new Object[] { personId });
	}

	@Override
	public Integer getSubordinateCount(Long leaderPersonId) {
		String hql = "from RelationshipEO rr where rr.leaderPersonId=?";
		Long count = getCount(hql, new Object[] { leaderPersonId });
		return Integer.valueOf(count + "");
	}

	@Override
	public List<RelationshipEO> getShipsByPersonIds(Long[] personIds) {
		String hql = "from RelationshipEO where personId in :personIds";
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("personIds", personIds);
		return getEntitiesByHql(hql, values);
	}

	@Override
	public Pagination getPage(RelationshipQueryVO vo) {
		StringBuffer sql = new StringBuffer(
				" from rbac_relationship r where 1=1");
		List<Object> values = new ArrayList<Object>();
		// 模糊姓名
		String n = vo.getBlurryName();
		if (!StringUtils.isEmpty(n) && !StringUtils.isEmpty(n.trim())) {
			sql.append(" and r.name_ like ? escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(n)).concat("%"));
		}
		// 模糊组织名
		String on = vo.getBlurryOrganName();
		if (!StringUtils.isEmpty(on) && !StringUtils.isEmpty(on.trim())) {
			sql.append(" and r.organ_name like ? escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(on)).concat("%"));
		}
		sql.append(" start with r.leader_person_id=? connect by prior r.person_id = r.leader_person_id");
		values.add(vo.getLeaderPersonId());
		// 排序字段
		String field = vo.getSortField();
		if (!StringUtils.isEmpty(field) && !StringUtils.isEmpty(field.trim())) {
			if("name".equals(field)){
				field = "name_";
			}else if("subordinateCount".equals(field)){
				field = "subordinate_count";
			}else if("createDate".equals(field)){
				field = "create_date";
			}else if("leaderName".equals(field)){
				field = "leader_name";
			}
			sql.append(" order by r.").append(field);
			String sortOrder = vo.getSortOrder();
			if ("desc".equals(sortOrder)) {
				sql.append(" desc");
			}
		}
		Long pageIndex = vo.getPageIndex();
		Long index = pageIndex==null||pageIndex<0?0:pageIndex;
		Integer pageSize = vo.getPageSize();
		Integer size = pageSize==null||pageSize<=0?15:pageSize;
		int startNum = Pagination.getStartNumber(index, size).intValue();
		//获取数据
		List<?> data = getPaginationRecoresBySql(sql.toString(), startNum, size, values.toArray());
		Long total = Long.valueOf(0);
		List<RelationshipEO> ships = null;
		if(data!=null&&data.size()>0){
			//获取记录总数量
			total = getCountBySql(sql.toString(), values.toArray());
			ships = new ArrayList<RelationshipEO>(data.size());
			for(Object obj:data){
				RelationshipEO ship = new RelationshipEO();
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String,Object>)obj;
				Long relationshipId = Long.valueOf(map.get("RELATIONSHIP_ID").toString());
				ship.setRelationshipId(relationshipId);
				Long personId = Long.valueOf(map.get("PERSON_ID").toString());
				ship.setPersonId(personId);
				String name = map.get("NAME_").toString();
				ship.setName(name);
				Long organId = Long.valueOf(map.get("ORGAN_ID").toString());
				ship.setOrganId(organId);
				//TODO
//				String organName = map.get("ORGAN_NAME").toString();
//				ship.setOrganName(organName);
				Long userId = Long.valueOf(map.get("USER_ID").toString());
				ship.setUserId(userId);
				Object lpi = map.get("LEADER_PERSON_ID");
				Long leaderPersonId = Long.valueOf(lpi==null?"0":lpi.toString());
				ship.setLeaderPersonId(leaderPersonId);
				Object lno = map.get("LEADER_NAME");
				String leaderName = lno==null?null:lno.toString();
				ship.setLeaderName(leaderName);
				Object loi = map.get("LEADER_ORGAN_ID");
				Long leaderOrganId = loi==null?null:Long.valueOf(loi.toString());
				ship.setLeaderOrganId(leaderOrganId);
				Integer subordinateCount = Integer.valueOf(map.get("SUBORDINATE_COUNT").toString());
				ship.setSubordinateCount(subordinateCount);
				Object cd = map.get("CREATE_DATE");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date createDate = sdf.parse(cd.toString());
					ship.setCreateDate(createDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				ships.add(ship);
			}
		}
		return new Pagination(ships, total, size, index);
	}

	@Override
	public List<RelationshipEO> getAllSubordinates(Long leaderId) {
		StringBuffer sql = new StringBuffer(
				"from rbac_relationship r where 1=1");
		sql.append(" start with r.leader_person_id=? connect by prior r.person_id = r.leader_person_id");
		//获取数据
		List<?> data = getObjectsBySql(sql.toString(), new Object[]{leaderId});
		List<RelationshipEO> ships = null;
		if(data!=null&&data.size()>0){
			//获取记录总数量
			ships = new ArrayList<RelationshipEO>(data.size());
			for(Object obj:data){
				RelationshipEO ship = new RelationshipEO();
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String,Object>)obj;
				Long relationshipId = Long.valueOf(map.get("RELATIONSHIP_ID").toString());
				ship.setRelationshipId(relationshipId);
				Long personId = Long.valueOf(map.get("PERSON_ID").toString());
				ship.setPersonId(personId);
				String name = map.get("NAME_").toString();
				ship.setName(name);
				Long organId = Long.valueOf(map.get("ORGAN_ID").toString());
				ship.setOrganId(organId);
				Long userId = Long.valueOf(map.get("USER_ID").toString());
				ship.setUserId(userId);
				Object lpi = map.get("LEADER_PERSON_ID");
				Long leaderPersonId = Long.valueOf(lpi==null?"0":lpi.toString());
				ship.setLeaderPersonId(leaderPersonId);
				Object lno = map.get("LEADER_NAME");
				String leaderName = lno==null?null:lno.toString();
				ship.setLeaderName(leaderName);
				Object loi = map.get("LEADER_ORGAN_ID");
				Long leaderOrganId = loi==null?null:Long.valueOf(loi.toString());
				ship.setLeaderOrganId(leaderOrganId);
				Integer subordinateCount = Integer.valueOf(map.get("SUBORDINATE_COUNT").toString());
				ship.setSubordinateCount(subordinateCount);
				Object cd = map.get("CREATE_DATE");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date createDate = sdf.parse(cd.toString());
					ship.setCreateDate(createDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				ships.add(ship);
			}
		}
		return ships;
	}

	
}
