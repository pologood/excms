package cn.lonsun.rbac.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.Person4NoticeVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.dao.IPersonDao;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.vo.PersonVO;
import cn.lonsun.rbac.internal.vo.RolePersonVO;
import cn.lonsun.rbac.vo.PersonQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("personDao")
public class PersonDaoImpl extends MockDao<PersonEO> implements IPersonDao {

	/**
	 * 为通知公告全平台发送获取人员信息，其他业务请慎用
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Person4NoticeVO> getPersons4Notice() {
		String sql = "select p.PERSON_ID as personId,p.NAME_ as name,p.ORGAN_ID as organId,p.ORGAN_NAME as organName,p.UNIT_ID as unitId,p.UNIT_NAME as unitName,p.MOBILE_ as mobile,p.PLATFORM_CODE as platformCode,p.USER_ID as userId from rbac_person p where p.IS_EXTERNAL_PERSON=? and p.record_status=?";
		return (List<Person4NoticeVO>) getBeansBySql(sql, new Object[] {
						Boolean.FALSE, RecordStatus.Normal.toString() },
				Person4NoticeVO.class);
	}

	@Override
	public List<PersonEO> getSubPersons(Long organId, String[] statuses) {
		String hql = "select p from PersonEO p,UserEO u where p.userId=u.userId";
		List<Object> values = new ArrayList<Object>();
		if(statuses!=null&&statuses.length>0){
			hql  = hql+" and (1=0";
			for(String status:statuses){
				hql = hql+" or u.status =?";
				values.add(status);
			}
			hql  = hql + ")";
		}
		hql = hql+" and u.recordStatus=? and p.recordStatus=? and p.organId=? order by p.sortNum";
		values.add(RecordStatus.Normal.toString());
		values.add(RecordStatus.Normal.toString());
		values.add(organId);
		return getEntitiesByHql(hql, values.toArray());
	}

	@Override
	public Long getCountByPlatformCode(String platformCode) {
		String hql = "from PersonEO p where p.platformCode=? and p.isPluralistic=? and p.recordStatus=?";
		return getCount(hql, new Object[] { platformCode, false,
				RecordStatus.Normal.toString() });
	}

	@Override
	public Long getSubPersonCount(Long organId) {
		String hql = "from PersonEO p where p.organId=? and p.recordStatus=?";
		return getCount(hql,
				new Object[] { organId, RecordStatus.Normal.toString() });
	}

	/**
	 * 根据DN查询人员
	 *
	 * @param organDn
	 * @return
	 */
	@Override
	public List<PersonEO> getPersonsByOrganDn(String organDn) {
		String hql = "from PersonEO p where p.dn like ? and p.recordStatus=? order by length(p.dn) asc,p.sortNum asc";
		return getEntitiesByHql(hql, new Object[] { "%" + organDn,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<PersonEO> getPersonsByOrganDn(String organDn, String name) {
		String hql = "from PersonEO p where p.dn like ? and p.name like ? and p.recordStatus=? order by length(p.dn) asc,p.sortNum asc";
		return getEntitiesByHql(hql, new Object[] { "%" + organDn,"%" + name + "%",
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public void deleteByOrganDn(String organDn) {
		String hql = "update PersonEO p set p.recordStatus=? where p.organDn like ? and p.recordStatus=?";
		Object[] values = new Object[] {
				AMockEntity.RecordStatus.Removed.toString(), "%" + organDn,
				AMockEntity.RecordStatus.Normal.toString() };
		executeUpdateByHql(hql, values);
	}

	@Override
	public List<Object> getPersons(Long pageIndex, int pageSize) {
		String hql = "from PersonEO p order by p.personId";
		int first = Pagination.getStartNumber(pageIndex, pageSize).intValue();
		int max = Pagination.getPageSize(pageSize);
		List<Object> persons = getPaginationRecores(hql, first, max,
				new Object[] {});
		return persons;
	}

	@Override
	public List<?> getPersonInfos(List<String> organDns) {
		StringBuilder hql = new StringBuilder(
				"select p.personId,p.name,p.organId,p.organName,p.unitId,p.unitName,p.simplePy,p.fullPy,replace(p.dn,',','-'),p.mobile,p.positions,p.userId from PersonEO p where p.recordStatus='");
		hql.append(AMockEntity.RecordStatus.Normal.toString()).append("'");
		// 根据DN过滤
		if (organDns != null && organDns.size() > 0) {
			hql.append(" and (1=0");
			for (String dn : organDns) {
				hql.append(" or instr(p.dn, '").append(dn).append("') > 0");
			}
			hql.append(")");
		}
		List<?> objects = getObjects(hql.toString(), null);
		return objects;
	}

	@Override
	public List<?> getPersonInfosByPlatformCode(List<String> organDns,
												String platformCode) {
		StringBuilder hql = new StringBuilder("select p.personId,p.name,p.organId,p.organName,p.unitId,p.unitName,p.simplePy,p.fullPy,p.dn,p.mobile,p.positions,p.userId,p.isExternalPerson,p.platformCode from PersonEO p,UserEO u where p.userId=u.userId and u.status!=? and p.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(UserEO.STATUS.Unable.toString());
		values.add(RecordStatus.Normal.toString());
		// 根据DN过滤
		if (organDns != null && organDns.size() > 0) {
			hql.append(" and (1=0");
			for (String dn : organDns) {
				hql.append(" or instr(p.dn, '").append(dn).append("') > 0");
			}
			hql.append(")");
		}
		if (!StringUtils.isEmpty(platformCode)) {
			hql.append(" and p.platformCode=?");
			values.add(platformCode);
		}
		hql.append(" order by p.sortNum");
		List<?> objects = getObjects(hql.toString(), values.toArray());
		return objects;
	}

	@Override
	public List<String> getNames(String[] simpleOrganDns, String blurryNameOrPY) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(
				"select distinct(p.NAME_) from rbac_person p where p.record_Status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		sql.append(" and (p.NAME_ like ? escape '\\' or p.full_PY like ? escape '\\' or p.simple_PY like ? escape '\\')");
		// 特殊字符处理
		blurryNameOrPY = SqlUtil.prepareParam4Query(blurryNameOrPY);
		blurryNameOrPY = "%".concat(blurryNameOrPY).concat("%");
		values.add(blurryNameOrPY);
		values.add(blurryNameOrPY);
		values.add(blurryNameOrPY);
		if (simpleOrganDns != null && simpleOrganDns.length > 0) {
			int size = simpleOrganDns.length;
			sql.append(" and (1=0");
			for (int i = 0; i < size; i++) {
				String dn = simpleOrganDns[i];
				if (!StringUtils.isEmpty(dn)) {
					sql.append(" or p.dn_ like ?");
					values.add("%".concat(dn).concat("%"));
				}
			}
			sql.append(")");
		}
		List<?> objects = getObjectsBySql(sql.toString(), values.toArray());
		List<String> names = null;
		if (objects != null && objects.size() > 0) {
			names = new ArrayList<String>(objects.size());
			for (Object obj : objects) {
				names.add(obj.toString());
			}
		}
		return names;
	}

	@Override
	public Pagination getPage4RoleBySql(PageQueryVO query, Long roleId,
										String searchText) {
		StringBuffer sql = new StringBuffer(
				"select p.name_,p.uid_,p.unit_name,p.organ_name,ra.role_assignment_Id,ra.create_date from rbac_person p,rbac_role_assignment ra where p.user_id=ra.user_Id and p.organ_Id=ra.organ_Id and ra.role_id=? and p.record_status=? and ra.record_status=? ");
		List<Object> values = new ArrayList<Object>();
		values.add(roleId);
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if (!StringUtils.isEmpty(searchText)) {
			sql.append(" and (p.uid_ like ? escape '\\' or p.name_ like ? escape '\\' or p.organ_name like ? escape '\\' or p.unit_name like ? escape '\\')");
			String target = SqlUtil.prepareParam4Query(searchText);
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
		}
		sql.append(" order by ra.create_date desc");
		List<String> fields = new ArrayList<String>();
		fields.add("name_");
		fields.add("uid_");
		fields.add("unit_name");
		fields.add("organ_name");
		fields.add("role_assignment_Id");
		fields.add("create_date");
		Pagination page = getPaginationBySql(query.getPageIndex(),
				query.getPageSize(), sql.toString(), values.toArray(), fields);
		List<?> data = page.getData();
		List<Object> rps = new ArrayList<Object>();
		for (Object obj : data) {
			if (obj == null) {
				continue;
			}
			RolePersonVO rp = new RolePersonVO();
			Object[] vs = (Object[]) obj;
			rp.setName(vs[0] == null ? null : vs[0].toString());
			rp.setUid(vs[1] == null ? null : vs[1].toString());
			rp.setUnitName(vs[2] == null ? null : vs[2].toString());
			rp.setOrganName(vs[3] == null ? null : vs[3].toString());
			rp.setRoleAssignmentId(vs[4] == null ? null : Long.valueOf(vs[4]
					.toString()));
			String ds = vs[5] == null ? null : vs[5].toString();
			if (ds != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					rp.setCreateDate(sdf.parse(ds));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			rps.add(rp);
		}
		page.setData(rps);
		return page;
	}

	/*
	 * @Override public List<PersonEO> getPersonsByRoleCode(String
	 * roleCode,Long[] organUnitIds){ String hql =
	 * "select p from PersonEO p,RoleAssignmentEO ra where p.userId=ra.userId and p.organId = ra.organId and p.recordStatus=(:recordStatus2) and ra.roleCode=(:roleCode) and ra.organId in (:organUnitIds) and ra.recordStatus=(:recordStatus1)"
	 * ; Map<String, Object> params = new HashMap<String, Object>();
	 * params.put("roleCode", roleCode); params.put("organUnitIds",
	 * organUnitIds); params.put("recordStatus1",
	 * AMockEntity.RecordStatus.Normal.toString()); params.put("recordStatus2",
	 * AMockEntity.RecordStatus.Normal.toString()); return getEntitiesByHql(hql,
	 * params); }
	 */

	@Override
	public List<PersonEO> getPersonsByRoleCode(String roleCode,
											   Long[] organUnitIds) {
		StringBuffer hql = new StringBuffer(
				"select p from PersonEO p,RoleAssignmentEO ra where p.userId=ra.userId and p.organId = ra.organId and p.recordStatus=? and ra.roleCode=?  and ra.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(RecordStatus.Normal.toString());
		values.add(roleCode);
		values.add(RecordStatus.Normal.toString());
		if (organUnitIds != null && organUnitIds.length > 0) {
			hql.append(" and (1=0");
			for (Long organUnitId : organUnitIds) {
				hql.append(" or ra.organId=?");
				values.add(organUnitId);
			}
			hql.append(")");
		}
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	/**
	 * 获取单位（只去直属的，去除下属单位的）或部门下角色编码为roleCode的人员信息
	 *
	 * @param roleCode
	 * @param organDns
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleCode(String roleCode,List<String> organDns) {
		StringBuffer hql = new StringBuffer(
				"select p from PersonEO p,RoleAssignmentEO ra where p.userId=ra.userId and p.organId = ra.organId and p.recordStatus=? and ra.roleCode=?  and ra.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(RecordStatus.Normal.toString());
		values.add(roleCode);
		values.add(RecordStatus.Normal.toString());
		if (organDns != null && organDns.size() > 0) {
			hql.append(" and (1=0");
			for (String organDn : organDns) {
				hql.append(" or p.dn like ?");
				if(organDn.startsWith("o=")){
					values.add("%ou=________________________________," + organDn);
				}else{
					values.add("%" + organDn);
				}
			}
			hql.append(")");
		}
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	/**
	 * 根据角色编码和用户ID获取人员
	 *
	 * @param roleCode
	 * @param userId
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleCodeAndUserId(String roleCode,
														Long userId) {
		StringBuffer hql = new StringBuffer(
				"select p from PersonEO p,RoleAssignmentEO ra where p.userId=ra.userId and p.organId = ra.organId and p.recordStatus=? and ra.roleCode=?  and ra.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(RecordStatus.Normal.toString());
		values.add(roleCode);
		values.add(RecordStatus.Normal.toString());
		hql.append(" and p.userId=?");
		values.add(userId);
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<PersonEO> getPersonsByRoleId(Long roleId) {
		String hql = "select p from PersonEO p,RoleAssignmentEO ra ";
		hql = hql
				.concat("where ra.roleId=(:roleId) and ra.userId=p.userId and ra.organId=p.organId and ra.recordStatus=(:recordStatus1)  and p.recordStatus=(:recordStatus2)");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("recordStatus1", AMockEntity.RecordStatus.Normal.toString());
		params.put("recordStatus2", AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql, params);
	}

	@Override
	public boolean isNameRight(Long personId, String name) {
		String hql = "from PersonEO p where p.personId=? and p.name=? and p.recordStatus=?";
		return getCount(hql, new Object[] { personId, name,
				AMockEntity.RecordStatus.Normal.toString() }) > 0 ? true
				: false;
	}

	@Override
	public List<PersonEO> getPluralisticPersons(Long personId) {
		String hql = "from PersonEO p where p.srcPersonId=? and p.isPluralistic=? and p.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { personId, true,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<PersonEO> getPersons(Long organId) {
		String hql = "from PersonEO p where p.organId=? and p.recordStatus=? order by p.sortNum";
		return getEntitiesByHql(hql, new Object[] { organId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public boolean isSortNumExisted(Long organId, Long sortNum) {
		String hql = "from PersonEO p where p.sortNum=? and p.organId=? and p.recordStatus=?";
		List<PersonEO> ps = getEntitiesByHql(hql, new Object[] { sortNum,
				organId, AMockEntity.RecordStatus.Normal.toString() });
		return ps != null && ps.size() > 0;
	}

	@Override
	public Long getMaxSortNum(Long organId) {
		Long maxSortNum = null;
		StringBuffer sb = new StringBuffer(
				"select max(o.sortNum) from PersonEO as o where 1=1");
		if (organId == null || organId <= 0) {
			sb.append(" and o.organId is null");
		} else {
			sb.append(" and  o.organId=?");
		}
		sb.append(" and o.recordStatus=?");
		Query query = getCurrentSession().createQuery(sb.toString());
		if (organId == null || organId <= 0) {
			query.setParameter(0, AMockEntity.RecordStatus.Normal.toString());
		} else {
			query.setParameter(0, organId);
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

	/**
	 * 获取已删除的记录
	 *
	 * @param query
	 * @return
	 */
	public Pagination getDeletedPersonsPage(PersonQueryVO query) {
		StringBuffer hql = new StringBuffer("from PersonEO p where 1=1");
		List<Object> values = new ArrayList<Object>();
		String searchText = query.getSearchText();
		if (searchText != null && !StringUtils.isEmpty(searchText.trim())) {
			hql.append(" and (p.name like ? escape '\\' or p.uid like ? escape '\\' or p.fullPy like ? escape '\\' and p.simplePy like ? escape '\\')");
			String target = SqlUtil.prepareParam4Query(searchText);
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
		}
		// 排序字段
		hql.append(" and p.recordStatus=? order by p.updateDate desc");
		values.add(RecordStatus.Removed.toString());
		return getPagination(query.getPageIndex(), query.getPageSize(),
				hql.toString(), values.toArray());
	}

	@Override
	public Pagination getPage(PersonQueryVO query) {
		StringBuffer hql = new StringBuffer(
				"from PersonEO p where p.recordStatus=?");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		String name = query.getName();
		String mobile = query.getMobile();
		String unitName = query.getUnitName();
		String officePhone = query.getOfficePhone();
		if (!StringUtils.isEmpty(name)) {
			hql.append(" and p.name like ?");
			String target = SqlUtil.prepareParam4Query(name);
			values.add("%".concat(target).concat("%"));
		}
		if (!StringUtils.isEmpty(unitName)) {
			hql.append(" and p.unitName like ?");
			String target = SqlUtil.prepareParam4Query(unitName);
			values.add("%".concat(target).concat("%"));
		}
		if (!StringUtils.isEmpty(officePhone)) {
			hql.append(" and p.officePhone like ?");
			String target = SqlUtil.prepareParam4Query(officePhone);
			values.add("%".concat(target).concat("%"));
		}
		if (!StringUtils.isEmpty(mobile)) {
			hql.append(" and p.mobile like ?");
			String target = SqlUtil.prepareParam4Query(mobile);
			values.add("%".concat(target).concat("%"));
		}

		// 排序字段
		String field = query.getSortField();
		if (!StringUtils.isEmpty(field)) {
			hql.append(" order by p.").append(field);
			String sortOrder = query.getSortOrder();
			if ("desc".equals(sortOrder)) {
				hql.append(" desc");
			}
		}
		return getPagination(query.getPageIndex(), query.getPageSize(),
				hql.toString(), values.toArray());
	}

	@Override
	public Pagination getInfoPage(PersonQueryVO query) {
		// 取参数
		StringBuffer hql = new StringBuffer();
		hql.append("select p.person_id,p.name_,p.user_id,p.uid_,p.is_pluralistic,p.dn_,u.login_times,u.last_login_date,u.status,p.src_person_id,p.MOBILE_,p.ORGAN_ID,p.UNIT_ID");
		hql.append(" from rbac_person p,rbac_user u where p.user_id=u.user_id and p.record_status=?");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if (!StringUtils.isEmpty(query.getSearchText())) {
			hql.append(" and (p.uid_ like ? or p.name_ like ? or p.organ_name like ? or p.unit_name like ? )");
			String target = SqlUtil.prepareParam4Query(query.getSearchText());
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
		}
		if(!StringUtils.isEmpty(query.getDn())){
			//获取该组织下的人员
			String target = SqlUtil.prepareParam4Query(query.getDn());
			hql.append(" and p.dn_ like ?");
			values.add("%".concat(target));
		}
		if(query.getPersonId() != null){
			hql.append(" and p.person_id = ?");
			values.add(query.getPersonId());
		}
		hql.append(" order by p.sort_num");
		Pagination page = getPaginationBySql(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray());
		return page;
	}

//	@Override
//	public Pagination getInfoPage(PersonQueryVO query) {
//		// 取参数
//		StringBuffer sql = new StringBuffer();
//		sql.append("select p.PERSON_ID,p.NAME_,p.ORGAN_NAME,p.UID_,u.LOGIN_TYPE,u.LOGIN_TIMES,u.last_login_date,u.last_login_ip,u.status,u.user_id,p.is_pluralistic,p.sort_num,p.organ_id,p.unit_id,p.unit_name,p.POSITIONS_,p.OFFICE_PHONE,p.MOBILE_,p.MAIL_,p.OFFICE_ADDRESS,p.SRC_PERSON_ID ");
//		sql.append(" from rbac_person p,rbac_user u where p.user_id = u.user_id");
//		List<Object> values = new ArrayList<Object>();
//		if (!StringUtils.isEmpty(query.getSearchText())) {
//			sql.append(" and (p.uid_ like ? escape '\\' or p.name_ like ? escape '\\' or p.organ_name like ? escape '\\')");
//			String target = SqlUtil.prepareParam4Query(query.getSearchText());
//			values.add("%".concat(target).concat("%"));
//			values.add("%".concat(target).concat("%"));
//			values.add("%".concat(target).concat("%"));
//		}
//		sql.append(" and p.record_Status=? and u.record_Status=?");
//		values.add(AMockEntity.RecordStatus.Normal.toString());
//		values.add(AMockEntity.RecordStatus.Normal.toString());
//		//获取该组织下的人员
//		String target = SqlUtil.prepareParam4Query(query.getDn());
//		sql.append(" and p.DN_ like ?");
//		values.add("%".concat(target));
//		String sortField = query.getSortField();
//		if (!StringUtils.isEmpty(sortField)) {
//			if ("name".equals(sortField)) {
//				sql.append(" order by p.name_");
//			} else if ("organName".equals(sortField)) {
//				sql.append(" order by p.organ_name");
//			} else if ("uid".equals(sortField)) {
//				sql.append(" order by p.uid_");
//			} else if ("loginType".equals(sortField)) {
//				sql.append(" order by u.login_type");
//			} else if ("loginTimes".equals(sortField)) {
//				sql.append(" order by u.login_times");
//			} else if ("lastLoginDate".equals(sortField)) {
//				sql.append(" order by u.last_login_date");
//			} else {
//				sql.append(" order by u.last_login_ip");
//			}
//			if (!StringUtils.isEmpty(query.getSortOrder())) {
//				sql.append(" ").append(query.getSortOrder());
//			}
//		} else {
//			sql.append(" order by p.sort_num,p.person_id");
//		}
//		List<String> fields = new ArrayList<String>();
//		fields.add("person_id");
//		fields.add("name_");
//		fields.add("organ_name");
//		fields.add("uid_");
//		fields.add("login_type");
//		fields.add("login_times");
//		fields.add("IS_PLURALISTIC");
//		fields.add("last_login_ip");
//		fields.add("status");
//		fields.add("user_id");
//		fields.add("last_login_date");
//		fields.add("sort_num");
//		fields.add("organ_id");
//		fields.add("unit_id");
//		fields.add("unit_name");
//		fields.add("POSITIONS_");
//		fields.add("OFFICE_PHONE");
//		fields.add("MOBILE_");
//		fields.add("MAIL_");
//		fields.add("OFFICE_ADDRESS");
//		fields.add("SRC_PERSON_ID");
//		Pagination page = getPaginationBySql(query.getPageIndex(),query.getPageSize(), sql.toString(), values.toArray(), fields);
//		List<?> data = page.getData();
//		List<Object> nodes = new ArrayList<Object>();
//		for (Object obj : data) {
//			if (obj == null) {
//				continue;
//			}
//			PersonVO node = new PersonVO();
//			Object[] vs = (Object[]) obj;
//			node.setPersonId(Long.valueOf(vs[0].toString()));
//			node.setName(vs[1] == null ? null : vs[1].toString());
//			node.setOrganName(vs[2] == null ? null : vs[2].toString());
//			node.setUid(vs[3] == null ? null : vs[3].toString());
//			node.setLoginType(vs[4] == null ? null : Integer.valueOf(vs[4]
//					.toString()));
//			node.setLoginTimes(vs[5] == null ? null : Integer.valueOf(vs[5]
//					.toString()));
//			boolean isPluralistic = false;
//			if (vs[6] != null) {
//				if (Integer.valueOf(vs[6].toString()) == 1) {
//					isPluralistic = true;
//				}
//				node.setIsPluralistic(isPluralistic);
//			}
//			node.setLastLoginIp(vs[7] == null ? null : vs[7].toString());
//			node.setStatus(vs[8] == null ? null : vs[8].toString());
//			node.setUserId(vs[9] == null ? null
//					: Long.valueOf(vs[9].toString()));
//			String ds = vs[10] == null ? null : vs[10].toString();
//			if (ds != null) {
//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"yyyy-MM-dd HH:mm:ss");
//				try {
//					node.setLastLoginDate(sdf.parse(ds));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//			node.setSortNum(vs[11] == null ? null : Long.valueOf(vs[11]
//					.toString()));
//			node.setOrganId(vs[12] == null ? null : Long.valueOf(vs[12]
//					.toString()));
//			node.setUnitId(vs[13] == null ? null : Long.valueOf(vs[13]
//					.toString()));
//			node.setUnitName(vs[14] == null ? null : vs[14].toString());
//			node.setPositions(vs[15] == null ? null : vs[15].toString());
//			node.setOfficePhone(vs[16] == null ? null : vs[16].toString());
//			node.setMobile(vs[17] == null ? null : vs[17].toString());
//			node.setMail(vs[18] == null ? null : vs[18].toString());
//			node.setOfficeAddress(vs[19] == null ? null : vs[19].toString());
//			node.setSrcPersonId(vs[20] == null ? null : Long.valueOf(vs[20]
//					.toString()));
//			nodes.add(node);
//		}
//		page.setData(nodes);
//		return page;
//	}

	@Override
	public Pagination getPage(PersonQueryVO query, Long[] roleIds,
							  Long[] organIds) {
		// 取参数
		StringBuffer sql = new StringBuffer();
		sql.append("select np.person_id,np.name_,np.organ_name,np.uid_,u.login_type,u.login_times,u.last_login_date,u.last_login_ip,u.status,u.user_id,np.is_pluralistic,np.sort_num,np.organ_id,np.unit_id,np.unit_name,np.POSITIONS_,np.OFFICE_PHONE,np.MOBILE_,np.MAIL_,np.OFFICE_ADDRESS,np.SRC_PERSON_ID ");
		sql.append(" from (select p.* from rbac_person p, rbac_organ_person op ");
		sql.append(" where p.person_Id = op.person_Id ");
		List<Object> values = new ArrayList<Object>();
		for (int i = 0; i < organIds.length; i++) {
			if (i == 0) {
				sql.append(" and (op.organ_id=?");
			} else {
				sql.append(" or op.organ_id=?");
			}
			if (i == organIds.length - 1) {
				sql.append(")");
			}
			values.add(organIds[i]);
		}

		if (!StringUtils.isEmpty(query.getSearchText())) {
			sql.append(" and (p.uid_ like ? escape '\\' or p.name_ like ? escape '\\' or p.organ_name like ? escape '\\')");
			String target = SqlUtil.prepareParam4Query(query.getSearchText());
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
		}
		String name = query.getName();
		if (!StringUtils.isEmpty(name)) {
			sql.append(" and p.NAME_ like ? escape '\\'");
			String target = SqlUtil.prepareParam4Query(name);
			values.add("%".concat(target).concat("%"));
		}
		String unitName = query.getUnitName();
		if (!StringUtils.isEmpty(unitName)) {
			sql.append(" and p.UNIT_NAME like ? escape '\\'");
			String target = SqlUtil.prepareParam4Query(unitName);
			values.add("%".concat(target).concat("%"));
		}
		String mobile = query.getMobile();
		if (!StringUtils.isEmpty(mobile)) {
			sql.append(" and p.MOBILE_ like ? escape '\\'");
			String target = SqlUtil.prepareParam4Query(mobile);
			values.add("%".concat(target).concat("%"));
		}
		String officePhone = query.getOfficePhone();
		if (!StringUtils.isEmpty(officePhone)) {
			sql.append(" and p.OFFICE_PHONE like ? escape '\\'");
			String target = SqlUtil.prepareParam4Query(officePhone);
			values.add("%".concat(target).concat("%"));
		}
		sql.append(" and op.record_Status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		sql.append(" and p.record_Status=?)");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if (roleIds != null && roleIds.length > 0) {
			sql.append(" np ,rbac_user u where ");
			StringBuffer areaSql = new StringBuffer(
					"exists (select * from (select distinct(u.user_id) from rbac_role_assignment ra, rbac_user u  where ra.record_status=? and u.record_status=? and ra.user_id = u.user_id");
			values.add(AMockEntity.RecordStatus.Normal.toString());
			values.add(AMockEntity.RecordStatus.Normal.toString());
			for (int i = 0; i < roleIds.length; i++) {
				Long roleId = roleIds[i];
				if (i == 0) {
					areaSql.append(" and (ra.role_id=?");
				} else {
					areaSql.append(" or ra.role_id=?");
				}
				if (i == roleIds.length - 1) {
					areaSql.append(")");
				}
				values.add(roleId);
			}
			areaSql.append(") tu where np.user_id=u.user_id and tu.user_id=u.user_id)");
			sql.append(areaSql);
		} else {
			sql.append(" np left join rbac_user u on ");
			sql.append(" np.user_id = u.user_id");
		}

		String sortField = query.getSortField();
		if (!StringUtils.isEmpty(sortField)) {
			if ("name".equals(sortField)) {
				sql.append(" order by np.name_");
			} else if ("organName".equals(sortField)) {
				sql.append(" order by np.organ_name");
			} else if ("uid".equals(sortField)) {
				sql.append(" order by np.uid_");
			} else if ("loginType".equals(sortField)) {
				sql.append(" order by u.login_type");
			} else if ("loginTimes".equals(sortField)) {
				sql.append(" order by u.login_times");
			} else if ("lastLoginDate".equals(sortField)) {
				sql.append(" order by u.last_login_date");
			} else {
				sql.append(" order by u.last_login_ip");
			}
			if (!StringUtils.isEmpty(query.getSortOrder())) {
				sql.append(" ").append(query.getSortOrder());
			}
		} else {
			sql.append(" order by np.sort_num,np.person_id");
		}
		List<String> fields = new ArrayList<String>();
		fields.add("person_id");
		fields.add("name_");
		fields.add("organ_name");
		fields.add("uid_");
		fields.add("login_type");
		fields.add("login_times");
		fields.add("IS_PLURALISTIC");
		fields.add("last_login_ip");
		fields.add("status");
		fields.add("user_id");
		fields.add("last_login_date");
		fields.add("sort_num");
		fields.add("organ_id");
		fields.add("unit_id");
		fields.add("unit_name");
		fields.add("POSITIONS_");
		fields.add("OFFICE_PHONE");
		fields.add("MOBILE_");
		fields.add("MAIL_");
		fields.add("OFFICE_ADDRESS");
		fields.add("SRC_PERSON_ID");
		Pagination page = getPaginationBySql(query.getPageIndex(),
				query.getPageSize(), sql.toString(), values.toArray(), fields);
		List<?> data = page.getData();
		List<Object> nodes = new ArrayList<Object>();
		for (Object obj : data) {
			if (obj == null) {
				continue;
			}
			PersonVO node = new PersonVO();
			Object[] vs = (Object[]) obj;
			node.setPersonId(Long.valueOf(vs[0].toString()));
			node.setName(vs[1] == null ? null : vs[1].toString());
			node.setOrganName(vs[2] == null ? null : vs[2].toString());
			node.setUid(vs[3] == null ? null : vs[3].toString());
			node.setLoginType(vs[4] == null ? null : Integer.valueOf(vs[4]
					.toString()));
			node.setLoginTimes(vs[5] == null ? null : Integer.valueOf(vs[5]
					.toString()));
			boolean isPluralistic = false;
			if (vs[6] != null) {
				if (Integer.valueOf(vs[6].toString()) == 1) {
					isPluralistic = true;
				}
				node.setIsPluralistic(isPluralistic);
			}
			node.setLastLoginIp(vs[7] == null ? null : vs[7].toString());
			node.setStatus(vs[8] == null ? null : vs[8].toString());
			node.setUserId(vs[9] == null ? null
					: Long.valueOf(vs[9].toString()));
			String ds = vs[10] == null ? null : vs[10].toString();
			if (ds != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					node.setLastLoginDate(sdf.parse(ds));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			node.setSortNum(vs[11] == null ? null : Long.valueOf(vs[11]
					.toString()));
			node.setOrganId(vs[12] == null ? null : Long.valueOf(vs[12]
					.toString()));
			node.setUnitId(vs[13] == null ? null : Long.valueOf(vs[13]
					.toString()));
			node.setUnitName(vs[14] == null ? null : vs[14].toString());
			node.setPositions(vs[15] == null ? null : vs[15].toString());
			node.setOfficePhone(vs[16] == null ? null : vs[16].toString());
			node.setMobile(vs[17] == null ? null : vs[17].toString());
			node.setMail(vs[18] == null ? null : vs[18].toString());
			node.setOfficeAddress(vs[19] == null ? null : vs[19].toString());
			node.setSrcPersonId(vs[20] == null ? null : Long.valueOf(vs[20]
					.toString()));
			nodes.add(node);
		}
		page.setData(nodes);
		return page;
	}

	@Override
	public List<PersonNodeVO> getExcelResults(Long[] organIds, Long[] roleIds,
											  String searchText, String sortField, String sortOrder) {
		StringBuffer sql = new StringBuffer();
		sql.append("select np.person_id,np.name_,np.organ_name,np.uid_,u.login_type,u.login_times,u.last_login_date,u.last_login_ip,u.status,u.user_id,np.is_pluralistic,np.sort_num ");
		sql.append(" from (select p.* from rbac_person p, rbac_organ_person op ");
		sql.append(" where p.person_Id = op.person_Id ");
		List<Object> values = new ArrayList<Object>();
		for (int i = 0; i < organIds.length; i++) {
			if (i == 0) {
				sql.append(" and (op.organ_id=?");
			} else {
				sql.append(" or op.organ_id=?");
			}
			if (i == organIds.length - 1) {
				sql.append(")");
			}
			values.add(organIds[i]);
		}

		if (!StringUtils.isEmpty(searchText)) {
			sql.append(" and (p.uid_ like ? escape '\\' or p.name_ like ? escape '\\' or p.organ_name like ? escape '\\')");
			String target = SqlUtil.prepareParam4Query(searchText);
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
			values.add("%".concat(target).concat("%"));
		}
		sql.append(" and op.record_Status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		sql.append(" and p.record_Status=?)");
		values.add(AMockEntity.RecordStatus.Normal.toString());

		if (roleIds != null && roleIds.length > 0) {
			sql.append(" np ,rbac_user u where ");
			StringBuffer areaSql = new StringBuffer(
					"exists (select * from (select distinct(u.user_id) from rbac_role_assignment ra, rbac_user u  where ra.record_status=? and u.record_status=? and ra.user_id = u.user_id");
			values.add(AMockEntity.RecordStatus.Normal.toString());
			values.add(AMockEntity.RecordStatus.Normal.toString());
			for (int i = 0; i < roleIds.length; i++) {
				Long roleId = roleIds[i];
				if (i == 0) {
					areaSql.append(" and (ra.role_id=?");
				} else {
					areaSql.append(" or ra.role_id=?");
				}
				if (i == roleIds.length - 1) {
					areaSql.append(")");
				}
				values.add(roleId);
			}
			areaSql.append(") tu where np.user_id=u.user_id and tu.user_id=u.user_id)");
			sql.append(areaSql);
		} else {
			sql.append(" np left join rbac_user u on ");
			sql.append(" np.user_id = u.user_id");
		}

		if (!StringUtils.isEmpty(sortField)) {
			if ("name".equals(sortField)) {
				sql.append(" order by np.name_");
			} else if ("organName".equals(sortField)) {
				sql.append(" order by np.organ_name");
			} else if ("uid".equals(sortField)) {
				sql.append(" order by np.uid_");
			} else if ("loginType".equals(sortField)) {
				sql.append(" order by u.login_type");
			} else if ("loginTimes".equals(sortField)) {
				sql.append(" order by u.login_times");
			} else if ("lastLoginDate".equals(sortField)) {
				sql.append(" order by u.last_login_date");
			} else {
				sql.append(" order by u.last_login_ip");
			}
			if (!StringUtils.isEmpty(sortOrder)) {
				sql.append(" ").append(sortOrder);
			}
		} else {
			sql.append(" order by np.sort_num");
		}

		List<String> fields = new ArrayList<String>();
		fields.add("person_id");
		fields.add("name_");
		fields.add("organ_name");
		fields.add("uid_");
		fields.add("login_type");
		fields.add("login_times");
		fields.add("IS_PLURALISTIC");
		fields.add("last_login_ip");
		fields.add("status");
		fields.add("user_id");
		fields.add("last_login_date");
		fields.add("sort_num");
		List<Object> data = getObjectsBySql(sql.toString(), values.toArray(),
				fields);
		List<PersonNodeVO> nodes = new ArrayList<PersonNodeVO>();
		for (Object obj : data) {
			if (obj == null) {
				continue;
			}
			PersonNodeVO node = new PersonNodeVO();
			Object[] vs = (Object[]) obj;
			node.setPersonId(Long.valueOf(vs[0].toString()));
			node.setName(vs[1] == null ? null : vs[1].toString());
			node.setOrganName(vs[2] == null ? null : vs[2].toString());
			node.setUid(vs[3] == null ? null : vs[3].toString());
			node.setLoginType(vs[4] == null ? null : Integer.valueOf(vs[4]
					.toString()));
			node.setLoginTimes(vs[5] == null ? null : Integer.valueOf(vs[5]
					.toString()));
			boolean isPluralistic = false;
			if (vs[6] == null) {
				if (Integer.valueOf(vs[6].toString()) == 1) {
					isPluralistic = true;
				}
				node.setIsPluralistic(isPluralistic);
			}
			node.setLastLoginIp(vs[7] == null ? null : vs[7].toString());
			node.setStatus(vs[8] == null ? null : vs[8].toString());
			node.setUserId(vs[9] == null ? null
					: Long.valueOf(vs[9].toString()));

			String ds = vs[10] == null ? null : vs[10].toString();
			if (ds != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
				try {
					node.setLastLoginDate(sdf.parse(ds));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			nodes.add(node);
		}
		return nodes;
	}

	@Override
	public PersonEO getUnpluralisticPersons(Long userId) {
		String hql = "from PersonEO p where p.recordStatus=? and p.isPluralistic=? and p.userId=?";
		return getEntityByHql(hql, new Object[] {
				AMockEntity.RecordStatus.Normal.toString(), false, userId });
	}

	@Override
	public PersonEO getPersonByUserId(Long organId, Long userId) {
		String hql = "from PersonEO p where p.organId=? and p.userId=? and p.recordStatus=?";
		return getEntityByHql(hql, new Object[] { organId, userId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public PersonEO getPersonByUid(Long organId, String uid) {
		StringBuffer hql = new StringBuffer("from PersonEO p where p.uid=?");
		Object[] values = new Object[3];
		values[0] = uid;
		if (organId == null || organId <= 0) {
			hql.append(" and p.isPluralistic=?");
			values[1] = Boolean.FALSE;
		} else {
			hql.append(" and p.organId=?");
			values[1] = organId;
		}
		hql.append(" and p.recordStatus=?");
		values[2] = AMockEntity.RecordStatus.Normal.toString();
		return getEntityByHql(hql.toString(), values);
	}

	@Override
	public List<PersonEO> getPersonsByUserIds(Long[] organIds, Long[] userIds) {
		StringBuffer hql = new StringBuffer("from PersonEO p where 1=1 ");
		List<Object> values = new ArrayList<Object>();
		if (organIds != null && organIds.length > 0) {
			hql.append("and (");
			int length = organIds.length;
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					hql.append(" (p.organId=? and p.userId=?)");
				} else {
					hql.append(" or (p.organId=? and p.userId=?)");
				}
				values.add(organIds[i]);
				values.add(userIds[i]);
			}
			hql.append(")");
		}
		hql.append("and p.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	// @Override
	// public List<PersonEO> getPersonsByDns(List<String> dns) {
	// String hql =
	// "from PersonEO p where p.dn in (:dns) and p.recordStatus=(:recordStatus) order by p.sortNum";
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("dns", dns);
	// params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
	// return getEntitiesByHql(hql, params);
	// }
	@Override
	public List<PersonEO> getPersonsByDns(List<String> dns) {
		// StringBuilder sql = new
		// StringBuilder("select p.PERSON_ID as personId,p.NAME_ as name,p.SEX_ as sex,p.BIRTH_ as birth,p.POSITIONS_ as positions,p.MOBILE_ as mobile,p.OFFICE_PHONE as officePhone,p.OFFICE_ADDRESS as officeAddress,p.MAIL_ as mail,p.MAIL_SEND_FILES as mailSendFiles,p.JPEG_PHOTO as jpegPhoto,p.IS_PLURALISTIC as isPluralistic,p.FULL_PY as fullPy,");
		// sql.append("p.SIMPLE_PY as simplePy,p.SORT_NUM as sortNum,p.SRC_PERSON_ID as srcPersonId,p.ORGAN_ID as organId,p.ORGAN_Name as organName,p.UNIT_ID as unitId,p.UNIT_NAME as unitName,p.DN_ as dn,p.USER_ID as userId,p.UID_ as uid,p.max_capacity as maxCapacity");
		// sql.append(" from rbac_person p where 1=1");
		StringBuilder sql = new StringBuilder("from PersonEO p where 1=1");
		List<Object> values = new ArrayList<Object>();
		if (dns != null && dns.size() > 0) {
			int size = dns.size();
			for (int i = 0; i < size; i++) {
				String dn = dns.get(i);
				if (i == 0) {
					sql.append(" and (p.dn=?");
				} else {
					sql.append(" or p.dn=?");
				}
				values.add(dn);
				if (i == size - 1) {
					sql.append(")");
				}
			}
		}
		sql.append(" and p.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		List<PersonEO> persons = getEntitiesByHql(sql.toString(),
				values.toArray());
		return persons;
	}

	@Override
	public List<PersonEO> getAllPersons() {
		String hql = "from PersonEO p where p.recordStatus=? order by p.sortNum";
		return getEntitiesByHql(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<PersonEO> getPsersonsByUnitId(Long unitId) {
		String hql = "from PersonEO p where p.recordStatus=? and p.unitId = ? order by p.sortNum";
		return getEntitiesByHql(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(),unitId});
	}

	@Override
	public List<PersonEO> getPersonsByLikeName(Long unitId,String name) {
		String hql = "from PersonEO p where p.recordStatus=? and p.name  like '%" + name + "%' escape '\\'";
		if(!AppUtil.isEmpty(unitId)) {
			hql += " and p.unitId ='" + unitId + "'";
		}
		return getEntitiesByHql(hql, new Object[] {AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<PersonEO> getPersonsByDn(String dn) {
		StringBuilder hql = new StringBuilder("from PersonEO o where o.recordStatus= ? and o.dn like ? order by o.sortNum asc");
		return getEntitiesByHql(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString(),"%"+dn});
	}
}
