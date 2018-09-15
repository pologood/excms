package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IUserDao;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;

@Repository("userDao")
public class UserDaoImpl extends MockDao<UserEO> implements IUserDao {
	
	@Override
	public UserEO getUserByMobile(String mobile){
		String hql = "select u from UserEO u,PersonEO p where u.userId=p.userId and p.isPluralistic=? and p.mobile=? and p.recordStatus=? and u.recordStatus=?";
		return getEntityByHql(hql, new Object[]{false,mobile,RecordStatus.Normal.toString(),RecordStatus.Normal.toString()});
	}

	@Override
	public UserEO getUser(String uid) {
		String hql = "from UserEO u where u.uid=? and u.recordStatus=?";
		return getEntityByHql(hql, new Object[] { uid,
				AMockEntity.RecordStatus.Normal.toString() });
	}
	
	@Override
	public UserEO getUserByUidOrMobile(String argument){
		String hql = "select u from UserEO u,PersonEO p where u.userId=p.userId and (u.uid=? or p.mobile=?) and u.recordStatus=? and p.recordStatus=? and p.isPluralistic=0";
		return getEntityByHql(hql, new Object[] { argument,argument,RecordStatus.Normal.toString(),RecordStatus.Normal.toString() });
	}

	@Override
	public String getDn4User(Long personId) {
		String hql = "from PersonEO p where p.id=? and p.recordStatus=?";
		PersonEO person = (PersonEO) getObject(hql, new Object[] { personId,
				AMockEntity.RecordStatus.Normal.toString() });
		return person == null ? null : person.getDn();
	}

	@Override
	public List<UserEO> getUsersBySubUid(String subUid, Integer maxCount) {
		String hql = "from UserEO u where u.uid like ? escape '\\' and u.recordStatus=?";
		String targetUid = "%".concat(SqlUtil.prepareParam4Query(subUid)).concat("%");
		return getEntities(hql, new Object[] { targetUid,
				AMockEntity.RecordStatus.Normal.toString() }, maxCount);
	}

    @Override
    public List<UserEO> getUsersByRoleId(Long roleId) {
        String hql = "select u from RoleAssignmentEO t,UserEO u where t.userId=u.userId and t.roleId=? and u.recordStatus=?";
        return this.getEntitiesByHql(hql, new Object[]{roleId,AMockEntity.RecordStatus.Normal.toString()});
    }
	
	@Override
	public Pagination getPageByUid(Long index, Integer size, Long organId,
			String subUid, String orderField, boolean isDesc) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select u from UserEO as u,OrganUserEO as ou where  u.userId=ou.userId and ou.organId=?");
		values.add(organId);
		
		if(!StringUtils.isEmpty(subUid)){
			hql.append(" and u.uid like ?  escape '\\'");
			String target = "%"+SqlUtil.prepareParam4Query(subUid)+"%";
			values.add(target);
		}
		hql.append(" and u.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(orderField)){
			hql.append(" order by u.").append(orderField);
			if(isDesc){
				hql.append(" desc");
			}else{
				hql.append(" asc");
			}
		}
		return getPagination(index, size, hql.toString(), values.toArray());
	}
	
	
	@Override
	public Pagination getPage(Long index, Integer size, Long organId,
			String subUid, String name,String organName,String orderField, boolean isDesc) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select u from UserEO as u,OrganUserEO as ou where  u.userId=ou.userId and ou.organId=?");
		values.add(organId);
		
		if(!StringUtils.isEmpty(subUid)){
			hql.append(" and u.uid like ?  escape '\\'");
			String target = "%"+SqlUtil.prepareParam4Query(subUid)+"%";
			values.add(target);
		}
		
		if(!StringUtils.isEmpty(organName)){
			hql.append(" and u.organName like ?  escape '\\'");
			String target = "%"+SqlUtil.prepareParam4Query(organName)+"%%";
			values.add(target);
		}
		
		if(!StringUtils.isEmpty(name)){
			hql.append(" and u.name like  ? escape '\\'");
			String target = "%"+SqlUtil.prepareParam4Query(name)+"%";
			values.add(target);
		}
		
		hql.append(" and u.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(orderField)){
			hql.append(" order by u.").append(orderField);
			if(isDesc){
				hql.append(" desc");
			}else{
				hql.append(" asc");
			}
		}
		return getPagination(index, size, hql.toString(), values.toArray());
	}
	
	@Override
	public String getPassword(Long userId){
		String password = null;
		String sql = "select u.password_ from rbac_user u where u.user_id=? and u.record_status=?";
		List<String> fields = new ArrayList<String>();
		fields.add("password_");
		List<?> objs = getObjectsBySql(sql, new Object[]{userId,AMockEntity.RecordStatus.Normal.toString()},fields);
		if(objs!=null&&objs.size()>0){
			password = objs.get(0)==null?null:objs.get(0).toString();
		}
		return password;
	}
	

	@Override
	public Pagination getPageByName(Long index, Integer size, Long organId,
			String name, String orderField, boolean isDesc) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select u from UserEO as u,OrganUserEO as ou where  u.userId=ou.userId and ou.organId=?");
		values.add(organId);
		if(!StringUtils.isEmpty(name)){
			hql.append(" and u.name like ?  escape '\\'");
			String target = "%"+SqlUtil.prepareParam4Query(name)+"%";
			values.add(target);
		}
		hql.append(" and u.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(!StringUtils.isEmpty(orderField)){
			hql.append(" order by u.").append(orderField);
			if(isDesc){
				hql.append(" desc");
			}else{
				hql.append(" asc");
			}
		}
		return getPagination(index, size, hql.toString(), values.toArray());
	}

}
