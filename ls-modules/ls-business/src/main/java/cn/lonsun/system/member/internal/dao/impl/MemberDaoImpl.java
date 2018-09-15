package cn.lonsun.system.member.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.vo.MemberQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MemberDaoImpl extends MockDao<MemberEO> implements IMemberDao {

	@Override
	public Pagination getPage(MemberQueryVO query) {
		// 取参数
		StringBuffer hql = new StringBuffer();
		List<Object> values = new ArrayList<Object>();
		hql.append("from MemberEO where recordStatus = ?");
		values.add(RecordStatus.Normal.toString());
		if(query.getSiteId() != null){
			hql.append(" and siteId = ?");
			values.add(query.getSiteId());
		}
		if(query.getMemberType() != null){
			hql.append(" and memberType = ?");
			values.add(query.getMemberType());
		}
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and (uid like ? or name like ? or phone like ? or idCard like ?)");
			values.add("%".concat(SqlUtil.prepareParam4Query(query.getSearchText())).concat("%"));
			values.add("%".concat(SqlUtil.prepareParam4Query(query.getSearchText())).concat("%"));
			values.add("%".concat(SqlUtil.prepareParam4Query(query.getSearchText())).concat("%"));
			values.add("%".concat(SqlUtil.prepareParam4Query(query.getSearchText())).concat("%"));
		}
		hql.append(" order by id desc");
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray()) ;
	}

	@Override
	public Long getUidCount(String uid,Long siteId, Long id) {
		String hql = "from MemberEO where recordStatus = ? and uid = ? and siteId = ?";
		if(id != null){
			hql +=" and id != "+id;
		}
		return getCount(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(),uid,siteId});
	}

	@Override
	public Long isExistPhone(String phone, Long siteId, Long id) {
		String hql = "from MemberEO where recordStatus = ? and phone = ? and siteId = ?";
		if(id != null){
			hql +=" and id != "+id;
		}
		return getCount(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(),phone,siteId});
	}

	@Override
	public List<MemberEO> getByNumber(String number, Long siteId) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from MemberEO where recordStatus = ? and siteId = ?";
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(siteId);
		if(!StringUtils.isEmpty(number)){
			hql+=" and (idCard=? or phone=?)";
			values.add(SqlUtil.prepareParam4Query(number));
			values.add(SqlUtil.prepareParam4Query(number));
		}
		return getEntitiesByHql(hql,values.toArray());
	}

	@Override
	public Object getMembers(Integer type,Long siteId) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from MemberEO where recordStatus = ? and siteId = ?";
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(siteId);
		if(type != null){
			hql +=" and memberType = ?";
			values.add(type);
		}
		hql +=" order by id desc";
		return getEntitiesByHql(hql,values.toArray());
	}
}
